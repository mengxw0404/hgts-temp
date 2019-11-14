package nc.bs.hgts.sendcarlist.ace.bp.rule;

import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.hgts.sendcarlist.SendCarListHVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 删除后回写日计划发运单
 * @author cl
 *
 */
public class AfterDeleteRule implements IRule<AggSendCarListHVO>{

	public AfterDeleteRule() {
		// TODO 自动生成的构造函数存根
	}

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	@Override
	public void process(AggSendCarListHVO[] vos) {
		// TODO 自动生成的方法存根
		if(null==vos || vos.length==0){
			return;
		}
		try {
			for(AggSendCarListHVO bill:vos){
				SendCarListBVO[] items=(SendCarListBVO[]) bill.getChildrenVO();
				for(SendCarListBVO item : items){
					String pk_dayplansend_b=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("csourcebid"));
					SendCarListHVO HVO =bill.getParentVO();
					
					String sql="select count(0) num from hgts_sendcarlist_b where nvl(dr,0)=0 and csourcebid='"+pk_dayplansend_b+"' and pk_sendcarlist <>'"+HVO.getPrimaryKey()+"'";
					Map<String,Integer> obj = (Map<String, Integer>) getDao().executeQuery(sql, new MapProcessor());
					
					String upadte_sql="update hgts_dayplansend_b set def6='"+ obj.get("num").intValue()+"' where pk_dayplansend_b='"+pk_dayplansend_b+"'";
					getDao().executeUpdate(upadte_sql);
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

}
