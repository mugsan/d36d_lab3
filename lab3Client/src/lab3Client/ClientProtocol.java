package lab3Client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

/**
 * The Class ClientProtocol.
 * Modifies the View depending on given msg.
 */
public class ClientProtocol {	

	/** The game view. */
	private GameView gameView = null;
	
	/** The id. This clients id, given by server.*/
	private int 		   id = 0;
	
	/** The state. Initial state of client.*/
	private ClientState state = ClientState.Connecting;
	
	
	//Keyboard vars set by keyboard listener.
	/** The dx. */
	private int        dx = 0;
	
	/** The dy. */
	private int        dy = 0;
	
	/** The speed. */
	private int     speed = 1;
	
	/** The moved. */
	private boolean moved = false;

	/**
	 * Instantiates a new client protocol.
	 *
	 * @param gameView the game view
	 */
	public ClientProtocol(GameView gameView){
		this.gameView = gameView;
		this.initiateListeners();
	}
	
	/**
	 * Initiate listeners.
	 * Keylistener to move the player.
	 * Windowlistener to change clientState if/when player closes window.
	 */
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

	/**
	 * Creates a msg depending on the state of client.
	 * 
	 * @return the msg
	 */
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

	/**
	 * Process msg.
	 * Modifies View depending on msg given.
	 *
	 * @param msg the msg given from server.
	 */
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
			System.out.println("playerLeft");
			this.gameView.removeObject(msg.id);
			System.out.println("removedPlayer");
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
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public ClientState getState(){
		return this.state;
	}
}
