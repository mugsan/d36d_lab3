package lab3Client;
import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	private Map<Integer,GameObject> storage;
	private int id = -1;
	
	
	public GameFrame(){
		this.storage = new HashMap<Integer, GameObject>();
		this.setSize(400,400);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void moveObject(int id, Point p){
		GameObject gObj = storage.get(id);
		if(gObj == null){
			gObj = this.createGameObject(id, p);
		}else{
			gObj.moveTo(p);
		}
	}
	
	private GameObject createGameObject(int id, Point p){
		if(this.id == -1) this.id = id;
		Color color = (id == this.id)? Color.red : Color.blue;
		GameObject gObj = new GameObject( p, color);
		this.add(gObj);
		this.validate();
		this.storage.put(id, gObj);
		return gObj;
	}
}
