package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.ISendcarlistMaintain;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/**
 * 派车单-审批
 * @author TR
 *
 */
public class N_YPCD_APPROVE extends AbstractPfAction<AggSendCarListHVO> {

	public N_YPCD_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggSendCarListHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggSendCarListHVO> processor = new CompareAroundProcesser<AggSendCarListHVO>(
				SendcarlistPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
//		processor.addAfterRule(new AfterApproveToInvoice());//审批自定生产 发货计量单
		return processor;
	}

	@Override
	protected AggSendCarListHVO[] processBP(Object userObj,
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills) {
		AggSendCarListHVO[] bills = null;
		ISendcarlistMaintain operator = NCLocator.getInstance().lookup(
				ISendcarlistMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
