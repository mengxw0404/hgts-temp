package nc.ui.bd.ref;


/**
 * �ɳ��� ��
 * @author cl
 *
 */
public class ToolVOToPcdRefModel extends AbstractRefModel {

	public ToolVOToPcdRefModel() {
		super();
		init();
	}
	
	private void init(){
	
		setRefNodeName("���乤�߹���");
		setRefTitle("���乤�߹���");
		setFieldCode(new String[] {
		"name",
		"code",
		"drivername",
		"drivertel",
		"driveridcord",
		"pk_supplier"
				});
		setFieldName(new String[] {
		"����",
		"����",
		"˾������",
		"˾���绰",
		"˾�����֤��",
		"��Ӧ��"
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
}