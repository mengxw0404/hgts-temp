package nc.bs.hgts.invoicesheet.ace.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 
 * @author TR
 *
 */
public class ValidNumCheckRule  implements IRule<AggInvoicesheetHVO> {

	
	public ValidNumCheckRule(){
		super();
	}
	
	@Override
	public void process(AggInvoicesheetHVO[] billvos) {

		if(billvos == null || billvos.length == 0){
			return;
		}
		AggInvoicesheetHVO billvo = billvos[0];
		if(billvo.getParentVO().getAttributeValue("pk_transporttype").equals(HgtsPubConst.TRANSPORT_LY)){
			return;
		}
		
		InvoicesheetBVO[] items =(InvoicesheetBVO[]) billvo.getChildrenVO();
		if(items == null || items.length == 0)
			return;
		BaseDAO dao = new BaseDAO();
		for(int i = 0, len = items.length; i < len; i++ ){
			String pkSend=((String)items[i].getAttributeValue("csourcebid"));
			if(pkSend==null||pkSend.length()==0)
				return;
			Object jingz =items[i].getAttributeValue("jingz") ;//������
			
			Object fnum = items[i].getAttributeValue("syl");//����֪ͨ������

			String bpk=items[i].getPrimaryKey();
			// ͬ����Դ���ľ��غ�
			String condition=" select sum(jingz) from hgts_invoicesheet_b where nvl(dr,0)=0  and csourcebid='"+pkSend+"'";
			if(null!=bpk){
				condition += "and pk_invoice_b<>'"+bpk+"'" ;
			}
			try {
				UFDouble sumJz = HgtsPubTool.getUFDoubleNullAsZero(dao.executeQuery(condition, new ColumnProcessor()));
				// ��ǰ�������еľ��غ� +��ǰ����
				UFDouble ygbnum=HgtsPubTool.getUFDoubleNullAsZero(jingz).add(sumJz);
				if(HgtsPubTool.getUFDoubleNullAsZero(jingz).doubleValue()>HgtsPubTool.getUFDoubleNullAsZero(fnum).doubleValue()){
					ExceptionUtils.wrappBusinessException("��"+(i+1)+"�й������");
					return;	
				}
				
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}

	}
}
