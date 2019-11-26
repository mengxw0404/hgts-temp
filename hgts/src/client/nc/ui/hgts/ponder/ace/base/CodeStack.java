package nc.ui.hgts.ponder.ace.base;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import nc.bs.logging.Logger;
import nc.ui.hgts.pondUI.ace.Hardware.InfraredPortUtil;
import nc.vo.pub.BusinessException;

public class CodeStack implements Observer
{
  private InfraredPortUtil IrDAreader = null;
  private String comName = "COM1";
  private int bytrate = 19200;
  private int databits = 8; // 数据位
  private int stopbits = 1;// 截止位
  private int parity = 0; // 校验位:0 无校验；1：奇校验；2：偶
  private int waitms = 2000;
  private int databufferlength = 16;
  private MeasPanel parents = null;
 
  public CodeStack(MeasPanel measPanel,String comName) 
  { 
	  this.parents = measPanel;
	  this.comName = comName;  
	  init();
  }
  
  public CodeStack(MeasPanel parents ,String comName, int bytrate, int databits, int stopbits, int parity, int waitms)
    throws BusinessException
  {
	this.parents = parents;
    this.comName = comName;
    this.bytrate = bytrate;
    this.databits = databits;
    this.stopbits = stopbits;
    this.parity = parity;
    this.waitms = waitms;
    init();
  }
  
  private void init()
  {
	  try {
		  	this.IrDAreader = new InfraredPortUtil(comName, bytrate, databits, stopbits, parity, waitms, databufferlength); 
		  	this.IrDAreader.initOpen();
      	    this.IrDAreader.addObserver(this);  
	  	}
	  catch (Exception e)
      {
        Logger.error(e.getMessage());
        JOptionPane.showMessageDialog(this.parents, e.getMessage(), "端口参数错误", 0);//essageDialog(this, e.getMessage(), "端口参数错误", 0);
        
        return;
      }
  }


  public InfraredPortUtil getIrDAreader()
  {
    return this.IrDAreader;
  }

  //红外状态变更模板显示
@Override
public void update(Observable o, Object obj) {
	
	if(obj instanceof String){
		if(obj.toString().trim().equals("03E1") ||obj.toString().trim().equals("0FE1") ){
		//双通//从站返回：01 02 01 03 E1 89（DI0、DI1 闭合，其余都断开）
			  //从站返回：01 02 01 0F E1 8C（四路全闭合
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
		this.parents.measdoc.setAttributeValue("isvia", "Y");
	}else if(obj.toString().trim().equals("00A1")){
		//双断 从站返回：01 02 01 00 A1 88（四路全断开）
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else if(obj.toString().trim().equals("0220")){
		//一断//从站返回：01 02 01 02 20 49（DI1 闭合，其余都断开）
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else if(obj.toString().trim().equals("0160")){
		//二断//从站返回：01 02 01 01 60 48（DI0 闭合，其余都断开）
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else{
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("未知");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("未知");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}
//		if(obj.toString().trim().equals("00aa")){
//			//双通
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
//			this.parents.measdoc.setAttributeValue("isvia", "Y");
//		}else if(obj.toString().trim().equals("0355")){
//			//双断
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else if(obj.toString().trim().equals("0155")){
//			//一断
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else if(obj.toString().trim().equals("0255")){
//			//二断
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else{
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("未知");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("未知");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}
		
	}
}
}
