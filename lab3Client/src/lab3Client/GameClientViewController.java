


package lab3Client;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import config.Config;

public class GameClientViewController extends Thread{
	
	private boolean       isScanning = false;
	private boolean        isPlaying = false;
	
	//Views for Structure.
	private JFrame             frame = null;
	private JPanel     verticalPanel = null;
	private JPanel   horizontalPanel = null;


	//ListView components.
	private Map<String, ListItem> addressStorage = null;
	private JList<String>  			  serverList = null;
	private JScrollPane     		  serverPane = null;
	
	
	//UserInput.
	private TextField    searchField = null;
	private JButton     searchButton = null;
	private JToggleButton scanButton = null;
	private TextField   addressField = null;
	
	
	//Models
	private Vector<String> listModel = null;
	
	//----
				
	private InetAddress selectedAddress = null;
	
	
	public GameClientViewController(){

		//Model init.
		this.addressStorage = new HashMap<String, ListItem>();
		this.listModel      = new Vector<String>();
		this.serverList 	= new JList<String>(this.listModel);

		//Main frame.
		this.frame = new JFrame("Server List");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);


		this.verticalPanel    = new JPanel();
		this.verticalPanel.setLayout(new BoxLayout(this.verticalPanel, BoxLayout.Y_AXIS));
		this.frame.add(this.verticalPanel);

		this.serverPane = new JScrollPane(this.serverList);
		this.verticalPanel.add(this.serverPane);
		//Insert list here.


		this.horizontalPanel = new JPanel();
		this.horizontalPanel.setLayout(new FlowLayout());
		this.verticalPanel.add(this.horizontalPanel);
		
		this.searchField  = new TextField(34);
		this.searchField.setText("Scan for servers or enter address.");
		
		this.scanButton   = new JToggleButton("Scan");
		this.searchButton = new JButton("Go!");
		
		this.horizontalPanel.add(this.searchField);
		this.horizontalPanel.add(this.searchButton);
		this.horizontalPanel.add(this.scanButton);
		
		this.addressField = new TextField(36);
		this.addressField.setEditable(false);
		this.verticalPanel.add(this.addressField);

		this.frame.pack();
		this.frame.setVisible(true);
		this.initListeners();
	}
	
	private void initListeners(){
		this.searchButton.addActionListener(new ActionListener() {
			GameClientViewController that = null;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = that.searchField.getText();
				InetAddress[] adrs;
				InetAddress adr;

				
				if(str.length() > 0){
					try {

						adr = InetAddress.getByName(str);

						adrs = Inet6Address.getAllByName(str);
						for(InetAddress addr : adrs){
							if(addr instanceof Inet6Address){
								adr = (Inet6Address)addr;
							}
						}

						that.addressField.setText(adr.getHostAddress());
						
					} catch (Exception e2) {
						that.addressField.setText("No address found!");
					}
				}
			}
			private ActionListener init(GameClientViewController that){
				this.that = that;
				return this;
			}
		}.init(this));
		
		
		
		this.scanButton.addItemListener(new ItemListener() {
			GameClientViewController that = null;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				that.isScanning = that.scanButton.isSelected();
			}
			private ItemListener init(GameClientViewController that){
				this.that = that;
				return this;
			}
		}.init(this));
		
		
	}
	
	private void addUrlToList(String string) throws UnknownHostException{

		String[] strArray = string.split(" ");
		
		String address,port,name;
		
		
		for(String str: strArray){
			System.out.println("Array: " + str);
		}

		if(!(strArray[0].equals("SERVICE") && strArray[1].equals("REPLY") && strArray[2].equals("JavaGameServer"))) return;
		
		
		name = strArray[3];
		address = strArray[4];
		port = strArray[5];

		System.out.println("waddafakk");
		
		if(this.addressStorage.get(name) == null){

			ListItem li = new ListItem();
			li.address  = InetAddress.getByName(address);
			li.port     = Integer.parseInt(port);
			
			this.addressStorage.put(name,li);
			this.listModel.add(name);
			this.serverList.updateUI();
		}
	}
	
	private String receive(DatagramSocket socket) throws IOException{
		
		byte[] buffer = new byte[128];
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(dp);

		return new String(dp.getData(),0 ,dp.getLength());
	}
	
	private void send(DatagramSocket socket, InetAddress ia) throws IOException{
		
		String msg = "SERVICE QUERY JavaGameServer";
		
		byte[] buffer = msg.getBytes();
		
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ia, Config.DSD_PORT);
		
		socket.send(dp);

	}
	
	@Override
	public void run(){
		try(
			DatagramSocket ds = new DatagramSocket();
		){
			InetAddress ia = InetAddress.getByName(Config.DSD_ADDRESS);
			
			new Thread(){
				GameClientViewController that = null;
				@Override
				public void run(){
					while(!that.isPlaying){
						try {
							String str = that.receive(ds);
							that.addUrlToList(str);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				private Thread init(GameClientViewController that){
					this.that = that;
					return this;
				}
			}.init(this).start();
			
			while(!this.isPlaying){
				if(this.isScanning){
					this.send(ds, ia);
					Thread.sleep(1000);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    public static void main(String[] args) throws IOException {

    	GameClientViewController gvc = new GameClientViewController();
    	gvc.start();
//    	InetAddress adr = InetAddress.getByName("localhost");
//		new GameThread(adr, 12000).start();
    }
}