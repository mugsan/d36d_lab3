package lab3Server;

import java.net.*;
import java.util.Vector;
import java.io.*;

import lab3DataPacket.Msg;
import config.Config;

public class GameServer {
	private GameModel 				   gameModel = null;
	private Vector<ObjectOutputStream> clientOos = null;

	private int incPort = Config.TCP_PORT_NUMBER;
	private int  nextId = 1;
	
	
	
	public GameServer(){
		this.gameModel = new GameModel();
		this.clientOos = new Vector<ObjectOutputStream>();
	}
	
	public void init(){	
		try ( 
			ServerSocket serverSocket = new ServerSocket(this.incPort);
	    ){
	        	
        	Socket socket;

        	new GameServerUDPThread(this.gameModel).start();

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