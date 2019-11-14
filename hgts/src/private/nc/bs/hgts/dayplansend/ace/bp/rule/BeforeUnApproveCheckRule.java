package nc.bs.hgts.dayplansend.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.dayplansend.AggDayplanSendHVO;
import nc.vo.hgts.dayplansend.DayplanSendBVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * �ռƻ� ȡ������ǰ����
 * @author cl
 * 2019��6��19��
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
		// TODO �Զ����ɵĹ��캯�����
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
						ExceptionUtils.wrappBusinessException("�������ռƻ����˵���ϸ������ɾ�����ε��ݺ���ִ�д˲���");
						break;
					}
					
					String carnum=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("def6"));
					if(HgtsPubTool.getUFDoubleNullAsZero(carnum).intValue()>0){
						ExceptionUtils.wrappBusinessException("�������ռƻ����˵���ϸ������ɾ�����ε��ݺ���ִ�д˲���");
						break;
					}
				}
			}
		}
	}

}
