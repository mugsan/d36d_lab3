package lab3Client;


import lab3Server.DataPacket;

public class ClientProtocol {
	private final int WAITING = 0;
	private final int MOVING  = 1;


	private GameView 	gameView = null;	
	

	public ClientProtocol(GameView gameView){
		this.gameView = gameView;
	}
	
	

	public void process(DataPacket dataPacket){
		switch (dataPacket.msg) {
		case "WELCOME":
			this.id = dataPacket.id;
			this.gameView.addObject(dataPacket.id,dataPacket.position);
			this.gameView.revalidate();
			this.gameView.repaint();
			this.STATE = this.MOVING;
			break;
		case "ADD":
			this.gameView.addObject(dataPacket.id,dataPacket.position);
			this.gameView.revalidate();
			this.gameView.repaint();
			break;
		case "MOVE":
			this.gameView.moveObjectTo(dataPacket.id, dataPacket.position);
			this.gameView.revalidate();
			this.gameView.repaint();
			break;

		default:
			break;
		}
	}
}
