import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.Timer;

public class LearnViewer extends JComponent {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Timer timer;
	private int width;
	private int height;
	private SOM som;

	public LearnViewer(int width, int height) {
		this.width = width;
		this.height = height;

		timer = new Timer(20,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
	}
	
	public void setBufferedImage(BufferedImage image) {
		this.image = image;
	}
	
	public void setSOM(SOM som) {
		this.som = som;
	}
	
	public void start() {
		timer.start();
	}
	
	public void stop() {
		timer.stop();
	}
	
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(this.width, this.height);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.width, this.height);
		som.draw(g, 0, 0, this.width, this.height);

		if(timer.isRunning()) {
			Random r=new Random();
			double a=(r.nextDouble()-0.5)/0.5;
			double b=(r.nextDouble()-0.5)/0.5;

			Vec2D wejscia=new Vec2D(a,b);
			som.ucz(wejscia);
			
			super.paintComponent(g);
		}
	}
}
