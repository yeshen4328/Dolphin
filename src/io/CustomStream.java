package io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

public class CustomStream {

	public CustomStream() {
		// TODO Auto-generated constructor stub
	}
	public static double[] ReadDoubleFromStringTxt(String path) throws FileNotFoundException{
		double[] result = null;
		try {
				FileReader reader = new FileReader(path);			
				BufferedReader br = new BufferedReader(reader);
		    	String data = br.readLine();
		    	String[] array = data.split(" ");
		    	result = new double[array.length];
		    	for(int i = 0; i < array.length; i++)
		    		result[i] = Double.parseDouble(array[i]);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return result;
	}

	public static void writeDoubleIntoTxt(byte[] data, BufferedWriter buffer){	
		for(int i = 0; i < data.length; i++)
			try {
					buffer.write(Byte.toString(data[i]) + " ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Log.i("msg","completed");
	}
	@SuppressWarnings("resource")
	public static double[] readRawDataAndTrans2Double(String path){
		double[] result = null;
		DataInputStream dis = null;
		try {
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
				int size = dis.available();
				result = new double[size];
				for(int i = 0; i < size; i++)
					result[i] = (double)dis.readShort() / 32768.0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
