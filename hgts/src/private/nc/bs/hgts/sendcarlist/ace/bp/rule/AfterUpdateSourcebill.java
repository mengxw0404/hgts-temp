package nc.bs.hgts.sendcarlist.ace.bp.rule;

import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.hgts.sendcarlist.SendCarListHVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 派车单  新增/修改 保存后 回写 日计划派车信息
 * 
 */
public class AfterUpdateSourcebill implements IRule<AggSendCarListHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public AfterUpdateSourcebill() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void process(AggSendCarListHVO[] vos) {
		try {
			SendCarListHVO HVO =vos[0].getParentVO();
			UFDouble plancars = HgtsPubTool.getUFDoubleNullAsZero(HVO.getAttributeValue("plancars"));
			SendCarListBVO[] BodyVO =(SendCarListBVO[]) vos[0].getChildrenVO();
			// 回写 日计划发运单 已发车辆 数量（def6）
			if(null !=BodyVO && BodyVO.length>0){
				for(SendCarListBVO bvo:BodyVO){					
					String pk_dayplansend_b =HgtsPubTool.getStringNullAsTrim( bvo.getAttributeValue("csourcebid"));//获取来源 日计划发运单子表主键
						String sql="select count(0) num from hgts_sendcarlist_b where nvl(dr,0)=0 and csourcebid='"+pk_dayplansend_b+"' and pk_sendcarlist <>'"+HVO.getPrimaryKey()+"'";
						Map<String,Integer> obj = (Map<String, Integer>) getDao().executeQuery(sql, new MapProcessor());

						String upadte_sql="update hgts_dayplansend_b set def6='"+plancars.add(obj.get("num").intValue())+"' where pk_dayplansend_b='"+pk_dayplansend_b+"'";
						getDao().executeUpdate(upadte_sql);

				}

			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
