package lab3Server;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameModel{

	private Map<Integer,Point> objects = null;
	private Dimension  objectDimension = new Dimension(20,20);	
	private Random 		 		  rand = new Random();
	private int 	    		   min = 0;
	private int 				   max = 400;

	public GameModel(){
		this.objects = new HashMap<Integer,Point>();
	}
	
	public Point addObject(int id){

		Point p = this.createRandomPoint();

		while (this.collisionCheckAll(p)){
			p = this.createRandomPoint();
		}

        this.objects.put(id, p);

		return p;
	}
	
	public Point moveObject(int id, Point direction){

		Point   p = this.objects.get(id);
		Point tmp = new Point(p.x + direction.x, p.y + direction.y);

		if(this.isPointWithinBounds(tmp) && !this.collisionCheckAll(tmp)){
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

		Map.Entry<Integer, Point>[] entries = (Map.Entry<Integer, Point>[])(this.objects.entrySet().toArray(new Map.Entry[this.objects.size()]));

		return entries;
	}

	private Point createRandomPoint(){

		int randX = rand.nextInt((this.max - this.min - this.objectDimension.width) + 1) + this.min;
		int randY = rand.nextInt((this.max - this.min - this.objectDimension.height) + 1) + this.min;

		return new Point(randX, randY);
	}
	
	private int collisionCheckSingle(Point a, Point b){

		Rectangle rectA = new Rectangle(a, this.objectDimension);
		Rectangle rectB = new Rectangle(b, this.objectDimension);
		
		return (rectA.intersects(rectB))? 1:0;
	}
	
	private boolean collisionCheckAll(Point a){

		Point[]  array = (Point[])(this.objects.values().toArray(new Point[this.objects.values().size()]));

		int collisions = 0;
		for(int i = 0,len = array.length; i < len; i++){
			collisions += this.collisionCheckSingle(a, array[i]);
		}

		return (collisions > 1);
	}
	
	private boolean isPointWithinBounds(Point p){

		return (this.min <= p.x) && (p.x + this.objectDimension.width <= this.max) && (this.min <= p.y) && (p.y + this.objectDimension.height <= this.max);
	}

}