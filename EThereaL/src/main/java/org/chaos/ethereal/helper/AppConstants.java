package org.chaos.ethereal.helper;

public class AppConstants {
	public static final String YES = "Y";
	public static final String NO = "N";
	
	public static final String TYPE_NUMBER = "Number";
	public static final String TYPE_DIE = "Die";
	public static final String TYPE_STRING = "String";
	
	public static final String FIGHTER = "Fighter";
	public static final String ROGUE = "Rogue";
	public static final String MAGI = "Magi";
	public static final String CLERIC = "Cleric";

	public static final String HUMAN = "Human";
	public static final String ELF = "Elf";
	public static final String DWARF = "Dwarf";
	public static final String HALFLING = "Halfling";

	public static final String STRENGTH = "Strength";
	public static final String DEXTERITY = "Dexterity";
	public static final String MAGIC = "Magic";

	public static final String S3_ARMY_PATH = "private/army";
	public static final String S3_BUCKET = "ethereal-app";

	public static final String HEROES = "Heroes";
	public static final String MONSTERS = "Monsters";

	public static final String DYNAMODB_DEFAULT_REGION = "eu-west-1";
	public static final String DYNAMODB_DEFAULT_NODE = "https://dynamodb.eu-west-1.amazonaws.com";
	public static final String SNS_SUCCESS_ARN_TOPIC = "arn:aws:sns:eu-west-1:928494240687:ethereal-battle_success";
	public static final String SNS_ERROR_ARN_TOPIC = "arn:aws:sns:eu-west-1:928494240687:ethereal-battle_error";

	public static final String TABLE_ETHEREAL_ID_GENERATOR = "ethereal_id_generator";
}
