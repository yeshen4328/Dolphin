package audiorecordUI;

import java.util.concurrent.LinkedBlockingQueue;

import mathTools._math;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

public class OscilloGraph {
	int rateX = 700;
	
	LinkedBlockingQueue<short[]> dataQueue = new LinkedBlockingQueue<short[]>();
	SurfaceView sfv = null;
	private boolean isFinish = false;
	public OscilloGraph(SurfaceView sfv) {
		// TODO Auto-generated constructor stub
		this.sfv = sfv;
	}
	public void put(short[] data)//将录音的数据加入到队列中
		{
			try {					
					dataQueue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Canvas lockCanvas()
	{
		return sfv.getHolder().lockCanvas();
	}
	public void unlockCanvasAndPost(Canvas canvas)
	{
		sfv.getHolder().unlockCanvasAndPost(canvas);
	}
	public int getSFVHeight()
	{
		return sfv.getHeight();
	}
	public int getSFVWidth()
	{
		return sfv.getWidth();
	}
	public short take()
	{
		short[] data = null;
		short temp = 0;
		try {
				data = dataQueue.take();						
				temp = _math.max(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return temp;
	}
	public  boolean isEmpty()
	{
		return dataQueue.isEmpty();
	}
	public synchronized void setFinish(boolean finish)
	{
		this.isFinish = finish;
	}
	public synchronized boolean isFinish()
	{
		return isFinish;
	}

}
