package mathTools;

public class Status {
	public static final int DECODE_FINISH = 0;
	public static final int DISPLAY_MESSAGE = 1;
	public static final int WRITING_FINISH = 2;
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
