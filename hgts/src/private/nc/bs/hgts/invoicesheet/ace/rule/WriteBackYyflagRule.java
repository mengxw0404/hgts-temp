package nc.bs.hgts.invoicesheet.ace.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;

/**
 * 发货计量单保存后，回写引用标识
 * @author Administrator
 *
 */
public class WriteBackYyflagRule implements IRule<AggInvoicesheetHVO> {
	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}
	public WriteBackYyflagRule(){
		super();
	}
	@Override
	public void process(AggInvoicesheetHVO[] vos) {
		if(null !=vos && vos.length !=0){
			InvoicesheetHVO hvo=vos[0].getParentVO();
			if(hvo.getAttributeValue("pk_transporttype").equals(HgtsPubConst.TRANSPORT_LY)){
				return;
			}
			
			String sendnoticebillno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("sendnoticebillno"));
			if(null !=sendnoticebillno && !"".equals(sendnoticebillno)){
				String sql = "update hgts_sendnoticebill set isyy = 'Y' where vbillno = '"+sendnoticebillno+"'";
				try {
					getDao().executeUpdate(sql);
				} catch (DAOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
