package nc.ui.hgts.sopact.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.busibean.ISysInitQry;
import nc.ui.hgts.ff.pub.AddRefAction;
import nc.vo.pub.BusinessException;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.AppContext;


/**
 * 参照竞价价格表
 */
public class AddJjpriceAction extends AddRefAction {
	private static final long serialVersionUID = 3286430955995336466L;

	@Override
	protected String getCurrBilltype() {
		// TODO 自动生成的方法存根
		return "YX40";
	}


	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根
		super.doAction(arg0);
	}


	@Override
	public void fieldsControll() {
		getEditor().getBillCardPanel().getHeadItem("iskztzd").setEnabled(false);
		/*getEditor().getBillCardPanel().getHeadItem("pk_cagetype").setEnabled(false);*/

		String[] key=new String[]{"price","gpprice","kuang","inv"};
		for(int i=0;i<key.length;i++){			
			getEditor().getBillCardPanel().getBodyItem("pk_pact_b", key[i]).setEnabled(false);
		}
	
		
	}


	@Override
	protected boolean isActionEnable() {
		ISysInitQry init=(ISysInitQry) NCLocator.getInstance().lookup(ISysInitQry.class.getName());
		String value="";
		try {
			SysInitVO sysInitVO = init.queryByParaCode(AppContext.getInstance().getPkGroup(),"FF17");
			value=sysInitVO.getValue();
			if(null !=value && !"".equals(value)){
				if("Y".equals(value)){				
					return true;
				}
			}
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		return false;
	}


}
