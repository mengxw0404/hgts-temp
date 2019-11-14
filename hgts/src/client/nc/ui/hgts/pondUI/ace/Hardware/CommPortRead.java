package nc.ui.hgts.pondUI.ace.Hardware;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Observable;

import nc.bs.logging.Logger;
import nc.ui.pub.ClientEnvironment;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

public class CommPortRead extends Observable  implements SerialPortEventListener
{
  private static CommPortIdentifier portId;
  private String comName = "COM1";
  private InputStream inputStream;
  private OutputStream outputStream;
  private RXTXPort RXTXPort;
  private int bytrate = 4800;
  private int databits = 8;
  private int stopbits = 1;
  private int parity = 0;
  private int waitms = 2000;
  private StringBuffer data = new StringBuffer();
  private int readlength = -1;
  private int efclength = -1;
  private boolean isasc = false;
  private String datatype = null;
  private String beginLabel = null;
  private String endLable = null;
  private int begindata;
  private int point;
  private String def1 = "1";
  private int meas;
  UFDouble pondervalue = UFDouble.ZERO_DBL;
  public SerialBuffer serialBuffer;
  public ReadSerial readSerial;
  
  private String ComBuffer = "";
  private RunClock thread;
  
  public RXTXPort getRXTXPort()
  {
    return this.RXTXPort;
  }
  
  public void setRXTXPort(RXTXPort RXTXPort)
  {
    this.RXTXPort = RXTXPort;
  }
  
  public static void main(String[] args)
    throws IOException, InterruptedException
  {/*
	  int c = 1;
	  String dataStr = String.valueOf(c);
	  Character d2 = Character.valueOf((char)c);
	  Character.valueOf((char)c).toString();
    CommPortRead reader = new CommPortRead("COM2", 1200, 7, 1, 0, 1000, 12, 5, true, "ASCII", "02", "03", 2, 8, 0, "1");
    try
    {
      reader.initPortReadOther();
      for (int i = 0; i < 1000; i++)
      {
        Thread.sleep(100L);
        UFDouble d = reader.readData();
        Logger.debug("获取值2：" + d);
      }
      reader.closeport();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  */}
  
  public CommPortRead(String comName, int bytrate, int databits, int stopbits, int parity, int waitms, int databufferlength)
  {
    if (comName != null) {
      this.comName = comName;
    }
    this.bytrate = bytrate;
    this.databits = databits;
    this.stopbits = stopbits;
    this.parity = parity;
    this.waitms = waitms;
  }
  
  public CommPortRead(String comName, int bytrate, int databits, int stopbits, int parity, int waitms)
  {
    if (comName != null) {
      this.comName = comName;
    }
    this.bytrate = bytrate;
    this.databits = databits;
    this.stopbits = stopbits;
    this.parity = parity;
    this.waitms = waitms;
  }
  
  public CommPortRead(String comName, int bytrate, int databits, int stopbits, int parity, int waitms, int readlength, int efclength, boolean isasc, String datatype, String beginLabel, String endLable, int begindata, int point, int meas, String def1)
  {
    this(comName, bytrate, databits, stopbits, parity, waitms, readlength * 2);
    this.readlength = readlength;
    this.efclength = efclength;
    this.isasc = isasc;
    this.datatype = datatype;
    this.beginLabel = beginLabel;
    this.endLable = endLable;
    this.begindata = begindata;
    this.point = point;
    this.meas = meas;
    this.def1 = def1;
    Logger.debug("初始化端口参数：" + comName + " " + bytrate + " " + databits + " " + stopbits + " " + parity + " " + waitms + " " + readlength + " " + efclength + " " + isasc + " " + datatype + " " + beginLabel + " " + endLable + " " + begindata + " " + point + " " + meas + " ");
  }
  
  public void initPortRead()
    throws Exception
  {
    try
    {
      portId = CommPortIdentifier.getPortIdentifier(this.comName);
      
      this.RXTXPort = ((RXTXPort)portId.open("CommPortReadApp", this.waitms));
      
      this.RXTXPort.setInputBufferSize(this.readlength * 3);
      
      this.RXTXPort.notifyOnDataAvailable(true);
      
      this.RXTXPort.setSerialPortParams(this.bytrate, this.databits, this.stopbits, this.parity);
      
      this.inputStream = this.RXTXPort.getInputStream();
      this.outputStream = this.RXTXPort.getOutputStream();
      
      /*this.RXTXPort.addEventListener(this);
      this.serialBuffer = new SerialBuffer();
      this.readSerial = new ReadSerial(this.serialBuffer, this.inputStream);
      this.readSerial.start();*/
      thread =  new RunClock(this.inputStream);
      thread.start();
    }
    catch (NoSuchPortException e){
    	
  	  throw new BusinessException("没有名字为的" + this.comName + "端口，请检查。"); 
  	  
    }
    catch (PortInUseException e) {
    	
  	  throw new BusinessException("端口" + this.comName + "被占用，请检查其他程序是否正在使用此端口。"); 
  	  
    }
    catch (UnsupportedCommOperationException e)
    {
      this.RXTXPort.close();
      throw e;
    }
 
    Logger.debug("成功打开端口" + this.comName);
  }
  
