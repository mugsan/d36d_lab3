package lab3Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import lab3DataPacket.Msg;
import config.Config;

/**
 * The Class GameServerUDPThread.
 * Receives UDP msgs from any clients connected, and multicasts to all clients.
 */
public class GameServerUDPThread extends Thread{

	/** The protocol. handles msgs*/
	private ServerProtocol      protocol = null;
	
	/** The multicast address. */
	private InetAddress multicastAddress = null;
	
	/** The multicast port. */
	private int            multicastPort = 0;
	
	/** The port. */
	private int             		port = 0;

	/**
	 * Instantiates a new game server udp thread.
	 *
	 * @param gameModel the game model
	 * @param port the port
	 * @throws UnknownHostException the unknown host exception
	 */
	public GameServerUDPThread(GameModel gameModel,int port) throws UnknownHostException{
		
		this.multicastAddress = InetAddress.getByName(Config.MULTICAST_IP_ADDRESS);
		this.multicastPort    = Config.MULTICASTSOCKET_PORT_NUMBER;

		this.port     = port;
		this.protocol = new ServerProtocol(gameModel);
		
	}

	/**
	 * Receive datagram from.
	 * 
	 *
	 * @param socket the socket to listen to
	 * @return the msg received
	 */
	private Msg receiveDatagramFrom(DatagramSocket socket){

		Msg msg = null;

		try { 
			
			byte[]     buffer = new byte[Config.BUF_SIZE];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			socket.receive(dp);

			ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
			ObjectInputStream     ois = new ObjectInputStream(bais);
			
			msg = (Msg)ois.readObject();

		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;
	}
	
	/**
	 * Send msg in datagram.
	 *
	 * @param socket the socket to send from.
	 * @param msg the msg to send.
	 */
	private void sendMsgInDatagram(MulticastSocket socket, Msg msg){
		try{

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream     oos = new ObjectOutputStream(baos);
			
			oos.writeObject(msg);
			oos.flush();
			
			byte[] data = baos.toByteArray();
			
			DatagramPacket dp = new DatagramPacket(data, data.length, this.multicastAddress, this.multicastPort);
			socket.send(dp);

		}catch(Exception e){
			e.printStackTrace();
		}
	}		
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * Listening and multicasting in block form.
	 */
	@Override
	public void run(){
		try (

			DatagramSocket  ds = new DatagramSocket(this.port);
			MulticastSocket ms = new MulticastSocket();
					
		){
            Msg in,out;

            while(this.protocol.getState() != ServerState.Disconnecting){
                in = this.receiveDatagramFrom(ds);
                if(in != null){
                    out = this.protocol.processMsg(in);
                    if(out != null) this.sendMsgInDatagram(ms, out);
                }
            }	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
