package org.chaos.ethereal;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.helper.SequenceHelper;
import org.chaos.ethereal.helper.UtilHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;

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
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.Base64;

public class EThereaLHordeHandler implements RequestHandler<S3Event, String> {

    private static final String SNS_ERROR_ARN_TOPIC = "arn:aws:sns:eu-west-1:928494240687:ethereal-battle_error";
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
    
    private void initHanlder(Context context) {
    	armyHelper = new ArmyHelper(context.getLogger());
		battleHelper = new BattleHelper(context.getLogger());
	}

    @Override
    public String handleRequest(S3Event event, Context context) {
        initHanlder(context);
    	context.getLogger().log("Received event: " + event);
        List<String> phases;
        System.setProperty("aws.accessKeyId", DECRYPTED_KEY);
        System.setProperty("aws.secretKey", DECRYPTED_SECRET_KEY);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        
        BattleReport report = new BattleReport();
        try {
            Army army = armyHelper.createArmyFromIS(UtilHelper.downloadObject(bucket, key));
            context.getLogger().log("Army created");
            armyHelper.validateArmy(army);
            phases = Arrays.asList(key.split("_")[1].split(""));
            report = battleHelper.resolveBattle(army, phases);
            report.setId(SequenceHelper.getNewSeq(UtilHelper.getTableName(report.getClass())));
            mapper.save(report);
            UtilHelper.sendMessageToSnsTopic("arn:aws:sns:eu-west-1:928494240687:ethereal-battle_success", String.format(
                    "The battle completed successfully. Battle report id is: "+report.getId(), key, bucket), null, "Battle success");
            return "OK";
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            UtilHelper.sendMessageToSnsTopic(SNS_ERROR_ARN_TOPIC, String.format(
                    "Error getting object %s from bucket %s. Make sure they exist and"
                            + " your bucket is in the same region as this function.", key, bucket), null, "Error in battle");
            throw e;
        } catch (Exception e1) {
        	 e1.printStackTrace();
             context.getLogger().log(String.format(
                 "Error getting object %s from bucket %s. Make sure they exist and"
                 + " your bucket is in the same region as this function.", key, bucket));
             UtilHelper.sendMessageToSnsTopic(SNS_ERROR_ARN_TOPIC, String.format(
                     e1.getMessage(), key, bucket), null, "Error in battle");
             throw e1;
        }
    }

}