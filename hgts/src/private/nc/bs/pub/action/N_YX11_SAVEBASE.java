package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.hgts.invoicesheet.plugin.bpplugin.InvoicesheetPluginPoint;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.itf.hgts.IInvoicesheetMaintain;
/**
 * 发货计量单-保存
 * @author TR
 *
 */
public class N_YX11_SAVEBASE extends AbstractPfAction<AggInvoicesheetHVO> {

	@Override
	protected CompareAroundProcesser<AggInvoicesheetHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggInvoicesheetHVO> processor = null;
		AggInvoicesheetHVO[] clientFullVOs = (AggInvoicesheetHVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggInvoicesheetHVO>(
					InvoicesheetPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggInvoicesheetHVO>(
					InvoicesheetPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggInvoicesheetHVO> rule = null;

		return processor;
	}

	@Override
	protected AggInvoicesheetHVO[] processBP(Object userObj,
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills) {

		if(null==clientFullVOs[0].getParentVO().getAttributeValue("yfxycode"))
			clientFullVOs[0].getParentVO().setAttributeValue("yfxycode", originBills[0].getParentVO().getAttributeValue("yfxycode"));
		if(null==clientFullVOs[0].getParentVO().getAttributeValue("zxxycode"))
			clientFullVOs[0].getParentVO().setAttributeValue("zxxycode", originBills[0].getParentVO().getAttributeValue("zxxycode"));
		
		AggInvoicesheetHVO[] bills = null;
		try {
			IInvoicesheetMaintain operator = NCLocator.getInstance().lookup(IInvoicesheetMaintain.class);
			
			
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
