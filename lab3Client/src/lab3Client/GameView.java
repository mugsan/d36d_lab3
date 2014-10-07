package lab3Client;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

/**
 * The Class GameView.
 */
@SuppressWarnings("serial")
public class GameView extends JFrame {
	
	/** The views. Map that holds views representing players on the game board. the key is the id of a client. */
	public Map<Integer, GameObject> views = null;
	
	/**
	 * Instantiates a new game view.
	 */
	public GameView(){ 
		System.out.println("New gameView");
		this.views = new HashMap<Integer, GameObject>();
		this.setSize(404,429);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}		
	
	/**
	 * Adds a GameObject to the game board.  
	 *
	 * @param id the id
	 * @param gO the g o
	 */
	public void addObject(int id, GameObject gO){
		this.views.put(id, gO);
		this.add(gO);
		this.validate();
	}
	
	/**
	 * Move object to.
	 *
	 * @param id the id
	 * @param p the p
	 */
	public void moveObjectTo(int id, Point p){
		this.views.get(id).moveTo(p);
	}
	
	/**
	 * Removes the object.
	 *
	 * @param id the id
	 */
	public void removeObject(int id){
		GameObject gO = this.views.get(id);
		this.views.remove(id);
		this.remove(gO);
		this.validate();
	}
}