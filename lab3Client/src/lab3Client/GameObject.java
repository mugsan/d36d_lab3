package lab3Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import config.Config;

@SuppressWarnings("serial")
public class GameObject extends JComponent {
	
	private Point position;
	private Color color;
	private Dimension dimension = Config.OBJECT_DIM;
	
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
