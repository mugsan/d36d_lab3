package lab3Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

	public static void main(String[] args) {

		String hostName = "localhost";
		int portNumber  = 19000;
		
		try(
				Socket socket = new Socket(hostName,portNumber);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				
		){
			String userInput;
			while((userInput = input.readLine()) != null ){
				out.println(userInput);
				System.out.println("echo" + in.readLine());
			}
		}catch(Exception e){
			
			System.err.println("Exception caught: " + e.toString());
		}
				

	}

}
