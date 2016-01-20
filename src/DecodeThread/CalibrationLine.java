package DecodeThread;

import android.util.Log;
import android.view.Display;
import mathTools.RsCoder;
import mathTools._math;
//数据校正线程，从数据提取线程中获取数据并用rs矫正接收到的数据，最后显示
public class CalibrationLine implements Runnable
{
	CalibrateData cali = null;
	SharedData share = null;
    private static final int NN = 255;  
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
		int size = 0;
		decodeArea = cali.take();
		size += decodeArea.length;
		
		while(!cali.isEmpty() || !cali.isFinish())			
		{
			while(size < NN && (!cali.isFinish() || !cali.isEmpty()))//填充缓冲区一直到255字节，用于后面rs解码
			{
				tmpReadData = cali.take();
				resize(tmpReadData.length + decodeArea.length);
				size += tmpReadData.length;		
			}
			if(cali.isFinish() && cali.isEmpty())
				break;
			int[] window = _math.copyByIndex(decodeArea, 0, NN - 1);//截取前225字节
			int[] tmp = decodeArea.clone();
			decodeArea = _math.copyByIndex(tmp, NN, tmp.length - 1);
			size = decodeArea.length;
			//rs解码得到校验后的数据
			byte[] msg = rs.rsDecode(window);
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

}
