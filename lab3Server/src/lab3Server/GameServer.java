package lab3Server;

import java.net.*;
import java.util.Vector;
import java.io.*;

public class GameServer {
    public static void main(String[] args) throws IOException {
    	Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();
    	GameModel gameModel = new GameModel();
    	int nextId  = 1;

        int portNumber = 12000;

        try ( 
        	ServerSocket serverSocket = new ServerSocket(portNumber);
        	){
        	Socket socket;

        	System.out.println("Server running...");
        	while(true){
        		if((socket = serverSocket.accept()) != null){
        			new GameServerThread(socket, clients, nextId,gameModel).start();
        			nextId++;
        		}
        	}
        } catch (Exception e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}