package nc.bs.hgts.invoicesheet.ace.bp;

import nc.bs.hgts.invoicesheet.ace.rule.InvoiceWriteBackSendRule;
import nc.bs.hgts.invoicesheet.ace.rule.ValidNumCheckRule;
import nc.bs.hgts.invoicesheet.ace.rule.WriteBackYyflagRule;
import nc.bs.hgts.invoicesheet.plugin.bpplugin.InvoicesheetPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.bs.hgts.invoicesheet.ace.rule.RSetValueRule;

/**
 * 标准单据新增BP
 */
public class AceInvoicesheetInsertBP {

	public AggInvoicesheetHVO[] insert(AggInvoicesheetHVO[] bills) {

		InsertBPTemplate<AggInvoicesheetHVO> bp = new InsertBPTemplate<AggInvoicesheetHVO>(
				InvoicesheetPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggInvoicesheetHVO> processor) {
		// TODO 新增后规则
		IRule<AggInvoicesheetHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YX11");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		//
		InvoiceWriteBackSendRule rule1 = new InvoiceWriteBackSendRule();
		processor.addAfterRule(rule1);
		
		RSetValueRule rule2=new RSetValueRule();
		processor.addAfterRule(rule2);
		///发货计量单保存后，回写引用标识
		WriteBackYyflagRule wbyr=new WriteBackYyflagRule();
		processor.addAfterRule(wbyr);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggInvoicesheetHVO> processer) {
		// TODO 新增前规则
		IRule<AggInvoicesheetHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("YX11");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		ValidNumCheckRule rule1= new ValidNumCheckRule();
		processer.addBeforeRule(rule1);
	}
}
