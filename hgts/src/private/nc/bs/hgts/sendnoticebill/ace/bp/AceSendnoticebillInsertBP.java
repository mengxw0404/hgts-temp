package nc.bs.hgts.sendnoticebill.ace.bp;

import nc.bs.hgts.sendnoticebill.plugin.bpplugin.SendnoticebillPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.bs.hgts.sendnoticebill.ace.rule.AfterInsertRule;
import nc.bs.hgts.sendnoticebill.ace.rule.BeforeSaveRule;
import nc.bs.hgts.sendnoticebill.ace.rule.ContractBeforeSaveRule;
/**
 * 标准单据新增BP
 */
public class AceSendnoticebillInsertBP {

	public AggSendnoticebillHVO[] insert(AggSendnoticebillHVO[] bills) {

		InsertBPTemplate<AggSendnoticebillHVO> bp = new InsertBPTemplate<AggSendnoticebillHVO>(
				SendnoticebillPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSendnoticebillHVO> processor) {
		// TODO 新增后规则
		IRule<AggSendnoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YX04");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
		.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
		.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		
		rule=new AfterInsertRule();
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSendnoticebillHVO> processer) {
		// TODO 新增前规则
		IRule<AggSendnoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("YX04");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
		.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
		.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		
//		rule=new BeforeSaveRule();
//		processer.addBeforeRule(rule);
//		 校验 通知单引用合同，数量是否超额
//		rule=new ContractBeforeSaveRule();
//		processer.addBeforeRule(rule);
		
	}
}
