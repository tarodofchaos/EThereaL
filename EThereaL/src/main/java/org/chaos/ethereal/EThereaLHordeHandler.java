package org.chaos.ethereal;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.chaos.ethereal.helper.AppConstants;
import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.helper.SequenceHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;
import org.chaos.ethereal.utils.AmazonUtils;
import org.chaos.ethereal.utils.UtilHelper;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.util.Base64;

public class EThereaLHordeHandler implements RequestHandler<S3Event, String> {

	private ArmyHelper armyHelper;
    private BattleHelper battleHelper;
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new SystemPropertiesCredentialsProvider()).withRegion(Regions.EU_WEST_1).build();
	DynamoDBMapper mapper = new DynamoDBMapper(client);

    public EThereaLHordeHandler() {}
    
    // This variable will hold your decrypted key. Decryption happens on first
    // invocation when the container is initialized and never again for
    // subsequent invocations.
    private static String DECRYPTED_KEY = decryptKey("aws_accessKeyId");
    private static String DECRYPTED_SECRET_KEY = decryptKey("aws_secretKey");

    private static String decryptKey(String key) {
        System.out.println("Decrypting key");
        byte[] encryptedKey = Base64.decode(System.getenv(key));
        

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }
    
    private void initHandler(Context context) {
    	armyHelper = new ArmyHelper(context.getLogger());
		battleHelper = new BattleHelper(context.getLogger());
	}

    @Override
    public String handleRequest(S3Event event, Context context) {
    	Date d1 = new Date();
    	initHandler(context);
    	context.getLogger().log("Received event: " + event);
        List<String> phases;
        //Workaround to changed Amazon protected environment variables for authentication
        //This is just a convenient way of building AWS SDK clients
        System.setProperty("aws.accessKeyId", DECRYPTED_KEY);
        System.setProperty("aws.secretKey", DECRYPTED_SECRET_KEY);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        
        BattleReport report = new BattleReport();
        try {
        	//We generate the POJO from the provided file. This would be the Extract phase
            Army army = armyHelper.createArmyFromIS(AmazonUtils.downloadObject(bucket, key));
            context.getLogger().log("Army created");
            //We validate the provided file. This is part of the Extract phase
            armyHelper.validateArmy(army);
            
            //A conveniently formatted filename gives us the battle phases. This is the start of the Transform phase
            phases = Arrays.asList(key.split("_")[1].split(""));
            report = battleHelper.resolveBattle(army, phases);
            
            //After all the transformations, a report is generated and save. This is the Load phase
            report.setId(SequenceHelper.getNewSeq(AmazonUtils.getTableName(report.getClass())));
            String miliseconds = UtilHelper.getSecondsAndMillisecondsDelta(d1, new Date());	
            report.setBattleTime(miliseconds);
            mapper.save(report);
            
            //To finish, an email is sent to all the subscribers of the SNS topic
            AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_SUCCESS_ARN_TOPIC, report.toString(), null, "Battle success");
            return "OK";
        } catch (Exception e) {
        	//Exceptions are converted and sent via email to SNS subscribers
        	 e.printStackTrace();
             context.getLogger().log(e.getMessage());
             AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_ERROR_ARN_TOPIC, String.format(
                     e.getMessage(), key, bucket), null, "Error in battle");
             return "KO";
        }
    }

}