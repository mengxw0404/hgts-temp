package nc.ui.hgts.sopact.actions;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.hgts.sopact.ace.parent.handler.ParentSopactCopyAction;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sopact.AggPactVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.BusinessException;

/**
 * 生成执行合同
 * 编辑态
 */
public class ExeContAction extends ParentSopactCopyAction{

	private static final long serialVersionUID = 1095031295509395361L;

	public ExeContAction() {
		super();
		super.setCode("exeContAction");
		super.setBtnName("生成执行合同");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggPactVO aggVO=(AggPactVO) getModel().getSelectedData();
		Object contcode=aggVO.getParentVO().getAttributeValue("contcode");
		super.doAction(e);

		this.getEditor().getBillCardPanel().setHeadItem("conttype", "2"); // 执行合同
		this.getEditor().getBillCardPanel().setHeadItem("zcontcode",contcode); // 主合同号

		AggPactVO aggvo=(AggPactVO) this.getEditor().getBillCardPanel().getBillValueVO(AggPactVO.class.getName(), PactVO.class.getName(), PactBVO.class.getName());
		IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
		try {
			aggvo=(AggPactVO) col.colPrice(aggvo);
			this.getEditor().getBillCardPanel().getBillModel("pk_pact_b").setBodyDataVO(aggvo.getTableVO("pk_pact_b"));
			
			this.getEditor().getBillCardPanel().getBillModel("pk_pact_b").loadLoadRelationItemValue();
			this.getEditor().getBillCardPanel().getBillModel("pk_cont_zlzb_b").loadLoadRelationItemValue();
			this.getEditor().getBillCardPanel().getBillModel("pk_cont_yzyj_b").loadLoadRelationItemValue();
			this.getEditor().getBillCardPanel().getBillModel("hgts_contchange_b").loadLoadRelationItemValue();
			
		} catch (BusinessException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected boolean isActionEnable() {
		AggPactVO aggVO=(AggPactVO) getModel().getSelectedData();
		if (aggVO == null) {
			return false;
		}
		if (aggVO.getParentVO() == null) {
			return false;
		}

		int status = ApproveStatus.FREE;
		String vbillstatus=HgtsPubTool.getStringNullAsTrim(aggVO.getParentVO().getAttributeValue("approvestatus"));
		status="".equals(vbillstatus)?-1:Integer.parseInt(vbillstatus);
		
		String conttype=HgtsPubTool.getStringNullAsTrim(aggVO.getParentVO().getAttributeValue("conttype"));
		
		if(this.getModel().getAppUiState() == AppUiState.NOT_EDIT && ApproveStatus.APPROVED==status && conttype.equals("1")){
			return true;
		}
		return false;
	}



}
