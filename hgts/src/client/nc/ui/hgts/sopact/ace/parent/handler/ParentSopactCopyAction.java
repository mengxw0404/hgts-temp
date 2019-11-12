package nc.ui.hgts.sopact.ace.parent.handler;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.actions.CopyAction;
import nc.vo.pub.pf.BillStatusEnum;

public class ParentSopactCopyAction extends CopyAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6145285331194288611L;

	@Override
	public void doAction(ActionEvent e) throws Exception {

		super.doAction(e);

		nc.ui.pub.bill.BillCardPanel bp=this.getEditor().getBillCardPanel();
		bp.setHeadItem("contcode", null);
		bp.setHeadItem("vbillno", null);
		bp.setHeadItem("approvestatus",  BillStatusEnum.FREE.value());
		bp.setHeadItem("pk_pact",  null);

		if(null!=bp.getBillModel("pk_pact_b")){
			int rowcount=bp.getBillModel("pk_pact_b").getRowCount();
			BillModel model=bp.getBillModel("pk_pact_b");
			if(rowcount>0){
				for(int i=0;i<rowcount;i++){
					model.setValueAt(null, i, "pk_pact");
					model.setValueAt(null, i, "pk_pact_b");
					model.setValueAt(null, i, "yzxnum");
					model.setValueAt(null, i, "ysmny");
				}
			}
		}
			
		
		if(null!=bp.getBillModel("pk_pact_b")){
			int rowcount=bp.getBillModel("pk_cont_zlzb_b").getRowCount();
			BillModel model=bp.getBillModel("pk_cont_zlzb_b");
			if(rowcount>0){
				for(int i=0;i<rowcount;i++){
					model.setValueAt(null, i, "pk_pact");
					model.setValueAt(null, i, "pk_cont_zlzb_b");
				}
			}
		}
		
		if(null!=bp.getBillModel("pk_pact_b")){
			int rowcount=bp.getBillModel("pk_cont_yzyj_b").getRowCount();
			BillModel model=bp.getBillModel("pk_cont_yzyj_b");
			if(rowcount>0){
				for(int i=0;i<rowcount;i++){
					model.setValueAt(null, i, "pk_pact");
					model.setValueAt(null, i, "pk_cont_yzyj_b");
				}
			}
		}
		

		if(null!=bp.getBillModel("hgts_contchange_b")){
			int rowcount=bp.getBillModel("hgts_contchange_b").getRowCount();
			BillModel model=bp.getBillModel("hgts_contchange_b");
			if(rowcount>0){
				for(int i=0;i<rowcount;i++){
					model.setValueAt(null, i, "pk_pact");
					model.setValueAt(null, i, "pk_hischange_b");
				}
			}

			bp.setTailItem("creator", null);
			bp.setTailItem("creationtime", null);
			bp.setTailItem("modifier", null);
			bp.setTailItem("modifiedtime", null);
			bp.setTailItem("approver", null);
			bp.setTailItem("approvedate", null);
			bp.setTailItem("approvenote", null);
		}
		

	}


}
