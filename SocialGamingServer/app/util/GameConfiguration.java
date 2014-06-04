package util;

/**
 * Simple class that holds all configuration specific information for the game.
 * 
 * @author Niklas Klügel
 *
 */
public class GameConfiguration {
	// Facebook App credentials
	public static String fbAppID = "302990826523232";	
	public static String fbAppSecret  = "ccbef7b6bbca054e8d4b8587aa94fa15";
	
	// Google App credentials
	public static String googleAppID = "prime-freedom-565";
	public static String googleAppKey = "AIzaSyDAc1Ey0o26y7_N2iKueB-fVmFEJwM_2AM";
	
	// limits the maximum distance for user lookups (meters)
	public static double MaxDistanceOfUserForNearbyUsers = 100.0;
	public static int MaxNumberOfReturnedUsers = 20;
	
	// this is the time to live for a user login to be still valid
	// e.g. if chosen as an opponent the user has to logged less than these seconds ago
	public static long MaxTimeForLoginTimeOutInSeconds = 360000;

}
