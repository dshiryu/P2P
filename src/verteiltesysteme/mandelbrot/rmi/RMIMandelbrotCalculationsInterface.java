package verteiltesysteme.mandelbrot.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIMandelbrotCalculationsInterface extends Remote {
	void iterateAllPoints(int maxIterations, double nrPointsX, double nrPointsY, double ULx, double ULy, double LRx, double LRy) throws RemoteException;
	public int[] getResult() throws RemoteException;
	/*
	public double getULx() throws RemoteException;
	public double getLRx() throws RemoteException;
	public double getULy() throws RemoteException;
	public double getLRy() throws RemoteException;
	public void setView(double ULx, double ULy, double LRx, double LRy) throws RemoteException;
	*/
}