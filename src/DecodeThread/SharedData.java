package DecodeThread;

import java.util.concurrent.LinkedBlockingQueue;

import mathTools.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SharedData 
{
	LinkedBlockingQueue<short[]> dataQueue = new LinkedBlockingQueue<short[]>();
	Handler mhandler;
	volatile boolean finish = false;
	boolean rekey = false;
	public SharedData(Handler mhandler)
	{
		// TODO Auto-generated constructor stub
		this.mhandler = mhandler;
	}
	public void put(short[] data)
	{
		try {
				dataQueue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double[] take()
	{
		short[] data = null;
		double[] dataInDouble = null;
		try {
				data = dataQueue.take();
				dataInDouble = new double[data.length];
				for(int i = 0; i < data.length; i++)
					dataInDouble[i] = (double)data[i] / 32768.0;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataInDouble;
	}
	public short[] takeRaw()
	{
		short[] data = null;
		try{
				data = dataQueue.take();
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public void setRekey(boolean b)
	{
		rekey = b;
	}
	public boolean getRekey()
	{
		return rekey;
	}
	public synchronized boolean isEmpty()
	{
		return dataQueue.isEmpty();
	}
	public synchronized boolean isFinish()
	{
		return finish;
	}
	public synchronized void setFinish(boolean flag)
	{
		finish = flag;
	}
	public int size()
	{
		return dataQueue.size();
	}
	public synchronized void setStatus(int id)
	{
		mhandler.sendEmptyMessage(id);
	}
	public void displayInfo(String str)
	{
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("intro", str);
		msg.what = Status.DISPLAY_MESSAGE;
		msg.setData(bundle);
		mhandler.sendMessage(msg);
	}
	public void toast(String str)
	{
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("toast", str);
		msg.what = Status.TOASTSTATUS;
		msg.setData(bundle);
		mhandler.sendMessage(msg);
	}
}
