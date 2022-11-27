import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ImageViewer extends JComponent {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int width;
	private int height;

	public ImageViewer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(this.width, this.height);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void load(String imgPath) {
		try {
			image = ImageIO.read(new File(imgPath));
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.width, this.height);
		if(image != null)
			g.drawImage(image, 0, 0, null);
		super.paintComponent(g);
	}
}
