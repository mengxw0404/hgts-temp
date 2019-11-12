package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.dayplansend.plugin.bpplugin.DayplansendPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.IDayplansendMaintain;
import nc.vo.hgts.dayplansend.AggDayplanSendHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 日计划发运单
 * @author TR
 *
 */
public class N_YDPS_APPROVE extends AbstractPfAction<AggDayplanSendHVO> {

	public N_YDPS_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggDayplanSendHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggDayplanSendHVO> processor = new CompareAroundProcesser<AggDayplanSendHVO>(
				DayplansendPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
//		processor.addAfterRule(new AfterApproveToPcd());//审批后生产派车单
		return processor;
	}

	@Override
	protected AggDayplanSendHVO[] processBP(Object userObj,
			AggDayplanSendHVO[] clientFullVOs, AggDayplanSendHVO[] originBills) {
		AggDayplanSendHVO[] bills = null;
		IDayplansendMaintain operator = NCLocator.getInstance().lookup(
				IDayplansendMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
