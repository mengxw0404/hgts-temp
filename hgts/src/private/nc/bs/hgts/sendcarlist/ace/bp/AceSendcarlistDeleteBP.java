package nc.bs.hgts.sendcarlist.ace.bp;

import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AceSendcarlistDeleteBP {

	public void delete(AggSendCarListHVO[] bills) {

		DeleteBPTemplate<AggSendCarListHVO> bp = new DeleteBPTemplate<AggSendCarListHVO>(
				SendcarlistPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSendCarListHVO> processer) {
		// TODO ǰ����
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSendCarListHVO> processer) {
		// TODO �����
		IRule<AggSendCarListHVO> rule = null;
		rule = new nc.bs.hgts.sendcarlist.ace.bp.rule.AfterDeleteRule();
		processer.addAfterRule(rule);
	}
}
