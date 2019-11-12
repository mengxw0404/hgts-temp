package nc.bs.pub.action;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hgts.invoicesheet.plugin.bpplugin.InvoicesheetPluginPoint;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.SysParamTool;
import nc.itf.hgts.IInvoicesheetMaintain;
/**
 * ������ɾ��
 * @author TR
 *
 */
public class N_YX11_DELETE extends AbstractPfAction<AggInvoicesheetHVO> {

	@Override
	protected CompareAroundProcesser<AggInvoicesheetHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggInvoicesheetHVO> processor = new CompareAroundProcesser<AggInvoicesheetHVO>(
				InvoicesheetPluginPoint.SCRIPT_DELETE);
		// TODO �ڴ˴����ǰ�����
		return processor;
	}

	@Override
	protected AggInvoicesheetHVO[] processBP(Object userObj,
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills) {
		
		// 2017-10-31 begin
		String pk_corp=HgtsPubTool.getStringNullAsTrim(clientFullVOs[0].getParentVO().getAttributeValue("pk_group"));
		String cjrOrSpr=HgtsPubTool.getStringNullAsTrim(clientFullVOs[0].getParentVO().getAttributeValue("creator"));
		SysParamTool tool=new SysParamTool();
		try {
			tool.checkAllowedDelOrUnapprove("DELETE", pk_corp, cjrOrSpr, InvocationInfoProxy.getInstance().getUserId());
		} catch (BusinessException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
			ExceptionUtils.wrappBusinessException(e1.getMessage());
		}
		// 2017-10-31 end

		// 2017-8-31 �����ε��������ˣ��޷�����ȡ���������� begin
		Object obj=clientFullVOs[0].getParentVO().getAttributeValue("pk_qualityreport");
		if(null !=obj){
			ExceptionUtils.wrappBusinessException("�õ����ѱ��ʼ챨�浥���ã��޷�ɾ��");
			return null;
		}
		// 2017-8-31 �����ε��������ˣ��޷�����ȡ���������� end
		IInvoicesheetMaintain operator = NCLocator.getInstance().lookup(
				IInvoicesheetMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
