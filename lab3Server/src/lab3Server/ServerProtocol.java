package lab3Server;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerProtocol {
	private static GameModel gameModel;

	private int 	nextId = 1;
	
	public ServerProtocol(){
		if(ServerProtocol.gameModel == null) ServerProtocol.gameModel = new GameModel();
	}

	public DataPacket processDataPacket(DataPacket dataPacket){
		DataPacket dp;
		switch (dataPacket.msg) {
		case "JOIN":		
			dp = new DataPacket(ServerProtocol.gameModel.getNextId(),"ADD");
			dp.position = ServerProtocol.gameModel.addObject();
			
			break;
		case "MOVE":
			dp = new DataPacket(dataPacket.id, "MOVE");
			dp.position = ServerProtocol.gameModel.moveObject(dataPacket.id, dataPacket.xDir, dataPacket.yDir);
			break;

		default:
			dp = new DataPacket(0,"");
			break;
		}
		return dp;
	}

	private class GameModel{
		private Map<Integer,Point> 	objects;
		private Dimension 			objectDimension = new Dimension(20,20);	
		private Random 				rand = new Random();
		private int 				min = 0;
		private int 				max = 400;
		private int 				nextId = 1;

		public GameModel(){
			this.objects = new HashMap<Integer,Point>();
		}
		
		public Point addObject(){
			Point p = this.createRandomPoint();
			if (this.isPointWithinBounds(p)) this.objects.put(this.nextId,p);
			this.nextId++;
			return p;
		}
		
		public Point moveObject(int id, int dX, int dY){
			Point p = this.objects.get(id);
			Point tmp = new Point(p.x + dX, p.y + dY);
			if (this.isPointWithinBounds(tmp)){
				this.objects.put(id, tmp);
				return tmp;
			}else{
				return p;
			}
		}
		public int getNextId(){
			return this.nextId;
		}
		
		private boolean isPointWithinBounds(Point p){
			System.out.println("x: " + p.x + " y: " + p.y + " x2: " + p.x + this.objectDimension.width + " y2: " + p.y + this.objectDimension.height);
			return (this.min <= p.x) && (p.x + this.objectDimension.width <= this.max) && (this.min <= p.y) && (p.y + this.objectDimension.height <= this.max);
		}
		
		private Point createRandomPoint(){
			int randX = rand.nextInt((this.max - this.min - this.objectDimension.width) + 1) + this.min;
			int randY = rand.nextInt((this.max - this.min - this.objectDimension.height) + 1) + this.min;
			return new Point(randX, randY);
		}
	}
}
