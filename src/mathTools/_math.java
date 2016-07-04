package mathTools;


public class _math {
	public static double[] avgP = {1.09099550356925, 1.01408538081358, 0.845503315726639, 1.41793270648472, 1.15783500369121, 1.25605168547745, 1.13879298599384,
	1.45977108883758, 1.53727259796955, 1.35503979815522, 1.42518983114684, 1.45528452685480, 1.98129170867238, 2.44365593280276, 2.70328379981867, 2.83117451081866,
	2.35215091517193, 2.20267536061849, 2.11477321186034, 1.98224790056663, 1.79723049943505, 1.56662934659121, 1.45435598403554, 1.33452095841046, 1.36415173991983,
	1.25780622881812, 1.02911772553080, 0.744143200221112, 0.783540877852885, 0.808748034288023, 0.740208665429142, 0.525752314252773, 0.488091710860314,
	0.436544940425893, 0.370285766188850, 0.473560370793287, 0.534271786681204, 0.495405981875659, 0.494152956617020, 0.427925876506345, 0.361134843797536,	
	0.236281275748521, 0.349333378782247, 0.528848704475724, 0.554934414238548, 0.759040706584406, 0.902906775556054, 0.953527100279217, 0.780926044228757,	
	0.691346979507293, 0.617731705834571, 0.823642620086294, 0.857129364253756, 0.770340149625863, 0.769623540983172, 0.698017817985873, 0.781846123998413,	
	0.733326754494887, 0.610773308003656, 0.571637239934991, 0.669290877134225, 0.631876497455619, 0.645521336267973, 0.468466771258406, 0.577603251180599};
	
