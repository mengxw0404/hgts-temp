package nc.ui.hgts.sendcarlist.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;

/**
 * 派车单 表头编辑后事件
 * @author TR
 *
 */
public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent>{

	public AceHeadTailAfterEditHandler() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent arg0) {
		// TODO 自动生成的方法存根
		if(arg0.getKey().equals("sendbillno")){//发运通知单
			UIRefPane pane=(UIRefPane) arg0.getBillCardPanel().getHeadItem(arg0.getKey()).getComponent();
			String refpk=pane.getRefPK(); // 子表pk
			UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(pane.getRefValue("nvl(shul,0) shul"));
			UFDouble yzxnum=HgtsPubTool.getUFDoubleNullAsZero(pane.getRefValue("nvl(yzxnum,0) yzxnum"));
			UFDouble syl=shul.sub(yzxnum);
			String vbillno=HgtsPubTool.getStringNullAsTrim(pane.getRefValue("vbillno"));			
			FormulaParseTool tool=new FormulaParseTool();
			String hpk=tool.getNameByID("hgts_sendnoticebill", "pk_sendnoticebill", "vbillno", vbillno);
			String pk_cust=tool.getNameByID("hgts_sendnoticebill", "pk_cust", "pk_sendnoticebill", hpk);
			String pk_mine=tool.getNameByID("hgts_sendnoticebill", "pk_fhkc", "pk_sendnoticebill", hpk);
			String pk_stordoc=tool.getNameByID("hgts_sendnoticebill", "pk_stordoc", "pk_sendnoticebill", hpk);
			String pk_mz=tool.getNameByID("hgts_sendnoticebill_b", "pz", "pk_sendnoticebill_b", refpk);
			
			arg0.getBillCardPanel().setHeadItem("pk_cust", pk_cust);
			arg0.getBillCardPanel().setHeadItem("pk_mine", pk_mine);
			arg0.getBillCardPanel().setHeadItem("pk_stordoc", pk_stordoc);
			arg0.getBillCardPanel().setHeadItem("pk_inv", pk_mz);
			arg0.getBillCardPanel().setHeadItem("shul", shul);
			arg0.getBillCardPanel().setHeadItem("yzxnum", yzxnum);
			arg0.getBillCardPanel().setHeadItem("syl", syl);
			arg0.getBillCardPanel().setHeadItem("pk_supplier", "");
		}
	}

}
