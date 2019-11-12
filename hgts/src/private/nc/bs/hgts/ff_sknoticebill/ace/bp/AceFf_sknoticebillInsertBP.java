package nc.bs.hgts.ff_sknoticebill.ace.bp;

import nc.bs.hgts.ff_sknoticebill.ace.bp.rule.AfterInsertRule;
import nc.bs.hgts.ff_sknoticebill.ace.bp.rule.SkBeforeInsertRule;
import nc.bs.hgts.ff_sknoticebill.plugin.bpplugin.Ff_sknoticebillPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.ffsknoticebill.AggFfSknoticebillHVO;

/**
 * 标准单据新增BP
 */
public class AceFf_sknoticebillInsertBP {

	public AggFfSknoticebillHVO[] insert(AggFfSknoticebillHVO[] bills) {

		InsertBPTemplate<AggFfSknoticebillHVO> bp = new InsertBPTemplate<AggFfSknoticebillHVO>(
				Ff_sknoticebillPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggFfSknoticebillHVO> processor) {
		// TODO 新增后规则
		IRule<AggFfSknoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YX20");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		//回写合同已收款金额
		rule=new AfterInsertRule();
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggFfSknoticebillHVO> processer) {
		// TODO 新增前规则
		IRule<AggFfSknoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("YX20");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		// 检验是否超合同金额
		rule=new SkBeforeInsertRule();
		processer.addBeforeRule(rule);
	}
}
