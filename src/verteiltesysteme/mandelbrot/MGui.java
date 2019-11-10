package verteiltesysteme.mandelbrot;

//Mandelbot Example A. Gepperth

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Complex {
	double r;
	double i;

	Complex() {
		r = 0.0;
		i = 0.0;
	}

	Complex(double r, double i) {
		this.r = r;
		this.i = i;
	}

	void mulBy(Complex other) {
		double rTmp = this.r * other.r - this.i * other.i;
		this.i = this.r * other.i + this.i * other.r;
		this.r = rTmp;
	}

	void add(Complex other) {
		this.r += other.r;
		this.i += other.i;
	}

	void sub(Complex other) {
		this.r -= other.r;
		this.i -= other.i;
	}

	double absValue() {
		return Math.sqrt(this.r * this.r + this.i * this.i);
	}
}

class MandelbrotCalculations {
	// hier wird das Ergebnis von iterateAll() gespeichert
	protected int[] result = null;

	// der augenblicklich gewaehlte Ausschnitt
	//double ULx = -2, ULy = -2, LRx = 2, LRy = 2;
	double ULx = -0.16099999999999995, ULy = -0.9365333333333333, LRx = -0.03533333333333327, LRy = -0.8108666666666666;

	int iterateOnePoint(Complex c, int maxIterations, double maxModulus) {
		Complex z = new Complex(0., 0.);

		for (int i = 0; i < maxIterations; i++) {
			z.mulBy(z);
			z.add(c);
			double m = z.absValue();
			if (m > maxModulus) {
				return i;
			}
		}
		return maxIterations;
	}

	void iterateAllPoints(int maxIterations, double nrPointsX, double nrPointsY) {

		this.result = new int[(int) (nrPointsX * nrPointsY)];

		int counter = 0;
		double stepX = (this.LRx - this.ULx) / nrPointsX;
		double stepY = (this.LRy - this.ULy) / nrPointsY;
		double i = this.ULy;
		for (int cy = 0; cy < nrPointsY; cy++) {
			double r = this.ULx;
			for (int cx = 0; cx < nrPointsX; cx++) {
				Complex c = new Complex(r, i);
				this.result[counter] = maxIterations - this.iterateOnePoint(c, maxIterations, 2.0);
				counter += 1;
				r += stepX;
			}
			i += stepY;
		}
	}

	public int[] getResult() {
		return this.result;
	}

	public double getULx() {
		return this.ULx;
	}

	public double getLRx() {
		return this.LRx;
	}

	public double getULy() {
		return this.ULy;
	}

	public double getLRy() {
		return this.LRy;
	}

	public void setView(double ULx, double ULy, double LRx, double LRy) {
		this.ULx = ULx;
		this.ULy = ULy;
		this.LRx = LRx;
		this.LRy = LRy;
	}

}

class MyJLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5184881932223853807L;
	int boxULx, boxULy, boxW, boxH;

	MyJLabel() {
		super();
	}

	MyJLabel(String s) {
		super(s);
	}

	void setBox(int boxULx, int boxULy, int boxW, int boxH) {
		this.boxULx = boxULx;
		this.boxULy = boxULy;
		this.boxW = boxW;
		this.boxH = boxH;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.drawRect(this.boxULx, this.boxULy, this.boxW, this.boxH);
	}

}

public class MGui implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {

	// GUI elements
	JFrame frame;
	JPanel panel;
	JButton endButton;
	JButton backButton;
	JButton calcButton;
	JSlider maxIterations;
	MyJLabel view;
	int nrIterations;

	// box management
	int boxULx, boxULy, boxW, boxH;
	boolean dragging = false;

	// this one does all the calculating work
	MandelbrotCalculations calcObj;

	// how many points in the image?
	// change if you want a higher resolution
	public static final int RESX = 600;
	public static final int RESY = 600;

