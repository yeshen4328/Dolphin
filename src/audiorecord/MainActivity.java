package audiorecord;

import io.CustomStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import mathTools.Status;

import com.example.audiorecord.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import audiorecord.*;
import audiorecord.Saudioclient.AudioRecordThread;


public class MainActivity extends Activity {

    private Button startRecord;   
    private Button stopRecord;  

    private TextView status;
    private TextView intro;
    private static final String DataName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.txt";
    private static final String RAWDATANAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.raw";
    protected Saudioclient m_recorder ;
    boolean isFinish = false, isStart = false;
    boolean[] selected = {false, false, false, false};
    boolean checked = false;
    String[] s1Item = {"读取音频文件", "录音"};
    String[] s2Item = {"写入文件", "实时解码"};

    Handler mHandler = new Handler(){
    	 public void handleMessage(Message msg){ 
    		 if(msg.what == Status.DECODE_FINISH)
    			 status.setText("decode finish");
    		 else if(msg.what == Status.DISPLAY_MESSAGE)
    		 {
    			 Bundle bundle = msg.getData();
    			 String str = bundle.getString("intro");
    			 intro.setText(str);
    		 }
    		 else if(msg.what == Status.WRITING_FINISH)
    			 status.setText("Writing finish");
    		 isFinish = true;
    		 isStart = false;
    	 }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        startRecord = (Button)findViewById(R.id.startRecord);  
        startRecord.setText(R.string.startRecord);  
        startRecord.setOnClickListener(new startRecordListener());           

        stopRecord = (Button)findViewById(R.id.stopRecord);  
        stopRecord.setText(R.string.stopRecord);  
        stopRecord.setOnClickListener(new stopRecordListener());  

        status = (TextView)findViewById(R.id.status);
        status.setText("Stop");  
        intro = (TextView)findViewById(R.id.intro);
        
        Spinner s1 = (Spinner)findViewById(R.id.srcIn);
        Spinner s2 = (Spinner)findViewById(R.id.dataOut);
        CheckBox box = (CheckBox)findViewById(R.id.offline);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, s1Item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, s2Item);
        
        s1.setAdapter(adapter1);
        s2.setAdapter(adapter2);
        s1.setOnItemSelectedListener(new selectedOne());
        s2.setOnItemSelectedListener(new selectedTwo());
        box.setOnCheckedChangeListener(new boxOnChecked());
	}
	class boxOnChecked implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			checked = !checked;
		}		
	}
	class selectedOne implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			// TODO Auto-generated method stub
			selected[1 - arg2] = false;
			selected[arg2] = true;
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0)
		{
			// TODO Auto-generated method stub		
		}
		
	}
	class selectedTwo implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			selected[3 - arg2] = false;
			selected[2 + arg2] = true;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub			
			}	
	}
    class startRecordListener implements OnClickListener
    {  
        @Override  
        public void onClick(View v) 
        {  
            // TODO Auto-generated method stub 
        	if(!isStart || isFinish)
        	{
        		 isStart = true;
        		 isFinish = false;
	             m_recorder = new Saudioclient(mHandler);  
	             m_recorder.init();
	             m_recorder.startRecord(selected, checked);
	             setStatus("Status:Recording and decoding");    
        	}
        	else
        		Toast.makeText(MainActivity.this, "alread start or not finish", Toast.LENGTH_LONG).show();;
        }          
    }  
    
    class stopRecordListener implements OnClickListener
    {  	  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub   
        	m_recorder.stopRecord();
        	setStatus("Status:Stopped");
            System.out.println("press stop btn");
        }  
          
    }

    public synchronized void setStatus(String text)
    {
    	status.setText(text);
    }
}
