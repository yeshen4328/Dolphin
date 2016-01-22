package audiorecord;

import mathTools.Status;
import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ButtonSelected implements OnTouchListener{
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		if(e.getAction() == MotionEvent.ACTION_DOWN)
		{
			v.getBackground().setColorFilter(new ColorMatrixColorFilter(Status.BT_SELECTED));  
		    v.setBackgroundDrawable(v.getBackground());
		}
		else if(e.getAction() == MotionEvent.ACTION_UP)
		{
			v.getBackground().setColorFilter(new ColorMatrixColorFilter(Status.BT_NOT_SELECTED));  
		    v.setBackgroundDrawable(v.getBackground()); 
		}
		return false;
	}

}
