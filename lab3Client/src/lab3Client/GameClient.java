
package lab3Client;

import java.io.*;
import java.net.Socket;

import lab3Server.DataPacket;


 
public class GameClient implements Runnable{
	private GameView gameView = null;
	
	public GameClient(){
		this.gameView = new GameView();
	}
	
	
	@Override
	public void run() {
		long fps = 1000;
        try (
                Socket s = new Socket("localhost",12000);
                ObjectOutputStream 	oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream 	ois = new ObjectInputStream(s.getInputStream());
            ){

        	DataPacket in,out;

        	while(true){
        		out = new DataPacket(0, "JOIN");
        		oos.writeObject(out);
        		System.out.println("before readobject");
        		in = (DataPacket)ois.readObject();
        		System.out.println("msg is" + in.msg);
        		Thread.sleep(fps);
        	}
        } catch (Exception e) {
                System.out.println(e.toString());
        }
	}

    public static void main(String[] args) throws IOException {
    	GameClient client = new GameClient();
    	client.run();
    }
}