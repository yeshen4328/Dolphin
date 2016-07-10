package DecodeThread;

import io.CustomStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;
import mathTools.Complex;
import mathTools.DFT;
import mathTools.Status;
import mathTools._math;
import mathTools.Mfft;

public class DataExtractionLine implements Runnable
{
	SharedData share = null;
	int para = 32;
	double fs = 44100;
	double preTime = 0.11;
	CalibrateData cali = new CalibrateData();
	public DataExtractionLine(SharedData share)
	{
		// TODO Auto-generated constructor stub
		this.share = share;
	}
	double[] decodeArea = new double[5000];
	double[] tmpReadData = null;
	@Override
	public void run() 
	{
		//定位头部
		long m1 = System.currentTimeMillis();
		preambleLocalization();
		long m2 = System.currentTimeMillis();
		Log.i("msg","preamble finish" + Long.toString(m2 - m1));
		//跳过中间间隙
		m1 = System.currentTimeMillis();
		jumpSilence();
		m2 = System.currentTimeMillis();
		Log.i("msg","jumpSilence finish"+ Long.toString(m2 - m1));
		//开始解码
		m1 = System.currentTimeMillis();
		decode();
		m2 = System.currentTimeMillis();
		Log.i("msg","decode finish"+ Long.toString(m2 - m1));
		share.setStatus(Status.DECODE_FINISH);
     }
     

	private void preambleLocalization() 
	{
		// TODO Auto-generated method stub
		int firstCheck = 0, stepLen = 5000, leftP = 0, rightP = 0, preamble = 0, preNum = _math.round(fs * preTime);
		double N = Math.pow(2,_math.nextpow2(5000));
		double peakDis = 100* N/ fs, startPoint = 198 * peakDis, prepks = 0;
		boolean pre_flag = false;
		double[] step , A, window;
		Mfft ft = new Mfft((int)N);
		Complex[] Y;
		int counter = 0;
		long timeSum = 0;
		while(!share.isEmpty() || !share.isFinish())
		{		
			if(firstCheck == 0)
			{
				decodeArea = share.take();
				leftP = 1;
				rightP = stepLen;
			}
			while((firstCheck == 0 ? decodeArea.length : decodeArea.length - leftP) < 2 * stepLen)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			//
			long start = System.currentTimeMillis();
			step = _math.copyByIndex(decodeArea, leftP, rightP);			
			Y = ft.fft(step);	
			A = _math.cAbs(Y);	 
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + peakDis * 2) - 1);
			double[] windowPow2 = _math.sqrArray(window);
			double pks = _math.sum(_math.copyByIndex(windowPow2, _math.round(2*peakDis/3 ), _math.round(4*peakDis/3) - 1));
			double mean = (_math.sum(windowPow2) - pks) / 2;
			double PAPR = pks / mean;
			if (pre_flag)
			{
				if(PAPR >  4 && pks > 1)
				{
					int le = _math.round(5000 * prepks / (prepks + pks)) + 1500;
					//int re = _math.round(5000 * pks / (prepks + pks)) + 1350;
					double[] tmp = decodeArea.clone();
					decodeArea = _math.copyByIndex(tmp, leftP - le - 1, tmp.length - 1);				
				}
				else
				{
					double[] tmp = decodeArea.clone();
					decodeArea = _math.copyByIndex(tmp, leftP - 6500 - 1, tmp.length - 1);				
				}
				break;
			}
			if(PAPR >  4 && pks > 1)
			{
				pre_flag = true;
				prepks = pks;
			}
			
