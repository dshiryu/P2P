package verteiltesysteme.mandelbrot.rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
//import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

class LoopbackSocketFactory extends RMISocketFactory {
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port, 5, InetAddress.getByName("0.0.0.0"));
    }

    public Socket createSocket(String host, int port) throws IOException {
        // just call the default client socket factory
        return RMISocketFactory.getDefaultSocketFactory()
                               .createSocket(host, port);
    }
}

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

public class RMIMandelbrotCalculationsServer implements RMIMandelbrotCalculationsInterface {
    public RMIMandelbrotCalculationsServer() {}

    public static void main(String args[]) {

        try {
          //RMISocketFactory.setSocketFactory(new LoopbackSocketFactory());

          RMIMandelbrotCalculationsServer obj = new RMIMandelbrotCalculationsServer();
          RMIMandelbrotCalculationsInterface stub = (RMIMandelbrotCalculationsInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("RMIMandelbrotCalculationsInterface", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

  // hier wird das Ergebnis von iterateAll() gespeichert
  protected int[] result = null;

  // der augenblicklich gewaehlte Ausschnitt
  //double ULx = -2, ULy = 2, LRx = 2, LRy = -2;

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

  public void iterateAllPoints(int maxIterations, double nrPointsX, double nrPointsY, double ULx, double ULy, double LRx, double LRy) {

    this.result = new int[(int) (nrPointsX * nrPointsY)];

    int counter = 0;
    double stepX = (LRx - ULx) / nrPointsX;
    double stepY = (LRy - ULy) / nrPointsY;
    double i = ULy;
    for (int cy = 0; cy < nrPointsY; cy++) {
      double r = ULx;
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

  /*
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
  */

}
