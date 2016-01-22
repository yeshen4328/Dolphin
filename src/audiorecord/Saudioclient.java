package audiorecord;
import io.CustomStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mathTools.Complex;
import mathTools.Status;
import mathTools._math;
import DecodeThread.DataExtractionLine;
import DecodeThread.SharedData;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class Saudioclient extends Thread {
    protected AudioRecord m_in_rec ; 
    protected volatile boolean m_keep_running = false ;
    private int audioSource =  MediaRecorder.AudioSource.MIC;  
    private static int sampleRateInHz = 44100; 
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO; //CHANNEL_IN_STEREOΪ˫������CHANNEL_IN_MONOΪ������  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT; 
    private int bufferSizeInBytes = 0;   
    private static final String TXTAUDIONAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.txt";
    private static final String RAWAUDIONAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.raw";
    private static final String MP3AUDIONAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.mp3";
    public short[] audioData;
    public Complex[] data_time;
    SharedData share;
    Handler mHandler;
    public Saudioclient(Handler mhandler)
    {
    	this.mHandler = mhandler;
    }
    public void init()
    {
    	bufferSizeInBytes =  AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);	   
    	audioData = new short[bufferSizeInBytes]; 
    	m_in_rec = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
    	
    }
    public void startRecord(int func)
    {  
    	m_in_rec.startRecording();   
	    share = new SharedData(mHandler);
	    if(func == 1)//本地离线解码
	    {
	    	double[] data = CustomStream.readRawDataAndTrans2Double(RAWAUDIONAME);
	    	new Thread(new AudioProcess(data, share)).start();
	    }
	    else if(func == 2)//本地实时解码
	    {
	    	new Thread(new AudioRead()).start();
	    	new Thread(new DataExtractionLine(share)).start();
	    }
	    else if(func == 3)//录音实时解码
	    {
	    	new Thread(new AudioRecordThread()).start();  
	    	new Thread(new DataExtractionLine(share)).start();
	    }
	    else //录音写入文件
	    {
	    	new Thread(new AudioRecordThread()).start(); 
	    	new Thread(new WriteToFile()).start();
	    }
    }
  
    public void stopRecord() 
    {
        close();  
    }  
  
    public void close() 
    {  
        if (m_in_rec != null) 
        {  
            System.out.println("stopRecord");  
            share.setFinish(true);
            m_in_rec.stop();  
            m_in_rec.release();
            m_in_rec = null;  
            share = null;
        }
    }   
    public class WriteToFile implements Runnable
    {
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			DataOutputStream dos = null;
			try {
					dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(RAWAUDIONAME)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(!share.isEmpty() || !share.isFinish())
			{
				short[] data = share.takeRaw();
				for(int i = 0; i < data.length; i++)
					try {
							dos.writeShort(data[i]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			try {
				dos.flush();
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			share.setStatus(Status.WRITING_FINISH);
		}	
    }
    public class AudioRecordThread implements Runnable
    {
    	 @Override  
         public void run() {    
             int readsize = 0; 
             short[] dataInShort = null;
             while (!share.isFinish()) 
             {  
                 readsize = m_in_rec.read(audioData, 0, bufferSizeInBytes);  
                 Log.i("TAG",Integer.toString(readsize) + " "+Integer.toString(bufferSizeInBytes));
                 if(readsize > 0)
                 {
                	dataInShort  = _math.copyByIndex(audioData, 0, readsize - 1);
                 	share.put(dataInShort); 
                 }
             }  
         }          
    }
    public class AudioRead implements Runnable
    {
		@Override
		public void run()
		{
			// TODOto-generated method stub
			DataInputStream dis = null;
			try {
					 dis = new DataInputStream(new BufferedInputStream(new FileInputStream(RAWAUDIONAME)));
					 int size = dis.available();					 
					 for(int i = 0; i < size/1024; i++)
					 {
						 short[] data = new short[1024];
						 for(int j = 0; j < 1024; j++)
							 data[j] = dis.readShort();
						 share.put(data);
					 }
					 short[] data = new short[size - (size/1024) * 1024];
					 for(int i = 0; i < data.length; i++)
						 data[i] = dis.readShort();
					 share.put(data);
					 share.setFinish(true);
					 dis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    }
}
