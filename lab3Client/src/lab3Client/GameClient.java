package lab3Client;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
 
public class GameClient {
    public static void main(String[] args) throws IOException {
        GameFrame gView = new GameFrame(); 
        GameViews gViews = new GameViews(10);
        ArrayList<GameObject> aList = new ArrayList<GameObject>();
        
        gView.add(gViews);
        
        int i = 0;
        long fps = 1000/30;
        while(true)
        {
        	try {

        		aList.add(new GameObject(new Point(i,i), 10));
        		i+=10;
        		gViews.setObjecets(aList);
        		System.out.println("hey");
        		gView.getContentPane().validate();
        		gView.getContentPane().repaint();
				Thread.sleep(fps);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
}