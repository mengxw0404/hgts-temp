package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.hgts.sopact.plugin.bpplugin.SopactPluginPoint;
import nc.vo.hgts.sopact.AggPactVO;
import nc.itf.hgts.ISopactMaintain;

public class N_YX40_SAVEBASE extends AbstractPfAction<AggPactVO> {

	@Override
	protected CompareAroundProcesser<AggPactVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPactVO> processor = null;
		AggPactVO[] clientFullVOs = (AggPactVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggPactVO>(
					SopactPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggPactVO>(
					SopactPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggPactVO> rule = null;

		return processor;
	}

	@Override
	protected AggPactVO[] processBP(Object userObj,
			AggPactVO[] clientFullVOs, AggPactVO[] originBills) {

		AggPactVO[] bills = null;
		try {
			ISopactMaintain operator = NCLocator.getInstance()
					.lookup(ISopactMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
