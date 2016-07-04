package mathTools;

public class Mfft {
//	static Complex[] x;
	FFT trans;
	int setN;
	public Mfft(int setN)
	{
		trans = new FFT(setN);
		this.setN = setN;
	}
	
    public static Complex[] fft(Complex[] s) {
    	Complex[] x = null;
        int N = s.length;
        int S = 0;
        // base case
        if (N == 1) return new Complex[] { s[0] };

        // radix 2 Cooley-Tukey FFT ������2�Ĵ��ݲ���
     
    	S = (int)Math.pow(2, _math.nextpow2(N));
    	x = new Complex[S];
    	
    	for(int i = 0;i<N;i++){
			x[i] = s[i];
		}
    	for(int i = N;i < S;i++){
    		x[i] = new Complex(0);
    	}
//        	throw new RuntimeException("N is not a power of 2"); 


        // fft of even terms
        
        N = x.length;
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
    public Complex[] fft(double[] s){

    	Complex[] x = null;
    	double[] real = new double[setN];
    	double[] im = new double[setN];
        int  N = s.length;
        if (N == 1)
        	return new Complex[]{new Complex(s[0])};
    	x = new Complex[setN];
    	System.arraycopy(s, 0, real, 0, s.length);
    	trans.fft(real, im);
    	for(int i = 0; i < setN; i++)
    		x[i] = new Complex(real[i],im[i]);  	
    	return x;
    }
    public static Complex[] fft(byte[] s){
    	Complex[] data_time = new Complex[s.length];
    	try{
    		for(int i = 0;i<s.length;i++){
    			data_time[i] = new Complex((double)s[i]);
    		}
    	}catch (Exception e) {  
            e.printStackTrace();  
        }
    	return fft(data_time);
    }
    public static Complex[] dft(double[] s){
    	Complex[] X = new Complex[s.length];
    	for(int i = 0; i < X.length; i++)
    		X[i] = new Complex(s[i]);
    	return dft(X);
    }
    public static Complex[] dft(Complex[] poly)
    {
      int m = poly.length;
      Complex[] cc = new Complex[m];
      double t = (2*Math.PI)/m;
      double t2 = 0;
      for (int k=0; k<m; k++)
      { 
        double a = 0;     
        double b = 0;
        double theta = 0;
        for (int j=0; j<m; j++)
        {
          double cos = Math.cos(theta);
          double sin = Math.sin(theta);
          a += (poly[j].re())*cos + (poly[j].im())*sin;
          b += (poly[j].im())*cos - (poly[j].re())*sin;
          theta += t2;
        }
        cc[k] = new Complex(a, b);
        t2 += t;
      }
      return(cc);
    }
    public static double[] idft(Complex[] fd)
    {
      int m = fd.length;
      Complex[] cc = new Complex[m];
      double t = (2*Math.PI)/m;
      double t2 = 0;
      for (int j=0; j<m; j++)
      {
        double a = 0;
        double b = 0;
        double theta = 0;
        for (int k=0; k<m; k++)
        {
          double cos = Math.cos(theta);
          double sin = Math.sin(theta);
          a += (fd[k].re())*cos - (fd[k].im())*sin;
          b += (fd[k].im())*cos + (fd[k].re())*sin;
          theta += t2;
        }
        cc[j] = new Complex(a/m, b/m);
        t2 += t;
      }
      double[] out = new double[cc.length];
      for(int i = 0; i < out.length; i++)
    	  out[i] = cc[i].re();
      return(out);
    }
   
    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }
    public static double[] ifft2(Complex[] x)
    {
    	Complex[] X = ifft(x);
    	double[] array = new double[x.length];
    	for(int i = 0; i < x.length; i++)
    		array[i] = X[i].re();
    	return array;
    }
    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) { throw new RuntimeException("Dimensions don't agree"); }

        int N = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2*x.length];
        for (int i = 0;        i <   x.length; i++) a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++) a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0;        i <   y.length; i++) b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++) b[i] = ZERO;

        return cconvolve(a, b);
    }

    // display an array of Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }


}
