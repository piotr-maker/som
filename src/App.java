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

	final double ETA = 0.35;

	// Ustawienia sieci dla łańcucha
	final int STRING_POINTS = 25;
	final double STRING_S = Math.sqrt(STRING_POINTS);
	final double STRING_EPS_ETA = 0.997;
	final double STRING_EPS_S = 0.996;
	final double STRING_TRESHOLD = 0.04;
	
	// Ustawienia sieci dla siatki
	final int NET_ROWS = 10;
	final int NET_COLS = 10;
	final double MESH_S = Math.sqrt(NET_ROWS * NET_COLS) - 4;
	final double MESH_EPS_S = 0.997;
	final double MESH_EPS_ETA = 0.999;
	final double MESH_TRESHOLD = 0.06;

	// Rozmiary obiektów
	final int MARGIN = 40;
	final int VIEWER_WIDTH = 250;
	final int VIEWER_HEIGHT = VIEWER_WIDTH;
	
	JButton startButton;

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

		startButton = new JButton("Play");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(startButton.getText() == "Play") {
					viewer.start();
					startButton.setText("Pause");
				} else {
					viewer.stop();
					startButton.setText("Play");
				}
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
		reloadImages();
		setVisible(true);
	}

	@Override
	public void viewFinished() {
		double S = filling == Filling.FILLED ? MESH_S : STRING_S;
		double epsS = filling == Filling.FILLED ? MESH_EPS_S : STRING_EPS_S;
		double epsEta = filling == Filling.FILLED ? MESH_EPS_ETA : STRING_EPS_ETA;
		double treshold = filling == Filling.FILLED ? MESH_TRESHOLD : STRING_TRESHOLD;

		currentImageNum++;
		if(currentImageNum % 2 == 1) {
			viewer.stop();
			viewer.setFinishTreshold(treshold);
			som.config(S, ETA, epsEta, epsS);
			viewer.setBufferedImage(rightImage.getImage());
			viewer.start();
		} else {
			viewer.stop();
			viewer.setFinishTreshold(treshold);
			som.config(S, ETA, epsEta, epsS);
			viewer.setBufferedImage(leftImage.getImage());
			viewer.start();
		}
	}

	protected void reloadImages() {
		currentImageNum = -1;
		String folder = filling.toString().toLowerCase();
		String name1 = shapeFirst.toString().toLowerCase();
		String name2 = shapeSecond.toString().toLowerCase();

		leftImage.load("./img/" + folder + "/" + name1 + ".png");
		leftImage.repaint();
		rightImage.load("./img/" + folder + "/" + name2 + ".png");
		rightImage.repaint();
		viewer.stop();

		if(filling == Filling.FILLED) {
			som = new SOM(NET_ROWS, NET_COLS, ETA, MESH_EPS_ETA, MESH_EPS_S);
		} else {
			som = new SOM(1, STRING_POINTS, ETA, STRING_EPS_ETA, MESH_EPS_S);
		}
		viewer.setSOM(som);
		viewer.repaint();
		viewer.setBufferedImage(leftImage.getImage());
		startButton.setText("Play");
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
