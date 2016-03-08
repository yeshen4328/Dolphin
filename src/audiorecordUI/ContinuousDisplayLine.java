package audiorecordUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;

public class ContinuousDisplayLine extends Thread{
	BitmapToDisRes bmres = null;
	private int lineWidth = 0, gapWidth = 0;
	OscilloGraph og = null;
	public ContinuousDisplayLine(BitmapToDisRes bmres,OscilloGraph og, int lineWidth, int gapWidth) {
		// TODO Auto-generated constructor stub
		this.bmres = bmres;
		this.gapWidth = gapWidth;
		this.lineWidth = lineWidth;
		this.og = og;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Bitmap bm = null;
		Paint p = null;
		while(!bmres.isEmpty() || !bmres.isFinish())
		{
			bm = bmres.take();
			p = bmres.takePaint();
			continuousDisplay(bm, p);
		}
	}
	private void continuousDisplay(Bitmap bm, Paint paint)
	{
		for(int s = 0; s <= lineWidth + gapWidth; s += 4)
		{
			Canvas canvas = og.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			canvas.drawBitmap(bm, lineWidth + gapWidth - s, 0, paint);
			og.unlockCanvasAndPost(canvas);
		}
		bm.recycle();
	}
}
