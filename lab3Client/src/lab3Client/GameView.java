package lab3Client;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameView extends JFrame {
	private Map<Integer, GameObject> views;
	private int speed = 3;
	private boolean moved = false;
	private int dx = 0;
	private int dy = 0;
	
	public GameView(){
		this.views = new HashMap<Integer, GameObject>();
		this.setSize(404,429);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
				case 39:
					setDx(0);
					setMoved(false);
					break;
				case 38:
				case 40:
					setDy(0);
					setMoved(false);
					break;
				default:
					break;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					setDx(-speed);
					setMoved(true);
					break;
				case 38:
					setDy(-speed);
					setMoved(true);
					break;
				case 39:
					setDx(speed);
					setMoved(true);
					break;
				case 40:
					setDy(speed);
					setMoved(true);
					break;

				default:
					break;
				}
			}
		});
	}		
	
	public void addObject(int arg_id, Point p){
		Color color = Color.red;
		GameObject gO = new GameObject(p, color);
		this.views.put(arg_id, gO);
		this.add(gO);
		this.validate();
	}
	
	public void moveObjectTo(int id, Point p){
		this.views.get(id).moveTo(p);
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}
}