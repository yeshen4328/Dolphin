package DecodeThread;
/**
 * v6.0
 */

import android.util.Log;

import java.io.File;
import java.io.FileWriter;

import mathTools._math;

public class DisplayLine extends ProcessLine implements Runnable
{
	SharedData share = null;
	DispalyData display = null;
	boolean rekey = false, findid = false;
	//[] decodeArea = null;
	public DisplayLine(SharedData share, DispalyData display) 
	{
		// TODO Auto-generated constructor stub
		this.share = share;
		this.display = display;
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		decodeArea = display.take();
		int size =  decodeArea.length;
		rekey = share.getRekey();
		byte id = 1;
		byte[] ki = _math.readBytes("/sdcard/dolphin/usrkey.txt");
		byte[] sk = null;
		File skfile = new File("/sdcard/dolphin/SK.txt");
		if(skfile.exists())
			 sk = _math.readBytes("/sdcard/dolphin/SK.txt");

		int bufferSize = rekey ? _math.KEY_LENGTH * 2 + 1 /*rekey mesage*/ : _math.KEY_LENGTH;//区分rekey Message和普通的Message

		while(!display.isEmpty() || !display.isFinish())
		{
			while(size < bufferSize && (!display.isEmpty() || !display.isFinish()))
			{
				tmpReadData = display.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;
			}
			/**
			 * decryption
			 */
			byte[] eblock = _math.copyByIndex(decodeArea, 0, bufferSize - 1);//key_legnth * 2 + 1
			byte[] block = null , content = null;
			if(rekey)
			{
				//接受rekey mesage
				try {
						if(eblock[0] == id)
						{
							block = _math.copyByIndex(eblock, 1, eblock.length - 1);
							content = _math.decrypt(block, ki);
							FileWriter writer = new FileWriter("/sdcard/dolphin/SK.txt");
							for(int i = 0; i < content.length; i++) writer.write(Byte.toString(content[i]) + " ");
							writer.close();
							findid = true;
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				//接受普通的message
				try {
						content = _math.decrypt(eblock, sk);
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			if(display.isEmpty() && display.isFinish())
				break;
			if(!rekey && content != null)
				share.displayInfo(new String(content));

			decodeArea = _math.copyByIndex(decodeArea, bufferSize, decodeArea.length - 1);
			size = decodeArea.length;
		}
		if(rekey && findid)
			share.toast("rekey finish");
		else if(rekey && !findid)
			share.toast("you are not in the group");
		Log.i("msg", "display finish");
		//share.displayInfo("display finish");
	}

}
