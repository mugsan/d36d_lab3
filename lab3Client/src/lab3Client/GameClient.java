
package lab3Client;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

import lab3Server.DataPacket;


 
public class GameClient implements Runnable{
	private final int 	SPEED   = 9;
	private GameFrame 	gFrame;
	
	
	private Boolean 	moved = false;
	private int 		xDir = 0;
	private int 		yDir = 0;
	private int 		id;

	//temp testing 
	private Point p;
	
	public GameClient(){
		this.gFrame = new GameFrame();
		this.id 	= 1;
		this.p 		= new Point(100,100);

		this.gFrame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					xDir += SPEED;
					break;
				case 38:
					yDir += SPEED;
					break;
				case 39:
					xDir -= SPEED;
					break;
				case 40:
					yDir -= SPEED;
					break;

				default:
					break;
				}

				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					xDir -= SPEED;
					moved = true;
					break;
				case 38:
					yDir -= SPEED;
					moved = true;
					break;
				case 39:
					xDir += SPEED;
					moved = true;
					break;
				case 40:
					yDir += SPEED;
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
		int i = 0;
		long fps = 1000 / 60;

		System.out.println("run outside try");
		
        try (
                Socket s = new Socket("localhost",10000);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ){
        	DataPacket in,out;
        	while(true){
        		out = new DataPacket(1,1);
        		out.direction = new Point(xDir,yDir);
        		oos.writeObject(out);
        		System.out.println("Client 1");

        		in = (DataPacket)ois.readObject();
        		

        		System.out.println("X: " + in.position.x + " Y: " + in.position.y);
        		this.gFrame.moveObject(1, in.position);
        		this.gFrame.revalidate();
        		this.gFrame.repaint();
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