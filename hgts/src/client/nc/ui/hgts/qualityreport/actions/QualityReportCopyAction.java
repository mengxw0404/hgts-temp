package nc.ui.hgts.qualityreport.actions;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.CopyAction;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;

public class QualityReportCopyAction extends CopyAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5510916434977266653L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		this.getEditor().getBillCardPanel().setHeadItem("pk_qualityreport", null);
		this.getEditor().getBillCardPanel().setHeadItem("vbillno", null);
		this.getEditor().getBillCardPanel().setHeadItem("vbillstatus",  BillStatusEnum.FREE.value());
		this.getEditor().getBillCardPanel().setHeadItem("dbilldate",  AppContext.getInstance().getBusiDate());
		this.getEditor().getBillCardPanel().getBillModel("hgts_weighlist_b").setBodyDataVO(null);
		this.getEditor().getBillCardPanel().getBillModel("hgts_quacarplan_b").setBodyDataVO(null);
		int rowcount=this.getEditor().getBillCardPanel().getBillModel("hgts_qualityreport_b").getRowCount();
		if(rowcount>0){
			for(int i=0;i<rowcount;i++){
				getEditor().getBillCardPanel().getBillModel("hgts_qualityreport_b").setValueAt(null, i, "pk_qualityreport_b");
				getEditor().getBillCardPanel().getBillModel("hgts_qualityreport_b").setValueAt(null, i, "pk_qualityreport");
			}
		}
		
	}

	
}
