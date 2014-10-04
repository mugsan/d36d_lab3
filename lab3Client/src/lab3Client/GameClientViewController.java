

package lab3Client;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class GameClientViewController {
	
	
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
	private TextField   addressField = null;
	
	
	//Models
	private Vector<String> listModel = null;
	
	//----
				
	private InetAddress selectedAddress = null;
	
	
	public GameClientViewController(){

		//Model init.
		this.listModel = new Vector<String>();
		this.serverList = new JList<String>(this.listModel);

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
				System.out.println("Pressed searchButton");
				String str = that.searchField.getText();
				InetAddress[] adrs;
				InetAddress adr;

				
				if(str.length() > 0){
					try {

						adrs = Inet6Address.getAllByName(str);
						adr = InetAddress.getByName(str);

						for(InetAddress addr : adrs){
							if(addr instanceof Inet6Address){
								System.out.println("ghetto");
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
	}
	
	public void addShit(){
		for(int i = 0,len = 34; i < len; i++){
			this.listModel.add("LOREM DIPSUM CHECKSUM SHITSUM");
		}
		this.serverList.updateUI();
	}
	
    public static void main(String[] args) throws IOException {

    	new GameClientViewController();
//    	InetAddress adr = InetAddress.getByName("localhost");
//		new GameThread(adr, 12000).start();
    }
}