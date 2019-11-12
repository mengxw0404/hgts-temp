package nc.bs.hgts.qualityproject.ace.rule;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pmpub.uap.util.ExceptionUtils;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.bd.AggQualityprojectVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class CheckCodeUniqueRule implements IRule<AggQualityprojectVO> {
	private ColumnProcessor pro = new ColumnProcessor();

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null)
			dao = new BaseDAO();
		return dao;
	}
	@Override
	public void process(AggQualityprojectVO[] billvos) {
		try {
		if (billvos == null || billvos.length == 0) {
			return;
		}
		//质检单号
		String code = (String) billvos[0].getParentVO().getAttributeValue("billno");
		String id=HgtsPubTool.getStringNullAsTrim(billvos[0].getParentVO().getAttributeValue("id"));
		if(null==id || "".equals(id)){
			id=" ";
		}
		String sql = "select count(0) from hgts_qualityproject where nvl(dr,0) = 0 and "
				+ " billno = '"+code+"' and id<>'"+id+"'";
		SQLParameter para = new SQLParameter();

		int iret = ValueUtils.getInt(getDao().executeQuery(sql, para, pro), -1);
		if (iret >= 1) {
				ExceptionUtils.asBusinessRuntimeException("质检项目编码重复！");
		}

		} catch (DAOException e) {
			
			ExceptionUtils.asBusinessRuntimeException(e.getMessage());
		}

	}

}
