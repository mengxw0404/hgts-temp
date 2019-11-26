package nc.ui.hgts.pondUI.ace.Hardware;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.AppContext;
/**
 * 红外
 * @author Administrator
 *
 */
public class InfraredPortUtil extends Observable
{
  static CommPortIdentifier portId;
  private String comName = "COM1";
  private InputStream inputStream;
  private SerialPort IrDAPort;
  private int bytrate = 9600;
  private int databits = 8; // 数据位
  private int stopbits = 1;// 截止位
  private int parity = 0; // 校验位:0 无校验；1：奇校验；2：偶
  private int waitms = 2000;
  private int databufferlength =16;
  private String ComBuffer = "";
  private InfrareRunClock thread;

  public InfraredPortUtil(String comName, int bytrate, int databits, int stopbits, int parity, int waitms, int databufferlength)
  {
    if (comName != null) {
      this.comName ="COM"+comName;
    }
    this.bytrate = bytrate;
    this.databits = databits;
    this.stopbits = stopbits;
    this.parity = parity;
    this.waitms = waitms;
    this.databufferlength  = databufferlength;
  }
  
  
  public void initOpen()
    throws Exception
  {
    try
    {
      portId = CommPortIdentifier.getPortIdentifier(this.comName);
        
      this.IrDAPort = ((SerialPort)portId.open("SimpleReadApp", this.waitms));
      this.IrDAPort.notifyOnDataAvailable(true);
      this.IrDAPort.setSerialPortParams(this.bytrate, this.databits, this.stopbits, this.parity);
      System.out.println("参数值："+this.comName+"，"+this.bytrate+"，"+this.databits+"，"+this.stopbits+"，"+ this.parity);
      this.inputStream = this.IrDAPort.getInputStream();
      thread =  new InfrareRunClock(this.inputStream);
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
        this.IrDAPort.close();
        throw e;
      }
 
    Logger.debug("成功打开端口" + this.comName);
   
  }
 /* public static void main(String[] args)
 {
	  InfraredPortUtil port = new InfraredPortUtil("COM2", 19200, 8, 1, 0, 2000, 16); 
	  try {
		port.initOpen();
	
	} catch (Exception e) {
		// TODO 自动生成的 catch 块
		e.printStackTrace();
	}
	//  port.addObserver(this);  
		
	}*/
  
  private void readFromBuffer(String comBuffer) { 
	System.out.println(AppContext.getInstance().getServerTime());
	//this.pondervalue = 
	System.out.println("readFromBuffer对应ComBuffer："+ComBuffer);
	String strats = explanData(comBuffer);	      
	setChanged();
	notifyObservers(strats);
	
  }
 private String explanData(String comBuffer) {
	 
	    String str = comBuffer.substring(6, 10).trim();
	    System.out.println("红外读数："+str);
	 /* String beginL = StringUtil.toStringHex("09");
	    String endL = StringUtil.toStringHex("0D");  
	    int beginIndex = comBuffer.indexOf(beginL);
	    int endIndex = 0;
	    try
	    {
	      endIndex = comBuffer.indexOf(endL, beginIndex + 1);
	    }
	    catch (IndexOutOfBoundsException e)
	    {
	      endIndex = comBuffer.indexOf(endL, beginIndex);
	    }
	    comBuffer = comBuffer.substring(beginIndex, endIndex);
	    comBuffer.substring(4, 6).trim();*/
	 return str; 
}

  public void closeport()
  {
	    try
	    {
	    if (this.IrDAPort != null) {
	    	if(this.thread != null){
	    		this.thread.ComPort.close();
	    		this.thread.setComPort(null);
	    	}
	    	IrDAPort.notifyOnDataAvailable(false);
	        if (this.inputStream != null) {
	         this.inputStream.close();
	        } 
	        this.IrDAPort.close();   
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
  
  class InfrareRunClock extends Thread
  {
  	public boolean beginchar= false;  
    private InputStream ComPort;
    
    public InfrareRunClock(InputStream Port) 
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
  	    	//判断端口是否关闭
  	        if (this.ComPort == null) {
  	          return;
  	        }else{
  	          int c = this.ComPort.read();
  	      	  if(!String.valueOf(c).equals("-1")){
  	      		  
  	      	 // String beginL = StringUtil.toStringHex("01");
  	      	 // String endL = StringUtil.toStringHex("01");
  	      		  System.out.println(c+"对应"+Integer.toHexString(c));
  	      		 //十进制转换为十六进制
  	      		  if(Integer.toHexString(c).equals("9")|| beginchar){
  	      			  beginchar = true;
  	      			  String bate = Integer.toHexString(c);
  	      			  if(bate.length()==1){
  	      				bate = "0"+Integer.toHexString(c);
  	      			  }
  	      			  ComBuffer= ComBuffer.concat(bate);
  	      			 System.out.println("对应ComBuffer"+ComBuffer);
  	  		          if (databufferlength * 2 <= ComBuffer.length()) {
  	  		          	    readFromBuffer(ComBuffer);
  	  		          	System.out.println("对应ComBuffer2"+ComBuffer);
  	  		          	    ComBuffer="";
  	  		          	    beginchar = false;
  	  		          }
  	      		  }
  	      	  }
  	        }

  		  } catch (IOException e) {
  			  // TODO 自动生成的 catch 块
  			  e.printStackTrace();
  		  }
  	    }
    }

  }
}
