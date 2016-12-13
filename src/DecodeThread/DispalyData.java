package DecodeThread;

import java.util.concurrent.LinkedBlockingQueue;

public class DispalyData
{
	LinkedBlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
	private volatile boolean finish = false;
	public DispalyData() {
		// TODO Auto-generated constructor stub
	}
	public void put(byte[] data)
	{
		try {
				dataQueue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public byte[] take()
	{
		byte[] data = null;
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
	public boolean isFinish()
	{
		return finish;
	}
	public synchronized void setFinish(boolean flag)
	{
		finish = flag;
	}
}
