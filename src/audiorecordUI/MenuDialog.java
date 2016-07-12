package audiorecordUI;

import com.example.audiorecord.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
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
	FrameLayout f1,f2,f3,f4;
	public MenuDialog(Context context,short sh,short sw, Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mHandler = mHandler;
		int shadow = 24;
		ad = new android.app.AlertDialog.Builder(context).create();
        ad.show();
        window = ad.getWindow();
        window.setContentView(R.layout.menudialog);
        f1 = (FrameLayout) window.findViewById(R.id.franme1);
        f2 = (FrameLayout) window.findViewById(R.id.franme2);
        f3 = (FrameLayout) window.findViewById(R.id.franme3);
      //**************************************************************************************************设置f1上边距
        FrameLayout.LayoutParams lp1 = (android.widget.FrameLayout.LayoutParams) f1.getLayoutParams();
        lp1.setMargins(0, 0, 0, 0);
        f1.setLayoutParams(lp1);
      //**************************************************************************************************设置f2上边距
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        f1.measure(w, h);
        int f1Height = f1.getMeasuredHeight() - shadow;
        FrameLayout.LayoutParams lp2 = (android.widget.FrameLayout.LayoutParams) f2.getLayoutParams();
        lp2.setMargins(0, f1Height , 0, 0);
        f2.setLayoutParams(lp2);
      //**************************************************************************************************设置f3上边距
        f2.measure(w, h);
        int f2Height = f2.getMeasuredHeight() - shadow;
        FrameLayout.LayoutParams lp3 = (android.widget.FrameLayout.LayoutParams) f3.getLayoutParams();
        lp3.setMargins(0, f2Height + f1Height, 0, 0);
        f3.setLayoutParams(lp3);

        LayoutParams p = window.getAttributes();
        p.width = (int)(sw*0.8);
        p.height = (int)(sh*0.4);     
        window.setAttributes(p);  
        setButton();
	}

	public void setButton()
	{
		/*
		 * 将音频保存为文件
		 */
		ib1 = (ImageButton)window.findViewById(R.id.b1);
		ib1.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(RECORD_WRITETOFILE);
				ad.dismiss();
			}});
		ib1.setOnTouchListener(new ButtonSelected());
//******************************************************************************
		/*
		 * 本地解码
		 */
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
		/*
		 * 实时解码
		 */
		ib3 = (ImageButton)window.findViewById(R.id.b3);
		ib3.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(RECORD_REALTIME_DECODE);
				ad.dismiss();
			}});
		ib3.setOnTouchListener(new ButtonSelected());

	}

}
