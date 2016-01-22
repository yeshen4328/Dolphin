package audiorecord;

import mathTools.Status;

import com.example.audiorecord.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private ImageButton startRecord;   
    private ImageButton menu;  
    private TextView intro, hint;
    private static final String DataName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.txt";
    private static final String RAWDATANAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.raw";
    protected Saudioclient m_recorder ;
    boolean isFinish = false, isStart = false;
    int funcSelect = 3;
    boolean startClicked = false;
    short sh, sw;
    Handler mHandler = new Handler(){
    	 public void handleMessage(Message msg){ 
    		 if(msg.what == Status.DECODE_FINISH)
    			 Toast.makeText(MainActivity.this, "decode Finish", Toast.LENGTH_LONG).show();
    		 else if(msg.what == Status.DISPLAY_MESSAGE)
    		 {
    			 Bundle bundle = msg.getData();
    			 String str = bundle.getString("intro");
    			 intro.setText(str);
    		 }
    		 else if(msg.what == Status.WRITING_FINISH)
    			 Toast.makeText(MainActivity.this, "Write Finish", Toast.LENGTH_LONG).show();
    		 else if(msg.what == MenuDialog.LOCAL_OFFLINE_DECODE)
    		 {
    			 funcSelect = 1;
    			 hint.setText("本地离线解码：");
    		 }
    		 else if(msg.what == MenuDialog.LOCAL_REALTIME_DECODE)
    		 {
    			 funcSelect = 2;
    			 hint.setText("本地实时解码：");
    		 }
    		 else if(msg.what == MenuDialog.RECORD_REALTIME_DECODE)
    		 {
    			 funcSelect = 3;
    			 hint.setText("录音实时解码：");
    		 }
    		 else if(msg.what == MenuDialog.RECORD_WRITETOFILE)
    		 {
    			 funcSelect = 4;
    			 hint.setText("录音写入文件：");
    		 }
    		 isFinish = true;
    		 isStart = false;
    	 }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		intro = (TextView)findViewById(R.id.intro);
		hint = (TextView)findViewById(R.id.hint);
        startRecord = (ImageButton)findViewById(R.id.start);  
        startRecord.setOnTouchListener(new ButtonSelected());
        startRecord.setOnClickListener(new startRecordListener());           
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
        	if(!startClicked)
        	{
	            // TODO Auto-generated method stub 
        		
        		startClicked = !startClicked;
        		startRecord.setImageResource(R.drawable.startbutton_tostop);
	        	if(!isStart || isFinish)
	        	{
	        		 isStart = true;
	        		 isFinish = false;
		             m_recorder = new Saudioclient(mHandler);  
		             m_recorder.init();
		             m_recorder.startRecord(funcSelect);
	        	}
	        	else
	        		Toast.makeText(MainActivity.this, "alread start or not finish", Toast.LENGTH_LONG).show();
        	}
        	else
        	{
        		startClicked = !startClicked;
        		startRecord.setImageResource(R.drawable.startbutton);
        		m_recorder.stopRecord();
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
