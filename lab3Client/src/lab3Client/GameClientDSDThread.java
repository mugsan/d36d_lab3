package lab3Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import config.Config;

/**
 * The Class GameClientDSDThread.
 * Probes the local network for gameservers.
 * Updates a list in GameClientViewController with found servers.
 */
public class GameClientDSDThread extends Thread{
	
	/** The gvc. reference to object holding the list to update.*/
	private GameClientViewController gvc = null;

	/**
	 * Instantiates a new game client dsd thread.
	 * 
	 * sets reference to object with list to update.
	 *
	 * @param gvc the gvc
	 */
	public GameClientDSDThread(GameClientViewController gvc) {
		this.gvc = gvc;
	}
	
	/**
	 * Receives msg from a potential server.
	 *
	 * @param socket the socket
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String receive(DatagramSocket socket) throws IOException{
		
		byte[] buffer = new byte[128];
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(dp);

		return new String(dp.getData(),0 ,dp.getLength());
		
	}
	
	/**
	 * Sends query string to multicastgroup.
	 *
	 * @param socket the socket
	 * @param ia the ia
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void send(DatagramSocket socket, InetAddress ia) throws IOException{
		
		String msg = "SERVICE QUERY JavaGameServer";
		
		byte[] buffer = msg.getBytes();
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ia, Config.DSD_PORT);
		
		socket.send(dp);

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run(){
		try(
			DatagramSocket ds = new DatagramSocket();
		){
			InetAddress ia = InetAddress.getByName(Config.DSD_ADDRESS);
			
			//Listening thread.
			new Thread(){
				GameClientDSDThread that = null;
				@Override
				public void run(){
					while(!that.gvc.getIsPlaying()){
						try {
							String str = that.receive(ds);
							//Hack/Work around to swings non-threadsafeness.
							SwingUtilities.invokeLater(new Runnable(){
								String str = null;

								@Override
								public void run() {
									try {
										that.gvc.add(str);
									} catch (UnknownHostException e) {
										e.printStackTrace();
									}
								}
								private Runnable init(String str){
									this.str = str;
									return this;
								}
								
							}.init(str));
						} catch (Exception e) {
						}
					}
					System.out.println("DSD Inc: thread done.");
				}
				private Thread init(GameClientDSDThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			//Probing loop.
			while(!this.gvc.getIsPlaying()){
				if(this.gvc.getIsScanning()){
					this.send(ds, ia);
					Thread.sleep(1000);
				}
			}
			System.out.println("DSD Out: thread done.");
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

}
