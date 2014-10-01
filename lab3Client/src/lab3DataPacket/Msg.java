package lab3DataPacket;

import java.awt.Point;
import java.io.Serializable;

public class Msg implements Serializable {
	private static final long serialVersionUID = 880817683694673801L;

	public MsgType type;
	public int     id;
	
	public Point position  = new Point();
	public Point direction = new Point();
}
