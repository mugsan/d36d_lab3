

package lab3Client;
import java.io.*;

public class GameClient {
	
	public GameClient(){
	}

    public static void main(String[] args) throws IOException {
		new GameClientThread("localhost", 12000).start();
    }
}