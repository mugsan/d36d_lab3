package lab3Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.*;




@SuppressWarnings("serial")
public class GameView extends JFrame{
	private Color cPlayer = Color.RED;
	private Color cNPlayer = Color.BLUE;

	private Dimension oSize;
	public ArrayList<Point> objects;


	public GameView(){
		this.oSize = new Dimension(20,20);
		this.objects = new ArrayList<Point>();
		this.objects.add(new Point(123, 123));
		this.setSize(400,400);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		Drawable draw = new Drawable();
		this.add(draw);
	}
	
	public class Drawable extends JComponent{
        public void paint(Graphics g){
                
                Graphics2D g2d = (Graphics2D)g;
                
                g2d.setPaint(cPlayer);
                g2d.setColor(Color.BLACK);
                for(int i = 0,len = objects.size(); i < len; i++){
                        Rectangle rect = new Rectangle(objects.get(i), oSize);
                        g2d.fill(rect);
                        
                }
        }
		
	}


}
