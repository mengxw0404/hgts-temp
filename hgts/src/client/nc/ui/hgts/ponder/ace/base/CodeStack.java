package nc.ui.hgts.ponder.ace.base;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import nc.bs.logging.Logger;
import nc.ui.hgts.pondUI.ace.Hardware.InfraredPortUtil;
import nc.vo.hgts.bd.cal.CalParaVO;
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

@Override
public void update(Observable o, Object obj) {
	
	if(obj instanceof String){
		
		if(obj.toString().trim().equals("00aa")){
			//双通
			this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
			this.parents.measdoc.setAttributeValue("isvia", "Y");
		}else if(obj.toString().trim().equals("0355")){
			//双断
			this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
			this.parents.measdoc.setAttributeValue("isvia", "N");
		}else if(obj.toString().trim().equals("0155")){
			//一断
			this.parents.getStatusDesginPanel().OneInfraredShow.setText("断");
			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("通");
			this.parents.measdoc.setAttributeValue("isvia", "N");
		}else if(obj.toString().trim().equals("0255")){
			//二断
			this.parents.getStatusDesginPanel().OneInfraredShow.setText("通");
			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("断");
			this.parents.measdoc.setAttributeValue("isvia", "N");
		}else{
			this.parents.getStatusDesginPanel().OneInfraredShow.setText("未知");
			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("未知");
			this.parents.measdoc.setAttributeValue("isvia", "N");
		}
		
	}
}

}