  public void initPortReadOther222()  throws Exception
  {/*
	try
	 {
		portId = CommPortIdentifier.getPortIdentifier(this.comName);
		      
		this.RXTXPort = (gnu.io.RXTXPort) (portId.open("CommPortReadApp", this.waitms));
		this.RXTXPort.notifyOnDataAvailable(true);
		      
		this.RXTXPort.setSerialPortParams(this.bytrate, this.databits, this.stopbits, this.parity);
		this.RXTXPort.setInputBufferSize(this.readlength * 3);
	      
	      this.RXTXPort.notifyOnDataAvailable(true);
	      
	      this.RXTXPort.setSerialPortParams(this.bytrate, this.databits, this.stopbits, this.parity);
	      
	      this.inputStream = this.RXTXPort.getInputStream();
	      this.outputStream = this.RXTXPort.getOutputStream();
	      
	      RunClock thread =  new RunClock(this.inputStream);
	      thread.start();
	}
	catch (NoSuchPortException e)
	{
		throw new BusinessException("没有名字为的" + this.comName + "端口，请检查。");
	}
	catch (PortInUseException e)
	{
		throw new BusinessException("端口" + this.comName + "被占用，请检查其他程序是否正在使用此端口。");
	}
	catch (UnsupportedCommOperationException e)
	{
		this.RXTXPort.close();
		throw e;
	}
	Logger.debug("成功打开端口" + this.comName);
  */}
		   
 
  private void readFromBuffer2(String str)  throws IOException
  {	   
	 System.out.println(AppContext.getInstance().getServerTime());
	 this.pondervalue = explanData(str);
		      
	 setChanged();
	 notifyObservers(this.pondervalue);
  }

public void closeport()
  {
    try
    {
    if (this.RXTXPort != null) {
    	/*if(this.readSerial != null && this.readSerial.getComPort()!=null){
    		//关闭线程读数
    		this.readSerial.getComPort().close();
        	this.readSerial.setComPort(null);
    	}*/
    	if(this.thread != null){
    		this.thread.ComPort.close();
    		this.thread.setComPort(null);
    	}
       RXTXPort.notifyOnDataAvailable(false);
     //RXTXPort.removeEventListener();
       if (this.inputStream != null) {
         this.inputStream.close();
       } 
       if (this.outputStream != null) {
         this.outputStream.close();
       }
        this.RXTXPort.close();   
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    } catch (Exception e) {
		// TODO 自动生成的 catch 块
		e.printStackTrace();
	}
    Logger.debug("成功关闭端口" + this.comName);
  }
  
  public void serialEvent(SerialPortEvent event)
  {
		//  System.out.println("信号"+ClientEnvironment.getInstance().getServerTime()+"---"+event.getEventType());
	    switch (event.getEventType())
	    {
		    case SerialPortEvent.BI:/*Break interrupt,通讯中断*/ 
		    case SerialPortEvent.OE:/*Overrun error，溢位错误*/ 
		    case SerialPortEvent.FE:/*Framing error，传帧错误*/ 
		    case SerialPortEvent.PE:/*Parity error，校验错误*/ 
		    case SerialPortEvent.CD:/*Carrier detect，载波检测*/ 
		    case SerialPortEvent.CTS:/*Clear to send，清除发送*/ 
		    case SerialPortEvent.DSR:/*Data set ready，数据设备就绪*/ 
		    case SerialPortEvent.RI:/*Ring indicator，响铃指示*/ 
		    case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*Output buffer is empty，输出缓冲区清空*/ 
	    	            break; 
		    case SerialPortEvent.DATA_AVAILABLE:/*Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端*/
		    	try
		    	{	
		    		if(serialBuffer.isAvailable())
		    			System.out.println("信号"+ClientEnvironment.getInstance().getServerTime()+"---"+event.getEventType());
		    			readFromBuffer();		
		    	}
		    	catch (IOException e)
		    	{
		    		Logger.error("向端口缓存写数据出异常！", e);
		    	}
	    	}
	    }
  
  public void write(byte[] data)
    throws IOException
  {
    this.outputStream.write(data);
    
  }
  
  private void readFromBuffer()
    throws IOException
  {
    String data = this.serialBuffer.getMsg(this.readlength * 2);
    if (data.length() == this.readlength * 2)
    {
      this.pondervalue = explanData(data.toString());
      
      setChanged();
      notifyObservers(this.pondervalue);
    }
  }
  
 /* private int readIntoBuffer()  throws IOException
  {
    int avb = 0;
    while ((avb = this.inputStream.available()) > 0)
    {
      byte[] b = new byte[avb];
      this.inputStream.read(b);
      String x = new String(b);
      
      this.data.append(x);
      if (this.data.length() > this.readlength * 2) {
        this.data.delete(0, x.length());
      }
      if (this.data.length() == this.readlength * 2)
      {
        this.pondervalue = explanData(this.data.toString());
        
        setChanged();
        notifyObservers(this.pondervalue);
      }
    }
    return this.data.length();
  }*/
  
  private String getBCDstrFromBuffer()
  {
    return BCDtoASCII(this.data.toString().getBytes());
  }
  
  private String getASCIIstrFromBuffer()
  {
    return this.data.toString();
  }
  
  /**
   * @deprecated
   */
  public String readData(int beginchar, int efclength, boolean isasc, String datatype)
  {
    String rets = "";
    if ("ASCII".equals(datatype)) {
      rets = getASCIIstrFromBuffer();
    } else if ("BCD".equals(datatype)) {
      rets = getBCDstrFromBuffer();
    }
    if (!isasc) {
      rets = reverseStr(rets);
    }
    if (rets.trim().equals("")) {
      return "";
    }
    int beginIndex = rets.indexOf((char)beginchar);
    if (beginIndex == -1) {
      return "";
    }
    rets = rets.substring(beginIndex + 1, beginIndex + efclength + 1);
    return rets;
  }
  
  /**
   * @deprecated
   */
  public UFDouble readData(String beginLabel, String endLable, int efclength, boolean isasc, String datatype, int begindata, int point, int meas, int destroy)
  {
    String rets = "";
    if ("ASCII".equals(datatype)) {
      rets = getASCIIstrFromBuffer();
    } else if ("BCD".equals(datatype)) {
      rets = getBCDstrFromBuffer();
    }
    if (!isasc) {
      rets = reverseStr(rets);
    }
    if (rets.trim().equals("")) {
      return new UFDouble(0.0D);
    }
    int beginIndex = rets.indexOf(StringUtil.toStringHex(beginLabel));
    int endIndex = rets.indexOf(StringUtil.toStringHex(endLable), beginIndex);
    if ((beginIndex == -1) || (endIndex == -1)) {
      return new UFDouble(0.0D);
    }
    rets = rets.substring(beginIndex, endIndex + 1);
    System.out.print("RES:" + rets);
    UFDouble res = new UFDouble(rets.substring(begindata, begindata + efclength + 1));
    System.out.print("数字:" + res);
    String pointData = "" + rets.charAt(point);
    UFDouble dpoint = new UFDouble(pointData);
    
    res = res.div(Math.pow(10.0D, dpoint.doubleValue()));
    if (meas != 2) {
      if (meas == 1) {
        res = res.div(1000.0D);
      } else if (meas == 3) {
        res = res.multiply(1000.0D);
      }
    }
    return res;
  }
  
  public UFDouble readData()
  {
    return this.pondervalue;
  }
  
  /**
   * @deprecated
   */
  public UFDouble readData(String beginLabel, String endLable, int efclength, boolean isasc, String datatype, int begindata, int point, int meas)
  {
    String rets = "";
    if ("ASCII".equals(datatype)) {
      rets = getASCIIstrFromBuffer();
    } else if ("BCD".equals(datatype)) {
      rets = getBCDstrFromBuffer();
    }
    return explanData(rets.toString());
  }
  
  private UFDouble explanData(String dataStr)
  {

    String beginL = StringUtil.toStringHex(this.beginLabel);
    String endL = StringUtil.toStringHex(this.endLable);
    if (!"ASCII".equals(this.datatype)) {
      if ("BCD".equals(this.datatype)) {
        dataStr = getBCDstrFromBuffer();
      }
    }
    if (!this.isasc) {
      dataStr = reverseStr(dataStr);
    }
    if (dataStr.trim().equals("")) {
      return new UFDouble(0.0D);
    }
    int beginIndex = dataStr.indexOf(beginL);
    
    int endIndex = 0;
    try
    {
      endIndex = dataStr.indexOf(endL, beginIndex + 1);
    }
    catch (IndexOutOfBoundsException e)
    {
      endIndex = dataStr.indexOf(endL, beginIndex);
    }
    if ((beginIndex == -1) || (endIndex == -1)) {
      return new UFDouble(0.0D);
    }
    dataStr = dataStr.substring(beginIndex, endIndex);
	System.out.println(dataStr+"-长度-"+dataStr.length());
    if (dataStr.length() != this.readlength)
    {
        beginIndex = dataStr.indexOf(beginL, endIndex);
        try
        {
          endIndex = dataStr.indexOf(endL, beginIndex + 1);
        }
        catch (IndexOutOfBoundsException e)
        {
          endIndex = dataStr.indexOf(endL, beginIndex);
        }
        if ((beginIndex == -1) || (endIndex == -1)) {
          return new UFDouble(0.0D);
        }
        dataStr = dataStr.substring(beginIndex, endIndex + 1);
        System.out.print(dataStr+"-222-"+this.readlength);
        if (dataStr.length() != this.readlength) {
          return UFDouble.ZERO_DBL;
        }
      }
   // String a= ;  
   	UFDouble res = new UFDouble(dataStr.substring(this.begindata, this.begindata + this.efclength).trim());
   	
    String flag = dataStr.substring(1, 2);
    try
    {
      flag = dataStr.substring(Integer.parseInt(this.def1), Integer.parseInt(this.def1) + 1);
    }
    catch (ClassCastException e) {}
    if ("-".equals(flag)) {
      res = UFDouble.ZERO_DBL.sub(res);
    }
    /*if (this.point != 99)
    {
      String pointData = ""+dataStr.charAt(this.point);
      
      UFDouble dpoint = new UFDouble(pointData);
      res = res.div(Math.pow(10.0D, dpoint.doubleValue()));
    }*/
    if (this.meas != 2) {
      if (this.meas == 1) {
        res = res.div(1000.0D);
      } else if (this.meas == 3) {
        res = res.multiply(1000.0D);
      }
    }
    return res;
  }
  
  public StringBuffer getDatabuffer()
  {
    return this.data;
  }
  
  private static String BCDtoASCII(byte[] bytes)
  {
    byte[] ascByte = bcdtoAsciiByte(bytes);
    return asciitoStr(ascByte);
  }
  
  private static byte[] bcdtoAsciiByte(byte[] BCDbytes)
  {
    byte[] ascBytes = new byte[BCDbytes.length * 2];
    for (int i = 0; i < BCDbytes.length; i++)
    {
      ascBytes[(i * 2)] = ((byte)(BCDbytes[i] >> 4 & 0xF));
      ascBytes[(i * 2 + 1)] = ((byte)(BCDbytes[i] & 0xF));
    }
    return ascBytes;
  }
  
  private static String asciitoStr(byte[] asciiByte)
  {
    char[] BTOA = "0123456789abcdef".toCharArray();
    StringBuffer rets = new StringBuffer();
    for (int i = 0; i < asciiByte.length; i++)
    {
      if ((asciiByte[i] < 0) || (asciiByte[i] > 15)) {
        throw new InvalidParameterException();
      }
      char temp = BTOA[asciiByte[i]];
      rets.append(temp);
    }
    return rets.toString();
  }
  
  private static String reverseStr(String str)
  {
    StringBuffer sb = new StringBuffer(str);
    return sb.reverse().toString();
  }
  
  /*private static byte[] hexStringToByte(String hex)
  {
    int len = hex.length() / 2;
    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++)
    {
      int pos = i * 2;
      result[i] = ((byte)(toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)])));
    }
    return result;
  }
  private static byte toByte(char c)
  {
    byte b = (byte)"0123456789ABCDEF".indexOf(c);
    return b;
  }*/

  class RunClock extends Thread
{
	  
  private InputStream ComPort;
  public RunClock(InputStream Port) 
  {
	    this.ComPort = Port;  
  }
  public void setComPort(InputStream comPort) {
	ComPort = comPort;
}

public void run()
  {
	//无限循环读数
	  for (;;)
	    {
	      try
	      {
	        if (this.ComPort == null) {
	          return;
	        }else{
	          int c = this.ComPort.read();
	      	  if(!String.valueOf(c).equals("-1")){
	      		 Character d = Character.valueOf((char)c);
		          ComBuffer = ComBuffer.concat(d.toString());
		          if (readlength * 2 <= ComBuffer.length()) {
		          	    readFromBuffer2(ComBuffer);
		          	    ComBuffer="";
		          }
	      	  }
	        //    notifyAll();
	        }

		  } catch (IOException e) {
			  // TODO 自动生成的 catch 块
			  e.printStackTrace();
		  }
	    }
  }
}
	
}
