import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;


public class Test_SOM extends JFrame {

	private static final long serialVersionUID = 1L;
	private MyComponent komponent;
	private SOM som;
	private Timer timer;
	
	class MyComponent extends JComponent{

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			int w=getWidth();
			int h=getHeight();
			g.drawRect(w/4, h/4, w/2, h/2);
			som.draw(g, w/4, h/4, w/2, h/2);
			
			Random r=new Random();
			double a=(r.nextDouble()-0.5)/0.5;
			double b=(r.nextDouble()-0.5)/0.5;
				
			Vec2D wejscia=new Vec2D(a,b);
			som.ucz(wejscia);
			
			super.paintComponent(g);
		}
		
	}
	public Test_SOM(String string) {
		super(string);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension d=kit.getScreenSize();
		setBounds(d.width/4,d.height/4,d.width/2,d.height/2);
		add(komponent=new MyComponent());
		som=new SOM(20,20,0.1,0.999,0.999);
		timer=new Timer(20,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				komponent.repaint();
			}
		});
		timer.start();
		setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Test_SOM("Test SOM");
			}
		});
	}

}
