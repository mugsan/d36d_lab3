package lab3Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

import config.Config;

public class GameServerDSD extends Thread{
	private InetAddress clientAddress = null;
	private int            clientPort = 0;
	private SocketAddress     address = null;
	
	private String receive(MulticastSocket socket) throws IOException{
		
		byte[] buffer = new byte[128];
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

		socket.receive(dp);
		
		this.clientAddress = dp.getAddress();
		this.clientPort    = dp.getPort();
		this.address       = dp.getSocketAddress();
		

		return new String(dp.getData(),0 ,dp.getLength());
	}
	
	private void send(DatagramSocket socket, String string) throws IOException{
		
		byte[] buffer = string.getBytes();
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, this.clientAddress, this.clientPort);
		
		socket.send(dp);
	}

	
	@Override
	public void run(){
		try( 
			DatagramSocket   ds = new DatagramSocket();
			MulticastSocket mcs = new MulticastSocket(1900);
		){
			InetAddress address = InetAddress.getByName("239.255.255.250");
			mcs.joinGroup(address);
			while(true){
				String str = this.receive(mcs);
				
				System.out.println("Received something: " + str);
				if(str.equals("SERVICE QUERY JavaGameServer")){
					
					str = "SERVICE REPLY JavaGameServer " + Config.SERVER_NAME + " " + InetAddress.getLocalHost().getHostAddress() + " " + Config.TCP_PORT_NUMBER;
					this.send(ds, str);
				}
			}
//			mcs.leaveGroup(address);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
