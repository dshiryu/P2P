package verteiltesysteme.mandelbrot.rmi;

import java.awt.BorderLayout;

//Mandelbot Example A. Gepperth

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.image.BufferedImage;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class MyJLabel extends JLabel {

  /**
   *
   */
  private static final long serialVersionUID = -7750935382747799343L;

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

class RMIMandelbrotCalculationThread extends Thread {
	MyJLabel view;
	RMIMandelbrotCalculationsInterface remoteCalcObj;
	int boxULx, boxULy, boxW, boxH;
	public static int RESX = 600;
	public static int RESY = 600;
	Boolean upperPart;
	int maxIterations;

	RMIMandelbrotCalculationThread(Boolean upperPart, MyJLabel view, RMIMandelbrotCalculationsInterface remoteCalcObj, int maxIterations) {
        this.view = view;
        this.remoteCalcObj = remoteCalcObj;
        this.upperPart = upperPart;
        this.maxIterations = maxIterations;
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

    public void run() {
        try {
            this.view.setIcon(new ImageIcon());
        	long timestampStart = System.currentTimeMillis();
        	
        	//double ULx = -0.16099999999999995, ULy = -0.9365333333333333, LRx = -0.03533333333333327, LRy = -0.8108666666666666;
        	
        	double ULx = -2;
            double ULy = 2;
            double LRx = 2;
            double LRy = -2;
            
        	//double ULx = this.remoteCalcObj.getULx();
			//double ULy = this.remoteCalcObj.getULy();
			//double LRx = this.remoteCalcObj.getLRx();
			//double LRy = this.remoteCalcObj.getLRy();
			System.out.println("actual="+ULx + "/" + ULy + "/" + LRx + "/" + LRy);
			double w = LRx - ULx;
			double h = LRy - ULy;
			// AG change define box such that it contains only upper half of apple man :-)
			if (this.upperPart == true)
			{
				boxULx = 0 ; boxULy = 0; boxW = MGuiRMI.RESX ; boxH = MGuiRMI.RESY/2;
			}
			else
			{
			    // for lower half:
			    boxULx = 0 ; boxULy = RESY/2; boxW = RESX ; boxH = RESY/2;
			}


			ULx = ULx + w * this.boxULx / (double) MGuiRMI.RESX;
			ULy = ULy + h * this.boxULy / (double) MGuiRMI.RESY;
			w *= this.boxW / (double) (RESX);
			h *= this.boxH / (double) (RESY);
			LRx = ULx + w;
			LRy = ULy + h;
			System.out.println("computed="+ULx + "/" + ULy + "/" + LRx + "/" + LRy);
			this.boxULx = 0;
			this.boxULy = 0;
			this.boxW = MGuiRMI.RESX;
			this.boxH = MGuiRMI.RESY;
			//this.remoteCalcObj.setView(ULx, ULy, LRx, LRy);
			// end alex
			  int[] colors = null;


			  this.remoteCalcObj.iterateAllPoints(this.maxIterations * 1000, MGuiRMI.RESX, MGuiRMI.RESY/2, ULx, ULy, LRx, LRy);

			  /*
			  int[] lookupTable = generateLookupTable(this.maxIterations.getValue() * 1000);
			  colors = new int[MGuiRMI.RESX * MGuiRMI.RESY];
			  MGuiRMI.applyLookupTable(remoteCalcObj.getResult(), colors, lookupTable);

			  BufferedImage colorImage = new BufferedImage(MGuiRMI.RESX, MGuiRMI.RESY, BufferedImage.TYPE_INT_ARGB);
			  colorImage.setRGB(0, 0, MGuiRMI.RESX, MGuiRMI.RESY, colors, 0, MGuiRMI.RESX);
			  this.view.setIcon(new ImageIcon(colorImage));

			  this.view.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
			  this.view.repaint();

			  long timestampEnd = System.currentTimeMillis();

			  String timeElapsed = "Dauer: " + ((timestampEnd - timestampStart) / 1000.0) + " sec";
			  System.out.println(timeElapsed);
			  this.message.setText(timeElapsed);
			  * */

			  int[] lookupTable = generateLookupTable(this.maxIterations * 1000);
			colors = new int[MGuiRMI.RESX * MGuiRMI.RESY/2];
			MGuiRMI.applyLookupTable(remoteCalcObj.getResult(), colors, lookupTable);

			BufferedImage colorImage = new BufferedImage(MGuiRMI.RESX, MGuiRMI.RESY/2, BufferedImage.TYPE_INT_ARGB);
			colorImage.setRGB(0, 0, MGuiRMI.RESX, MGuiRMI.RESY/2, colors, 0, MGuiRMI.RESX);
			this.view.setIcon(new ImageIcon(colorImage));
			
		      long timestampEnd = System.currentTimeMillis();
	
		      String timeElapsed = "Zeit: " + ((timestampEnd - timestampStart) / 1000.0) + " sec";
		      System.out.println(timeElapsed);
		      //this.view.setText(timeElapsed);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

public class MGuiRMI implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {

  // GUI elements
  JFrame frame;
  JPanel panel;
  JButton endButton;
  JButton backButton;
  JButton calcButton;
  JTextField host1TextField;
  JTextField host2TextField;
  JSlider maxIterations;
  MyJLabel viewUpper;
  MyJLabel viewLower;
  JLabel message;
  int nrIterations;

  // box management
  int boxULx, boxULy, boxW, boxH;
  boolean dragging = false;

  // this one does all the calculating work
  RMIMandelbrotCalculationsInterface remoteCalcObj;
  RMIMandelbrotCalculationsInterface remoteCalcObj2;

  // how many points in the image?
  // change if you want a higher resolution
  public static int RESX = 600;
  public static int RESY = 600;

  MGuiRMI() {
    // set up GUI
    this.frame = new JFrame("Mandelbrotmenge");
    this.panel = new JPanel();
    this.panel.setBorder(new EmptyBorder(0,0,0,0));
    this.endButton = new JButton("Ende");
    this.panel.add(this.endButton);
    this.backButton = new JButton("Zurück");
    this.panel.add(this.backButton);
    this.calcButton = new JButton("Rechnen");
    this.host1TextField = new JTextField(10);
    this.host1TextField.setText("localhost");
    this.host2TextField = new JTextField(10);
    this.host2TextField.setText("localhost");
    this.panel.add(this.calcButton);
    this.panel.add(this.host1TextField);
    this.panel.add(this.host2TextField);
    this.frame.add(this.panel);
    this.frame.setSize(700, 720);
    this.frame.setVisible(true);
    this.viewUpper = new MyJLabel();
    this.viewLower = new MyJLabel();
    this.maxIterations = new JSlider(0, 100, 30);
    this.panel.add(this.maxIterations);
    this.panel.add(this.viewUpper);
    this.panel.add(this.viewLower);
    this.message = new JLabel("Status");
    this.message.setBorder(new BevelBorder(BevelBorder.LOWERED));
    this.frame.add(this.message,BorderLayout.SOUTH);

    this.endButton.addActionListener(this);
    this.backButton.addActionListener(this);
    this.calcButton.addActionListener(this);
    this.maxIterations.addChangeListener(this);

    this.maxIterations.setMajorTickSpacing(10);
    this.maxIterations.setPaintTicks(true);
    this.maxIterations.setPaintLabels(true);

    this.viewUpper.addMouseListener(this);
    this.viewUpper.addMouseMotionListener(this);
    this.viewLower.addMouseListener(this);
    this.viewLower.addMouseMotionListener(this);

    this.boxULx = 0;
    this.boxULy = 0;
    this.boxW = MGuiRMI.RESX;
    this.boxH = MGuiRMI.RESY;

    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Non-Gui stuff
    this.nrIterations = 1000;
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
      // Zoom und Ausschnitt zurücksetzen
      this.boxULx = 0;
      this.boxULy = 0;
      this.boxW = MGuiRMI.RESX;
      this.boxH = MGuiRMI.RESY;
      this.viewUpper.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
      this.viewUpper.repaint();
      this.viewLower.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
      this.viewLower.repaint();
      if (this.remoteCalcObj == null)
      {
        String message = "Fehler: noch nicht verbunden, noch keine Berechnung durchgeführt";
        this.message.setText(message);
        System.out.println(message);
      }
      else
      {
    	/*
        try {
          this.remoteCalcObj.setView(ULx, ULy, LRx, LRy);
        } catch (RemoteException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        */
        this.message.setText("Ausschnitt zurückgesetzt");
      }
      if (this.remoteCalcObj2 == null)
      {
        String message = "Fehler: noch nicht verbunden, noch keine Berechnung durchgeführt";
        this.message.setText(message);
        System.out.println(message);
      }
      else
      {
        /*
    	try {
          this.remoteCalcObj2.setView(ULx, ULy, LRx, LRy);
        } catch (RemoteException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        */
        this.message.setText("Ausschnitt zurückgesetzt");
      }
    } else

    if (ae.getSource() == this.calcButton) {
      String hostname1 = this.host1TextField.getText();
      String hostname2 = this.host2TextField.getText();
      System.out.println("Verbinde mit RMI Registry auf Host: " + hostname1);
      try {
    	  //double ULx = -0.16099999999999995, ULy = -0.9365333333333333, LRx = -0.03533333333333327, LRy = -0.8108666666666666;
	      //double ULx = -2, ULy = 2, LRx = 2, LRy = -2;
        Registry registry = LocateRegistry.getRegistry(hostname1);
        //Registry registry = LocateRegistry.createRegistry(1099);
        this.remoteCalcObj = (RMIMandelbrotCalculationsInterface) registry.lookup("RMIMandelbrotCalculationsInterface");
        //this.remoteCalcObj.setView(ULx, ULy, LRx, LRy);
        Registry registry2 = LocateRegistry.getRegistry(hostname2);
        this.remoteCalcObj2 = (RMIMandelbrotCalculationsInterface) registry2.lookup("RMIMandelbrotCalculationsInterface");
        //this.remoteCalcObj2.setView(ULx, ULy, LRx, LRy);
        /** CODE SEBASTIAN

        System.out.println("Rechnen");
        long timestampStart = System.currentTimeMillis();

        //this.boxW = this.boxW / 2;
        //MGuiRMI.RESX = MGuiRMI.RESX / 2;

        double ULx = 0;
        double ULy = 0;
        double LRx = 0;
        double LRy = 0;

        ULx = this.remoteCalcObj.getULx();
        ULy = this.remoteCalcObj.getULy();
        LRx = this.remoteCalcObj.getLRx();
        LRy = this.remoteCalcObj.getLRy();

        double w = LRx - ULx;
        double h = LRy - ULy;
        ULx = ULx + w * this.boxULx / (double) MGuiRMI.RESX;
        ULy = ULy + h * this.boxULy / (double) MGuiRMI.RESY;
        w *= this.boxW / (double) (RESX);
        h *= this.boxH / (double) (RESX);
        LRx = ULx + w;
        LRy = ULy + h;
        System.out.println(ULx + "/" + ULy + "/" + LRx + "/" + LRy);
        this.boxULx = 0;
        this.boxULy = 0;
        this.boxW = MGuiRMI.RESX;
        this.boxH = MGuiRMI.RESY;
        int[] colors = null;

        //this.remoteCalcObj.setView(ULx, ULy, LRx, LRy);
        this.remoteCalcObj.setView(ULx, ULy, LRx, LRy);

        end */
        /* Code Alex */
      System.out.println("Rechnen");
      this.message.setText("Rechnen");

      RMIMandelbrotCalculationThread rmimct = new RMIMandelbrotCalculationThread(true, this.viewUpper, this.remoteCalcObj, maxIterations.getValue());
      new Thread(rmimct).start();
      RMIMandelbrotCalculationThread rmimct2 = new RMIMandelbrotCalculationThread(false, this.viewLower, this.remoteCalcObj2, maxIterations.getValue());
      new Thread(rmimct2).start();
      
      } catch (RemoteException | NotBoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
  }

  // Zoom not working for lower...
  
  public void mousePressed(MouseEvent e) {
    this.boxULx = e.getX();
    this.boxULy = e.getY();
    this.boxW = 1;
    this.boxH = 1;
    this.viewUpper.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
    this.message.setText("boxULx: " + this.boxULx + " boxULy: " + this.boxULy + " boxW: " + this.boxW + " boxH: " + this.boxH);
  }

  public void mouseDragged(MouseEvent e) {
    this.boxW = e.getX() - this.boxULx + 1;
    this.boxH = e.getY() - this.boxULy + 1;
    int m = Math.min(boxW, boxH);
    this.boxW = m;
    this.boxH = m;
    this.viewUpper.setBox(this.boxULx, this.boxULy, this.boxW, this.boxH);
    this.message.setText("boxULx: " + this.boxULx + " boxULy: " + this.boxULy + " boxW: " + this.boxW + " boxH: " + this.boxH);
    this.viewUpper.repaint();
  }

  public void mouseReleased(MouseEvent e) {

    if (e.getButton() == MouseEvent.BUTTON1) {
      this.viewUpper.repaint();
      this.message.setText("Gesetzt: boxULx: " + this.boxULx + " boxULy: " + this.boxULy + " boxW: " + this.boxW + " boxH: " + this.boxH);
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
      // System.out.println("Neuer Wert");
      this.nrIterations = this.maxIterations.getValue() * 1000;
    }
  }

  // main, just instantiate an MGui object, gives control to the Gui
  public static void main(String[] args) {
    @SuppressWarnings("unused")
    MGuiRMI gui = new MGuiRMI();
  }
}
