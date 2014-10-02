package lab3Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import lab3DataPacket.Msg;



public class GameServerThread extends Thread{
	

	private Vector<ObjectOutputStream> clients = null;

	private ObjectOutputStream oos  	   = null;
	private ServerProtocol	   protocol	   = null;
	private Socket 			   socket 	   = null;
	private int 			   clientID    = 0;
	
	public GameServerThread(Socket socket, Vector<ObjectOutputStream> clients, int clientID, GameModel gameModel){
		this.socket    = socket;
		this.clients   = clients;
		this.clientID  = clientID;
		this.protocol  = new ServerProtocol(gameModel, clientID);
		
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
	
	@Override
	public void run() {
		try(
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream  ois = new ObjectInputStream(socket.getInputStream());
		){
			this.oos = oos;
			clients.add(oos);

			System.out.println("Client with id: " + this.clientID + " connected!");
			System.out.println("Amount online: " + clients.size());

			Msg in,out;
			
			in = (Msg)ois.readObject();
			
			out = this.protocol.processMsg(in); //Join msg from client.
			this.sendMsg(out);
			
			ArrayList<Msg> list = this.protocol.getMsgListOfObjects(); //Sends Mode to client.
			Iterator<Msg> it = list.iterator();
			while(it.hasNext()){
				this.sendMsg(it.next());
			}
			
			out = this.protocol.addClientToModel();
			this.broadcastMsg(out);//BroadCasts that there is a new client in the model.

			while(this.protocol.getState() != ServerState.Disconnecting){
				in 	   = (Msg)ois.readObject();
				out    = this.protocol.processMsg(in);
				this.broadcastMsg(out);
			}
			this.clients.remove(oos);
			System.out.println("Player #" + this.clientID + ": left.");

		}catch(Exception e){
			System.out.println(e.toString());
		}
	}	

}
