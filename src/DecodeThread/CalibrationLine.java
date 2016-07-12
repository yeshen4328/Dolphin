package DecodeThread;

import android.util.Log;
import mathTools.RsCoder;
import mathTools._math;
//数据校正线程，从数据提取线程中获取数据并用rs矫正接收到的数据，最后显示
public class CalibrationLine implements Runnable
{
	boolean DOUBLE_ERROR_CORRECTION = true;//二重纠错开关
	CalibrateData cali = null;
	SharedData share = null;
    private int NN = _math.NN;  
    private int KK = _math.KK;
    private int SS = _math.SS;
    int[] tmpReadData = null;
    int[] decodeArea = null;
	public CalibrationLine(CalibrateData cali, SharedData share) 
	{
		// TODO Auto-generated constructor stub
		this.cali = cali;
		this.share = share;
	}
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		DispalyData display = new DispalyData();
		new Thread(new DisplayLine(share, display)).start();
		RsCoder rs = new RsCoder();
		int size = 0;
		decodeArea = cali.take();
		size += decodeArea.length;
		
		int[] block = new int[NN];//一个block的数据，包括NN个码字
		
		int errorNum = 0, errorPos = 0;
		while(!cali.isEmpty() || !cali.isFinish())
		{
			/*
			 * 一次读取一个set的数据
			 */
			while(size < SS * NN  && (!cali.isFinish() || !cali.isEmpty()))//填充缓冲区一直到5 NN + 1字节，用于后面rs解码
			{
				tmpReadData = cali.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;
			}
			if(cali.isFinish() && cali.isEmpty())
				break;
			/*
			 * 分5次处理每一个block
			 */
			byte[][] msgs = new byte[SS][KK - 1];//msgs为一个set中所含有的5 *（NN - 1）个信息码
			for(int i = 0; i < SS; i++)//处理5个block,信息码放在windows中，解码的信息放在msgs中
			{
				/*
				 * 1. 读取第i个block；
				 * 2. 解码block；
				 * 3. 将有效信息码复制到msgs数组中
				 * 4. 利用有效信息码计算抑或，与接受到的抑或值对比
				 */
				block = _math.copyByIndex(decodeArea, i * NN, i * NN + NN - 1);
				byte[] rec = rs.rsDecode(block);//直接发返回信息码
				System.arraycopy(rec, 0, msgs[i], 0, KK - 1);
				int xor = 0;
				for(int k = 0; k < KK - 1; k++)
					xor ^= msgs[i][k];
				if(xor != rec[KK- 1])
				{
					errorNum++;
					errorPos = i;
				}
			}
			if(DOUBLE_ERROR_CORRECTION && errorNum == 1 && errorPos != SS - 1)
			{
				byte[] xor = new byte[KK - 1];		
				for(int k = 0; k < KK - 1; k++)
					for(int j = 0; j < SS; j++)
						if(j != errorPos)
							xor[k] ^= msgs[j][k];
				System.arraycopy(xor, 0, msgs[errorPos], 0, KK - 1);
			}
			errorNum = 0;
			errorPos = 0;
			int[] tmp = decodeArea.clone();
			decodeArea = _math.copyByIndex(tmp, SS * NN, tmp.length - 1);
			size = decodeArea.length;
			//rs解码得到校验后的数据
			byte[] msg = combination(msgs);
			
			display.put(msg);
		}
		Log.i("msg","cali finish");
		display.setFinish(true);
	}
	private void resize(int newCpacity)
	{
		int[] newArea = new int[newCpacity];
		transfer(newArea);
		decodeArea = newArea;
	}
	private void transfer(int[] newArea)
	{
		int dataCapacity = tmpReadData.length;
		int oldCapacity = decodeArea.length;
		System.arraycopy(decodeArea, 0, newArea, 0, oldCapacity);
		System.arraycopy(tmpReadData, 0, newArea, oldCapacity, dataCapacity);
	}
	private byte[] combination(byte[] msg1, byte[] msg2)
	{
		byte[] msg = new byte[(msg1.length + msg2.length)/2];
		byte[] merge = new byte[msg1.length + msg2.length];
		System.arraycopy(msg1, 0, merge, 0, msg1.length);
		System.arraycopy(msg2, 0, merge, msg1.length, msg2.length);
		
		for(int i = 0, j = 0; i < merge.length; i += 2, j++)
		{
			msg[j] = (byte) ((merge[i] & 0xf) << 4);
			msg[j] ^= (merge[i + 1] & 0xf) ;
		}
		return msg;
	}
	private byte[] combination(byte[][] msg)
	{
		/*把msg中的码字合并为字节
		 * out为最后输出的数组，长度：所有码字的一半
		 * merge为msg中元素平铺后的数组
		 * 
		 */
		byte[] out = new byte[(SS - 1) * (KK - 1) / 2];
		byte[] merge = new byte[(SS - 1) * (KK - 1)];
		for(int i = 0; i < SS - 1; i++)
			System.arraycopy(msg[i], 0, merge, i * (KK - 1), KK - 1);
		
		for(int i = 0, j = 0; i < merge.length; i+=2, j++)
		{
			out[j] = (byte)((merge[i] & 0xf) << 4);
			out[j] ^= (merge[i + 1] & 0xf) ;
		}
		return out;
	}
}
