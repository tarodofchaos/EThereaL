package org.chaos.ethereal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.chaos.ethereal.helper.AmazonUtils;
import org.chaos.ethereal.helper.AppConstants;
import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.helper.SequenceHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EThereaLRestHandler implements RequestStreamHandler {

	JSONParser parser = new JSONParser();
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
		JSONObject event;
		BattleReport report = new BattleReport();
		//Workaround to changed Amazon protected environment variables for authentication
        //This is just a convenient way of building AWS SDK clients
		System.setProperty("aws.accessKeyId", System.getenv("aws_accessKeyId"));
        System.setProperty("aws.secretKey", System.getenv("aws_secretKey"));
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			event = (JSONObject) this.parser.parse(reader);
			JSONObject pps = (JSONObject) event.get("pathParameters");
			
			if (pps != null) {
				Integer heroes = Integer.parseInt((String) pps.get("heroes"));
				Integer monsters = Integer.parseInt((String) pps.get("monsters"));
				List<String> phases = Arrays.asList(((String)pps.get("phases")).split(""));
				
				//We generate the POJO from the provided parameters. This would be the Extract phase
				Army army = armyHelper.createArmy(monsters, heroes);
				
				//We validate the provided file. This is part of the Extract phase
	            armyHelper.validateArmy(army);
	            
	          //This is the start of the Transform phase
				report = battleHelper.resolveBattle(army, phases);
				
				//After all the transformations, a report is generated and save. This is the Load phase
	            report.setId(SequenceHelper.getNewSeq(AmazonUtils.getTableName(report.getClass())));
	            dbMapper.save(report);
	            
	          //To finish, an email is sent to all the subscribers of the SNS topic
	            AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_SUCCESS_ARN_TOPIC, report.toString(), null, "Battle success");
				
	          //Since it is also a REST call, an status code and a message are returned to the caller
				OutputMessageDTO output = new OutputMessageDTO();
				output.setSuccessful(true);
				event = new JSONObject();
				event.put("statusCode", "200");
				event.put("body", mapper.writeValueAsString(output));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
				writer.write(event.toJSONString());
				writer.close();
			}else {
				throw new Exception("All parameters must be sent");
			}
		
		} catch (Exception e) {
			//Exceptions are converted and sent via email to SNS subscribers
			//Since it is also a REST call, an status code and a message are returned to the caller
			e.printStackTrace();
			AmazonUtils.sendMessageToSnsTopic(AppConstants.SNS_ERROR_ARN_TOPIC, e.getMessage(), null, "Error in battle");
			OutputMessageDTO output = new OutputMessageDTO();
			output.setSuccessful(false);
			event = new JSONObject();
			event.put("statusCode", "500");
			event.put("body", mapper.writeValueAsString(output));
			OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
			writer.write(event.toJSONString());
			writer.close();
		}
    }

}
