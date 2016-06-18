package DecodeThread;

import java.util.concurrent.LinkedBlockingQueue;

public class CalibrateData {
	LinkedBlockingQueue<int[]> dataQueue = new LinkedBlockingQueue<int[]>();
	private volatile boolean isFinish = false;
	public CalibrateData() {
		// TODO Auto-generated constructor stub
	}
	public void put(int[] data)
	{
		try {
				dataQueue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int[] take()
	{
		int[] data = null;
		try {
				data = dataQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public boolean isEmpty()
	{
		return dataQueue.isEmpty();
	}
	public synchronized boolean isFinish()
	{
		return isFinish;
	}
	public synchronized void setFinish(boolean finish)
	{
		this.isFinish = finish;
	}
}
