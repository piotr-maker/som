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
	private ViewerListener listener;
	private BufferedImage image;
	private Timer timer;
	private int width;
	private int height;
	private SOM som;
	private double finishTreshold = 0.15;

	public LearnViewer(ViewerListener listener, int width, int height) {
		this.listener = listener;
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
	
	public void setFinishTreshold(double treshold) {
		finishTreshold = treshold;
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
			double a=r.nextDouble();
			double b=r.nextDouble();

			int rgb = image.getRGB((int)(a * image.getWidth()), (int)(b * image.getHeight()));
			if((rgb & 0x00FFFFFF) != 0x00FFFFFF) {
				a = (a - 0.5) * 2;
				b = (b - 0.5) * 2;
				Vec2D wejscia=new Vec2D(a,b);
				som.ucz(wejscia);
				if(som.getEta() < finishTreshold) {
					listener.viewFinished();
				}
			}
			
			super.paintComponent(g);
		}
	}
}
