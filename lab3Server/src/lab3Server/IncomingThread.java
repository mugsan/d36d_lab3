package lab3Server;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

public class IncomingThread extends Thread {
	private Vector<DataPacket> output = null;
	private Socket socket = null;
	private boolean isConnected = false;
	
	public IncomingThread(Socket socket, Vector<DataPacket> output){
		this.socket = socket;
		this.output = output;
		
	}

	@Override
	public void run() {
		try( ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());){
			System.out.println("IncomingThreadRun");
			this.isConnected = true;
			ServerProtocol sp = new ServerProtocol();
			DataPacket in;

			while(this.isConnected){
				in = (DataPacket)ois.readObject();
				output.add(sp.processDataPacket(in));
			}
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}


	

}
