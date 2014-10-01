

package lab3Client;

import java.io.*;
public class GameClient implements Runnable{
	
	public GameClient(){
	}
	
	
	@Override
	public void run() {
		new ClientGameThread("localhost", 12000).start();
	}

    public static void main(String[] args) throws IOException {
    	GameClient client = new GameClient();
    	client.run();
    }
}