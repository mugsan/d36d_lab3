package lab3Server;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

/**
 * The Class ServerProtocol.
 * Keeps a reference to the global GameModel.
 * Adjusts the model according to incoming msg.
 */
public class ServerProtocol {
	
	/** The game model. Reference to global model*/
	private GameModel gameModel = null;
	
	/** The state. enum*/
	private ServerState   state = null;
	
	/**
	 * Instantiates a new server protocol.
	 * sets reference to global gamemodel.
	 * @param gameModel the game model
	 */
	public ServerProtocol(GameModel gameModel){
		this.gameModel = gameModel;
	}
	
	/**
	 * Process msg.
	 * Modifies gamemodel depending on given msg.
	 * Returns a new msg with servers response if incoming was approved.
	 *
	 * @param msg the msg
	 * @return the msg
	 */
	public Msg processMsg(Msg msg){
		Msg out = null;
		switch (msg.type) {
		case Join:

			out 	   = new Msg();
			out.id 	   = msg.id;
			out.type   = MsgType.Join;
			this.state = ServerState.Connected;
			break;
			
		case Move:

			out = new Msg();
			if((out.position = this.gameModel.moveObject(msg.id, msg.direction)) != null){
				out.id   = msg.id;
				out.type = MsgType.PlayerMoved;
			}else{
				out = null;
			}
			break;
			
		case Leave:

			out      = new Msg();
			out.id   = msg.id;
			out.type = MsgType.PlayerLeft;

			this.gameModel.removeObject(msg.id);

			this.state = ServerState.Disconnecting;
			break;
		
		default:
			break;
		}
		return out;
	}
	
	/**
	 * Adds the client to model.
	 *
	 * @param id the id
	 * @return the msg
	 */
	public Msg addClientToModel(int id){

        Msg out 		 = new Msg();
            out.id 		 = id;
            out.type  	 = MsgType.NewPlayer;
            out.position = gameModel.addObject(id);

		return out;
	}
	
	/**
	 * Gets the msg list of objects.
	 * Creates a list of Msg's holding id and position of all objets in the current model.
	 * @return the msg list of objects
	 */
	public ArrayList<Msg> getMsgListOfObjects(){

        Map.Entry<Integer, Point>[] entries = gameModel.getObjects();
		ArrayList<Msg> list = new ArrayList<Msg>(entries.length);

        for(int i = 0, len = entries.length; i < len; i++){

        	Msg out 		 = new Msg();
                out.id       = entries[i].getKey();
                out.type     = MsgType.NewPlayer;
                out.position = entries[i].getValue();
                list.add(out);
        }
        return list;
	}
	
	/**
	 * Gets the server state.
	 *
	 * @return the state
	 */
	public ServerState getState(){
		return this.state;
	}
}
