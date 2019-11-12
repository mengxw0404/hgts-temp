package nc.ui.hgts.sendcarlist.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;

/**
 * �ɳ��� ��ͷ�༭���¼�
 * @author TR
 *
 */
public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent>{

	public AceHeadTailAfterEditHandler() {
		// TODO �Զ����ɵĹ��캯�����
	}

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent arg0) {
		// TODO �Զ����ɵķ������
		if(arg0.getKey().equals("sendbillno")){//����֪ͨ��
			UIRefPane pane=(UIRefPane) arg0.getBillCardPanel().getHeadItem(arg0.getKey()).getComponent();
			String refpk=pane.getRefPK(); // �ӱ�pk
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
