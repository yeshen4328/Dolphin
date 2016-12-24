package DecodeThread;
/**
 * v6.1
 */
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import bswabe.Bswabe;
import bswabe.BswabeCph;
import bswabe.BswabeElementBoolean;
import bswabe.BswabePrv;
import bswabe.BswabePub;
import bswabe.SerializeUtils;
import cpabe.Common;
import mathTools._math;

import static android.R.attr.key;

public class DisplayLine extends ProcessLine implements Runnable
{
	SharedData share = null;
	DispalyData display = null;
	boolean rekey = false, findid = false;
	int cphlen = 0, aeslen = 0;
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
		int size = 0, addupsize = 0;
		int bufferSize = 16;//区分rekey Message和普通的Message
		int endfl;
		byte[] key = null;
		try {
				key = getcphBuf_key();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		//*********get aes length**********
		aeslen = getLength();
		size = decodeArea.length;
		while(addupsize < aeslen && (!display.isEmpty() || !display.isFinish()))
		{
			while(size < bufferSize && (!display.isEmpty() || !display.isFinish()))
			{
				tmpReadData = display.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;
				addupsize += tmpReadData.length;
			}
			/**
			 * decryption
			 */
			byte[] eblock = _math.copyByIndex(decodeArea, 0, bufferSize - 1);//
			byte[] content = null;

			//接受message
			try {
					content = _math.decrypt(eblock, key);
			}catch (Exception e)
			{
				e.printStackTrace();
			}
			if(display.isEmpty() && display.isFinish())
				break;
			if(content != null)
				share.displayInfo(new String(content));
			decodeArea = _math.copyByIndex(decodeArea, bufferSize, decodeArea.length - 1);
			size = decodeArea.length;
		}
		Log.i("msg", "display finish");
		share.toast("finish");
		//share.displayInfo("display finish");
	}
	public byte[] getcphBuf_key() throws Exception
	{
		int size = 0;
		int bufferSize = 4;
		byte[] prv_byte;
		byte[] pub_byte;
		BswabePub pub;
		BswabePrv prv;
		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile("/sdcard/dolphin/file_dir/pub_key");
		pub = SerializeUtils.unserializeBswabePub(pub_byte);
		prv_byte = Common.suckFile("./sdcard/dolphin/file_dir/prv_key");
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		//********************************read the length of cphbuf********************************
		cphlen = getLength();
		size = decodeArea.length;
		while(size < cphlen && (!display.isEmpty() || !display.isFinish()))
		{
			tmpReadData = display.take();
			resize(tmpReadData.length + decodeArea.length);
			size += tmpReadData.length;
		}
		byte[] cphbuf = _math.copyByIndex(decodeArea, 0, cphlen - 1);
		decodeArea = _math.copyByIndex(decodeArea, cphlen, decodeArea.length - 1);
		size = decodeArea.length;

		//********************************decode cphbuf********************************
		BswabeElementBoolean beb;
		byte[] key = null;
		BswabeCph cph;
		/* read ciphertext */
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphbuf);
		beb = Bswabe.dec(pub, prv, cph);
		try{
			if(beb.e == null)
			{
				share.toast("decode fail");
				return null;
			}
				byte[] bebebytes = beb.e.toBytes();
				key = _math.getRawKey(bebebytes);
		}catch (Exception e)
		{
			e.printStackTrace();
		}

		return key;
	}
	public int getLength()
	{
		int l = 0;
		int size = decodeArea.length, bufferSize = 4;
		while(size < bufferSize && (!display.isEmpty() || !display.isFinish()))
		{
			tmpReadData = display.take();
			resize(tmpReadData.length + decodeArea.length);
			size += tmpReadData.length;
		}
		byte[] lengthInbytes = _math.copyByIndex(decodeArea, 0,	bufferSize - 1);
		int[] lengthInint = new int[4];
		for(int i = 0; i < 4; i++)
			lengthInint[i] = (int)lengthInbytes[i] & 0xff;
		for (int i = 3; i >= 0; i--)
			l |= lengthInint[3 - i] << (i * 8);
		decodeArea = _math.copyByIndex(decodeArea, bufferSize , decodeArea.length - 1);
		return l;
	}


}
