package nc.bs.hgts.pricepolicy.ace.rule;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.hgts.pricepolicy.PricepolicyHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/**
 * 价格弃审-是否引用判断
 * @author TR
 *
 */
public class UnapproveCheckRule implements IRule<AggPricepolicyHVO> {
	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null)
			dao = new BaseDAO();
		return dao;
	}
	public UnapproveCheckRule(){
		super();
	}
	@Override
	public void process(AggPricepolicyHVO[] vos) {

		if (vos == null) {
			return;
		}
		for (AggPricepolicyHVO bill : vos) {
			AggPricepolicyHVO aggvo=(AggPricepolicyHVO) bill;
			PricepolicyHVO vo=aggvo.getParentVO();
			Object obj=vo.getPrimaryKey();
			try {
				Collection<SendnoticebillBVO> coll = getDao().retrieveByClause(SendnoticebillBVO.class, " nvl(dr,0)=0 and nvl(blatest,'N') = 'Y' and csourceid='"+obj+"'");
				if(null !=coll && coll.size()>0){
					ExceptionUtils.wrappBusinessException("该单据已被引用，无法取消审批");
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
			
		}

	
		
	}

}
