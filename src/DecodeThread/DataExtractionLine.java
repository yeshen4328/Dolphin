package DecodeThread;

import io.CustomStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;
import mathTools.Complex;
import mathTools.Status;
import mathTools._math;
import mathTools.Mfft;

public class DataExtractionLine implements Runnable
{
	SharedData share = null;
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
		long m1 = System.currentTimeMillis();
		preambleLocalization();
		long m2 = System.currentTimeMillis();
		Log.i("msg","preamble finish" + Long.toString(m2 - m1));
		m1 = System.currentTimeMillis();
		jumpSilence();
		m2 = System.currentTimeMillis();
		Log.i("msg","jumpSilence finish"+ Long.toString(m2 - m1));
		m1 = System.currentTimeMillis();
		decode();
		m2 = System.currentTimeMillis();
		Log.i("msg","decode finish"+ Long.toString(m2 - m1));
		share.setStatus(Status.DECODE_FINISH);
     }
     

	private void preambleLocalization() 
	{
		// TODO Auto-generated method stub
		int firstCheck = 0, stepLen = 5000, leftP = 0, rightP = 0;
		int N = (int) Math.pow(2,_math.nextpow2(5000)), preamble = 0, preNum = _math.round(fs * preTime);
		double peakDis = 100*(double)N/(double)fs, startPoint = 166 * peakDis, prepks = 0;
		boolean pre_flag = false;
		double[] step , A, window;
		Mfft ft = new Mfft(N);
		Complex[] Y;
		int counter = 0;
		long timeSum = 0;
		while(!share.isEmpty() || !share.isFinish())
		{
			
			if(firstCheck == 0)
			{
				decodeArea = share.take();
				leftP = 0;
				rightP = stepLen - 1;
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
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + peakDis*4) + 1);
			double[] windowPow2 = _math.sqrArray(window);
			//double pks = _math.max(window);
			double pks = _math.sum(_math.copyByIndex(windowPow2, _math.round(3*peakDis/2), _math.round(5*peakDis/2) - 1));
			//double loc = _math.maxLoc(window);
			double mean = (_math.sum(windowPow2) - pks) / 3;
			double PAPR = pks / mean;
			if (pre_flag)
			{
				if(PAPR >  4 && pks > 1)
				{
					int le = _math.round(5000 * prepks / (prepks + pks)) + 1500;
					int re = _math.round(5000 * pks / (prepks + pks)) + 1350;
					double[] tmp = decodeArea.clone();
					decodeArea = _math.copyByIndex(tmp, leftP - le, tmp.length - 1);
					
				}
				else
				{
					double[] tmp = decodeArea.clone();
					decodeArea = _math.copyByIndex(tmp, leftP - 6500, tmp.length - 1);				
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
			if(firstCheck == 66)
			{
				firstCheck = 66;
			}
			firstCheck++;
			//
			long end = System.currentTimeMillis();
			counter++;
			timeSum += end - start;
			Log.i("time","preamble1:" + Long.toString(end - start));
		}
		Log.i("time","preamble1_average:" + Float.toString((float)timeSum/(float)counter));
		long start = System.currentTimeMillis();
		N = (int) Math.pow(2,_math.nextpow2(preNum));
		ft = new Mfft(N);
		peakDis = 100 *  N/ (double)fs;
		startPoint = 167 * peakDis + peakDis / 2;
		double[] pksSequence = new double[101];
		double[] temp;	
		double energy = 0;	
		for(int i = 0; i < 3000; i += 30)
		{
			while(i + preNum > decodeArea.length)
			{
				tmpReadData = share.take();
				resize(decodeArea.length + tmpReadData.length);
			}
			temp = _math.copyByIndex(decodeArea, i, i + preNum - 1);
			Y = ft.fft(temp);
			A = _math.cAbs(Y);
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + peakDis) - 1);
			energy = _math.sum(_math.sqrArray(window));
			pksSequence[i/30 + 1] = energy;
		}
		int loc = _math.maxLoc(pksSequence);
		preamble =  30 * (loc - 1) + 4850;
		double[] tmp = decodeArea.clone();
		decodeArea = _math.copyByIndex(tmp, preamble - 1, tmp.length - 1);
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
		decodeArea = _math.copyByIndex(tmp, silenceNum + 1, tmp.length - 1);
	}
	private void decode()
	{
		// TODO Auto-generated method stub	
		new Thread(new CalibrationLine(cali, share)).start();
		
		byte[] oneWord = new byte[8];
		int Ns = 100;		
		double symbolTime = 0.22;
		double sigNum_sym = fs * symbolTime;//每个symbol的信号数，采样频率*每个symbol持续时间；
		double[] sigsPerSymbol = new double[_math.round(sigNum_sym) - 441];//一个symbol的信号
		double[] A;
		Complex[] Y;
		byte sig = 0;
		int N = (int) Math.pow(2, _math.nextpow2(_math.round(sigNum_sym) - 441));//一个symbol的采样点数
		Mfft ft = new Mfft(N);
		double PEAKDIS = 100 * (double)N / (double)fs;//一帧的时间  
		double startPoint = 0;
		double[] window = new double[(int)PEAKDIS];	  
		byte[] sins = new byte[Ns * 64];
		Complex[] tmp;
		int counter = 0;
		long timesum = 0;
		for(int i = 0; i < Ns; i++)//解每一个symbol
		{
			while(decodeArea.length < sigsPerSymbol.length + 441)
			{
				tmpReadData = share.take();
				resize(tmpReadData.length + decodeArea.length);
			}
			long start = System.currentTimeMillis();
			startPoint = 201 * PEAKDIS / 2;
			System.arraycopy(decodeArea, 441, sigsPerSymbol, 0, sigsPerSymbol.length);
			double[] cpy = decodeArea.clone();
			decodeArea = _math.copyByIndex(cpy, _math.round(sigNum_sym), cpy.length - 1);
			long m1 = System.currentTimeMillis();
			Y = ft.fft(sigsPerSymbol);
			long m2 = System.currentTimeMillis();
			Log.i("fft",Long.toString(m2 - m1));
			tmp = new Complex[Y.length / 2];
			System.arraycopy(Y, 0, tmp, 0, tmp.length);
			A = _math.cAbs(tmp);
			
			int[] msg = new int[64 / _math.MM];
				
			for(int j = 0, countWord = 0, countBit = 0; j < 64; j++)
			{
				sig = 0;
				System.arraycopy(A, _math.round(startPoint), window, 0, window.length);
				double pks = _math.max(window);
				int loc = _math.maxLoc(window);
				double mean = _math.mean(window);
				double PAPR = pks / mean;
				
				if(loc > PEAKDIS / 3  &&  loc < 2 * PEAKDIS / 3)
				{
					mean = (_math.sum(_math.sqrArray(window)) - Math.pow(pks,2)) / (PEAKDIS - 1);
					PAPR = Math.pow(pks, 2) / mean;
					if(PAPR > 4.7)
					sig = 1;	
				}
				sins[i * 64 + j] = sig;
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
			out += d[i] * Math.pow(2, 7 - i);	
		return out;
	}

}
