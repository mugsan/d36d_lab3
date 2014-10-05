package lab3Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;



public class GameServerTCPThread extends Thread{
	

	private Vector<ObjectOutputStream> clients = null;

	private ServerProtocol protocol = null;
	private Socket 			 socket = null;
	private int 		   clientID = 0;
	

	
	public GameServerTCPThread(Socket socket,
	    Vector<ObjectOutputStream> clients, 
							   int clientID, 
					     GameModel gameView) throws UnknownHostException{
		
		this.socket        = socket;
		this.clients       = clients;
		this.clientID      = clientID;
		this.protocol      = new ServerProtocol(gameView);

	}
	
	private void sendMsg(Msg msg, ObjectOutputStream oos) throws IOException{
		oos.writeObject(msg);
		oos.flush();
	}
	
	private void broadcastMsg(Msg msg) throws IOException{
		Iterator<ObjectOutputStream> it = this.clients.iterator();
		while(it.hasNext()){
			this.sendMsg(msg, it.next());
		}
	}

	
	@Override
	public void run() {
		try(
			ObjectOutputStream   oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream    ois = new ObjectInputStream(socket.getInputStream());
		){
			clients.add(oos);

			System.out.println("Client with id: " + this.clientID + " connected!");
			System.out.println("Amount online: " + clients.size());

			//Step 1: Receive Join msg from a new client. Respond with Join and Id#.
			Msg in,out;
			in    = (Msg)ois.readObject();
			in.id = this.clientID;
			out   = this.protocol.processMsg(in); 
			this.sendMsg(out, oos);
			
			//Step 2: Send all current objects in the model to the new client.
			ArrayList<Msg> list = this.protocol.getMsgListOfObjects(); 
			Iterator<Msg>    it = list.iterator();
			while(it.hasNext()){
				this.sendMsg(it.next(), oos);
			}

			
			//Step 3: Add the new client to the model and broadcast the addition.
			out = this.protocol.addClientToModel(this.clientID);
			this.broadcastMsg(out);

			//Step 4: Enter update loop.
			while(this.protocol.getState() != ServerState.Disconnecting){
				in 	   = (Msg)ois.readObject();
				out    = this.protocol.processMsg(in);
				if(out != null) this.broadcastMsg(out);
			}
			this.clients.remove(oos);
			System.out.println("Player #" + this.clientID + ": left.");

		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.toString());
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

}
