package nc.bs.hgts.sendcarlist.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;

public class BeforeUpdateHeadplanCars implements IRule<AggSendCarListHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public BeforeUpdateHeadplanCars() {
		// TODO �Զ����ɵĹ��캯�����
	}

	@Override
	public void process(AggSendCarListHVO[] arg0) {
		// TODO �Զ����ɵķ������
		arg0[0].getParentVO().setAttributeValue("plancars", arg0[0].getChildrenVO().length);//�����ɳ�����	
	}

}
