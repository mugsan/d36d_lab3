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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import config.Config;
import lab3DataPacket.Msg;



public class GameServerThread extends Thread{
	

	private Vector<ObjectOutputStream> clients = null;

	private ObjectOutputStream  oos = null;
	private ServerProtocol protocol = null;
	private Socket 			 socket = null;
	private int 		   clientID = 0;
	
	//UDP vars.
	private InetAddress multiCAddress = null;

	//UDP unicast info.
	private InetAddress clientAddress = null;
	private int            clientPort = 0;
	
	public GameServerThread(Socket socket,
	   Vector<ObjectOutputStream> clients, 
							 int clientID, 
					  GameModel gameModel) throws UnknownHostException{
		
		this.socket        = socket;
		this.clients       = clients;
		this.clientID      = clientID;
		this.protocol      = new ServerProtocol(gameModel, clientID);
		this.multiCAddress = InetAddress.getByName(Config.MULTICAST_IP_ADDRESS);

	}
	
	private void sendMsg(Msg msg) throws IOException{
		this.oos.writeObject(msg);
		this.oos.flush();
	}
	
	private void broadcastMsg(Msg msg) throws IOException{
		Iterator<ObjectOutputStream> it = this.clients.iterator();
		while(it.hasNext()){
			ObjectOutputStream oos = it.next();
			oos.writeObject(msg);
			oos.flush();
		}
	}
	private Msg receiveDatagramFrom(DatagramSocket socket){

		Msg msg = null;

		try { 
			
			byte[]     buffer = new byte[Config.BUF_SIZE];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			socket.receive(dp);
			
			//set InetAdress and port.
			this.clientAddress = dp.getAddress();
			this.clientPort = dp.getPort();

			ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
			ObjectInputStream     ois = new ObjectInputStream(bais);
			
			msg = (Msg)ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("receiveDatagram: " + e.toString());
		}

		return msg;
	}
	
	private void sendMsgInDatagram(MulticastSocket socket, Msg msg){
		try{

			System.out.println("SND");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream     oos = new ObjectOutputStream(baos);
			
			oos.writeObject(msg);
			oos.flush();
			
			byte[] data = baos.toByteArray();
			
			DatagramPacket dp = new DatagramPacket(data, data.length, this.multiCAddress, Config.MULTICASTSOCKET_PORT_NUMBER);
//			DatagramPacket dp = new DatagramPacket(data, data.length, this.clientAddress, this.clientPort);
			socket.send(dp);

			System.out.println("MSG SNT");
		}catch(Exception e){
			System.out.println("sendMsgInDatagram: " + e.toString());
		}
	}	
	
	@Override
	public void run() {
		try(
			ObjectOutputStream   oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream    ois = new ObjectInputStream(socket.getInputStream());
			DatagramSocket datSocket = new DatagramSocket(Config.TCP_PORT_NUMBER);
			MulticastSocket      mcs = new MulticastSocket();
		){
			this.oos = oos;
			clients.add(oos);

			System.out.println("Client with id: " + this.clientID + " connected!");
			System.out.println("Amount online: " + clients.size());


			//UDP Thread.
			new Thread(){
				GameServerThread that = null;
				@Override
				public void run(){
					Msg in,out;
					while(that.protocol.getState() != ServerState.Disconnecting){
						in = that.receiveDatagramFrom(datSocket);
						
						System.out.println("Well hello there mr: " + in.id + " with type: " + in.type);
						if(in != null){
							out = that.protocol.processMsg(in);
							System.out.println("Well good day to you sir!: " + out);
							if(out != null) that.sendMsgInDatagram(mcs, out);
						}
					}
				}
				private Thread init(GameServerThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();

			Msg in,out;
			in  = (Msg)ois.readObject();
			out = this.protocol.processMsg(in); //Join msg from client.
			this.sendMsg(out);
			
			ArrayList<Msg> list = this.protocol.getMsgListOfObjects(); //Sends Model to client.
			Iterator<Msg> it = list.iterator();
			while(it.hasNext()){
				this.sendMsg(it.next());
			}
			
			out = this.protocol.addClientToModel();
			this.broadcastMsg(out);//BroadCasts that there is a new client in the model.

			while(this.protocol.getState() != ServerState.Disconnecting){
				in 	   = (Msg)ois.readObject();
				out    = this.protocol.processMsg(in);
				if(out != null) this.broadcastMsg(out);
				//this.broadcastMsg(out);
			}
			this.clients.remove(oos);
			System.out.println("Player #" + this.clientID + ": left.");

		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}	

}
