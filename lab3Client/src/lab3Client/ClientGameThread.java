package lab3Client;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

public class ClientGameThread extends Thread{

	private GameView gameView = null;

	//Game State
	private enum State{
		Connecting, 
		Connected, 
		Disconnecting
	}

	private State 	state       = State.Connecting;
	
	//Connection details
	private String  host 	    = null;
	private int     port 	    = 0;
	private boolean isConnected = false;
	
	//Given by server.
	private int 	id			= 0;
	
	public ClientGameThread(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	
	public Msg getMsg(){
		Msg msg = new Msg();
		switch (this.state) {
		case Connecting:
			if(this.id == 0){
				msg.id   = this.id;
				msg.type = MsgType.Join;
			}else{
				msg = null;
			}
			break;
		case Connected:
			if(this.gameView.isMoved()){
                msg.id   = this.id;
                msg.type = MsgType.Move;
                msg.direction.x = this.gameView.getDx();
                msg.direction.y = this.gameView.getDy();
			}else{
				msg = null;
			}
			break;
		case Disconnecting:
			msg.id   = this.id;
			msg.type = MsgType.Leave;
			break;
		}
		return msg;
	}

	public void processMsg(Msg msg){
		switch (msg.type) {
		case Join:
			this.id = msg.id;
			this.state = State.Connected;
			break;
		case NewPlayer: 
			Color  	   color = (msg.id == this.id)? Color.red : Color.blue;
			GameObject gO    = new GameObject(msg.position, color);
			this.gameView.addObject(msg.id, gO);
			break;
		case PlayerLeft: 
			this.gameView.removeObject(msg.id);
			if(msg.id == this.id){
				this.isConnected = false;
			}
			break;
		case PlayerMoved: 
			this.gameView.moveObjectTo(msg.id, msg.position);
			break;
		default:
			break;
		}
	} 

	@Override
	public void run() {
		try( 
				Socket s = new Socket(this.host, this.port);

				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		){
			this.gameView = new GameView();
			
			this.gameView.addWindowListener(new WindowListener() {
				
				
				@Override
				public void windowClosing(WindowEvent e) {
					state = State.Disconnecting;
					System.out.println("Closing window.");
				}
				
				@Override
				public void windowClosed(WindowEvent e) {
					System.out.println("Closed window.");
				}
				
				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			this.isConnected = true;
			new Thread(){
				ClientGameThread that = null;
				@Override
				public void run(){
					try( ObjectInputStream ois = new ObjectInputStream(s.getInputStream());){
						while(that.isConnected){
							Msg in = (Msg)ois.readObject();
							that.processMsg(in);
						}
					}catch(Exception e){
						System.out.println(e.toString());
					}
				}
				private Thread init(ClientGameThread that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			Msg out = new Msg();
			
			while(this.isConnected){
				if((out = this.getMsg()) != null){
					oos.writeObject(out);
					oos.flush();
				}

				this.gameView.revalidate();
				this.gameView.repaint();
				Thread.sleep(1000/30);
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
}
