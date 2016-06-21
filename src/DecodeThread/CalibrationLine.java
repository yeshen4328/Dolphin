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
		int[] block = new int[NN];
		int size = 0, _i = 0;
		decodeArea = cali.take();
		size += decodeArea.length;
		
		while(!cali.isEmpty() || !cali.isFinish())			
		{
			while(size < 2 * NN && (!cali.isFinish() || !cali.isEmpty()))//填充缓冲区一直到255字节，用于后面rs解码
			{
				tmpReadData = cali.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;
			}
			if(cali.isFinish() && cali.isEmpty())
				break;
			int[] window1 = _math.copyByIndex(decodeArea, 0, NN - 1);//截取前NN字节
			int[] window2 = _math.copyByIndex(decodeArea, NN, 2 * NN - 1);
			int[] tmp = decodeArea.clone();
			decodeArea = _math.copyByIndex(tmp, NN, tmp.length - 1);
			size = decodeArea.length;
			//rs解码得到校验后的数据
			long start = System.currentTimeMillis();
			
			byte[] msg1 = rs.rsDecode(window1);
			byte[] msg2 = rs.rsDecode(window2);
			byte[] msg = combination(msg1, msg2);
			long end = System.currentTimeMillis();
			Log.i("time","rsDecodeTime:"+Long.toString(end - start)); 
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
		for(int i = 0, j = 0; i < msg.length; i += 2, j++)
		{
			msg[j] = (byte) (merge[i] & 0xf);
			msg[j] &= (merge[i + 1] & 0xf) << 4;
		}
		return msg;
	}
}
