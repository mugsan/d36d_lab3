package lab3Server;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class OutgoingThread extends Thread{
	private Vector<DataPacket> output = null;
	private Socket socket = null;
	private boolean isConnected = false;
	
	public OutgoingThread(Socket socket, Vector<DataPacket> output){
		this.socket = socket;
		this.output = output;
	}
	
	@Override
	public void run() {
		try(ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream())){
			this.setConnected(true);
			while(true){
				if(output.size() > 0){
					System.out.println("Running que");
					oos.writeObject(output.get(0));
					output.remove(0);
				}
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
