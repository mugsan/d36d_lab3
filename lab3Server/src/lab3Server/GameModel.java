package lab3Server;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class GameModel{
	private Map<Integer,Point> 	objects;
	private Dimension 			objectDimension = new Dimension(20,20);	
	private Random 				rand = new Random();
	private int 				min = 0;
	private int 				max = 400;

	public GameModel(){
		this.objects = new HashMap<Integer,Point>();
	}
	
	public Point addObject(int id){
		Point p = this.createRandomPoint();
        this.objects.put(id, p);
		return p;
	}
	
	public Point moveObject(int id, Point direction){
		Point p = this.objects.get(id);
		Point tmp = new Point(p.x + direction.x, p.y + direction.y);
		if(this.isPointWithinBounds(tmp)){
			this.objects.put(id, tmp);
			return tmp;
		}else{
			return null;
		}
	}
	
	public void removeObject(int id){
		this.objects.remove(id);
	}
	
	@SuppressWarnings("unchecked")
	public Map.Entry<Integer, Point>[] getObjects(){
		return (Map.Entry<Integer, Point>[])(this.objects.entrySet().toArray(new Map.Entry[this.objects.size()]));
	}

	private Point createRandomPoint(){
		int randX = rand.nextInt((this.max - this.min - this.objectDimension.width) + 1) + this.min;
		int randY = rand.nextInt((this.max - this.min - this.objectDimension.height) + 1) + this.min;
		return new Point(randX, randY);
	}
	
	private boolean isPointWithinBounds(Point p){
		return (this.min <= p.x) && (p.x + this.objectDimension.width <= this.max) && (this.min <= p.y) && (p.y + this.objectDimension.height <= this.max);
	}

}