	public static int MM = 4; 
	public static final int NN = 15;  
	public static final int KK = 1;  
	public static final int TT = (NN - KK) / 2; 
	public static final int[] pp = {1,1,0,0,1}; 
	public static byte[] intToByteA(int[] src)
	{
		byte[] des = new byte[src.length];
		for(int i = 0; i < src.length; i++)
			des[i] = (byte)src[i];
		return des;
	}
	public static byte[] mergeArray(byte[] src1, byte[] src2)
	{
		byte[] des = new byte[src1.length + src2.length];
		System.arraycopy(src1, 0, des, 0, src1.length);
		System.arraycopy(src2, 0, des, src1.length, src2.length);
		return des;
	}
	public static short[] mergeArray(short[] src1, short[] src2)
	{
		short[] des = new short[src1.length + src2.length];
		System.arraycopy(src1, 0, des, 0, src1.length);
		System.arraycopy(src2, 0, des, src1.length, src2.length);
		return des;
	}
	public static int round(double a)
	{	
		int b = 0;
		if (a > 0)		
			b = (a - (int)a) > 0.5 ? ((int)a + 1):(int)a;	
		else if(a == 0)
			b = 0;
		else
			b = (Math.abs(a) - Math.abs((int)a)) > 0.5 ? ((int)a -1) : (int)a;
		return b;		
	}
	public static int max(int[] a)
	{
		int max = a[0];
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
				max = a[i];
		return max;
	}
	public static double max(double[] a)
	{
		double max = a[0];
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
				max = a[i];
		return max;
	}
	public static short max(short[] a)
	{
		short max = a[0];
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
				max = a[i];
		return max;
	}
	public static double max(Double[] a)
	{
		double max = a[0];
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
				max = a[i];
		return max;
	}
	public static double min(double[] a)
	{
		double min = a[0];
		for(int i=0;i<a.length;i++)
			if(a[i] < min)
				min = a[i];
		return min;
	}
	public static int maxLoc(double[] a)
	{
		double max = a[0];
		int index = 0;
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
			{
				max = a[i];
				index = i;
			}
		return index;
	}
	public static int maxLoc(short[] a)
	{
		short max = a[0];
		int index = 0;
		for(int i=0;i<a.length;i++)
			if(a[i] > max)
			{
				max = a[i];
				index = i;
			}
		return index;
	}
	public static double mean(double[] a)
	{
		double mean = 0;
		double sum = 0;
		for(int i = 0;i< a.length; i++)
			sum +=a[i];
		mean = sum / a.length;
		return mean;
	}
	public static int nextpow2(int p) {
		// TODO Auto-generated method stub
		int i=0;
		while(Math.pow(2,i) < Math.abs(p))i++;
		return i;
	}
	public static double[] cAbs(Complex[] a)
    {
    	double[] result = new double[a.length];
    	for(int i = 0; i < a.length; i++)
    		result[i] = a[i].abs();   	
    	return result;
    }
	public static double[] normalize(byte[] audiodata)
	{
    	double[] Naudiodata = new double[audiodata.length/2];  	
    	for(int i = 0, j = 0; i < audiodata.length;i += 2, j++)
    	{
    		byte low = audiodata[i];
    		byte high = audiodata[i + 1];
    		short z = (short)(((high & 0x00FF) << 8) | (0x00FF & low));
    		Naudiodata[j] = (double) z / 32768.0;
    	}
    	return Naudiodata;
    }
	public static byte[] iNormalize(double[] normalized)
	{
		byte[] byteData = new  byte[normalized.length * 2];
		for(int i = 0; i < normalized.length; i++)
		{
			short z = (short)_math.round(normalized[i] * 32768.0);
			byte high =  (byte) (0x000000FF & (z >> 8));
			byte low = (byte) (0x000000FF & z);
			byteData[2 * i] = low;
			byteData[2 * i + 1] = high;
		}
		return byteData;
	}
	public static double[] normalize(short[] audiodata)
	{
		double[] normalized = new double[audiodata.length];
		for(int i = 0; i < audiodata.length; i++)
			normalized[i] = (double)audiodata[i] / 32768.0;
		return normalized;
	}
	public static double sum(double[] a ){
		double sum = 0;
		for(double i: a)
			sum +=i;
		return sum;
	}
	public static double[] copyByIndex(double[] src, int start, int end){
		double[] copy = new double[end - start + 1];
		if (copy.length == 0)
			return null;
		if(end >= src.length)
			try {
					throw new copyByIndexOutOfIndex();
			} catch (copyByIndexOutOfIndex e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.arraycopy(src, start, copy, 0, copy.length);
		return copy;
	}
	public static short[] copyByIndex(short[] src, int start, int end){
		short[] copy = new short[end - start + 1];
		if (copy.length == 0)
			return null;
		if(end >= src.length)
			try {
					throw new copyByIndexOutOfIndex();
			} catch (copyByIndexOutOfIndex e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.arraycopy(src, start, copy, 0, copy.length);
		return copy;
	}
	public static byte[] copyByIndex(byte[] src, int start, int end){
		byte[] copy = new byte[end - start + 1];
		if (copy.length == 0)
			return null;
		if(end >= src.length)
			try {
					throw new copyByIndexOutOfIndex();
			} catch (copyByIndexOutOfIndex e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.arraycopy(src, start, copy, 0, copy.length);
		return copy;
	}
	public static int[] copyByIndex(int[] src, int start, int end){
		int[] copy = new int[end - start + 1];
		if (copy.length == 0)
			return new int[0];
		if(end >= src.length)
			try {
					throw new copyByIndexOutOfIndex();
			} catch (copyByIndexOutOfIndex e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.arraycopy(src, start, copy, 0, copy.length);
		return copy;
	}
	public static double[] sqrArray(double[] a){
		double[] pow2 = new double[a.length];
		for(int i = 0; i < a.length; i++)
			pow2[i] = Math.pow(a[i], 2);
		return pow2;
	}
	public static double[][] reshape(double[] src, int row, int col){
		double[][] metrix = new double[row][col];		
		return metrix;
	}
	public static short avg(short[] d)
	{
		float avg = 0;
		for(int i = 0; i < d.length; i++)
			avg += (float)d[i];
		return (short) (avg/(float)d.length);
	}
	public static byte arrayXor(byte[] arr)
	{
		byte xor = 0;
		for(int i = 0; i < arr.length; i++)
			xor ^= arr[i];
		return xor;
	}
	public static boolean isArrayEqual(int[] a, int[] b)
	{
		if(a.length != b.length)
			return false;
		for(int i = 0; i < a.length; i ++)
			if(a[i] != b[i])
				return false;
		return true;
	}
}
