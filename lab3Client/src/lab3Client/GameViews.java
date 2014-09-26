package lab3Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;

public class GameViews extends JComponent{
	private ArrayList<GameObject> objects;
	private int playerId;
	private Dimension objectSize = new Dimension(20,20);
	
	public GameViews(int playerId){
		this.playerId = playerId;
	}
	
	public void paint(Graphics g){
		if(objects != null){
			
                Graphics2D g2 = (Graphics2D)g;
                for(int i = 0,len = objects.size(); i < len; i++){
                	Rectangle rect = new Rectangle(objects.get(i).position,objectSize);
                	Color color = (playerId == objects.get(i).id)? Color.red : Color.blue;
                	g2.setColor(color);
                	g2.fill(rect);
                }
		}
	}

	public void setObjecets(ArrayList<GameObject> objects){
		this.objects = objects;
	}
}
