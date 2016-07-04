package mathTools;

public class DFT {
	static
	{
		System.loadLibrary("AudioRecord");
	}

	public native double[] dft(double[] inreal, double[] inimag, int N );
	public DFT() {
		// TODO Auto-generated constructor stub
	}
	public Complex[] _dft(double[] inData)
	{	
		int N = inData.length;
		double[] img = new double[N];
		double[] dftResult = dft(inData, img, N);
		double[] outRel = new double[N];
		double[] outImg = new double[N];
		System.arraycopy(dftResult, 0, outRel, 0, N);
		System.arraycopy(dftResult, N, outImg, 0, N);
		Complex[] outData = new Complex[inData.length];
		for(int i = 0; i < N; i++)
			outData[i] = new Complex(outRel[i], outImg[i]);
		return outData;
	}
}
