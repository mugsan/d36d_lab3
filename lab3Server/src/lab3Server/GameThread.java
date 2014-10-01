package lab3Server;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;



public class GameThread extends Thread{
	

	private Vector<ObjectOutputStream> clients = null;

	private ObjectOutputStream oos  	   = null;
	private GameModel 		   gameModel   = null;
	private Socket 			   socket 	   = null;
	private boolean 		   isConnected = false;
	private int 			   clientID    = 0;
	
	public GameThread(Socket socket, Vector<ObjectOutputStream> clients, int clientID, GameModel gameModel){
		this.socket    = socket;
		this.clients   = clients;
		this.clientID  = clientID;
		this.gameModel = gameModel;
	}
	
	private void processMsg(Msg msg) throws IOException{
		Msg out;

		switch (msg.type) {
		case Join:

			//Send join confirmation and clients new id.
			out 	 = new Msg();
			out.id 	 = this.clientID;
			out.type = MsgType.Join;
			this.sendMsg(out);
			
			//Send position of all other objects.
			Map.Entry<Integer, Point>[] entries = gameModel.getObjects();
			for(int i = 0, len = entries.length; i < len; i++){

				out 	     = new Msg();
				out.type     = MsgType.NewPlayer;
				out.id       = entries[i].getKey();
				out.position = entries[i].getValue();

				this.sendMsg(out);
			}

			//Broadcast the new player to all clients.
			out 		 = new Msg();
			out.type  	 = MsgType.NewPlayer;
			out.id 		 = this.clientID;
			out.position = gameModel.addObject(this.clientID);

			this.broadCastMsg(out);
			break;
			
		case Move:
			out    = new Msg();
			out.id = this.clientID;

			if((out.position = this.gameModel.moveObject(msg.id, msg.direction)) != null){
				out.type = MsgType.PlayerMoved;
				this.broadCastMsg(out);
			}
			break;
			
		case Leave:
			System.out.println("Player left.");

			out      = new Msg();
			out.id   = this.clientID;
			out.type = MsgType.PlayerLeft;

			this.gameModel.removeObject(this.clientID);
			
			this.broadCastMsg(out);
			this.clients.remove(this.oos);

			System.out.println("ModelSize: " + this.gameModel.getObjects().length + " VectorSize: " + clients.size());
			break;
			
		
		default:
			break;
		}
	}
	

	
	private void sendMsg(Msg msg) throws IOException{
		this.oos.writeObject(msg);
		this.oos.flush();
	}
	
	private void broadCastMsg(Msg msg) throws IOException{
		Iterator<ObjectOutputStream> it = this.clients.iterator();
		while(it.hasNext()){
			ObjectOutputStream oos = it.next();
			oos.writeObject(msg);
			oos.flush();
		}
	}
	
	@Override
	public void run() {
		try(
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream  ois = new ObjectInputStream(socket.getInputStream());
		){
			System.out.println("Client with id: " + this.clientID + " connected!");
			this.isConnected = true;

			this.oos = oos;
			clients.add(oos);

			Msg in,out;

			while(this.isConnected){
				in 	   = (Msg)ois.readObject();

				//Run given Msg through protocol.
				this.processMsg(in);
			}
			
		}catch(Exception e){
			
		}
	}	

}
