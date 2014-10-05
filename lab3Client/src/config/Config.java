package config;

public abstract class Config {

	public static final int                    BUF_SIZE = 1024;

	public static final String 	   MULTICAST_IP_ADDRESS = "239.4.4.4";
	public static final int MULTICASTSOCKET_PORT_NUMBER = 13291;
	public static final int             TCP_PORT_NUMBER = 12000;
	
	//Dynamic Server Discovery.
	public static final int                    DSD_PORT = 1900;
	public static final String              DSD_ADDRESS = "239.255.255.250";
	public static final String              SERVER_NAME = "Manges_Server";
}
