package lab3Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import config.Config;

public class GameClientDSDThread extends Thread{
	private GameClientViewController gvc = null;

	public GameClientDSDThread(GameClientViewController gvc) {
		this.gvc = gvc;
	}
	
	private String receive(DatagramSocket socket) throws IOException{
		
		byte[] buffer = new byte[128];
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(dp);

		return new String(dp.getData(),0 ,dp.getLength());
		
	}
	
	private void send(DatagramSocket socket, InetAddress ia) throws IOException{
		
		String msg = "SERVICE QUERY JavaGameServer";
		
		byte[] buffer = msg.getBytes();
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ia, Config.DSD_PORT);
		
		socket.send(dp);

	}
	
	@Override
	public void run(){
		try(
			DatagramSocket ds = new DatagramSocket();
		){
			InetAddress ia = InetAddress.getByName(Config.DSD_ADDRESS);
			
			new Thread(){
				GameClientDSDThread that = null;
				@Override
				public void run(){
					while(!that.gvc.getIsPlaying()){
						try {
							String str = that.receive(ds);
							that.gvc.add(str);
						} catch (Exception e) {
							System.out.println(e.toString());
						}
					}
				}
				private Thread init(GameClientDSDThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			while(!this.gvc.getIsPlaying()){
				if(this.gvc.getIsScanning()){
					this.send(ds, ia);
					Thread.sleep(1000);
				}
			}
			System.out.println("Thread out.");
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

}
