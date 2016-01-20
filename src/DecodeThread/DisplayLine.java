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
			else
			{
				byte[] tmp = display.take();
				decodeArea = _math.mergeArray(decodeArea, tmp);
			}
			
			int count = 0;
			while(count < decodeArea.length)
			{
				if(decodeArea[count] == 44 || decodeArea[count] == 46)
				{
					byte[] tmp = _math.copyByIndex(decodeArea, 0, count);
					
					share.displayInfo(new String(tmp));
					try {
							Thread.sleep(2000);

					} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
					if(count < decodeArea.length - 1)
						decodeArea = _math.copyByIndex(decodeArea, count + 1, decodeArea.length - 1);
					else
					{
						decodeArea = null;
						break;
					}
					count = 0;
				}
				count++;
			}
		}
		Log.i("msg", "display finish");
		share.displayInfo("display finish");
	}

}
