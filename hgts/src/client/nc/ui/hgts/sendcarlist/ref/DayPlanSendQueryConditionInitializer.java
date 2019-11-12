package nc.ui.hgts.sendcarlist.ref;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pubapp.uif2app.query2.IQueryConditionDLGInitializer;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.refregion.QueryDefaultOrgFilter;

public class DayPlanSendQueryConditionInitializer implements IQueryConditionDLGInitializer {

@Override
public void initQueryConditionDLG(QueryConditionDLGDelegator condDLGDelegator) {
  // TODO ��ʼ����ѯģ���߼�
  //�������Ŀǰ�ǿյģ���ҵ�������Ҫ����֯���˵Ĳ����ֶμ��뵽���������
  List<String> targetFields = new ArrayList<String>();
  // TODO ������Ҫ����֯���˵Ĳ����ֶ�
//  targetFields.add("usecompany");
  
  QueryDefaultOrgFilter orgFilter=new QueryDefaultOrgFilter(condDLGDelegator,"pk_org",targetFields);
  orgFilter.addEditorListener();
}

}
