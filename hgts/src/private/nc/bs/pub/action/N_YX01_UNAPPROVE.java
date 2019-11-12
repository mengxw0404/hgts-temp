package nc.bs.pub.action;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hgts.pricepolicy.ace.rule.UnapproveCheckRule;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hgts.pricepolicy.plugin.bpplugin.PricepolicyPluginPoint;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.SysParamTool;
import nc.itf.hgts.IPricepolicyMaintain;
/**
 * 价格政策 弃审
 * @author TR
 *
 */
public class N_YX01_UNAPPROVE extends AbstractPfAction<AggPricepolicyHVO> {

	@Override
	protected CompareAroundProcesser<AggPricepolicyHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPricepolicyHVO> processor = new CompareAroundProcesser<AggPricepolicyHVO>(
				PricepolicyPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		processor.addBeforeRule(new UnapproveCheckRule());
		return processor;
	}

	@Override
	protected AggPricepolicyHVO[] processBP(Object userObj,
			AggPricepolicyHVO[] clientFullVOs, AggPricepolicyHVO[] originBills) {
		
		// 2017-10-31 begin
		String pk_corp=HgtsPubTool.getStringNullAsTrim(clientFullVOs[0].getParentVO().getAttributeValue("pk_group"));
		String cjrOrSpr=HgtsPubTool.getStringNullAsTrim(originBills[0].getParentVO().getAttributeValue("approver"));
		SysParamTool tool=new SysParamTool();
		try {
			tool.checkAllowedDelOrUnapprove("UNAPPROVE", pk_corp, cjrOrSpr, InvocationInfoProxy.getInstance().getUserId());
		} catch (BusinessException e1) {
			e1.printStackTrace();
			ExceptionUtils.wrappBusinessException(e1.getMessage());
		}
		// 2017-10-31 end

		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggPricepolicyHVO[] bills = null;
		try {
			IPricepolicyMaintain operator = NCLocator.getInstance()
					.lookup(IPricepolicyMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
