


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
import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;


/**
 * The Class GameClientViewController.
 * ViewController
 */
public class GameClientViewController{
	
	//Thread logic.
	/** The is scanning. */
	private boolean       isScanning = false;
	
	/** The is playing. */
	private boolean        isPlaying = false;
	
	//Views for Structure.
	/** The frame. */
	private JFrame             frame = null;
	
	/** The vertical panel. */
	private JPanel     verticalPanel = null;
	
	/** The horizontal panel. */
	private JPanel   horizontalPanel = null;

	//ListView components.
	/** The server list. */
	private JList<String> serverList = null;
	
	/** The server pane. */
	private JScrollPane   serverPane = null;
	
	//UserInput.
	/** The search field. */
	private TextField    searchField = null;
	
	/** The search button. */
	private JButton     searchButton = null;
	
	/** The scan button. */
	private JToggleButton scanButton = null;
	
	//Output.
	/** The output field. */
	private TextField   outputField = null;
	
	//Models
	/** The address storage. */
	private Map<String, ListItem> addressStorage = null;
	
	/** The s list. */
	private DefaultListModel<String>       sList = null;
	
	/**
	 * Instantiates a new game client view controller.
	 */
	public GameClientViewController(){

		//Model init.
		this.addressStorage = new HashMap<String, ListItem>();
		this.sList          = new DefaultListModel<String>();
		this.serverList     = new JList<String>(this.sList);

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
	}
	
	/**
	 * Inits the listeners.
	 */
	public void initListeners(){
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


		this.serverList.addMouseListener(new MouseAdapter() {
			GameClientViewController that = null;
			
			@SuppressWarnings("unchecked")
			public void mouseClicked(MouseEvent e){
				JList<String> list = (JList<String>)e.getSource();
				
				
				Point cursor = e.getPoint();

				Rectangle bounds = list.getCellBounds(0, that.serverList.getLastVisibleIndex());
				
				
				if(bounds == null || !bounds.contains(cursor))return;
				

				int index = list.locationToIndex(cursor);
				
				String name = (String) that.sList.get(index);
				
				ListItem li = that.addressStorage.get(name);
				
				
				String version = (li.address instanceof Inet6Address)? "Ipv6: ":"Ipv4: ";
				
				that.outputField.setText(version + li.address.getHostAddress());
				that.searchField.setText(name);
				
				if(e.getClickCount() >= 2){
					
					try {
						new GameThread(li.address, li.port, that).start();
						that.setVisible(false);
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
	
	/**
	 * Gets the ipv6 address.
	 *
	 * @param string the string
	 * @return the ipv6 address
	 * @throws UnknownHostException the unknown host exception
	 */
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
	
	
	/**
	 * Adds the.
	 *
	 * @param string the string
	 * @throws UnknownHostException the unknown host exception
	 */
	public void add(String string) throws UnknownHostException{

		String[] strArray = string.split(" ");
		
		String address,port,name;
		

		if(!(strArray[0].equals("SERVICE") && strArray[1].equals("REPLY") && strArray[2].equals("JavaGameServer"))) return;
		
		
		name    = strArray[3];
		address = strArray[4];
		port    = strArray[5];

		
		if(this.addressStorage.get(name) == null){

			ListItem li = new ListItem();

			li.address  = this.getIpv6Address(address);
			li.port     = Integer.parseInt(port);
			
			this.addressStorage.put(name,li);
			synchronized (this.sList) {
				this.sList.addElement(name);
			}
		}
	}
	
	/**
	 * Sets the visible.
	 *
	 * @param b the new visible
	 */
	public void setVisible(boolean b){
		System.out.println("Visible");
		this.frame.setVisible(b);
		this.isPlaying = !b;
		if(b) new GameClientDSDThread(this).start();
	}
	
	/**
	 * Gets the checks if is playing.
	 *
	 * @return the checks if is playing
	 */
	public boolean getIsPlaying(){
		return this.isPlaying;
	}
	
	/**
	 * Gets the checks if is scanning.
	 *
	 * @return the checks if is scanning
	 */
	public boolean getIsScanning(){
		return this.isScanning;
	}
	

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {

    	GameClientViewController gvc = new GameClientViewController();
    	gvc.initListeners();
    	gvc.setVisible(true);
    }
}