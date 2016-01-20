package audiorecord;

import io.CustomStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import mathTools.Complex;
import mathTools.Status;
import mathTools._math;
import mathTools.Mfft;
import DecodeThread.SharedData;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class AudioProcess implements Runnable{
	Complex[] audio;
	double[]	rawAudio;
	int FS = 44100;
	double preTime = 0.11;
	SharedData share = null;
	public AudioProcess(double[] audio, SharedData share) {
		// TODO Auto-generated constructor stub
		//this.audio =  FFT.fft(audio);	
		this.share = share;
		this.rawAudio  = audio;
		
	}
	private int preambleLocalization()
	{
		int N = (int) Math.pow(2,_math.nextpow2(5000)), preamble = 0, preLeft = 0, preNum = _math.round(FS * preTime);
		double peakDis = 100*(double)N/(double)FS, startPoint = 166 * peakDis, prepks = 0;
		boolean pre_flag = false;
		double[] step , preambleRegion = null, A, window;
		Complex[] Y;
		Mfft ft = new Mfft(N);
		for(int i = 0; i < rawAudio.length; i += 5000)
		{
			step = _math.copyByIndex(rawAudio, i, i + 4999);		
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
					preambleRegion = _math.copyByIndex(rawAudio, i - le, i + re);
					preLeft = i - le;
				}
				else
				{
					preambleRegion = _math.copyByIndex(rawAudio, i - 6500, i + 1350);
					preLeft = i - 6500;
				}
				break;
			}
			if(PAPR >  4 && pks > 1)
			{
				pre_flag = true;
				prepks = pks;
			}
			
		}
		N = (int) Math.pow(2,_math.nextpow2(preNum));
		peakDis=100 *  N/ (double)FS;
		startPoint = 167*peakDis + peakDis / 2;
		double[] pksSequence = new double[101];
		double[] temp;	
		double energy = 0;
		ft = new Mfft(N);
		for(int i = 0; i < 3000; i +=30)
		{
			temp = _math.copyByIndex(preambleRegion, i, i + preNum - 1);
			Y = ft.fft(temp);
			A = _math.cAbs(Y);
			window = _math.copyByIndex(A, _math.round(startPoint), _math.round(startPoint + peakDis) - 1);
			energy = _math.sum(_math.sqrArray(window));
			pksSequence[i/30 + 1] = energy;
		}
		int loc = _math.maxLoc(pksSequence);
		preamble = preLeft + 30 * (loc - 1) + 4850;
		return preamble - 1 ;
	}

	private void dataDecode(){
		int paral = 64, Ns = 100;
		double symbolTime = 0.22;
		double silentime = 0.11;//premble之后静默时间
		double silennum = _math.round(FS * silentime);//静默信号个数
		double sigNum_sym = FS * symbolTime;//每个symbol的信号数，采样频率*每个symbol持续时间；
		double sigNum_frame = sigNum_sym * Ns;//一帧的信号数，长度为所有100个symbol的信号数总和
		int preamble = preambleLocalization();//提取第一帧位置
		Log.i("msg","start at" + Integer.toString(preamble));
		double[] sigs_frame = new double[_math.round(sigNum_frame)];//一帧的信号
		double[] sigs_symbol = new double[_math.round(sigNum_sym) - 441];//一个symbol的信号
		double[] A;
		Complex[] Y;
		//System.arraycopy(rawAudio, preamble +(int) silennum + 1, sigs_frame, 0, _math.round(sigNum_frame));//从原始数据中将第一帧信号数提取到数组中
		sigs_frame = _math.copyByIndex(rawAudio, (int)(preamble + silennum + 1), (int)(preamble + silennum + sigNum_frame));
		int N = 0;
		byte sig = 0;
		N = (int) Math.pow(2, _math.nextpow2(_math.round(sigNum_sym) - 441));//一个symbol的采样点数
		Mfft ft = new Mfft(N);
		double PEAKDIS = 100 * (double)N / (double)FS;//一帧的时间  
		double startPoint;
		double[] window = new double[(int)PEAKDIS];	  
		byte[] sins = new byte[Ns * 64];
		Complex[] tmp;
		for(int i = 0; i < Ns; i++)//解每一个symbol
		{
			startPoint = 201 * PEAKDIS / 2;
			System.arraycopy(sigs_frame, i * _math.round(sigNum_sym) + 441, sigs_symbol, 0, sigs_symbol.length);			
			Y = ft.fft(sigs_symbol);
			tmp = new Complex[Y.length / 2];
			System.arraycopy(Y, 0, tmp, 0, tmp.length);
			A = _math.cAbs(tmp);		
			for(int j = 0; j < 64; j++)
			{
				sig = 0;
				System.arraycopy(A, _math.round(startPoint), window, 0, window.length);
				double pks = _math.max(window);
				int loc = _math.maxLoc(window);
				double mean = _math.mean(window);
				double PAPR = pks / mean;
				
				if(loc > PEAKDIS / 3  &&  loc < 2 * PEAKDIS / 3){
					mean = (_math.sum(_math.sqrArray(window)) - Math.pow(pks,2)) / (PEAKDIS - 1);
					PAPR = Math.pow(pks, 2) / mean;
					if(PAPR > 8)
					sig = 1;	
				}
				sins[i * 64 + j] = sig;
				startPoint += PEAKDIS;
			}
		}
		try {
			FileWriter fout = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/result.txt");
			BufferedWriter fb = new BufferedWriter(fout);
			CustomStream.writeDoubleIntoTxt(sins, fb);
			fb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		dataDecode();
		share.setStatus(Status.DECODE_FINISH);
	}
	
}
