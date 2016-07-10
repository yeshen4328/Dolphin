package DecodeThread;

import android.util.Log;
import mathTools.RsCoder;
import mathTools._math;
//数据校正线程，从数据提取线程中获取数据并用rs矫正接收到的数据，最后显示
public class CalibrationLine implements Runnable
{
	CalibrateData cali = null;
	SharedData share = null;
    private int NN = _math.NN;  
    private int KK = _math.KK;
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
		int[] window = new int[NN];
		byte[][] msgs = new byte[5][KK];
		int errorNum = 0, errorPos = 0;
		while(!cali.isEmpty() || !cali.isFinish())
		{
			while(size < 5 * (NN + 1) && (!cali.isFinish() || !cali.isEmpty()))//填充缓冲区一直到5 NN + 1字节，用于后面rs解码
			{
				tmpReadData = cali.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;
			}
			if(cali.isFinish() && cali.isEmpty())
				break;
			
			for(int i = 0; i < 5; i++)//处理5个block,信息码放在windows中，解码的信息放在msgs中
			{
				window = _math.copyByIndex(decodeArea, i * (NN + 1), i * (NN + 1) + NN - 1);//截取前NN字节
				msgs[i] = rs.rsDecode(window);			
				byte xorReceived = (byte)decodeArea[(i + 1) * (NN + 1) - 1];
				if(xorReceived != msgs[i][0])
				{
					errorNum++;
					errorPos = i;
				}
			}
			if(errorNum == 1)
			{
				byte tmpXor = 0;
				for(int i = 0; i < 5; i++)
					if(errorPos != 4 && i != errorPos)
						tmpXor ^= msgs[i][0];
				msgs[errorPos][0] = tmpXor;	
			}
			
			int[] tmp = decodeArea.clone();
			decodeArea = _math.copyByIndex(tmp, 5 * (NN + 1), tmp.length - 1);
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
		byte[] out = new byte[msg.length/2];
		byte[] merge = new byte[4 * KK];
		for(int i = 0; i < 4; i++)
			System.arraycopy(msg[i], 0, merge, i*KK, KK);
		
		for(int i = 0, j =0; i < merge.length; i+=2, j++)
		{
			out[j] = (byte)((merge[i] & 0xf) << 4);
			out[j] ^= (merge[i + 1] & 0xf) ;
		}
		return out;
	}
}
