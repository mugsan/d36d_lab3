package lab3Client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameView extends JFrame {
	public Map<Integer, GameObject> views = null;
	
	public GameView(){ 
		System.out.println("New gameView");
		this.views = new HashMap<Integer, GameObject>();
		this.setSize(404,429);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}		
	
	public void addObject(int id, GameObject gO){
		this.views.put(id, gO);
		this.add(gO);
		this.validate();
	}
	
	public void moveObjectTo(int id, Point p){
		this.views.get(id).moveTo(p);
	}
	
	public void removeObject(int id){
		GameObject gO = this.views.get(id);
		this.views.remove(id);
		this.remove(gO);
		this.validate();
	}
}