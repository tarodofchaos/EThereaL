package org.chaos.ethereal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.helper.SequenceHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;
import org.chaos.ethereal.utils.AmazonUtils;
import org.chaos.ethereal.utils.AppConstants;
import org.chaos.ethereal.utils.UtilHelper;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class EThereaLRestHandler implements RequestStreamHandler {

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
    	Date d1 = new Date();
    	initHandler(context);
    	ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		Gson gson = new Gson();
		BattleReport report = new BattleReport();
		//Workaround to changed Amazon protected environment variables for authentication
        //This is just a convenient way of building AWS SDK clients
		System.setProperty("aws.accessKeyId", System.getenv("aws_accessKeyId"));
        System.setProperty("aws.secretKey", System.getenv("aws_secretKey"));
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			Map<?, ?> event = gson.fromJson(reader, Map.class);
			Map<?, ?> pps = (Map<?, ?>) event.get("pathParameters");
			
			if (pps != null) {
				Integer heroes = Integer.parseInt((String) pps.get("heroes"));
				Integer monsters = Integer.parseInt((String) pps.get("monsters"));
				List<String> phases = Arrays.asList(((String)pps.get("phases")).split(""));
				
				//We generate the POJO from the provided parameters. This would be the Extract phase
				context.getLogger().log("Starting army creation with "+monsters+" and "+heroes+" heroes");
				Army army = armyHelper.createArmy(monsters, heroes);
				context.getLogger().log("Army creation finished");
				
				//We validate the provided file. This is part of the Extract phase
				context.getLogger().log("Starting army validation");
	            armyHelper.validateArmy(army);
	            context.getLogger().log("Army validation finished");
	            
	          //This is the start of the Transform phase
	            context.getLogger().log("Starting battle");
				report = battleHelper.resolveBattle(army, phases);
				context.getLogger().log("Battlefinished");
				
				//After all the transformations, a report is generated and save. This is the Load phase
	            report.setId(SequenceHelper.getNewSeq(AmazonUtils.getTableName(report.getClass())));
	            String miliseconds = UtilHelper.getSecondsAndMillisecondsDelta(d1, new Date());	
	            report.setBattleTime(miliseconds);
	            context.getLogger().log("Saving battle report");
	            dbMapper.save(report);
	            context.getLogger().log("Battle report saved");
	            
	          //To finish, an email is sent to all the subscribers of the SNS topic
	            context.getLogger().log("Sending emails");
	            AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_SUCCESS_ARN_TOPIC, report.toString(), null, "Battle success");
	            context.getLogger().log("Emails sent");
				
	          //Since it is also a REST call, an status code and a message are returned to the caller
				OutputMessageDTO output = new OutputMessageDTO();
				output.setSuccessful(true);
				Map<String, Object> outMap = new HashMap<>();
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
