
package lab3Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

import lab3Server.DataPacket;


 
public class GameClient implements Runnable{
	private final int 	SPEED = 1;
	private ClientProtocol cp;
	
	
	private Boolean 	moved = false;
	private int 		xDir  = 0;
	private int 		yDir  = 0;

	
	public GameClient(){
		this.cp = new ClientProtocol();
		this.cp.getFrame().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					xDir = 0;
					break;
				case 38:
					yDir = 0;
					break;
				case 39:
					xDir = 0;
					break;
				case 40:
					yDir = 0;
					break;

				default:
					break;
				}

				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					xDir = -SPEED;
					moved = true;
					break;
				case 38:
					yDir = -SPEED;
					moved = true;
					break;
				case 39:
					xDir = SPEED;
					moved = true;
					break;
				case 40:
					yDir = SPEED;
					moved = true;
					break;

				default:
					break;
				}
				
			}
		});
	}//--End Constructor
	
	
	@Override
	public void run() {
		long fps = 1000 / 60;

		
        try (
                Socket s = new Socket("localhost",12000);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ){
        	DataPacket in,out;
        	out = new DataPacket(0,"JOIN");
        	oos.writeObject(out);
        	

        	while(true){
        		in = (DataPacket)ois.readObject();
        		this.cp.process(in);
        		out = new DataPacket(1,"MOVE");
        		out.xDir = this.xDir;
        		out.yDir = this.yDir;
        		oos.writeObject(out);

        		Thread.sleep(fps);
        	}
        } catch (Exception e) {
                System.out.println(e.toString());
        }
	}

    public static void main(String[] args) throws IOException {
    	GameClient client = new GameClient();
    	client.run();
        
        
    }
}