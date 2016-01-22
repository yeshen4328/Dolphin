package audiorecord;

import mathTools.Status;

import com.example.audiorecord.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;


public class MenuDialog {
	public static final int LOCAL_OFFLINE_DECODE = 3;
	public static final int LOCAL_REALTIME_DECODE = 4;
	public static final int RECORD_REALTIME_DECODE = 5;
	public static final int RECORD_WRITETOFILE = 6;
	Context context;
	Window window ;
	private AlertDialog ad;
	ImageButton ib1, ib2, ib3, ib4; 
	Handler mHandler = null;
	public MenuDialog(Context context,short sh,short sw, Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mHandler = mHandler;
		ad = new android.app.AlertDialog.Builder(context).create();
        ad.show();
        window = ad.getWindow();
        window.setContentView(R.layout.menudialog);
        LayoutParams p = window.getAttributes();
        p.width = (int)(sw*0.7);
        p.height = (int)(sh*0.35);     
        window.setAttributes(p);  
        setButton();
	}

	public void setButton()
	{
		ib1 = (ImageButton)window.findViewById(R.id.b1);
		ib1.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(LOCAL_OFFLINE_DECODE);
				ad.dismiss();
			}});
		ib1.setOnTouchListener(new ButtonSelected());
//******************************************************************************
		ib2 = (ImageButton)window.findViewById(R.id.b2);
		ib2.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(LOCAL_REALTIME_DECODE);
				ad.dismiss();
			}});
		ib2.setOnTouchListener(new ButtonSelected());
//******************************************************************************		
		ib3 = (ImageButton)window.findViewById(R.id.b3);
		ib3.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(RECORD_REALTIME_DECODE);
				ad.dismiss();
			}});
		ib3.setOnTouchListener(new ButtonSelected());
		//******************************************************************************		
		ib4 = (ImageButton)window.findViewById(R.id.b4);
		ib4.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(RECORD_WRITETOFILE);
				ad.dismiss();
			}});
		ib4.setOnTouchListener(new ButtonSelected());
	}

}
