package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.pricepolicy.ace.rule.AfterApproveRule;
import nc.bs.hgts.pricepolicy.plugin.bpplugin.PricepolicyPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.IPricepolicyMaintain;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 价格政策审批
 * @author TR
 *
 */
public class N_YX01_APPROVE extends AbstractPfAction<AggPricepolicyHVO> {

	public N_YX01_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggPricepolicyHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPricepolicyHVO> processor = new CompareAroundProcesser<AggPricepolicyHVO>(
				PricepolicyPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggPricepolicyHVO[] processBP(Object userObj,
			AggPricepolicyHVO[] clientFullVOs, AggPricepolicyHVO[] originBills) {
		AggPricepolicyHVO[] bills = null;
		IPricepolicyMaintain operator = NCLocator.getInstance().lookup(
				IPricepolicyMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);

			// TODO 此次补丁注释,补丁后放开此注释 2017-11-7 
			// 2018-4-10 暂时注释
			AfterApproveRule rule=new AfterApproveRule();
			rule.process(clientFullVOs);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
