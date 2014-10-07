package lab3Server;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import config.Config;

/**
 * The Class GameModel.
 * Holds and controls all positions of players.
 * One exists per server. 
 */
public class GameModel{

	/** The objects. Integer: clients ID */
	private Map<Integer,Point> objects = null;
	
	/** The object dimension. */
	private Dimension  objectDimension = Config.OBJECT_DIM;	
	
	/** The rand. Used to create random points.*/
	private Random 		 		  rand = new Random();
	
	/** The min. Border of gamespace. */
	private int 	    		   min = 0;
	
	/** The max. Border of gamespace.*/
	private int 				   max = 400;

	/**
	 * Instantiates a new game model.
	 */
	public GameModel(){
		this.objects = new HashMap<Integer,Point>();
	}
	
	/**
	 * Creates a new point at @param id.
	 * Used when a new player logs on to the server.
	 *
	 * @param id the id
	 * @return the point
	 */
	public Point addObject(int id){

		Point p = this.createRandomPoint();

		while (this.collisionCheckAll(p)){
			p = this.createRandomPoint();
		}

        this.objects.put(id, p);

		return p;
	}
	
	/**
	 * Move object.
	 *
	 * @param id the id
	 * @param direction the direction
	 * @return the point
	 */
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
	
	/**
	 * Removes the object.
	 *
	 * @param id the id
	 */
	public void removeObject(int id){
		this.objects.remove(id);
	}
	
	/**
	 * Returns all clients positions and their ids.
	 *
	 * @return the objects
	 */
	@SuppressWarnings("unchecked")
	public Map.Entry<Integer, Point>[] getObjects(){

		Map.Entry<Integer, Point>[] entries = (Map.Entry<Integer, Point>[])(this.objects.entrySet().toArray(new Map.Entry[this.objects.size()]));

		return entries;
	}

	/**
	 * Creates the random point.
	 *
	 * @return the point
	 */
	private Point createRandomPoint(){

		int randX = rand.nextInt((this.max - this.min - this.objectDimension.width) + 1) + this.min;
		int randY = rand.nextInt((this.max - this.min - this.objectDimension.height) + 1) + this.min;

		return new Point(randX, randY);
	}
	
	/**
	 * Collision check single point.
	 * Returns int instead of bool so a sum of collision is easy to calulate.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the int
	 */
	private int collisionCheckSingle(Point a, Point b){

		Rectangle rectA = new Rectangle(a, this.objectDimension);
		Rectangle rectB = new Rectangle(b, this.objectDimension);
		
		return (rectA.intersects(rectB))? 1:0;
	}
	
	/**
	 * Collision check all points.
	 * checks collisions with all points in the model.
	 * the point given will always collide with itself.
	 * if calculated collisions are above 1, then it would collide with another point.
	 *
	 * @param a the a
	 * @return true, if successful
	 */
	private boolean collisionCheckAll(Point a){

		Point[]  array = (Point[])(this.objects.values().toArray(new Point[this.objects.values().size()]));

		int collisions = 0;
		for(int i = 0,len = array.length; i < len; i++){
			collisions += this.collisionCheckSingle(a, array[i]);
		}

		return (collisions > 1);
	}
	
	/**
	 * Checks if is point within gamespace.
	 *
	 * @param p the p
	 * @return true, if is point within bounds
	 */
	private boolean isPointWithinBounds(Point p){

		return (this.min <= p.x) && (p.x + this.objectDimension.width <= this.max) && (this.min <= p.y) && (p.y + this.objectDimension.height <= this.max);
	}

}