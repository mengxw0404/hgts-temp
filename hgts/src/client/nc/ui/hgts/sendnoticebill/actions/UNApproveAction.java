package nc.ui.hgts.sendnoticebill.actions;

import nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;

public class UNApproveAction extends UNApproveScriptAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3665653987643815398L;

	@Override
	 protected boolean isActionEnable()
    {
		//关闭的单据不可弃审
	  AggSendnoticebillHVO aggHVO = (AggSendnoticebillHVO) getModel().getSelectedData();	
	  if(null!=aggHVO && "Y".equals(aggHVO.getParentVO().getAttributeValue("closeflag"))){
		  return false;
	  }
	  return super.isActionEnable();
     
    }
}
