package nc.ui.hgts.ponder.ace.base;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public abstract interface VideoPlay
  extends StdCallLibrary
{
  public static final VideoPlay INSTANCE = (VideoPlay)Native.loadLibrary("dhnetsdk", VideoPlay.class);
  
  public abstract boolean a();
  
  public abstract void display();
  
  public abstract boolean CLIENT_Logout(NativeLong paramNativeLong);
  
  public abstract int CLIENT_Login(String paramString1, int paramInt1, String paramString2, String paramString3, NET_DVR_DEVICEINFO paramNET_DVR_DEVICEINFO, int paramInt2);
  
  public abstract NativeLong CLIENT_RealPlayEx(NativeLong paramNativeLong, int paramInt, W32API.HWND paramHWND, _RealPlayType param_RealPlayType);
  
  public abstract NativeLong CLIENT_RealPlay(NativeLong paramNativeLong, int paramInt, W32API.HWND paramHWND);
  
  public abstract boolean CLIENT_StopRealPlay(NativeLong paramNativeLong);
  
  public abstract boolean CLIENT_SetRealDataCallBack(NativeLong paramNativeLong);
  
  public abstract boolean CLIENT_Init(fDisConnect paramfDisConnect, VideoPlay paramVideoPlay);
  
  public abstract boolean CLIENT_CapturePicture(NativeLong paramNativeLong, String paramString);
  
  public abstract void CLIENT_Cleanup();
  
  public static class NET_DVR_DEVICEINFO
    extends Structure
  {
    public byte[] sSerialNumber = new byte[48];
    public byte byAlarmInPortNum;
    public byte byAlarmOutPortNum;
    public byte byDiskNum;
    public byte byDVRType;
    public byte byChanNum;
  }
  
  public static enum _RealPlayType
  {
    DH_RType_Realplay,  DH_RType_Multiplay,  DH_RType_Realplay_0,  DH_RType_Realplay_1,  DH_RType_Realplay_2,  DH_RType_Realplay_3,  DH_RType_Multiplay_1,  DH_RType_Multiplay_4,  DH_RType_Multiplay_8,  DH_RType_Multiplay_9,  DH_RType_Multiplay_16,  DH_RType_Multiplay_6,  DH_RType_Multiplay_12;
  }
  
  public static abstract interface fDisConnect
    extends StdCallLibrary.StdCallCallback
  {
    public abstract void invoke(NativeLong paramNativeLong1, String paramString, NativeLong paramNativeLong2, NativeLong paramNativeLong3);
  }
  
  public static abstract interface fHaveReConnect
    extends StdCallLibrary.StdCallCallback
  {
    public abstract void invoke(NativeLong paramNativeLong1, String paramString, NativeLong paramNativeLong2, NativeLong paramNativeLong3);
  }
}
