package nc.ui.bd.ref;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.ponder.IPonderItf;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hgts.pub.comm.ClientContext;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.AppContext;

/**
 * 派车单 用
 * @author cl
 *
 */
public class ToolVOToPcdRefModel extends AbstractRefModel {

	public ToolVOToPcdRefModel() {
		super();
		init();
	}
	
	private void init(){
	
		setRefNodeName("运输工具管理");
		setRefTitle("运输工具管理");
		setFieldCode(new String[] {
		"name",
		"code",
		"drivername",
		"drivertel",
		"driveridcord",
		"pk_supplier"
				});
		setFieldName(new String[] {
		"车名",
		"车号",
		"司机姓名",
		"司机电话",
		"司机身份证号",
		"供应商"
				});
		setHiddenFieldCode(new String[] {
		"id",
		"driveraddress",
		"toolcategory",
		"istrue",
		"creator",
		"creationtime",
		"modifier",
		"modifiedtime",
		"pk_group",
		"pk_org",
		"def1",
		"def2",
		"def3",
		"def4",
		"def5",
		"standardweight"
			});
		setPkFieldCode("id");
		setWherePart("1=1 and nvl(dr,0)=0 and istrue='Y'");
		setTableName("hgts_tool");
		setRefCodeField("code");
		setRefNameField("code");
	
	}

	@Override
	protected String getEnvWherePart() {
		try {
			StringBuffer sqlwhe = new StringBuffer();
			sqlwhe.append("1=1 and nvl(dr,0)=0 and istrue='Y' ") ;
			return sqlwhe.toString();
//			IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);
//			Object ofmine = ponder.getDateVO( new ClientContext().getConfigName(), AppContext.getInstance().getPkGroup()).getAttributeValue("ofmine");
//			StringBuffer sqlwhe = new StringBuffer();
//			if(ofmine!=null && !ofmine.toString().equals("")){
//				sqlwhe.append("def1 = '"+ofmine.toString()+"' and istrue='Y' ");
//			}else{
//				sqlwhe.append("1=1 and nvl(dr,0)=0 and istrue='Y' ") ;
//			}
//			return sqlwhe.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
}