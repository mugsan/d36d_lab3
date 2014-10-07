package lab3Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import config.Config;

/**
 * The Class GameServerDSD.
 * Exists so clients can find the server.
 */
public class GameServerDSD extends Thread{
	
	/** The client address. set by incoming datagram.*/
	private InetAddress clientAddress = null;
	
	/** The client port. set by incoming datagram.*/
	private int            clientPort = 0;
	
	
	/** The name. the server name*/
	private String name = null;
	
	/** The port. server port*/
	private int    port = 0;
	
	/**
	 * Instantiates a new game server dsd.
	 *
	 * @param name the name
	 * @param port the port
	 */
	public GameServerDSD(String name, int port){
		this.name = name;
		this.port = port;
	}

	/**
	 * Receive.
	 * Listens to multicastgroup for msg from potential clients.
	 * Sets variables address/port given from incoming datagram.
	 *
	 * @param socket the socket
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String receive(MulticastSocket socket) throws IOException{
		
		byte[] buffer = new byte[128];
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

		socket.receive(dp);
		
		this.clientAddress = dp.getAddress();
		this.clientPort    = dp.getPort();
		

		return new String(dp.getData(),0 ,dp.getLength());
	}
	
	/**
	 * Send.
	 * Sends the reply back to the incoming address/port given in this.receive
	 *
	 * @param socket the socket
	 * @param string the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void send(DatagramSocket socket, String string) throws IOException{
		
		byte[] buffer = string.getBytes();
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, this.clientAddress, this.clientPort);
		
		socket.send(dp);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 
	 * Listens to multicastgroup and responds to relevant msg.
	 * 
	 */
	@Override
	public void run(){
		try( 
			DatagramSocket   ds = new DatagramSocket();
			MulticastSocket mcs = new MulticastSocket(Config.DSD_PORT);
		){
			InetAddress address = InetAddress.getByName(Config.DSD_ADDRESS);
			while(true){
				mcs.joinGroup(address);
				String str = this.receive(mcs);
				
				if(str.equals("SERVICE QUERY JavaGameServer")){
					
					

					str = "SERVICE REPLY JavaGameServer " + this.name + " " + InetAddress.getLocalHost().getHostAddress() + " " + this.port;
					this.send(ds, str);
				}
				mcs.leaveGroup(address);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
