import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

enum Filling {
	EMPTY, FILLED
}

enum Shape {
	RECT, CIRCLE, TRIANGLE
}

public class App extends JFrame implements ViewerListener {
	private static final long serialVersionUID = 1L;
	private int currentImageNum = 0;
	private SOM som;
	private LearnViewer viewer;
	private ImageViewer leftImage;
	private ImageViewer rightImage;
	
	Filling filling = Filling.EMPTY;
	Shape shapeFirst = Shape.RECT;
	Shape shapeSecond = Shape.CIRCLE;

	// Ustawienia sieci
	final int NET_ROWS = 5;
	final int NET_COLS = 4;
	final double ETA = 0.35;
	final double EPS_ETA = 0.997;
	final double EPS_S = 0.995;
	
	// Rozmiary obiektów
	final int MARGIN = 40;
	final int VIEWER_WIDTH = 250;
	final int VIEWER_HEIGHT = VIEWER_WIDTH;

	public App(String string) {
		super(string);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension d=kit.getScreenSize();
		int width = 4 * MARGIN + 3 * VIEWER_WIDTH;
		int height = 4 * MARGIN + VIEWER_HEIGHT;

		setBounds((d.width-width)/2, (d.height-height)/2, width, height);
		setLayout(new BorderLayout());

		JMenuBar menu_bar = new JMenuBar();
		JMenu menu = new JMenu("Plik");
		JMenuItem menu_item = new JMenuItem("Zamknij");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menu_item);
		menu_bar.add(menu);

		menu = new JMenu("Wypełnienie");
		menu_item = new JMenuItem("Brak");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				filling = Filling.EMPTY;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_item = new JMenuItem("Pełne");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				filling = Filling.FILLED;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_bar.add(menu);

		menu = new JMenu("Figura pierwsza");
		menu_item = new JMenuItem("Kwadrat");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeFirst = Shape.RECT;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_item = new JMenuItem("Koło");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeFirst = Shape.CIRCLE;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_item = new JMenuItem("Trójkąt");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeFirst = Shape.TRIANGLE;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_bar.add(menu);

		menu = new JMenu("Figura Druga");
		menu_item = new JMenuItem("Kwadrat");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeSecond = Shape.RECT;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_item = new JMenuItem("Koło");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeSecond = Shape.CIRCLE;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_item = new JMenuItem("Trójkąt");
		menu_item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				shapeSecond = Shape.TRIANGLE;
				reloadImages();
			}
		});
		menu.add(menu_item);
		menu_bar.add(menu);

		setJMenuBar(menu_bar);
		
		leftImage = new ImageViewer(VIEWER_WIDTH, VIEWER_HEIGHT);
		rightImage = new ImageViewer(VIEWER_WIDTH, VIEWER_HEIGHT);
		viewer = new LearnViewer(this, VIEWER_WIDTH, VIEWER_HEIGHT);
		reloadImages();

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.start();
			}
		});
		
		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.add(leftImage);
		panel.add(viewer);
		panel.add(rightImage);
		add(Box.createHorizontalStrut(MARGIN), BorderLayout.WEST);
		add(Box.createVerticalStrut(MARGIN), BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(startButton, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	@Override
	public void viewFinished() {
		currentImageNum++;
		if(currentImageNum <= 1) {
			viewer.stop();
			som.config(ETA, EPS_ETA, EPS_S);
			viewer.setBufferedImage(rightImage.getImage());
			viewer.start();
		} else {
			viewer.stop();
		}
	}
	
	protected void reloadImages() {
		currentImageNum = 0;
		String folder = filling.toString().toLowerCase();
		String name1 = shapeFirst.toString().toLowerCase();
		String name2 = shapeSecond.toString().toLowerCase();

		leftImage.load("./img/" + folder + "/" + name1 + ".png");
		leftImage.repaint();
		rightImage.load("./img/" + folder + "/" + name2 + ".png");
		rightImage.repaint();
		
		if(filling == Filling.FILLED) {
			viewer.setFinishTreshold(0.1);
			som = new SOM(NET_ROWS, NET_COLS, ETA+0.1, EPS_ETA, EPS_S);
		} else {
			viewer.setFinishTreshold(0.18);
			som = new SOM(1, NET_ROWS * NET_COLS, ETA, EPS_ETA, EPS_S);	
		}
		viewer.stop();
		viewer.setSOM(som);
		viewer.setBufferedImage(leftImage.getImage());
		viewer.repaint();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new App("SOM application");
			}
		});
	}
}
