package lab3Server;

import java.net.*;
import java.awt.Point;
import java.io.*;

public class GameServer {
    public static void main(String[] args) throws IOException {
         
 
        int portNumber = 10000;

 
        Point point = new Point(100,100);
        
        System.out.println("hello world");
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        	ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        	){
 
        	DataPacket in,out;

        	while(true){
        		in = (DataPacket)ois.readObject();
        		point.translate(in.direction.x, in.direction.y);
        		System.out.println("X: " + point.x + " Y: " + point.y);

        		out = new DataPacket(1,1);
        		out.position = new Point(point);
        		oos.writeObject(out);
        	}
        	
        } catch (Exception e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}