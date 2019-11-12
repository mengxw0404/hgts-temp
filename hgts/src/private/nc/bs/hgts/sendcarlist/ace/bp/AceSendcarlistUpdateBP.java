package nc.bs.hgts.sendcarlist.ace.bp;

import nc.bs.hgts.sendcarlist.ace.bp.rule.AfterUpdateSourcebill;
import nc.bs.hgts.sendcarlist.ace.bp.rule.BeforeUpdateHeadplanCars;
import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

/**
 * 派车单 修改保存的BP
 * 
 */
public class AceSendcarlistUpdateBP {

	public AggSendCarListHVO[] update(AggSendCarListHVO[] bills,
			AggSendCarListHVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggSendCarListHVO> bp = new UpdateBPTemplate<AggSendCarListHVO>(
				SendcarlistPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSendCarListHVO> processer) {
		// TODO 后规则
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
		// TODO 前规则
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
