package lab3Server;

import java.awt.Point;
import java.io.Serializable;

public class DataPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8891177319188145587L;

	public int id;
	public String msg;
	
	public Point position;
	public int yDir;
	public int xDir;
	
	public DataPacket(int id, String msg){
		this.id = id;
		this.msg = msg;
	}
}
