

package lab3Client;
import java.io.*;

public class GameClient {
	
	public GameClient(){
	}
	
	public void init(){
		ClientProtocol protocol = new ClientProtocol(new GameView());
//		Thread sender =
	}

    public static void main(String[] args) throws IOException {
		new GameClientThread("localhost", 12000).start();
    }
}