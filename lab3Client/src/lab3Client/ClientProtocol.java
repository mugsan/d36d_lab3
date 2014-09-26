package lab3Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lab3Server.DataPacket;

public class ClientProtocol {
	private GameView 	frame;
	private int 		id = 0;
	
	public ClientProtocol(){
		this.frame = new GameView();
	}

	public void process(DataPacket dataPacket){
		switch (dataPacket.msg) {
		case "ADD":
			if(this.id == 0) this.id = dataPacket.id;
			this.frame.addObject(dataPacket.id,dataPacket.position);
			this.frame.revalidate();
			this.frame.repaint();
			break;
		case "MOVE":
			this.frame.moveObjectTo(dataPacket.id, dataPacket.position);
			this.frame.revalidate();
			this.frame.repaint();
			break;

		default:
			break;
		}
		
	}
	
	public GameView getFrame(){
		return this.frame;
	}
	
	@SuppressWarnings("serial")
	public class GameView extends JFrame {
		private Map<Integer, GameObject> views;
		
		public GameView(){
			this.views = new HashMap<Integer, GameObject>();
			this.setSize(400,400);
			this.setTitle("someGame");
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setUndecorated(true);
			this.setVisible(true);
		}		
		
		public void addObject(int arg_id, Point p){
			Color color = (arg_id == id)? Color.red : Color.blue;
			GameObject gO = new GameObject(p, color);
			this.views.put(arg_id, gO);
			this.add(gO);
			this.validate();
		}
		
		public void moveObjectTo(int id, Point p){
			this.views.get(id).moveTo(p);
		}
		
		private class GameObject extends JComponent {
			
			private Point position;
			private Color color;
			private Dimension dimension = new Dimension(20,20);
			
			public GameObject(Point aPosition, Color color){
				this.position = aPosition;
				this.color = color;
			}
			
			public void paint(Graphics g){
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(this.color);
				g2.fill(new Rectangle(this.position, this.dimension));
			}
			
			public void moveTo(Point p){
				this.position = p;
			}
		}
	}
	
	
	
}
