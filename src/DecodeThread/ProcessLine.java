package DecodeThread;

import mathTools._math;

/**
 * Created by Tao Lei on 2016/11/26.
 */

public class ProcessLine {
    byte[] tmpReadData = null;
    byte[] decodeArea = null;
    public void resize(int newCpacity)
    {
        byte[] newArea = new byte[newCpacity];
        transfer(newArea);
        decodeArea = newArea;
    }
    public void transfer(byte[] newArea)
    {
        int dataCapacity = tmpReadData.length;
        int oldCapacity = decodeArea.length;
        System.arraycopy(decodeArea, 0, newArea, 0, oldCapacity);
        System.arraycopy(tmpReadData, 0, newArea, oldCapacity, dataCapacity);
    }
    public int bin2Byte(byte[] d)
    {
        int out = 0;
        for(int i = 0; i <  _math.MM; i++)
            out += d[i] * Math.pow(2, _math.MM - 1 - i);
        return out;
    }
}
