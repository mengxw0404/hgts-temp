package nc.ui.hgts.sendcarlist.ref;

import java.awt.Container;


import nc.ui.pubapp.billref.src.DefaultBillReferQuery;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.vo.querytemplate.TemplateInfo;

public class DayPlanSendRefQlgForSendCarList extends DefaultBillReferQuery{
	
	public DayPlanSendRefQlgForSendCarList(Container c, TemplateInfo info) {
		super(c, info);
		// TODO 自动生成的构造函数存根
	}

	protected void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator) {		  
		new DayPlanSendQueryConditionInitializer().initQueryConditionDLG(dlgDelegator);
	}

}
