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
 * ����
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
  private int databits = 8; // ����λ
  private int stopbits = 1;// ��ֹλ
  private int parity = 0; // У��λ:0 ��У�飻1����У�飻2��ż
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
      System.out.println("����ֵ��"+this.comName+"��"+this.bytrate+"��"+this.databits+"��"+this.stopbits+"��"+ this.parity);
      this.inputStream = this.IrDAPort.getInputStream();
      thread =  new InfrareRunClock(this.inputStream);
      thread.start();
    }
    catch (NoSuchPortException e){
    	
    	  throw new BusinessException("û������Ϊ��" + this.comName + "�˿ڣ����顣"); 
    	  
      }
      catch (PortInUseException e) {
      	
    	  throw new BusinessException("�˿�" + this.comName + "��ռ�ã��������������Ƿ�����ʹ�ô˶˿ڡ�"); 
    	  
      }
      catch (UnsupportedCommOperationException e)
      {
        this.IrDAPort.close();
        throw e;
      }
 
    Logger.debug("�ɹ��򿪶˿�" + this.comName);
   
  }
 /* public static void main(String[] args)
 {
	  InfraredPortUtil port = new InfraredPortUtil("COM2", 19200, 8, 1, 0, 2000, 16); 
	  try {
		port.initOpen();
	
	} catch (Exception e) {
		// TODO �Զ����ɵ� catch ��
		e.printStackTrace();
	}
	//  port.addObserver(this);  
		
	}*/
  
  private void readFromBuffer(String comBuffer) { 
	System.out.println(AppContext.getInstance().getServerTime());
	//this.pondervalue = 
	System.out.println("readFromBuffer��ӦComBuffer��"+ComBuffer);
	String strats = explanData(comBuffer);	      
	setChanged();
	notifyObservers(strats);
	
  }
 private String explanData(String comBuffer) {
	 
	    String str = comBuffer.substring(6, 10).trim();
	    System.out.println("���������"+str);
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
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	    Logger.debug("�ɹ��رն˿�" + this.comName);
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
  	//����ѭ������
  	  for (;;)
  	    {
  	      try
  	      {
  	    	//�ж϶˿��Ƿ�ر�
  	        if (this.ComPort == null) {
  	          return;
  	        }else{
  	          int c = this.ComPort.read();
  	      	  if(!String.valueOf(c).equals("-1")){
  	      		  
  	      	 // String beginL = StringUtil.toStringHex("01");
  	      	 // String endL = StringUtil.toStringHex("01");
  	      		  System.out.println(c+"��Ӧ"+Integer.toHexString(c));
  	      		 //ʮ����ת��Ϊʮ������
  	      		  if(Integer.toHexString(c).equals("9")|| beginchar){
  	      			  beginchar = true;
  	      			  String bate = Integer.toHexString(c);
  	      			  if(bate.length()==1){
  	      				bate = "0"+Integer.toHexString(c);
  	      			  }
  	      			  ComBuffer= ComBuffer.concat(bate);
  	      			 System.out.println("��ӦComBuffer"+ComBuffer);
  	  		          if (databufferlength * 2 <= ComBuffer.length()) {
  	  		          	    readFromBuffer(ComBuffer);
  	  		          	System.out.println("��ӦComBuffer2"+ComBuffer);
  	  		          	    ComBuffer="";
  	  		          	    beginchar = false;
  	  		          }
  	      		  }
  	      	  }
  	        }

  		  } catch (IOException e) {
  			  // TODO �Զ����ɵ� catch ��
  			  e.printStackTrace();
  		  }
  	    }
    }

  }
}
