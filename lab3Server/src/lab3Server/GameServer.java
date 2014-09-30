package lab3Server;

import java.net.*;
import java.util.Vector;
import java.io.*;

public class GameServer {
    public static void main(String[] args) throws IOException {

    	Vector<DataPacket> output = new Vector<DataPacket>();
         
 
        int portNumber = 12000;

 
        try ( 
        	ServerSocket serverSocket = new ServerSocket(portNumber);
        	){
        	Socket socket;

        	while(true){
        		if((socket = serverSocket.accept()) != null){
        			new OutgoingThread(socket, output).start();;
        			new IncomingThread(socket, output).start();;
        		}
        	}
        } catch (Exception e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}