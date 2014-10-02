package lab3Server;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import lab3DataPacket.Msg;
import lab3DataPacket.MsgType;

public class ServerProtocol {
	private GameModel gameModel = null;
	private ServerState   state = null;
	private int              id = 0;
	
	public ServerProtocol(GameModel gameModel,int id){
		this.gameModel = gameModel;
		this.id 	   = id;
	}
	
	public Msg processMsg(Msg msg){
		Msg out = null;
		switch (msg.type) {
		case Join:

			//Send join confirmation and clients new id.
			out 	   = new Msg();
			out.id 	   = this.id;
			out.type   = MsgType.Join;
			this.state = ServerState.Connected;
			break;
			
		case Move:

			out = new Msg();
			if((out.position = this.gameModel.moveObject(msg.id, msg.direction)) != null){
				out.id   = this.id;
				out.type = MsgType.PlayerMoved;
			}
			break;
			
		case Leave:
			System.out.println("Player left.");

			out      = new Msg();
			out.id   = this.id;
			out.type = MsgType.PlayerLeft;

			this.gameModel.removeObject(this.id);

			this.state = ServerState.Disconnecting;
			break;
		
		default:
			break;
		}

		return out;
	}
	
	public Msg addClientToModel(){

        Msg out 		 = new Msg();
            out.id 		 = this.id;
            out.type  	 = MsgType.NewPlayer;
            out.position = gameModel.addObject(this.id);

		return out;
	}
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
	
	public ServerState getState(){
		return this.state;
	}
}
