package nc.bs.hgts.sendcarlist.ace.bp;

import nc.bs.hgts.sendcarlist.ace.bp.rule.AfterUpdateSourcebill;
import nc.bs.hgts.sendcarlist.ace.bp.rule.BeforeUpdateHeadplanCars;
import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

/**
 * 派车单  标准单据新增BP
 */
public class AceSendcarlistInsertBP {

	public AggSendCarListHVO[] insert(AggSendCarListHVO[] bills) {

		InsertBPTemplate<AggSendCarListHVO> bp = new InsertBPTemplate<AggSendCarListHVO>(
				SendcarlistPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSendCarListHVO> processor) {
		// TODO 新增后规则
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YPCD");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		rule = new AfterUpdateSourcebill();
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSendCarListHVO> processer) {
		// TODO 新增前规则
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("YPCD");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		rule = new BeforeUpdateHeadplanCars();
		processer.addBeforeRule(rule);
	}
}
