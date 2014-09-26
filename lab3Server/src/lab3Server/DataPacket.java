package lab3Server;

import java.awt.Point;
import java.io.Serializable;

public class DataPacket implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8891177319188145587L;

	public final static int MOVETO = 1;
	public final static int LEAVE  = 2;
	public final static int JOIN   = 3;

	public int id;
	public int msg;
	
	public Point position;
	public Point direction;
	
	
	public DataPacket(int id, int msg){
		this.id = id;
		this.msg = msg;
	}
}
