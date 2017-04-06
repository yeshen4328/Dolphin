package DecodeThread;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Tao Lei on 2017/3/31.
 */

public class ArrayPool {
    public class ShortData{
        public short[] data;
        public int size;
        public int id = 0;
        public ShortData(short[] data, int l)
        {
            this.data = data;
            size = l;
        }
        public ShortData(int l)
        {
            data = new short[l];
            size = l;
        }

    }
    private int objectNum;
    private int maxObjextNum;
    private int SIZE_OF_SHORT_DATA;
    private AtomicInteger count = new AtomicInteger(0);
    public static short max(ShortData _data){
        short m = 0;
        for(int i = 0; i  < _data.size; i++)
            if(m < _data.data[i])
                m = _data.data[i];
        return m;
    }
    List<ShortData> freeBuffer;
    public ArrayPool(int maxObjextNum, int SIZE_OF_SHORT_DATA)
    {
        this.maxObjextNum = maxObjextNum;
        this.SIZE_OF_SHORT_DATA = SIZE_OF_SHORT_DATA;
        if(freeBuffer == null)
            freeBuffer = new LinkedList<ShortData>();

    }
    public ShortData borrrowObj()
    {

        ShortData data = null;

        if(count.get() == 0)
        {
            Log.i("arraypool","borrow_create!");
            data = new ShortData(SIZE_OF_SHORT_DATA);
           // count.incrementAndGet();
        }
        else
        {
            Log.i("arraypool","borrow_getold!");
            data = freeBuffer.remove(0);
            count.decrementAndGet();
        }

        return data;
    }
    public void returnObj(ShortData data)
    {
        Log.i("arraypool","return!");
        freeBuffer.add(data);
        count.incrementAndGet();
    }
    public void removeAll()
    {
        freeBuffer.clear();
    }


}
