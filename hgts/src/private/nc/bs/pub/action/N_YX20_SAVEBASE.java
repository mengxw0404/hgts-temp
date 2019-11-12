package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hgts.ff_sknoticebill.plugin.bpplugin.Ff_sknoticebillPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.hgts.pc.WriteBackCustBalance;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hgts.IFf_sknoticebillMaintain;
import nc.vo.hgts.ffsknoticebill.AggFfSknoticebillHVO;
import nc.vo.hgts.ffsknoticebill.FfSknoticebillHVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_YX20_SAVEBASE extends AbstractPfAction<AggFfSknoticebillHVO> {

	@Override
	protected CompareAroundProcesser<AggFfSknoticebillHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggFfSknoticebillHVO> processor = null;
		AggFfSknoticebillHVO[] clientFullVOs = (AggFfSknoticebillHVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggFfSknoticebillHVO>(
					Ff_sknoticebillPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggFfSknoticebillHVO>(
					Ff_sknoticebillPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggFfSknoticebillHVO> rule = null;

		return processor;
	}

	@Override
	protected AggFfSknoticebillHVO[] processBP(Object userObj,
			AggFfSknoticebillHVO[] clientFullVOs, AggFfSknoticebillHVO[] originBills) {

		AggFfSknoticebillHVO[] bills = null;
		try {
			IFf_sknoticebillMaintain operator = NCLocator.getInstance()
					.lookup(IFf_sknoticebillMaintain.class);
			
			// 2017-8-29 add begin
			boolean hpk=StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey());			
			// 2017-8-29 add end
			
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}

			// 回写客户余额
			WriteBackCustBalance back=new WriteBackCustBalance();
			FfSknoticebillHVO hvo=clientFullVOs[0].getParentVO();
			FfSknoticebillHVO oldhvo=originBills[0].getParentVO();
			String pk_org=hvo.getAttributeValue("pk_org").toString();
			String pk_billtype=hvo.getAttributeValue("pk_billtypeid").toString();
			String custid=hvo.getAttributeValue("customer").toString();
			String paytype=hvo.getAttributeValue("pk_balatype").toString();
			//FfSknoticebillBVO[] bvos=(FfSknoticebillBVO[]) clientFullVOs[0].getChildrenVO();
			UFDouble total_xh=UFDouble.ZERO_DBL;
			UFDouble total_cd=UFDouble.ZERO_DBL;
			// 价税合计
			UFDouble norigtaxmny=HgtsPubTool.getUFDoubleNullAsZero(hvo.getAttributeValue("skmny"));
			UFDouble old_skmny=HgtsPubTool.getUFDoubleNullAsZero(oldhvo.getAttributeValue("skmny"));
			String old_custid=oldhvo.getAttributeValue("customer").toString();
			if(HgtsPubConst.paytype_xh.equals(paytype)){//现汇
				if(hpk){ // 主键为空，则为新增
					total_xh=total_xh.add(norigtaxmny);
					back.writeBackBal(pk_org, pk_billtype, custid, null, total_xh, total_cd,null, "SAVEBASE");
				}else {	//修改		
					total_xh=total_xh.add(norigtaxmny.sub(old_skmny));
					if(! custid.equals(old_custid)){
						// 不是同一客户，减去 之前的收款金额  ;  新客户 则 加上本次修改为的   收款金额
						old_skmny=old_skmny.multiply(-1);						
						back.writeBackBal(pk_org, pk_billtype, old_custid, null, old_skmny, total_cd,null, "SAVEBASE");
						back.writeBackBal(pk_org, pk_billtype, custid, null, norigtaxmny, total_cd, null,"SAVEBASE");		
					}else{
						back.writeBackBal(pk_org, pk_billtype, custid, null, total_xh, total_cd,null, "SAVEBASE");
					}
				}
			}else if(HgtsPubConst.paytype_cd.equals(paytype)){//承兑
				if(hpk){ // 主键为空，则为新增
					total_cd=total_cd.add(norigtaxmny);
					back.writeBackBal(pk_org, pk_billtype, custid, null, total_xh, total_cd, null,"SAVEBASE");
				}else{
					total_cd=total_cd.add(norigtaxmny.sub(old_skmny));
					if(! custid.equals(old_custid)){
						// 不是同一客户，修改前的客户 减去 之前 加上的收款金额  ;  新客户 则 加上本次修改为的   收款金额
						old_skmny=old_skmny.multiply(-1);						
						back.writeBackBal(pk_org, pk_billtype, old_custid, null, old_skmny, total_cd, null,"SAVEBASE");
						back.writeBackBal(pk_org, pk_billtype, custid, null, norigtaxmny, total_cd, null,"SAVEBASE");		
					}else{
						back.writeBackBal(pk_org, pk_billtype, custid, null, total_xh, total_cd,null, "SAVEBASE");
					}
				}
			}

		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
