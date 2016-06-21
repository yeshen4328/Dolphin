package audiorecordUI;

import mathTools.Status;

import com.example.audiorecord.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private RecordButton startRecord;   
    private RecordWave wave;
    private ImageButton menu;  
    private TextView intro, hint;
    private static final String DataName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.txt";
    private static final String RAWDATANAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.raw";
    protected Saudioclient m_recorder ;
    boolean isFinish = false, isStart = false;
    int funcSelect = 3;
    boolean startClicked = false;
    boolean firsttxt = true;
    short sh, sw;
    SurfaceView sfv = null;
    Handler mHandler = new Handler(){
    	
    	 public void handleMessage(Message msg){ 
    		 if(msg.what == Status.DECODE_FINISH)
    		 {
    			 Toast.makeText(MainActivity.this, "decode Finish", Toast.LENGTH_LONG).show();
    			 
    		 }
    		 else if(msg.what == Status.DISPLAY_MESSAGE)
    		 {
    			 if(firsttxt)
    			 {
    				 firsttxt = false;
    				 intro.setText("");
    			 }
    			 Bundle bundle = msg.getData();
    			 String str = bundle.getString("intro");
    			 //intro.setText(str);
    			 intro.append(str);
    		 }
    		 else if(msg.what == Status.WRITING_FINISH)
    			 Toast.makeText(MainActivity.this, "Write Finish", Toast.LENGTH_LONG).show();
    		 else if(msg.what == MenuDialog.RECORD_WRITETOFILE)
    		 {
    			 funcSelect = 1;
    			 hint.setText("Local offline");
    		 }
    		 else if(msg.what == MenuDialog.LOCAL_REALTIME_DECODE)
    		 {
    			 funcSelect = 2;
    			 hint.setText("Local decoding");
    		 }
    		 else if(msg.what == MenuDialog.RECORD_REALTIME_DECODE)
    		 {
    			 funcSelect = 3;
    			 hint.setText("Restore the original settings");
    		 }
    		 isFinish = true;
    		 isStart = false;
    	 }
    };
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		sfv = (SurfaceView)findViewById(R.id.soundWave);
		sfv.setZOrderOnTop(true);
		sfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		intro = (TextView)findViewById(R.id.intro);
		intro.setMovementMethod(new ScrollingMovementMethod());
		hint = (TextView)findViewById(R.id.hint);
        startRecord = (RecordButton)findViewById(R.id.start);  
        //startRecord.setOnTouchListener(new ButtonSelected());
        startRecord.setOnClickListener(new startRecordListener());    
        wave = (RecordWave)findViewById(R.id.wave);
        menu = (ImageButton)findViewById(R.id.menu);
        menu.setOnClickListener(new menuListener());  
        menu.setOnTouchListener(new ButtonSelected());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sh = (short) dm.heightPixels;
        sw = (short) dm.widthPixels;
	}

    class startRecordListener implements OnClickListener
    {  
        @Override  
        public void onClick(View v) 
        {  
        	Log.i("msg","clicked");
        	startRecord.trigeAnnimation();
        	if(!startClicked)
        	{
	            // TODO Auto-generated method stub        		
        		startClicked = !startClicked;
	        	if(!isStart || isFinish)
	        	{
	        		 isStart = true;
	        		 isFinish = false;
		             m_recorder = new Saudioclient(mHandler);  
		             m_recorder.init();
		             m_recorder.startRecord(funcSelect, sfv);
		             wave.setTrigger();
		             wave.postInvalidate();
	        	}
	        	else
	        		Toast.makeText(MainActivity.this, "alread start or not finish", Toast.LENGTH_LONG).show();
        	}
        	else
        	{
        		startClicked = !startClicked;
        		m_recorder.stopRecord();
        		wave.setTrigger();
        		firsttxt = true;
                System.out.println("press stop btn");
        	}
        	
        }          
    }  
    
    class menuListener implements OnClickListener
    {  	  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub   
        	MenuDialog menu = new MenuDialog(MainActivity.this, sh, sw, mHandler);
        }  
         
    }

}
