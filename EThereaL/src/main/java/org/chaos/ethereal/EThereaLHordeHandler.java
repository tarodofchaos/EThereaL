package org.chaos.ethereal;

import java.util.Arrays;

import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.helper.UtilHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class EThereaLHordeHandler implements RequestHandler<S3Event, String> {

	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
    private ArmyHelper armyHelper = new ArmyHelper();
    private BattleHelper battleHelper = new BattleHelper();

    public EThereaLHordeHandler() {}

    EThereaLHordeHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        
        BattleReport report = new BattleReport();
        try {
            Army army = armyHelper.createArmyFromIS(UtilHelper.downloadObject(bucket, key));
            report = battleHelper.resolveBattle(army, Arrays.asList(key.split("_")[1].split("")));
            context.getLogger().log("Army created");
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            throw e;
        }
    }
}