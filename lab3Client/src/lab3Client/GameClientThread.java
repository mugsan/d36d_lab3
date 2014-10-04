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

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

public class GameClientThread extends Thread{

	private GameView 	   gameView = null;
	private ClientProtocol protocol = null;
	
	//Connection details
	private InetAddress mCAdr = null;
	private InetAddress  host = null;
	private int          port = 0;
	private int        mcPort = 13921;
	private int      buffSize = 1024;

	
	public GameClientThread(String host, int port) throws UnknownHostException{
		//Need reference to repaint in loop.
		this.gameView = new GameView();
		this.protocol = new ClientProtocol(this.gameView);
		this.mCAdr = InetAddress.getByName("239.0.42.99");
		this.host = InetAddress.getByName(host); 
		this.port = port;
	}
	
	private Msg receiveDatagramFrom(MulticastSocket socket){

		Msg msg = null;

		try { 
			System.out.println("RCV - Client.");
			byte[]     buffer = new byte[this.buffSize];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			System.out.println("about to recive.");
			socket.receive(dp);
			System.out.println("received?");
			
			ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
			ObjectInputStream     ois = new ObjectInputStream(bais);
			
			msg = (Msg)ois.readObject();
		}catch(Exception e){
			System.out.println("receiveDatagram: " + e.toString());
		}

		return msg;
	}
	
	private void sendMsgInDatagram(DatagramSocket socket, Msg msg){
		try{

			System.out.println("SND - Client.");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream     oos = new ObjectOutputStream(baos);
			
			oos.writeObject(msg);
			oos.flush();
			
			byte[] data = baos.toByteArray();
			
			DatagramPacket dp = new DatagramPacket(data, data.length, this.host, this.port);
			socket.send(dp);

		}catch(Exception e){
			System.out.println("sendMsgInDatagram: " + e.toString());
		}
	}

	@Override
	public void run() {
		try( 
				Socket s = new Socket(this.host, this.port);
				ObjectOutputStream   oos = new ObjectOutputStream(s.getOutputStream());
				DatagramSocket datSocket = new DatagramSocket();
				MulticastSocket      mcs = new MulticastSocket(this.mcPort); 
		){
			mcs.joinGroup(this.mCAdr);
			
			//TCP listener.
			new Thread(){
				GameClientThread that = null;
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
				private Thread init(GameClientThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			//UDP Listener.
			new Thread(){
				GameClientThread that = null;
				@Override
				public void run(){
					while(that.protocol.getState() != ClientState.Disconnected){
					
						Msg in = that.receiveDatagramFrom(mcs);
						if(in != null) that.protocol.processMsg(in);
					}
				}
				private Thread init(GameClientThread that){
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
			System.out.println("Game Client Disconnected.");
		}catch(Exception e){
			e.getStackTrace();
			System.out.println(e.toString());
		}
	}
}
