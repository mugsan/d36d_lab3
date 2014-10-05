package lab3Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import config.Config;
import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

public class GameThread extends Thread{

	private GameView 	   gameView = null;
	private ClientProtocol protocol = null;
	
	//Connection details
	private InetAddress multicastAddress = null;
	private InetAddress  			host = null;
	private int          			port = 0;
	private int      			buffSize = Config.BUF_SIZE;
	
	private GameClientViewController  previousView = null;

	
	public GameThread(InetAddress host, int port, GameClientViewController previousView) throws UnknownHostException{

		this.previousView = previousView;
		this.gameView 	  = new GameView();
		this.protocol 	  = new ClientProtocol(this.gameView);

		this.multicastAddress = InetAddress.getByName(Config.MULTICAST_IP_ADDRESS);
		this.host         	  = host; 
		this.port     		  = port;

	}
	
	private Msg receiveDatagramFrom(MulticastSocket socket){

		Msg msg = null;

		try { 
			byte[]     buffer = new byte[this.buffSize];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			socket.receive(dp);
			
			if(!dp.getAddress().equals(this.host) && this.port != dp.getPort()) return null;
			
			ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
			ObjectInputStream     ois = new ObjectInputStream(bais);
			
			msg = (Msg)ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;
	}
	
	private void sendMsgInDatagram(DatagramSocket socket, Msg msg){
		try{

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream     oos = new ObjectOutputStream(baos);
			
			oos.writeObject(msg);
			oos.flush();
			
			byte[] data = baos.toByteArray();
			
			DatagramPacket dp = new DatagramPacket(data, data.length, this.host, this.port);
			socket.send(dp);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try( 
				Socket s = new Socket(this.host, this.port);
				
				ObjectOutputStream   oos = new ObjectOutputStream(s.getOutputStream());
				DatagramSocket datSocket = new DatagramSocket();
		){
			
			
			
			
			//TCP listener.
			new Thread(){
				GameThread that = null;
				@Override
				public void run(){
					try( ObjectInputStream ois = new ObjectInputStream(s.getInputStream());){
						while(that.protocol.getState() != ClientState.Disconnected){
							Msg in = (Msg)ois.readObject();
							that.protocol.processMsg(in);
						}
					}catch(Exception e){
						System.out.println("TCPListener: " + e.toString());
					}
				}
				private Thread init(GameThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			//UDP Listener.
			new Thread(){
				GameThread that = null;
				@Override
				public void run(){
					
					try(
						MulticastSocket mcs = new MulticastSocket(Config.MULTICASTSOCKET_PORT_NUMBER);
					){
						mcs.joinGroup(that.multicastAddress);
						while(that.protocol.getState() != ClientState.Disconnected){
							Msg in = that.receiveDatagramFrom(mcs);
							if(in != null)that.protocol.processMsg(in);
						}
						mcs.leaveGroup(that.multicastAddress);
					}catch(Exception e){
					}
				}
				private Thread init(GameThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			
			
			
			Msg out;
			
			
			//TCP&UDP sender.
			while(this.protocol.getState() != ClientState.Disconnected){
				if((out = this.protocol.getMsg()) != null){
					if(out.type == MsgType.Move){
						this.sendMsgInDatagram(datSocket, out);
					}else{
                        oos.writeObject(out);
                        oos.flush();
					}
				}

			
				this.gameView.revalidate();
				this.gameView.repaint();
				Thread.sleep(1000/90);
			}
			this.previousView.setVisible(true);
			System.out.println("Game Client Disconnected.");
		}catch(Exception e){
			System.out.println("MainGameThread: " + e.toString());
			//e.printStackTrace();
		}
	}
}
