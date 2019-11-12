package nc.bs.hgts.invoicesheet.ace.bp;

import java.util.Date;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.hgts.invoicesheet.ace.rule.DeleteWriteBackSendRule;
import nc.bs.hgts.invoicesheet.plugin.bpplugin.InvoicesheetPluginPoint;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;


/**
 * 标准单据删除BP
 */
public class AceInvoicesheetDeleteBP {

	public void delete(AggInvoicesheetHVO[] bills) {

		DeleteBPTemplate<AggInvoicesheetHVO> bp = new DeleteBPTemplate<AggInvoicesheetHVO>(
				InvoicesheetPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		
		// 2018-10-25 删除 处理为作废 begin
		//bp.delete(bills);
		
		InvoicesheetHVO hvo = bills[0].getParentVO();
		InvoicesheetBVO[] items =(InvoicesheetBVO[]) bills[0].getChildrenVO();
		BaseDAO dao =new BaseDAO();
		UFDouble jingz=UFDouble.ZERO_DBL;
		try{
			for(int i = 0, len = items.length; i < len; i++ ){
				String pkSend=((String)items[i].getAttributeValue("csourcebid"));
				if(pkSend==null||pkSend.length()==0)
					return;
				String sqlb =  "select nvl(jingz,0) from hgts_invoicesheet_b where pk_invoice_b='"+items[i].getPrimaryKey()+"' and nvl(dr,0)=0 ";
				jingz =HgtsPubTool.getUFDoubleNullAsZero(dao.executeQuery(sqlb, new ColumnProcessor()));
				if(null !=jingz && jingz.doubleValue()>0){
					String sql = "update hgts_sendnoticebill_b set yzxnum = nvl(yzxnum,0)-"+jingz
							+" where  pk_sendnoticebill_b = '"+pkSend+"'  ";
					dao.executeUpdate(sql);
				}

				String sql2="update hgts_invoicesheet_b set maoz=piz,jingz=null where pk_invoice_b = '"+items[i].getPrimaryKey()+"' ";
				dao.executeUpdate(sql2);
				
				// 清空派车单子表 发货计量单号字段值
				String pk_sendcarlist_b=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("csrcbid"));// 派车单子表pk
				String sql3="update hgts_sendcarlist_b set invoicebillno='',isdispatch = "
						+ "'N' where pk_sendcarlist_b='"+pk_sendcarlist_b+"' ";
				dao.executeUpdate(sql3);

			}
			String note=hvo.getAttributeValue("note")==null||"".equals(hvo.getAttributeValue("note"))?"删除前净重"+jingz:hvo.getAttributeValue("note")+"删除前净重"+jingz;
			String sqlH="update hgts_invoicesheet set vbillstatus=1,def3='作废',note='"+note+"',"
					+ " approver='"+InvocationInfoProxy.getInstance().getUserId()+"',"
					+ " tapprovetime='"+new UFDateTime(new Date()).toString()+"'"
					+ " where pk_invoice = '"+hvo.getPrimaryKey()+"' ";
			dao.executeUpdate(sqlH);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		// end
	}

	private void addBeforeRule(AroundProcesser<AggInvoicesheetHVO> processer) {
		// TODO 前规则
		IRule<AggInvoicesheetHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggInvoicesheetHVO> processer) {
		
		// TODO 后规则
		IRule<AggInvoicesheetHVO> rule = null;
		 rule = new DeleteWriteBackSendRule();
		 processer.addAfterRule(rule);

	}
}
