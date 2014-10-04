

package lab3Client;
import java.io.*;
import java.net.InetAddress;

public class GameClient {
    public static void main(String[] args) throws IOException {

    	InetAddress adr = InetAddress.getByName("localhost");
		new GameClientThread(adr, 12000).start();
    }
}