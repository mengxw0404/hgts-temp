package nc.bs.hgts.dayplansend.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.dayplansend.AggDayplanSendHVO;
import nc.vo.hgts.dayplansend.DayplanSendBVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 日计划 取消审批前规则
 * @author cl
 * 2019年6月19日
 */
public class BeforeUnApproveCheckRule implements IRule<AggDayplanSendHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public BeforeUnApproveCheckRule() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void process(AggDayplanSendHVO[] vos) {
		if(null !=vos && vos.length>0){
			for(AggDayplanSendHVO aggvo:vos){
				DayplanSendBVO[] bodys=(DayplanSendBVO[]) aggvo.getChildrenVO();
				for(int i=0;i<bodys.length;i++){
					DayplanSendBVO item=bodys[i];
					String dayplandetailno=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("dayplandetailno"));
					if(!"".equals(dayplandetailno)){
						ExceptionUtils.wrappBusinessException("已生成日计划发运单明细，请先删除下游单据后再执行此操作");
						break;
					}
					
					String carnum=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("def6"));
					if(HgtsPubTool.getUFDoubleNullAsZero(carnum).intValue()>0){
						ExceptionUtils.wrappBusinessException("已生成日计划发运单明细，请先删除下游单据后再执行此操作");
						break;
					}
				}
			}
		}
	}

}
