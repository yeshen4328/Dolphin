package audiorecordUI;

import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Bitmap;
import android.graphics.Paint;

public class BitmapToDisRes {
	LinkedBlockingQueue<Bitmap> dataQueue = new LinkedBlockingQueue<Bitmap>();
	LinkedBlockingQueue<Paint> paint = new LinkedBlockingQueue<Paint>();
	private boolean isFinish = false;
	public BitmapToDisRes() {
		// TODO Auto-generated constructor stub
	}
	public void put(Bitmap bm, Paint p)
	{
		try {
				dataQueue.put(bm);
				paint.put(p);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Bitmap take()
	{
		Bitmap bm = null;
		try {
				bm = dataQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}
	public Paint takePaint()
	{
		Paint p = null;
		try {
				p = paint.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	public boolean isFinish()
	{
		return isFinish;
	}
	public boolean isEmpty()
	{
		return dataQueue.isEmpty();
	}

}
