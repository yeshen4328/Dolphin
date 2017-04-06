package DecodeThread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Tao Lei on 2017/3/31.
 */

public class LinkedBlockingList {
    class Item{
        Item next;
        short value;
        public Item(short v){this.value = v;}
    }
    private AtomicInteger count;
    Item header, tail;
    ReentrantLock putLock, takeLock;
    Condition notEmpty;
    public LinkedBlockingList()
    {
        header = tail = new Item((short)0);
        putLock = new ReentrantLock();
        takeLock = new ReentrantLock();
        notEmpty = takeLock.newCondition();
        count = new AtomicInteger(0);
    }
    public void put(short s) throws InterruptedException
    {
    	ReentrantLock putLock = this.putLock;
    	AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        //enqueue operation
        Item item = new Item(s);
        item.next = null;
        tail.next = item;
        tail = item;
        int c = count.getAndIncrement();
        putLock.unlock();
        if(c == 0) {
        	ReentrantLock takeLock = this.takeLock;
        	takeLock.lockInterruptibly();
            notEmpty.signal();
            takeLock.unlock();
        }

    }
    public short take() throws InterruptedException
    {
        short v = 0;
        AtomicInteger count =this.count;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        while(count.get() == 0)
           notEmpty.await();
        Item item = header.next;
        header.next = header;
        header = item;
        v = item.value;
        header.value = 0;
        count.getAndDecrement();
        takeLock.unlock();
        return v;
    }
    public boolean isEmpty()
    {
        return count.get() == 0;
    }

}
