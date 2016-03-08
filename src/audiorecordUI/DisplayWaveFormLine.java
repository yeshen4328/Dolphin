package audiorecordUI;

import mathTools._math;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.Log;

public class DisplayWaveFormLine extends Thread{
	OscilloGraph og = null;
	int drawTime = 1000 / 15;
	long lastTime;
	int baseLine, sfvWidth, sfvHeight;
	int gapWidth = 7, lineWidth = 5;
	int pos = 0, posImBm = 0, threshold = 300;
	short[] disArray;
	int maxLineNum = 0;
	BitmapToDisRes bmres = null;
	
	public DisplayWaveFormLine(OscilloGraph og) 
	{
		// TODO Auto-generated constructor stub
		this.og = og;
		baseLine = og.getSFVHeight() / 2;
		sfvHeight = og.getSFVHeight();
		sfvWidth = og.getSFVWidth();
		maxLineNum = sfvWidth / (lineWidth + gapWidth);
		bmres = new BitmapToDisRes();
		//new Thread( new ContinuousDisplayLine(bmres, og,lineWidth, gapWidth)).start();
		
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub		
		disArray = null;
		while(!og.isFinish() || !og.isEmpty())
		{
        	short[] data = og.take();
        	if(disArray == null)       	
        		disArray = data.clone();       	
        	else
        		disArray = _math.mergeArray(disArray,data);//将新加的data数据添加到显示数组后边
        	drawWaveForm();     //将disArray中的数据绘制出来
		}	
	}
	private void drawWaveForm() 
	{		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		for(int i = pos; i  < disArray.length;i++)
		{		
//			Canvas canvas = og.lockCanvas(); //一号方法
//			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR); 
			Bitmap bm = Bitmap.createBitmap(sfvWidth, sfvHeight, Config.ARGB_8888);//显示波形的线程运行时间太长会导致位图过多而内存溢出，二号方法
		
			Canvas bmCanvas = new Canvas(bm);
			bmCanvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			
			for(int j = 0;j <= i; j++)
			{			
				if(sfvWidth - (i - j + 1) * (lineWidth + gapWidth) < 0)
					continue;
//					if(canvas == null)
//						return;
				
				float volume = (float) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 200 + 4;
				volume /= 2;
				volume = (float) Math.floor(volume);
				int alpha = (int) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 255 + 150;
				alpha = (alpha > 255) ? 255 : alpha;
				paint.setAlpha(alpha);								
//				canvas.drawRect(sfvWidth - (i - j + 1) * (lineWidth + gapWidth), (baseLine + volume), sfvWidth - (i - j) * (lineWidth + gapWidth ) - gapWidth, (baseLine - volume), paint);				
				bmCanvas.drawRect(sfvWidth - (i - j + 1) * (lineWidth + gapWidth), (baseLine + volume), sfvWidth - (i - j) * (lineWidth + gapWidth ) - gapWidth, (baseLine - volume), paint);
			}
			bmres.put(bm, paint);
			continuousDisplay(bm, paint);
// 			og.unlockCanvasAndPost(canvas);
		}
		if(disArray.length > maxLineNum)
			disArray = _math.copyByIndex(disArray, disArray.length - maxLineNum, disArray.length - 1);	
		
		pos = disArray.length;
	}
	private void continuousDisplay(Bitmap bm, Paint paint)
	{
		for(int s = 0; s <= lineWidth + gapWidth; s += 8)
		{
			Canvas canvas = og.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			canvas.drawBitmap(bm, lineWidth + gapWidth - s, 0, paint);
			og.unlockCanvasAndPost(canvas);
		}
		bm.recycle();
	}
}
