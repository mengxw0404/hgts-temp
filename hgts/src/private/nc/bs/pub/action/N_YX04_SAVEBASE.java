package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.sendnoticebill.plugin.bpplugin.SendnoticebillPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.hgts.pc.QueryCustMnyImpl;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.ISendnoticebillMaintain;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_YX04_SAVEBASE extends AbstractPfAction<AggSendnoticebillHVO> {

	@Override
	protected CompareAroundProcesser<AggSendnoticebillHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggSendnoticebillHVO> processor = null;
		AggSendnoticebillHVO[] clientFullVOs = (AggSendnoticebillHVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggSendnoticebillHVO>(
					SendnoticebillPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggSendnoticebillHVO>(
					SendnoticebillPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggSendnoticebillHVO> rule = null;

		return processor;
	}

	@Override
	protected AggSendnoticebillHVO[] processBP(Object userObj,
			AggSendnoticebillHVO[] clientFullVOs, AggSendnoticebillHVO[] originBills) {

		AggSendnoticebillHVO[] bills = null;
		try {
			ISendnoticebillMaintain operator = NCLocator.getInstance()
					.lookup(ISendnoticebillMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
			
			// 回写客商档案 业务占用
			SendnoticebillHVO hvo=clientFullVOs[0].getParentVO();
			String pk_billtype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_billtypeid"));
			String pk_org=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_org"));
			String custid=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_cust"));
			SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) clientFullVOs[0].getChildrenVO();
			QueryCustMnyImpl impl=new QueryCustMnyImpl();
			UFDouble mny=impl.getAllSendMny(pk_org,custid,hvo.getPrimaryKey(),pk_billtype);
			//UFDouble fp=impl.getFPMNY(pk_org, custid,null,pk_billtype);
			if(null !=bvos && bvos.length>0){
				UFDouble zymny=UFDouble.ZERO_DBL;
				for(int i=0;i<bvos.length;i++ ){
					UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("shul"));
					UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("zxprice"));
					UFDouble ykpnum=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("ykpnum"));
					//zymny=zymny.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("jstotal")));
					zymny=zymny.add((shul.sub(ykpnum)).multiply(zxprice));
				}
				zymny=zymny.add(mny)/*.sub(fp)*/;
				
				// 2017-12-6 不回写占用金额相关信息了 begin
				/*WriteBackCustBalance wbb=new WriteBackCustBalance();
				wbb.writeBackBal(null, pk_billtype, custid, null, null, null,zymny, "SAVEBASE");*/
				// 2017-12-6 不回写占用金额相关信息了 end
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
	
}