			if(rightP >= 3 * stepLen - 1)
			{
				double[] tmp = decodeArea.clone();
				decodeArea = _math.copyByIndex(tmp, rightP - 2 * stepLen, tmp.length - 1);
				/*leftP -= stepLen;
				rightP -= stepLen;*/
			}
			else
			{
				leftP += stepLen;
				rightP += stepLen;
			}
			Log.i("preamble",Integer.toString(share.size()));
			long end = System.currentTimeMillis();
			firstCheck++;
			counter++;
			timeSum += end - start;
			Log.i("time","preamble1:" + Long.toString(end - start));
		}
		Log.i("time","preamble1_average:" + Float.toString((float)timeSum/(float)counter));
		long start = System.currentTimeMillis();
		N = Math.pow(2,_math.nextpow2(preNum));
		ft = new Mfft((int)N);
		peakDis = 100 *  N/ fs;
		startPoint = 19800 * N / fs + 2 *  peakDis / 3;
		double[] pksSequence = new double[31];
		double[] temp;	
		double energy = 0;	
		for(int i = 0; i < 3000; i += 100)
		{
			while(i + preNum > decodeArea.length)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			temp = _math.copyByIndex(decodeArea, i, i + (int)preNum - 1);
			Y = ft.fft(temp); 
			A = _math.cAbs(Y);
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + 2*peakDis/3) - 1);
			energy = _math.sum(_math.sqrArray(window));
			pksSequence[i/100] = energy;
		}
		int loc = _math.maxLoc(pksSequence);
	
		if(loc > 0 && loc < 30)
		{
			if(pksSequence[loc - 1] > pksSequence[loc + 1])
			{
				while(100 + preNum > decodeArea.length)
				{
					tmpReadData = share.take();
					resize(decodeArea.length + tmpReadData.length);
				}
				decodeArea = _math.copyByIndex(decodeArea, 100 * (loc - 1), decodeArea.length - 1);
			}
			else
			{
				while(100 + preNum > decodeArea.length)
				{
					tmpReadData = share.take();
					resize(decodeArea.length + tmpReadData.length);
				}
				decodeArea = _math.copyByIndex(decodeArea, 100 * (loc ), decodeArea.length - 1);
			}
		}
		else if(loc == 1)
		{
			while(100 + preNum > decodeArea.length)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			decodeArea = _math.copyByIndex(decodeArea, 100 * (loc ), decodeArea.length - 1);
		}
		else
		{
			while(100 + preNum > decodeArea.length)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			decodeArea = _math.copyByIndex(decodeArea, 100 * (loc - 1), decodeArea.length - 1);
		}
		double[] pkSquence2 = new double[21];
		for(int i = 0; i <= 100; i+=5)
		{
			while(100 + preNum > decodeArea.length)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			double[] tmp = _math.copyByIndex(decodeArea, i, i + (int)preNum - 1);
			Y = ft.fft(tmp);
			A = _math.cAbs(Y);
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + 2*peakDis/3 - 1));
			energy = _math.sum(_math.sqrArray(window));
			pkSquence2[i/5] = energy;
		}
		
		int loc2 = _math.maxLoc(pkSquence2);
		preamble =   5*(loc2) + 4850;
		decodeArea = _math.copyByIndex(decodeArea, (int)preamble, decodeArea.length - 1);
		long end = System.currentTimeMillis();
		Log.i("time","preamble2:" + Long.toString(end - start));
	} 
	private void jumpSilence() 
	{
		// TODO Auto-generated method stub
		double silentTime = 0.11;
		int silenceNum = _math.round(fs * silentTime);
		while(decodeArea.length <= silenceNum)
		{
			tmpReadData = share.take();
			resize(tmpReadData.length + decodeArea.length);
		}
		double[] tmp = decodeArea.clone();
		decodeArea = _math.copyByIndex(tmp, silenceNum + 2, tmp.length - 1);
	}
	private void decode()
	{
		// TODO Auto-generated method stub	
		new Thread(new CalibrationLine(cali, share)).start();
		boolean flagEstimate = false, flagEmbed = true;
		byte[] oneWord = new byte[_math.MM];
		int Ns = 100, para = 60;		
		double symbolTime = 0.11;
		double sigNum_sym = fs * symbolTime;//每个symbol的信号数，采样频率*每个symbol持续时间；
		double[] sigsPerSymbol = new double[_math.round(sigNum_sym) - 441];//一个symbol的信号
		double[] A;
		Complex[] Y;
		byte sig = 0;
		int N = (int) Math.pow(2,_math.nextpow2(sigsPerSymbol.length));//一个symbol的采样点数
		Mfft ft = new Mfft(N);
		double PEAKDIS = Ns * (double)N / (double)fs;//一帧的时间
		double startPoint = 0, carrierStart = 0;
		
		double[] thresholdPAPR = null;
		double[] window = null;	  
		int[] prePAPR = new int[para];
		for(int i = 0; i < para; i++)prePAPR[i] = 1;
		byte[] sins = new byte[Ns * para];
		Complex[] tmp;
		int counter = 0;
		long timesum = 0;
		for(int i = 0; i < Ns + 1; i++)//解每一个symbol
		{
			while(decodeArea.length < sigsPerSymbol.length + 441)
			{
				tmpReadData = share.take();
				resize(tmpReadData.length + decodeArea.length);
			}
			long start = System.currentTimeMillis();
			
			System.arraycopy(decodeArea, 400, sigsPerSymbol, 0, sigsPerSymbol.length);
			
			double[] cpy = decodeArea.clone();
			decodeArea = _math.copyByIndex(cpy, _math.round(sigNum_sym), cpy.length - 1);
			long m1 = System.currentTimeMillis();
			Y = ft.fft(sigsPerSymbol);
			long m2 = System.currentTimeMillis();
			Log.i("fft",Long.toString(m2 - m1));
			tmp = new Complex[Y.length / 2];
			System.arraycopy(Y, 0, tmp, 0, tmp.length);
			
			A = _math.cAbs(tmp);
			/*
			 * 保留待用
			 */
			/*startPoint = 200 * PEAKDIS - 2;//必须放在该位置
			window = _math.copyByIndex(A, (int)Math.floor(startPoint), (int)Math.floor(startPoint + PEAKDIS - 1));
			double[] _window = _math.sqrArray(window);
			int _loc = _math.maxLoc(window);
			if( _loc + 1 >= PEAKDIS - 3 && _loc + 1 <= PEAKDIS + 3)
			{
				double _pks = _math.sum(_math.copyByIndex(_window, _loc-1, _loc+1));
				double _dtmean = (_math.sum(_window) - _pks) / (2 * PEAKDIS - 4);
				double PAPR = _pks / _dtmean;
				if(PAPR > 10)
					flagEmbed = true;
			}*/
			
			int[] msg = new int[para / _math.MM];
			carrierStart = 14000 * N / fs;
			if(flagEmbed)
				if(!flagEstimate)
				{
					thresholdPAPR = PAPREstimate(A, PEAKDIS, carrierStart, para);
					flagEstimate = true;
				}
				else
				{
					startPoint = carrierStart - (PEAKDIS - 1) / 2;//必须放在该位置
					for(int j = 0, countWord = 0, countBit = 0; j < para; j++)
					{
						sig = 0;
						window = _math.copyByIndex(A, (int)Math.floor(startPoint), (int)Math.floor(startPoint + PEAKDIS - 1));
						double pks = _math.max(window);
						int loc = _math.maxLoc(window);
						if(loc + 1>= (PEAKDIS + 1)/2 -2 &&  loc + 1<=  (PEAKDIS + 1) / 2 + 2)
						{
							pks = Math.pow(pks, 2) + Math.max(Math.pow(window[loc - 1], 2), Math.pow(window[loc + 1], 2));
							double dtmean = (_math.sum(_math.sqrArray(window)) - pks) / (PEAKDIS - 2);
							double PAPR = pks / dtmean;
							double condition = thresholdPAPR[j] * (1 + 0.3 * prePAPR[j]);
							if(PAPR > condition)
								sig = 1;
							prePAPR[j] = sig;
						}
						sins[(i - 1) * para + j] = sig;
						startPoint += PEAKDIS;
		//*********************************************************************************************
						oneWord[countBit++] = sig;
						if(countBit == _math.MM)
						{	
							countBit = 0;
							int tmpByte = bin2Byte(oneWord);							
							msg[countWord++] = tmpByte;
						}			
		//*********************************************************************************************
					}
					long end = System.currentTimeMillis();
					counter ++;
					timesum += end - start;
					Log.i("time","decode a symbol:" + Long.toString(end - start));
					cali.put(msg);
				}
		}
		Log.i("time", "decode a symbol average:"+Float.toString((float)timesum/(float)counter));
		cali.setFinish(true);
		cali.put(new int[0]);
		Log.i("msg", "DataExtactionLine: caliFinish " + Boolean.toString(cali.isFinish()));
		try {
				FileWriter fout = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/result.txt");
				BufferedWriter fb = new BufferedWriter(fout);
				CustomStream.writeDoubleIntoTxt(sins, fb);
				fb.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private double[] PAPREstimate(double[] A, double PEAKDIS,
			double carrierStart, int para) {
		// TODO Auto-generated method stub
		double startPoint = carrierStart + (PEAKDIS - 1)/2, PAPRtotal = 0;
		double[] thresholdPAPR = new double[para], PAPRall = new double[para/2];
		int count = 0;
		for(int i = 0; i < para/2; i++)
		{
			double[] window = new double[(int) PEAKDIS];
			System.arraycopy(A, (int)Math.floor(startPoint), window, 0, (int)Math.floor(PEAKDIS));
			double max = _math.max(window);
			int loc = _math.maxLoc(window);
			if(loc + 1>= (PEAKDIS + 1)/2 - 2 && loc + 1<= (PEAKDIS + 1)/2 + 2)
			{
				max = max * max + Math.max(window[loc -1] * window[loc -1] , window[loc + 1] * window[loc + 1]);
 				double dtmean = (_math.sum(_math.sqrArray(window)) - max) / (PEAKDIS - 2);
				double PAPR = max / dtmean;
				count++;
				PAPRall[i] = PAPR;
				PAPRtotal += PAPR;
			}
			startPoint += 2 * PEAKDIS;
		}
		double PAPRavg = PAPRtotal/count;
		double avgThreasholdPAPR = 0;
		if(PAPRavg > 4.2 && PAPRavg < 23)
			avgThreasholdPAPR = -0.001066 * Math.pow(PAPRavg, 3) + 0.04446 * Math.pow(PAPRavg, 2) - 0.34351 * PAPRavg + 3.63456;		
		else if(PAPRavg <= 4.2)
			avgThreasholdPAPR = 2.89;
		else if(PAPRavg <= 100)
			avgThreasholdPAPR = 0.000008174926439765669 * Math.pow(PAPRavg, 3) - 0.002436227743368 * Math.pow(PAPRavg, 2) + 0.2486 * PAPRavg + 1.5669;
		else 
			avgThreasholdPAPR = 10.24;
		double rate = 0.8;
		for(int i = 0; i < para; i++)
			thresholdPAPR[i] = rate * avgThreasholdPAPR + (1 - rate) * _math.avgP_NETE4[60*i/para] * avgThreasholdPAPR;
		
		return thresholdPAPR;
	}


	private double[] genfArray(int N)
	{
		double[] f = new double[N/2];
		for(int i = 0; i < N/2; i++)
			f[i] = (int) (fs / N * (i + 1));
		return f;
	}
	private void resize(int newCpacity)
	{
		double[] newArea = new double[newCpacity];
		transfer(newArea);
		decodeArea = newArea;
	}
	private void transfer(double[] newArea)
	{
		int dataCapacity = tmpReadData.length;
		int oldCapacity = decodeArea.length;
		System.arraycopy(decodeArea, 0, newArea, 0, oldCapacity);
		System.arraycopy(tmpReadData, 0, newArea, oldCapacity, dataCapacity);
	}
	
	private int bin2Byte(byte[] d)
	{
		int out = 0;
		for(int i = 0; i <  _math.MM; i++)		
			out += d[i] * Math.pow(2, _math.MM - 1 - i);	
		return out;
	}

}
