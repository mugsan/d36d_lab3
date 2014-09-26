package lab3Client;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	public GameFrame(){
		this.setSize(400,400);
		this.setTitle("someGame");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
