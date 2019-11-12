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
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void process(AggSendCarListHVO[] arg0) {
		// TODO 自动生成的方法存根
		arg0[0].getParentVO().setAttributeValue("plancars", arg0[0].getChildrenVO().length);//更新派车数量	
	}

}
