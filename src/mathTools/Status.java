package mathTools;

public class Status {
	public static final int DECODE_FINISH = 0;
	public static final int DISPLAY_MESSAGE = 1;
	public static final int WRITING_FINISH = 2;
	public static final int TOASTSTATUS = 3;
	public static final int LOCAL_REALTIME_DECODE = 4;
	public static final int RECORD_REALTIME_DECODE = 5;
	public static final int RECORD_WRITETOFILE = 6;
	public static final int START_CLEAR = 7;
//	颜色过滤
	public static final float[] BT_SELECTED=new float[] {   
      1, 0, 0, 0, -40,   
      0, 1, 0, 0, -40,   
      0, 0, 1, 0, -40,   
      0, 0, 0, 1, 0 };  
//     恢复颜色
	public static final float[] BT_NOT_SELECTED=new float[] {   
      1, 0, 0, 0, 0,   
      0, 1, 0, 0, 0,   
      0, 0, 1, 0, 0,   
      0, 0, 0, 1, 0 }; 
}
