package DecodeThread;

import android.util.Log;
import mathTools._math;

public class DisplayLine implements Runnable
{
	SharedData share = null;
	DispalyData display = null;
	public DisplayLine(SharedData share, DispalyData display) 
	{
		// TODO Auto-generated constructor stub
		this.share = share;
		this.display = display;
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		byte[] decodeArea = null;
		while(!display.isEmpty() || !display.isFinish())
		{
			if(decodeArea == null)
				decodeArea = display.take();
			share.displayInfo(new String(decodeArea));
			decodeArea = null;
		}
		Log.i("msg", "display finish");
		//share.displayInfo("display finish");
	}

}
