package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hgts.dayplansend.ace.bp.rule.BeforeUnApproveCheckRule;
import nc.bs.hgts.dayplansend.plugin.bpplugin.DayplansendPluginPoint;
import nc.vo.hgts.dayplansend.AggDayplanSendHVO;
import nc.itf.hgts.IDayplansendMaintain;

public class N_YDPS_UNAPPROVE extends AbstractPfAction<AggDayplanSendHVO> {

	@Override
	protected CompareAroundProcesser<AggDayplanSendHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggDayplanSendHVO> processor = new CompareAroundProcesser<AggDayplanSendHVO>(
				DayplansendPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());
		processor.addBeforeRule(new BeforeUnApproveCheckRule());
		return processor;
	}

	@Override
	protected AggDayplanSendHVO[] processBP(Object userObj,
			AggDayplanSendHVO[] clientFullVOs, AggDayplanSendHVO[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggDayplanSendHVO[] bills = null;
		try {
			IDayplansendMaintain operator = NCLocator.getInstance()
					.lookup(IDayplansendMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
