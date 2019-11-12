package nc.bs.hgts.sendcarlist.ace.bp;

import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceSendcarlistDeleteBP {

	public void delete(AggSendCarListHVO[] bills) {

		DeleteBPTemplate<AggSendCarListHVO> bp = new DeleteBPTemplate<AggSendCarListHVO>(
				SendcarlistPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSendCarListHVO> processer) {
		// TODO 前规则
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSendCarListHVO> processer) {
		// TODO 后规则
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.hgts.sendcarlist.ace.bp.rule.AfterDeleteRule();
		processer.addAfterRule(rule);
	}
}
