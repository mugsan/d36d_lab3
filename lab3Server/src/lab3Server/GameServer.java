package lab3Server;

import java.net.*;
import java.awt.Point;
import java.io.*;

public class GameServer {
    public static void main(String[] args) throws IOException {
         
 
        int portNumber = 12000;

 
        Point point = new Point(100,100);
        
        System.out.println("hello world");
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        	ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        	){
 
        	ServerProtocol sp = new ServerProtocol();
        	DataPacket in,out;


        	while(true){
        		in = (DataPacket)ois.readObject();
        		out = sp.processDataPacket(in);
        		oos.writeObject(out);
        	}
        	
        } catch (Exception e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}