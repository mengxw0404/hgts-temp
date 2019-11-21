package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hgts.sendcarlist.ace.bp.rule.BeforeUnApproveCheckRule;
import nc.bs.hgts.sendcarlist.plugin.bpplugin.SendcarlistPluginPoint;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.itf.hgts.ISendcarlistMaintain;

public class N_YPCD_UNAPPROVE extends AbstractPfAction<AggSendCarListHVO> {

	@Override
	protected CompareAroundProcesser<AggSendCarListHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggSendCarListHVO> processor = new CompareAroundProcesser<AggSendCarListHVO>(
				SendcarlistPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());
		processor.addBeforeRule(new BeforeUnApproveCheckRule());
		return processor;
	}

	@Override
	protected AggSendCarListHVO[] processBP(Object userObj,
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggSendCarListHVO[] bills = null;
		try {
			ISendcarlistMaintain operator = NCLocator.getInstance()
					.lookup(ISendcarlistMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
