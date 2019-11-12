package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.ff_sknoticebill.plugin.bpplugin.Ff_sknoticebillPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.IFf_sknoticebillMaintain;
import nc.vo.hgts.ffsknoticebill.AggFfSknoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/**
 * 收款通知单
 * @author TR
 *
 */
public class N_YX20_APPROVE extends AbstractPfAction<AggFfSknoticebillHVO> {

	public N_YX20_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggFfSknoticebillHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggFfSknoticebillHVO> processor = new CompareAroundProcesser<AggFfSknoticebillHVO>(
				Ff_sknoticebillPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggFfSknoticebillHVO[] processBP(Object userObj,
			AggFfSknoticebillHVO[] clientFullVOs, AggFfSknoticebillHVO[] originBills) {
		AggFfSknoticebillHVO[] bills = null;
		IFf_sknoticebillMaintain operator = NCLocator.getInstance().lookup(
				IFf_sknoticebillMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
