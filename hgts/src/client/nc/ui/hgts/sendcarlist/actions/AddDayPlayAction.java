package nc.ui.hgts.sendcarlist.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.busibean.ISysInitQry;
import nc.ui.hgts.ff.pub.AddRefAction;
import nc.vo.pub.BusinessException;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.AppContext;


/**
 * �����ռƻ����˵�
 */
public class AddDayPlayAction extends AddRefAction {
	private static final long serialVersionUID = 3286430955995336466L;

	@Override
	protected String getCurrBilltype() {
		// TODO �Զ����ɵķ������
		return "YPCD";
	}


	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO �Զ����ɵķ������
		super.doAction(arg0);
	}


//	@Override
//	public void fieldsControll() {
//		getEditor().getBillCardPanel().getHeadItem("iskztzd").setEnabled(false);
//	
//		String[] key=new String[]{"price","gpprice","kuang","inv"};
//		for(int i=0;i<key.length;i++){			
//			getEditor().getBillCardPanel().getBodyItem("pk_pact_b", key[i]).setEnabled(false);
//		}
//	
//		
//	}

}
