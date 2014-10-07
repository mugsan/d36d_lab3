package lab3Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import lab3DataPacket.Msg;



/**
 * The Class GameServerTCPThread.
 * Listen and sends tcp msgs to client/clients
 */
public class GameServerTCPThread extends Thread{
	

	/** The clients. Reference to all clients outputstreams connected to the server.*/
	private Vector<ObjectOutputStream> clients = null;

	/** The protocol. */
	private ServerProtocol protocol = null;
	
	/** The socket. */
	private Socket 			 socket = null;
	
	/** The client id. This threads clientId.*/
	private int 		   clientID = 0;
	

	
	/**
	 * Instantiates a new game server tcp thread.
	 * 
	 * 
	 *
	 * @param socket the socket
	 * @param clients the clients
	 * @param clientID the client id
	 * @param gameView the game view
	 * @throws UnknownHostException the unknown host exception
	 */
	public GameServerTCPThread(Socket socket,
	    Vector<ObjectOutputStream> clients, 
							   int clientID, 
					     GameModel gameModel) throws UnknownHostException{
		
		this.socket        = socket;
		this.clients       = clients;
		this.clientID      = clientID;
		this.protocol      = new ServerProtocol(gameModel);

	}
	
	/**
	 * Send msg.
	 *
	 * @param msg the msg
	 * @param oos the oos
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void sendMsg(Msg msg, ObjectOutputStream oos) throws IOException{
		oos.writeObject(msg);
		oos.flush();
	}
	
	/**
	 * Broadcast msg.
	 *
	 * @param msg the msg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void broadcastMsg(Msg msg) throws IOException{
		Iterator<ObjectOutputStream> it = this.clients.iterator();
		while(it.hasNext()){
			this.sendMsg(msg, it.next());
		}
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try(
			ObjectOutputStream   oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream    ois = new ObjectInputStream(socket.getInputStream());
		){
			//Step 1: Receive Join msg from a new client. Respond with Join and Id#.
			Msg in,out;

			in    = (Msg)ois.readObject();
			in.id = this.clientID;
			out   = this.protocol.processMsg(in); 
			this.sendMsg(out, oos);

			System.out.println("Client with id: " + this.clientID + " connected!");
			System.out.println("Amount online: " + clients.size());
			
			//Step 2: Send all current objects in the model to the new client.
			ArrayList<Msg> list = this.protocol.getMsgListOfObjects(); 
			Iterator<Msg>    it = list.iterator();
			while(it.hasNext()){
				this.sendMsg(it.next(), oos);
			}

			
			//Step 3: Add the new client to the model and broadcast the addition.
			clients.add(oos); //add this oos to client streams.
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
				if(socket != null){
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

}
