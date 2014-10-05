package lab3Server;

import java.net.*;
import java.util.Random;
import java.util.Vector;
import java.io.*;


public class GameServer {
	private GameModel 				   gameModel = null;
	private Vector<ObjectOutputStream> clientOos = null;
	private Random                          rand = null;

	private int  nextId = 1;
	
	
	private String name = null;
	private int    port = 0;
	
	public GameServer(){
		 
		this.rand = new Random();
		
		this.gameModel = new GameModel();
		this.clientOos = new Vector<ObjectOutputStream>();
		this.port = this.randomPort();
		this.name = "Manges_server:" + this.port;
	}
	
	private int randomPort(){
		int port;

		do {
			port = this.randomNum(12000, 65000);
		} while (!this.testPort(port));

		return port;
	}
	
	private int randomNum(int min, int max){
		return rand.nextInt(max - min + 1) + min;
	}
	
	private boolean testPort(int port){
		try(
				ServerSocket ss = new ServerSocket(port);
		){
			return true;
		}catch (IOException ioe){
			return false;
		}
	}
	
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

    public static void main(String[] args) throws IOException {
    	System.out.println("Server starting...");
    	GameServer gs = new GameServer();
    	gs.init();
    }
}