	MGui() {
		// set up GUI
		this.frame = new JFrame("Mandelbrotmenge");
		this.panel = new JPanel();
		this.endButton = new JButton("Ende");
		this.panel.add(this.endButton);
		this.backButton = new JButton("Zurueck");
		this.panel.add(this.backButton);
		this.calcButton = new JButton("Rechnen");
		this.panel.add(this.calcButton);
		this.frame.add(this.panel);
		this.frame.setSize(500, 500);
		this.frame.setVisible(true);
		this.view = new MyJLabel("Platzhalter");
		this.maxIterations = new JSlider(0, 50, 30);
		this.panel.add(this.maxIterations);
		this.panel.add(this.view);

		this.endButton.addActionListener(this);
		this.backButton.addActionListener(this);
		this.calcButton.addActionListener(this);
		this.maxIterations.addChangeListener(this);

		this.maxIterations.setMajorTickSpacing(10);
		this.maxIterations.setPaintTicks(true);
		this.maxIterations.setPaintLabels(true);

		this.view.addMouseListener(this);
		this.view.addMouseMotionListener(this);

		this.boxULx = 0;
		this.boxULy = 0;
		this.boxW = MGui.RESX;
		this.boxH = MGui.RESY;

		// Non-Gui stuff
		this.calcObj = new MandelbrotCalculations();
		this.nrIterations = 1000;
	}

	// visualization related stuff, do not touch!
	int[] generateLookupTable(int nrHues) {
		int[] tmp = new int[nrHues + 1];
		int cycle = 2048;
		for (int i = 0; i < nrHues; i++) {
			float hue = 1 * ((float) (i % cycle)) / cycle/* ((float)nrHues) */ ;
			tmp[i] = Color.HSBtoRGB(hue, 0.8f, 1.0f);
		}
		tmp[0] = Color.HSBtoRGB(0f, 1.0f, 0.0f);
		return tmp;
	}

	// visualization
	static void applyLookupTable(int[] data, int[] dest, int[] lookupTable) {
		for (int i = 0; i < data.length; i++) {
			dest[i] = lookupTable[data[i]];
		}
	}

	// here we check what the buttons do
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == this.endButton) {
			System.out.println("Ende");
			System.exit(0);
		} else

		if (ae.getSource() == this.backButton) {
			System.out.println("Zurueck");
		} else

		if (ae.getSource() == this.calcButton) {
			System.out.println("Rechnen");
			long timestampStart = System.currentTimeMillis();
			
			double ULx = this.calcObj.getULx();
			double ULy = this.calcObj.getULy();
			double LRx = this.calcObj.getLRx();
			double LRy = this.calcObj.getLRy();
			double w = LRx - ULx;
			double h = LRy - ULy;
			ULx = ULx + w * this.boxULx / (double) MGui.RESX;
			ULy = ULy + h * this.boxULy / (double) MGui.RESY;
			w *= this.boxW / (double) (RESX);
			h *= this.boxH / (double) (RESX);
			LRx = ULx + w;
			LRy = ULy + h;
			System.out.println(ULx + "/" + ULy + "/" + LRx + "/" + LRy);
			this.boxULx = 0;
			this.boxULy = 0;
			this.boxW = MGui.RESX;
			this.boxH = MGui.RESY;
			this.calcObj.setView(ULx, ULy, LRx, LRy);
			this.calcObj.iterateAllPoints(this.maxIterations.getValue() * 1000, MGui.RESX, MGui.RESY);
			int[] lookupTable = generateLookupTable(this.maxIterations.getValue() * 1000);
			int[] colors = new int[MGui.RESX * MGui.RESY];
			MGui.applyLookupTable(calcObj.getResult(), colors, lookupTable);

			BufferedImage colorImage = new BufferedImage(MGui.RESX, MGui.RESY, BufferedImage.TYPE_INT_ARGB);
			colorImage.setRGB(0, 0, MGui.RESX, MGui.RESY, colors, 0, MGui.RESX);
			this.view.setIcon(new ImageIcon(colorImage));

			long timestampEnd = System.currentTimeMillis();

			String timeElapsed = "Zeit: " + ((timestampEnd - timestampStart) / 1000.0) + " sec";
			System.out.println(timeElapsed);
			this.view.setText(timeElapsed);
		}
	}

	public void mousePressed(MouseEvent e) {
		this.boxULx = e.getX();
		this.boxULy = e.getY();
		this.boxW = 1;
		this.boxH = 1;
		this.view.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
	}

	public void mouseDragged(MouseEvent e) {
		this.boxW = e.getX() - this.boxULx + 1;
		this.boxH = e.getY() - this.boxULy + 1;
		int m = Math.min(boxW, boxH);
		this.boxW = m;
		this.boxH = m;
		this.view.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
		this.view.repaint();
	}

	public void mouseReleased(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			this.view.repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	// process a new slider value
	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == this.maxIterations) {
			//System.out.println("Neuer Wert");
			this.nrIterations = this.maxIterations.getValue() * 1000;
		}
	}

	// main, just instantiate an MGui object, gives control to the Gui
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MGui gui = new MGui();
	}
}
