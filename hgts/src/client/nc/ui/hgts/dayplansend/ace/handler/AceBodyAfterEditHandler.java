package nc.ui.hgts.dayplansend.ace.handler;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;
/**
 * 日计划发运单-表体编辑后事件
 * @author TR
 *
 */
public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent>{

	public AceBodyAfterEditHandler() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO 自动生成的方法存根
		BillCardPanel panel = e.getBillCardPanel();
		BillModel bm=panel.getBillModel();
		FormulaParseTool tool=new FormulaParseTool();
		//表体参照通知单 信息
		if(e.getKey().equals("sendnoticebillno")){
			UIRefPane pane=(UIRefPane) e.getBillCardPanel().getBodyItem(e.getKey()).getComponent();
			String refpk=pane.getRefPK();
			UFDouble syl=HgtsPubTool.getUFDoubleNullAsZero(pane.getRefValue("nvl(hgts_sendnoticebill_b.shul,0)-nvl(hgts_sendnoticebill_b.yzxnum,0) syl"));
			String pk_sendnoticebill=tool.getNameByID("hgts_sendnoticebill_b", "pk_sendnoticebill", "pk_sendnoticebill_b", refpk);
			String pk_cust=tool.getNameByID("hgts_sendnoticebill", "pk_cust", "pk_sendnoticebill", pk_sendnoticebill);
			String pk_mine=tool.getNameByID("hgts_sendnoticebill", "pk_fhkc", "pk_sendnoticebill", pk_sendnoticebill);
			String pk_dept=tool.getNameByID("hgts_sendnoticebill", "pk_dept", "pk_sendnoticebill", pk_sendnoticebill);
			String pk_stordoc=tool.getNameByID("hgts_sendnoticebill", "pk_stordoc", "pk_sendnoticebill", pk_sendnoticebill);
			String pk_material=tool.getNameByID("hgts_sendnoticebill_b", "pz", "pk_sendnoticebill_b", refpk);
			
			String pk_psndoc=tool.getNameByID("sm_user", "pk_psndoc", "cuserid", tool.getNameByID("hgts_sendnoticebill", "creator", "pk_sendnoticebill", pk_sendnoticebill));;
			//panel.setHeadItem("pk_dept", pk_dept);		
			panel.setHeadItem("orgman", pk_psndoc);//用户
			panel.setBodyValueAt(pk_psndoc, e.getRow(), "billmater");
			panel.setBodyValueAt(pk_sendnoticebill, e.getRow(), "pk_sendnoticebill");
			panel.setBodyValueAt(pk_cust, e.getRow(), "pk_cust");
			panel.setBodyValueAt(pk_mine, e.getRow(), "pk_mine");
			panel.setBodyValueAt(null!=e.getBillCardPanel().getHeadItem("pk_dept").getValue() ?e.getBillCardPanel().getHeadItem("pk_dept").getValue():pk_dept, e.getRow(), "pk_dept");
			panel.setBodyValueAt(pk_stordoc, e.getRow(), "pk_stordoc");
			panel.setBodyValueAt(pk_material, e.getRow(), "pk_material");
			panel.setBodyValueAt(syl, e.getRow(), "syl");
			panel.getBillModel().loadLoadRelationItemValue();
		}
		else if(e.getKey().equals("plansendnum")){
			UFDouble syl = HgtsPubTool.getUFDoubleNullAsZero(panel.getBodyValueAt(e.getRow(), "syl"));
			UFDouble plansendnum = HgtsPubTool.getUFDoubleNullAsZero(panel.getBodyValueAt(e.getRow(), "plansendnum"));
			if(syl.doubleValue()<plansendnum.doubleValue()){
				MessageDialog.showErrorDlg(panel.getParent(), "提示", "计划发运数量不得大于剩余数量");
				panel.setBodyValueAt(0.0, e.getRow(), "plansendnum");
			}
			
		}
	}

}
