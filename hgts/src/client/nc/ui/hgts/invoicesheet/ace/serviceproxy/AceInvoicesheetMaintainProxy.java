package nc.ui.hgts.invoicesheet.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.IInvoicesheetMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceInvoicesheetMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IInvoicesheetMaintain query = NCLocator.getInstance().lookup(
				IInvoicesheetMaintain.class);
		return query.query(queryScheme);
	}

}