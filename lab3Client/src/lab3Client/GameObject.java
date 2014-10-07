package lab3Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import config.Config;

/**
 * The Class GameObject.
 * Moving objects on the canvas.
 */
@SuppressWarnings("serial")
public class GameObject extends JComponent {
	
	/** The position. */
	private Point position;
	
	/** The color. */
	private Color color;
	
	/** The dimension. */
	private Dimension dimension = Config.OBJECT_DIM;
	
	/**
	 * Instantiates a new game object.
	 *
	 * @param aPosition the a position
	 * @param color the color
	 */
	public GameObject(Point aPosition, Color color){
		this.position = aPosition;
		this.color = color;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(this.color);
		g2.fill(new Rectangle(this.position, this.dimension));
	}
	
	/**
	 * Move to.
	 *
	 * @param p the p
	 */
	public void moveTo(Point p){
		this.position = p;
	}
}
