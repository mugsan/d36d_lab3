package lab3Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import lab3DataPacket.Msg;
import config.Config;

public class GameServerUDPThread extends Thread{

	private ServerProtocol      protocol = null;
	private InetAddress multicastAddress = null;
	private int            multicastPort = 0;
	private int              inboundPort = 0;

	public GameServerUDPThread(GameModel gameModel) throws UnknownHostException{
		
		this.multicastAddress = InetAddress.getByName(Config.MULTICAST_IP_ADDRESS);
		this.multicastPort    = Config.MULTICASTSOCKET_PORT_NUMBER;
		this.inboundPort      = Config.TCP_PORT_NUMBER;
		this.protocol  		  = new ServerProtocol(gameModel);
		
	}

	private Msg receiveDatagramFrom(DatagramSocket socket){

		Msg msg = null;

		try { 
			
			byte[]     buffer = new byte[Config.BUF_SIZE];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			socket.receive(dp);

			ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
			ObjectInputStream     ois = new ObjectInputStream(bais);
			
			msg = (Msg)ois.readObject();

		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;
	}
	
	private void sendMsgInDatagram(MulticastSocket socket, Msg msg){
		try{

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream     oos = new ObjectOutputStream(baos);
			
			oos.writeObject(msg);
			oos.flush();
			
			byte[] data = baos.toByteArray();
			
			DatagramPacket dp = new DatagramPacket(data, data.length, this.multicastAddress, this.multicastPort);
			socket.send(dp);

		}catch(Exception e){
			e.printStackTrace();
		}
	}		
	
	@Override
	public void run(){
			try (
				DatagramSocket  ds = new DatagramSocket(this.inboundPort);
				MulticastSocket ms = new MulticastSocket();
			){
                Msg in,out;
                while(this.protocol.getState() != ServerState.Disconnecting){
                	System.out.println("ReceiveDGP");
                    in = this.receiveDatagramFrom(ds);
                    if(in != null){
                    	System.out.println("UDP wanted to move.");
                        out = this.protocol.processMsg(in);
                        if(out != null) this.sendMsgInDatagram(ms, out);
                    }
                }	
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
