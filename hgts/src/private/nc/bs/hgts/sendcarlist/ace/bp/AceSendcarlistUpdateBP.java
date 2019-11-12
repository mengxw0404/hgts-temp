package nc.bs.hgts.sendcarlist.ace.bp;

import nc.bs.hgts.sendcarlist.ace.bp.rule.AfterUpdateSourcebill;
import nc.bs.hgts.sendcarlist.ace.bp.rule.BeforeUpdateHeadplanCars;
import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

/**
 * �ɳ��� �޸ı����BP
 * 
 */
public class AceSendcarlistUpdateBP {

	public AggSendCarListHVO[] update(AggSendCarListHVO[] bills,
			AggSendCarListHVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggSendCarListHVO> bp = new UpdateBPTemplate<AggSendCarListHVO>(
				SendcarlistPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSendCarListHVO> processer) {
		// TODO �����
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YPCD");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);
		
		rule = new AfterUpdateSourcebill();
		processer.addAfterRule(rule);

	}

	private void addBeforeRule(CompareAroundProcesser<AggSendCarListHVO> processer) {
		// TODO ǰ����
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggSendCarListHVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCbilltype("YPCD");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
		rule = new BeforeUpdateHeadplanCars();
		processer.addBeforeRule(rule);
	}

}
