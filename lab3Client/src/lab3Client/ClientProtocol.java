package lab3Client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

public class ClientProtocol {	

	private GameView gameView = null;
	private int 		   id = 0;
	private ClientState state = ClientState.Connecting;
	
	
	//Keyboard vars
	private int        dx = 0;
	private int        dy = 0;
	private int     speed = 1;
	private boolean moved = false;

	public ClientProtocol(GameView gameView){
		this.gameView = gameView;
		this.initiateListeners();
	}
	
	private void initiateListeners(){
		this.gameView.addKeyListener(new KeyListener() {
			private ClientProtocol that = null;
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
				case 39:
					that.dx = 0;
					that.moved = (that.dy != 0);
					break;
				case 38:
				case 40:
					that.dy = 0;
					that.moved = (that.dx != 0);
					break;
				default:
					break;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					that.dx = -that.speed;
					that.moved = true;
					break;
				case 38:
					that.dy = -that.speed;
					that.moved = true;
					break;
				case 39:
					that.dx = that.speed;
					that.moved = true;
					break;
				case 40:
					that.dy = that.speed;
					that.moved = true;
					break;

				default:
					break;
				}
			}
			private KeyListener init(ClientProtocol that){
				this.that = that;
				return this;
			}
		}.init(this));
	
		this.gameView.addWindowListener(new WindowListener() {
			private ClientProtocol that = null;
			
			@Override
			public void windowClosing(WindowEvent e) {
				that.state = ClientState.Disconnecting;
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			private WindowListener init(ClientProtocol that){
				this.that = that;
				return this;
			}

			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}	

		}.init(this));
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
			if(this.moved){
                msg.id   		= this.id;
                msg.type 		= MsgType.Move;
                msg.direction.x = this.dx;
                msg.direction.y = this.dy;
			}else{
				msg = null;
			}
			break;
		case Disconnecting:
			msg.id   = this.id;
			msg.type = MsgType.Leave;
			break;
		case Disconnected:
			msg = null;
			break;
		}
		return msg;
	}

	public void processMsg(Msg msg){
		switch (msg.type) {
		case Join:
			if(this.id != 0) break;
			this.id = msg.id;
			this.state = ClientState.Connected;
			break;
		case NewPlayer: 
			Color  	   color = (msg.id == this.id)? Color.red : Color.blue;
			GameObject gO    = new GameObject(msg.position, color);
			this.gameView.addObject(msg.id, gO);
			break;
		case PlayerLeft: 
			this.gameView.removeObject(msg.id);
			if(msg.id == this.id){
				this.state = ClientState.Disconnected;
			}
			break;
		case PlayerMoved: 
			this.gameView.moveObjectTo(msg.id, msg.position);
			break;
		default:
			break;
		}
	} 
	
	public ClientState getState(){
		return this.state;
	}
	
	public GameView getView(){
		return this.gameView;
	}
}
