package audiorecordUI;

import mathTools._math;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceView;

public class DisplayWaveForm{
	long lastTime;
	int baseLine, sfvWidth;
	int gapWidth = 6, lineWidth = 4;
	int pos = 0, threshold = 9000;
	short[] disArray;
	int maxLineNum = 0, rateX = 300;
	SurfaceView sfv;
	public DisplayWaveForm(SurfaceView sfv) 
	{
		// TODO Auto-generated constructor stub
		this.sfv = sfv;
		baseLine = sfv.getHeight() / 2;
		sfvWidth = sfv.getWidth();
		maxLineNum = sfvWidth / (lineWidth + gapWidth);
		disArray = null;		
	}

	public void run(short[] data) 
	{
		// TODO Auto-generated method stub		
		short[] temp = new short[data.length / rateX];
		
		for(int i = 0,j = 0; j < temp.length; i += rateX,j++)					
			temp[j] = (short) Math.abs(data[i]);
		short mean = _math.avg(temp);
		for(int i = 0; i < temp.length; i++)
		{
			if(temp[i] < mean)
				temp[i] *= 0.5;
			else
				temp[i] *= 1.5;
		}
        	if(disArray == null)       	
        		disArray = temp.clone();       	
        	else
        		disArray = _math.mergeArray(disArray,temp);
        	drawWaveForm();     
	}
	private void drawWaveForm() 
	{		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Paint gap = new Paint();
		gap.setColor(Color.TRANSPARENT);
		
		for(int i = pos; i  < disArray.length;i++)
		{		
			Canvas canvas = sfv.getHolder().lockCanvas();
			Log.i("disArray:",Short.toString(disArray[i]));
			for(int j = 0;j <= i; j++)
			{			
				if(sfvWidth - (i - j + 1) * (lineWidth + gapWidth) < 0)
					continue;
				if(canvas == null)
					return;	
				float volume = (float) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 100 + 4;
				volume /= 2;
				volume = (float) Math.floor(volume);
				Log.i("volume:",Float.toString(volume));
				canvas.drawRect(sfvWidth - (i - j + 1) * (lineWidth + gapWidth), (baseLine + volume), sfvWidth - (i - j) * (lineWidth + gapWidth) - gapWidth, (baseLine - volume), paint);
				//canvas.drawRect(sfvWidth - (i - j) * (lineWidth + gapWidth) - gapWidth + 1, (baseLine + volume), sfvWidth - (i - j) * (lineWidth + gapWidth), (baseLine - volume), gap);				
			}			
			sfv.getHolder().unlockCanvasAndPost(canvas);
		} 
		if(disArray.length > maxLineNum)
		{
			disArray = _math.copyByIndex(disArray, disArray.length - maxLineNum, disArray.length - 1);		
		}
		pos = disArray.length;
	}

	
}
