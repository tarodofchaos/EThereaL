package org.chaos.ethereal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.utils.AmazonUtils;
import org.chaos.ethereal.utils.AppConstants;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class EThereaLRestAsyncHandler implements RequestStreamHandler {

    ArmyHelper armyHelper;
	BattleHelper battleHelper;
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new SystemPropertiesCredentialsProvider()).withRegion(Regions.EU_WEST_1).build();
	DynamoDBMapper dbMapper = new DynamoDBMapper(client);
	
    private void initHandler(Context context) {
    	armyHelper = new ArmyHelper(context.getLogger());
		battleHelper = new BattleHelper(context.getLogger());
	}
    
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	initHandler(context);
    	ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		Gson gson = new Gson();
		//Workaround to changed Amazon protected environment variables for authentication
        //This is just a convenient way of building AWS SDK clients
		System.setProperty("aws.accessKeyId", System.getenv("aws_accessKeyId"));
        System.setProperty("aws.secretKey", System.getenv("aws_secretKey"));
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			Map<?, ?> event = gson.fromJson(reader, Map.class);
			String heroesEv = (String) event.get("heroes");
			String monstersEv = (String) event.get("monsters");
			String phasesEv = (String) event.get("phases");
			
			if (StringUtils.isNotEmpty(heroesEv) && StringUtils.isNotEmpty(monstersEv) && StringUtils.isNotEmpty(phasesEv)) {
				int socketTimeout = AmazonUtils.getTimeoutLambda(System.getenv("LAMBDA_NAME")) * 1000;
				context.getLogger().log("Set up the socket timeout to " + socketTimeout + " millis");
		    	
		    	ClientConfiguration clientConfig = new ClientConfiguration(); 
		    	clientConfig.setSocketTimeout(socketTimeout); 		    	
		    	AWSLambdaClientBuilder awsLambdaClientBuilder = AWSLambdaClientBuilder.standard().withRegion(Regions.EU_WEST_1);
		    	awsLambdaClientBuilder.setClientConfiguration(clientConfig);
            	AWSLambda client = awsLambdaClientBuilder.build();
            	
            	InvokeRequest request = new InvokeRequest().withFunctionName(System.getenv("LAMBDA_NAME")).withPayload(mapper.writeValueAsString(event));
				String outputStr = new String(((InvokeResult)client.invoke(request)).getPayload().array());	   					
				Map<String, Object> outMap = new HashMap<>();
				outMap.put("callResult", outputStr);
				outMap.put("msg", "Battle has begun!!");
	          //Since it is also a REST call, an status code and a message are returned to the caller
				OutputMessageDTO output = new OutputMessageDTO();
				output.setSuccessful(true);
				output.setOutput(outMap);
				outMap.put("statusCode", "200");
				outMap.put("body", mapper.writeValueAsString(output));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
				writer.write(gson.toJson(outMap));
				writer.close();
			}else {
				throw new Exception("All parameters must be sent");
			}
		
		} catch (Exception e) {
			//Exceptions are converted and sent via email to SNS subscribers
			//Since it is also a REST call, an status code and a message are returned to the caller
			AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_ERROR_ARN_TOPIC, e.getMessage() != null ? e.getMessage():"Unknown error", null, "Error in battle");
			OutputMessageDTO output = new OutputMessageDTO();
			output.setSuccessful(false);
			Map<String, Object> outMap = new HashMap<>();
			outMap.put("statusCode", "500");
			outMap.put("body", mapper.writeValueAsString(output));
			OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
			writer.write(gson.toJson(outMap));
			writer.close();
		}
    }

}
