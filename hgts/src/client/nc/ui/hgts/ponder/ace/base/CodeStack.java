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
  private int databits = 8; // ����λ
  private int stopbits = 1;// ��ֹλ
  private int parity = 0; // У��λ:0 ��У�飻1����У�飻2��ż
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
        JOptionPane.showMessageDialog(this.parents, e.getMessage(), "�˿ڲ�������", 0);//essageDialog(this, e.getMessage(), "�˿ڲ�������", 0);
        
        return;
      }
  }


  public InfraredPortUtil getIrDAreader()
  {
    return this.IrDAreader;
  }

  //����״̬���ģ����ʾ
@Override
public void update(Observable o, Object obj) {
	
	if(obj instanceof String){
		if(obj.toString().trim().equals("03E1") ||obj.toString().trim().equals("0FE1") ){
		//˫ͨ//��վ���أ�01 02 01 03 E1 89��DI0��DI1 �պϣ����඼�Ͽ���
			  //��վ���أ�01 02 01 0F E1 8C����·ȫ�պ�
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("ͨ");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("ͨ");
		this.parents.measdoc.setAttributeValue("isvia", "Y");
	}else if(obj.toString().trim().equals("00A1")){
		//˫�� ��վ���أ�01 02 01 00 A1 88����·ȫ�Ͽ���
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("��");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("��");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else if(obj.toString().trim().equals("0220")){
		//һ��//��վ���أ�01 02 01 02 20 49��DI1 �պϣ����඼�Ͽ���
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("��");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("ͨ");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else if(obj.toString().trim().equals("0160")){
		//����//��վ���أ�01 02 01 01 60 48��DI0 �պϣ����඼�Ͽ���
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("ͨ");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("��");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}else{
		this.parents.getStatusDesginPanel().OneInfraredShow.setText("δ֪");
		this.parents.getStatusDesginPanel().TwoInfraredShow.setText("δ֪");
		this.parents.measdoc.setAttributeValue("isvia", "N");
	}
//		if(obj.toString().trim().equals("00aa")){
//			//˫ͨ
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("ͨ");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("ͨ");
//			this.parents.measdoc.setAttributeValue("isvia", "Y");
//		}else if(obj.toString().trim().equals("0355")){
//			//˫��
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("��");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("��");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else if(obj.toString().trim().equals("0155")){
//			//һ��
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("��");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("ͨ");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else if(obj.toString().trim().equals("0255")){
//			//����
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("ͨ");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("��");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}else{
//			this.parents.getStatusDesginPanel().OneInfraredShow.setText("δ֪");
//			this.parents.getStatusDesginPanel().TwoInfraredShow.setText("δ֪");
//			this.parents.measdoc.setAttributeValue("isvia", "N");
//		}
		
	}
}
}
