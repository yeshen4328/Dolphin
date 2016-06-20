package DecodeThread;

import mathTools.RsCoder;
import mathTools._math;

public class DoubleErrorCorrection {
	private static final int MM = 4;  
    private static final int NN = 15;  
    private static final int KK = 9;  
    private static final int TT = (NN - KK) / 2; 
    private static final int BB = 5; 
	public DoubleErrorCorrection() {
		// TODO Auto-generated constructor stub
	}
	public byte[] errorCorrect(int[] window)
	{
		int errorCount = 0;
		int error = -1;
		byte[] out = new byte[(BB - 1) * KK / 2];
		RsCoder rs = new RsCoder();
		int[] xorBlock = new int[NN + 1];
		int[] rsBlock = new int[NN];
		byte xorSaved = 0;
		for(int i = 0; i < BB - 1; i++)//先用rs解码每一个symbol，并把解码的文本和symbol末的异或值对比
		{
			System.arraycopy(window, i*(NN + 1), rsBlock, 0, NN);
			xorSaved = (byte)window[(i + 1) * NN];
			byte[] msg = rs.rsDecode(rsBlock);
			if(xorSaved != _math.arrayXor(msg))
			{
				errorCount++;
				error = i;
			}

		}
		if(errorCount == 1)//如果BB-1个symbol中有一个不对，则实施二重纠错，否则不做处理
		{
			for(int i = 0; i < NN + 1; i++)
				for(int j = 0; j < BB - 1; j++)
					xorBlock[j*(NN + 1) + i] ^= window[i + j*(NN + 1)];
			int[] lastBlock = new int[NN + 1];
			System.arraycopy(window, (BB-1) * (NN + 1), lastBlock, 0, NN + 1);
			if(!_math.isArrayEqual(xorBlock, lastBlock) && error != -1 && error != BB -1)
			{
				
			}
		}
		
		//System.arraycopy(window, (BB-2)*(NN + 1), lastBlock, 0, NN + 1);
		
		
		return out;
	}

}
