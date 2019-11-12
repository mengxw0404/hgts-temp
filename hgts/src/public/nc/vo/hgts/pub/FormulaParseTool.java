package nc.vo.hgts.pub;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

import java.util.List;
import java.util.ArrayList;
/**
 * 公式执行器 工具类  分前台后台  请区分调用
 * @author zhanghlg
 *
 */
public class FormulaParseTool {
	
	private static nc.bs.pub.formulaparse.FormulaParse fp = new nc.bs.pub.formulaparse.FormulaParse();

	/**
	 * zhf 后台公式执行器
	 * @param fomular
	 * @param names
	 * @param values
	 * @return
	 * @throws BusinessException
	 */
	public static final Object execFomularForBS(String fomular, String[] names,
			String[] values) throws BusinessException {
		fp.setExpress(fomular);
		if(names == null || names.length == 0){
			return fp.getValue();
		}
		if (names.length != values.length) {
			throw new BusinessException("传入参数异常");
		}
		int index = 0;
		for (String name : names) {
			fp.addVariable(name, values[index]);
			index++;
		}
		return fp.getValue();
	}
	
	public String getNameByID(String tablename, String name, String colNm,String id) {
		nc.ui.pub.formulaparse.FormulaParse parse = new nc.ui.pub.formulaparse.FormulaParse();
		String express = "name->getColValue(\"" + tablename + "\", \"" + name
		+ "\", \"" + colNm + "\", value)";
		// 设置公式
		parse.setExpress(express);
		// 添加参数
		List<String> list = new ArrayList<String>();
		list.add(id);
		parse.addVariable("value", list);
		// 结果
		String[] values = parse.getValueS();
		return values == null ? null : values[0];
	}

	/**
	 * 后台调用
	 * @param tablename
	 * @param name
	 * @param colNm
	 * @param id
	 * @return
	 */
	public String getBsNameByID(String tablename, String name, String colNm,String id) {
		nc.bs.pub.formulaparse.FormulaParse parse = new nc.bs.pub.formulaparse.FormulaParse();
		String express = "name->getColValue(\"" + tablename + "\", \"" + name
		+ "\", \"" + colNm + "\", value)";
		// 设置公式
		parse.setExpress(express);
		// 添加参数
		List<String> list = new ArrayList<String>();
		list.add(id);
		parse.addVariable("value", list);
		// 结果
		String[] values = parse.getValueS();
		return values == null ? null : values[0];
	}
	
	/**
	 * 获取税率
	 * @param pk_material
	 * @return
	 */
	public UFDouble getTaxrate(String pk_material){
		String date=AppContext.getInstance().getServerTime().toString().substring(0, 10);
		String sql="select taxrate from bd_taxrate "
				+ " where nvl(dr, 0) = 0 "
				+ " and pk_taxcode = (select pk_taxcode from bd_taxcode where nvl(dr, 0) = 0"
				+ " and mattaxes =(select pk_mattaxes from bd_mattaxes where nvl(dr, 0) = 0 "
				+ " and pk_mattaxes =(select pk_mattaxes from bd_material_v where nvl(dr, 0) = 0 "
                + " and pk_material = '"+pk_material+"')))"
                + " and (substr(begindate,0,10) <='"+date+"'"
                + " and substr(enddate,0,10) >='"+date+"')";
		IUAPQueryBS bs=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		UFDouble taxrate=UFDouble.ZERO_DBL;
		try {
			taxrate=HgtsPubTool.getUFDoubleNullAsZero(bs.executeQuery(sql, new ColumnProcessor()));
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return taxrate;
	}
}
