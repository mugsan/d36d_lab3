package lab3Server;

import java.net.*;
import java.util.Random;
import java.util.Vector;
import java.io.*;

import config.Config;

/**
 * The Class GameServer.
 * Allows clients to find the server via DSD thread.
 * Creates Sockets as clients connects.
 * 
 */
public class GameServer {
	
	/** The game model. */
	private GameModel 				   gameModel = null;
	
	/** The client oos. */
	private Vector<ObjectOutputStream> clientOos = null;
	
	/** The rand. used to calc random ints */
	private Random                          rand = null;

	/** The next id. Given to clients and increments.*/
	private int  nextId = 1;
	
	
	/** The name. */
	private String name = null;
	
	/** The port. */
	private int    port = 0;
	
	/**
	 * Instantiates a new game server.
	 * Instantiates datastructures and generates a free port number.
	 * Generates a server name based on that port number.
	 */
	public GameServer(){
		 
		this.rand = new Random();
		
		this.gameModel = new GameModel();
		this.clientOos = new Vector<ObjectOutputStream>();
		this.port = this.randomPort();
		this.name = Config.SERVER_NAME + this.port;
	}
	
	/**
	 * Random port.
	 * Generates a random port number.
	 * @return the int 
	 */
	private int randomPort(){
		int port;

		do {
			port = this.randomNum(12000, 65000);
		} while (!this.testPort(port));

		return port;
	}
	
	/**
	 * Random num.
	 *
	 * @param min the min
	 * @param max the max
	 * @return the int
	 */
	private int randomNum(int min, int max){
		return rand.nextInt(max - min + 1) + min;
	}
	
	/**
	 * Test port.
	 *
	 * @param port the port
	 * @return true, if successful
	 */
	private boolean testPort(int port){
		try(
				ServerSocket ss = new ServerSocket(port);
		){
			return true;
		}catch (IOException ioe){
			return false;
		}
	}
	
	/**
	 * Starts the threads.
	 */
	public void init(){	
		try ( 
			ServerSocket serverSocket = new ServerSocket(this.port);
	    ){
	        	

        	Socket socket;

        	new GameServerUDPThread(this.gameModel, this.port).start();//UDP LISTENER

        	new GameServerDSD(this.name, this.port).start();//Dynamic Server Discovery

        	while(true){
        		if((socket = serverSocket.accept()) != null){
        			new GameServerTCPThread(socket, this.clientOos, nextId,this.gameModel).start();
        			nextId++;
        		}
        	}
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }	
	}

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
    	GameServer gs = new GameServer();
    	gs.init();
    }
}