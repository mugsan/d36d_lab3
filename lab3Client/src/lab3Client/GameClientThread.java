package lab3Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lab3DataPacket.Msg;

public class GameClientThread extends Thread{

	private GameView 	   gameView = null;
	private ClientProtocol protocol = null;
	
	//Connection details
	private String  host 	    = null;
	private int     port 	    = 0;

	
	public GameClientThread(String host, int port){
		//Need reference to repaint in loop.
		this.gameView = new GameView();
		this.protocol = new ClientProtocol(this.gameView);
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		try( 
				Socket s = new Socket(this.host, this.port);
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		){
			new Thread(){
				GameClientThread that = null;
				@Override
				public void run(){
					try( ObjectInputStream ois = new ObjectInputStream(s.getInputStream());){
						while(that.protocol.getState() != ClientState.Disconnected){
							Msg in = (Msg)ois.readObject();
							that.protocol.processMsg(in);
						}
					}catch(Exception e){
						System.out.println(e.toString());
					}
				}
				private Thread init(GameClientThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			Msg out;
			
			while(this.protocol.getState() != ClientState.Disconnected){
				if((out = this.protocol.getMsg()) != null){
					oos.writeObject(out);
					oos.flush();
				}

			
				this.gameView.revalidate();
				this.gameView.repaint();
				Thread.sleep(1000/90);
			}
			System.out.println("Game Client Out.");
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
}
