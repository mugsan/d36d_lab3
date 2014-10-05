


package lab3Client;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import config.Config;

public class GameClientViewController extends Thread{
	
	//Thread logic.
	private boolean       isScanning = false;
	private boolean        isPlaying = false;
	
	//Views for Structure.
	private JFrame             frame = null;
	private JPanel     verticalPanel = null;
	private JPanel   horizontalPanel = null;


	//ListView components.
	private JList<String> serverList = null;
	private JScrollPane   serverPane = null;
	
	
	//UserInput.
	private TextField    searchField = null;
	private JButton     searchButton = null;
	private JToggleButton scanButton = null;
	
	//Output.
	private TextField   outputField = null;
	
	
	//Models
	private Map<String, ListItem> addressStorage = null;
	private Vector<String>     	list = null;
	
	//----
				
	private ListItem  selectedServer = null;
	
	
	public GameClientViewController(){

		//Model init.
		this.addressStorage = new HashMap<String, ListItem>();
		this.list      = new Vector<String>();
		this.serverList 	= new JList<String>(this.list);

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
		
		this.outputField = new TextField(36);
		this.outputField.setEditable(false);
		this.verticalPanel.add(this.outputField);

		this.frame.pack();
		this.setVisible(true);
		this.initListeners();
	}
	
	private void initListeners(){
		this.searchButton.addActionListener(new ActionListener() {
			GameClientViewController that = null;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = that.searchField.getText();
				
				if(str.length() > 0){
					try {

						InetAddress adr = that.getIpv6Address(str);
						String version = (adr instanceof Inet6Address)? "Ipv6: ": "Ipv4: ";

						that.outputField.setText(version + adr.getHostAddress());
						
					} catch (Exception e2) {
						that.outputField.setText("No address found!");
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
		
		
//		this.serverList.addListSelectionListener(new ListSelectionListener() {
//
//			GameClientViewController that = null;
//			
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				String name = that.list.get(e.getFirstIndex());
//				that.searchField.setText(name);  
//				ListItem li = that.addressStorage.get(name);
//				that.outputField.setText(li.address.getHostAddress());
//				that.selectedAddress = li.address;
//			}
//
//			private ListSelectionListener init(GameClientViewController that){
//				this.that = that;
//				return this;
//			}
//
//		}.init(this));	

		this.serverList.addMouseListener(new MouseAdapter() {
			GameClientViewController that = null;
			
			public void mouseClicked(MouseEvent e){
				JList list = (JList)e.getSource();
				
				
				Point cursor = e.getPoint();

				Rectangle bounds = list.getCellBounds(0, that.serverList.getLastVisibleIndex());
				
				
				if(bounds == null || !bounds.contains(cursor))return;
				

				int index = list.locationToIndex(cursor);
				
				String name = that.list.get(index);
				
				ListItem li = that.addressStorage.get(name);
				
				
				String version = (li.address instanceof Inet6Address)? "Ipv6: ":"Ipv4: ";
				
				that.outputField.setText(version + li.address.getHostAddress());
				that.searchField.setText(name);
				
				if(e.getClickCount() >= 2){
					
					try {
						new GameThread(li.address, li.port, that).start();
						that.isPlaying = true;
						that.setVisible(false);;
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}
			private MouseAdapter init(GameClientViewController that){
				this.that = that;
				return this;
			}
		}.init(this));
	}
	
	private InetAddress getIpv6Address(String string) throws UnknownHostException{

		InetAddress i6a = null;
		
		InetAddress[] ias = InetAddress.getAllByName(string);
		
		for(InetAddress ia: ias){
			if(ia instanceof Inet6Address){
				i6a = (InetAddress)ia;
				break;
			}
		}
		
		return (i6a != null)? i6a: InetAddress.getByName(string);
	}
	
	
	private void addUrlToList(String string) throws UnknownHostException{

		String[] strArray = string.split(" ");
		
		String address,port,name;
		
		for(String str: strArray){
			System.out.println("Info: " + str);
		}

		if(!(strArray[0].equals("SERVICE") && strArray[1].equals("REPLY") && strArray[2].equals("JavaGameServer"))) return;
		
		
		name = strArray[3];
		address = strArray[4];
		port = strArray[5];

		
		if(this.addressStorage.get(name) == null){

			ListItem li = new ListItem();

			li.address  = this.getIpv6Address(address);
			li.port     = Integer.parseInt(port);
			
			this.addressStorage.put(name,li);
			this.list.add(name);
			synchronized (serverList) {
				this.serverList.updateUI();
			}
		}
	}
	
	public void setVisible(boolean b){
		this.frame.setVisible(b);
		this.isPlaying = !b;
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