package nc.ui.hgts.pub.action;

import nc.ui.pubapp.uif2app.query2.IQueryConditionDLGInitializer;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.util.QueryDlgUtils;
import nc.ui.scmpub.query.refregion.QDeptFilter;
import nc.vo.pub.lang.UFBoolean;

public class QueryDLGInitalizer implements IQueryConditionDLGInitializer{

	@Override
	public void initQueryConditionDLG( QueryConditionDLGDelegator DLGDelegator) {

		//≤ø√≈
		QDeptFilter deptFiler = QDeptFilter.createDeptFilterOfALL(DLGDelegator, "pk_dept");
		deptFiler.setbUsedflag(UFBoolean.TRUE);
		deptFiler.setPk_orgCode("pk_org");
		deptFiler.addEditorListener();
		
		DLGDelegator.setDefaultValue("pk_org", QueryDlgUtils.getDefaultOrgUnit());
		DLGDelegator.setPowerEnable(true);
	}

}
