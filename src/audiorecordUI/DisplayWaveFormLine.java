package audiorecordUI;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;

public class DisplayWaveFormLine extends Thread{
	OscilloGraph og = null;
	int drawTime = 1000 / 15;
	long lastTime;
	int baseLine, sfvWidth, sfvHeight;
	int gapWidth = 8, lineWidth = 4;
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
		disArray = new short[maxLineNum];		
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub		
		while(!og.isFinish() || !og.isEmpty())
		{
        	short data = og.take();
        	if(pos + 1 >= maxLineNum)  //如果disArray满了，则将第一个元素除去，整体向前移动一个元素
        	{
        		short[] temp = disArray.clone();
        		System.arraycopy(temp, 1, disArray, 0, temp.length - 1);  
        		disArray[pos] = data;
        	}
        	else
        		disArray[++pos] = data;//将新加的data数据添加到显示数组后边
        	drawWaveForm();     //将disArray中的数据绘制出来
		}	
	}
	private void drawWaveForm() 
	{		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Bitmap bm = Bitmap.createBitmap(sfvWidth, sfvHeight, Config.ARGB_8888);//显示波形的线程运行时间太长会导致位图过多而内存溢出，二号方法		
		Canvas bmCanvas = new Canvas(bm);
		bmCanvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);		
		for(int j = 0; j <= pos; j++)//绘制第pos个数据前的0到pos个数据
		{			
			if(sfvWidth - (pos - j + 1) * (lineWidth + gapWidth) < 0)
				continue;				
			float volume = (float) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 350 + 4;
			volume /= 2;
			volume = (float) Math.floor(volume );
			int alpha = (int) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 255 + 150;
			alpha = (alpha > 255) ? 255 : alpha;
			paint.setAlpha(alpha);
			bmCanvas.drawRect(sfvWidth - (pos - j + 1) * (lineWidth + gapWidth), (baseLine + volume), sfvWidth - (pos - j) * (lineWidth + gapWidth ) - gapWidth, (baseLine - volume), paint);
		}
		continuousDisplay(bm, paint);
	}
	private void continuousDisplay(Bitmap bm, Paint paint)//将一个位图向前移动
	{
		for(int s = 0; s <= lineWidth + gapWidth; s += 8)
		{
			Canvas canvas = og.lockCanvas();
			if(canvas == null)
				return;
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			canvas.drawBitmap(bm, lineWidth + gapWidth - s, 0, paint);
			og.unlockCanvasAndPost(canvas);
		}
		bm.recycle();
	}
}
