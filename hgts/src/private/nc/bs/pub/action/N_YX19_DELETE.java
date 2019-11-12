package nc.bs.pub.action;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.hgts.pc.WriteBackCustBalance;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hgts.ffsaleinvoice.plugin.bpplugin.FfsaleinvoicePluginPoint;
import nc.vo.hgts.ffsaleinvoice.AggFfSaleinvoiceHVO;
import nc.vo.hgts.ffsaleinvoice.FfSaleinvoiceBVO;
import nc.vo.hgts.ffsaleinvoice.FfSaleinvoiceHVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.SysParamTool;
import nc.itf.hgts.IFfsaleinvoiceMaintain;
import nc.itf.hgts.ref.IRefWriteBack;
import nc.impl.hgts.ref.RefWriteBackImpl;
import nc.jdbc.framework.processor.ColumnProcessor;

public class N_YX19_DELETE extends AbstractPfAction<AggFfSaleinvoiceHVO> {

	@Override
	protected CompareAroundProcesser<AggFfSaleinvoiceHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggFfSaleinvoiceHVO> processor = new CompareAroundProcesser<AggFfSaleinvoiceHVO>(
				FfsaleinvoicePluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggFfSaleinvoiceHVO[] processBP(Object userObj,
			AggFfSaleinvoiceHVO[] clientFullVOs, AggFfSaleinvoiceHVO[] originBills) {
		IFfsaleinvoiceMaintain operator = NCLocator.getInstance().lookup(
				IFfsaleinvoiceMaintain.class);
		try {

			// 2017-10-31 begin
			String pk_corp=HgtsPubTool.getStringNullAsTrim(clientFullVOs[0].getParentVO().getAttributeValue("pk_group"));
			String cjrOrSpr=HgtsPubTool.getStringNullAsTrim(clientFullVOs[0].getParentVO().getAttributeValue("creator"));
			SysParamTool tool=new SysParamTool();
			tool.checkAllowedDelOrUnapprove("DELETE", pk_corp, cjrOrSpr, InvocationInfoProxy.getInstance().getUserId());
			// 2017-10-31 end

			operator.delete(clientFullVOs, originBills);

			FfSaleinvoiceHVO hvo=clientFullVOs[0].getParentVO();
			String pk_org=hvo.getAttributeValue("pk_org").toString();
			String pk_billtype=hvo.getAttributeValue("pk_billtypeid").toString();
			String custid=hvo.getAttributeValue("cinvoicecustid").toString();
			FfSaleinvoiceBVO[] bvos=(FfSaleinvoiceBVO[]) clientFullVOs[0].getChildrenVO();
			UFDouble total_xh=UFDouble.ZERO_DBL;
			UFDouble total_cd=UFDouble.ZERO_DBL;
			UFDouble total_zy=UFDouble.ZERO_DBL;
			if(null !=bvos && bvos.length>0){
				UFDouble kpnum=UFDouble.ZERO_DBL;
				for(int i=0;i<bvos.length;i++){

				if(i!=0 && i!=bvos.length-1){	// 最后一行增加的辅助行，不纳入计算	
					String paytype=bvos[i].getAttributeValue("pk_balatype").toString();
					// 价税合计
					UFDouble norigtaxmny=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("norigtaxmny"));
					if(HgtsPubConst.paytype_xh.equals(paytype)){//现汇
						total_xh=total_xh.add(norigtaxmny);
					}else if(HgtsPubConst.paytype_cd.equals(paytype)){//承兑
						total_cd=total_cd.add(norigtaxmny);
					}

					kpnum =kpnum.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("nastnum"))) ; // 发票数量
					}
				}
				String pkSendnotice_b=((String)bvos[0].getAttributeValue("ocsourcebid"));
				String sql="select zxprice from hgts_sendnoticebill_b where nvl(dr,0)=0 and pk_sendnoticebill_b='"+pkSendnotice_b+"'";
				BaseDAO dao=new BaseDAO();
				UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(dao.executeQuery(sql, new ColumnProcessor()));

				total_zy=total_zy.add(kpnum.multiply(zxprice));

				// 2017-12-6 不回写占用金额相关信息了 begin
				/*WriteBackCustBalance back=new WriteBackCustBalance();
				back.writeBackBal(pk_org, pk_billtype, custid, null, total_xh, total_cd,total_zy, "DELETE");*/
				// 2017-12-6 不回写占用金额相关信息了 end

				// 2018-1-10 回写引用标识 改到 保存后规则类 里面写了
				/*String csourceid=HgtsPubTool.getStringNullAsTrim(bvos[0].getAttributeValue("csourceid"));
				IRefWriteBack iwb=new RefWriteBackImpl();
				iwb.writeHjsettleYyflag(csourceid, "DELETE");*/
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
