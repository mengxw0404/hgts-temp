package nc.bs.hgts.invoicesheet.ace.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.impl.hgts.ponder.PonderImpl;
import nc.vo.pub.lang.UFBoolean ;

public class InvoiceWriteBackSendRule implements IRule<AggInvoicesheetHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public InvoiceWriteBackSendRule(){
		super();
	}
	@Override
	public void process(AggInvoicesheetHVO[] billvos) {
		// TODO 自动生成的方法存根
		try {
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
			
			//
			PonderImpl ponder=new PonderImpl();
			//
			String ofmine=HgtsPubTool.getStringNullAsTrim(billvo.getParentVO().getAttributeValue("pk_kc"));
			boolean isDIn=ponder.isDrxIn(ofmine, billvo);
			//交易类型
			int jytype=billvo.getParentVO().getAttributeValue("jytype")==null?1:Integer.parseInt(billvo.getParentVO().getAttributeValue("jytype").toString());
			
			for(int i = 0, len = items.length; i < len; i++ ){
				String pkSend=((String)items[i].getAttributeValue("csourcebid"));
				if(pkSend==null||pkSend.length()==0)
					return;
				Object jingz =items[i].getAttributeValue("jingz") ;// 净重量
				//Object fyigb = items[i].getAttributeValue("fyigb");  // 发运通知单已过磅量
				//UFDouble  valid = HgtsPubTool.getUFDoubleNullAsZero(jingz).add(HgtsPubTool.getUFDoubleNullAsZero(fyigb));
				String bpk=items[i].getPrimaryKey();
				String sql="";
				if(null==bpk|| "".equals(bpk)){
					sql = "update hgts_sendnoticebill_b set "
							// 2018-4-13： false 调入洗  非 调入业务  ;  true 调入业务 
							+(!new UFBoolean(isDIn).booleanValue() || jytype==1 || jytype==4?" yzxnum = nvl(yzxnum,0)+"+jingz:" dynum = nvl(dynum,0)+"+jingz)
							+" where  pk_sendnoticebill_b = '"+pkSend+"'";
					getDao().executeUpdate(sql);
				}else{
					// 除当前数据所有的净重和 +当前净重
					String condition=" select sum(jingz) from hgts_invoicesheet_b where nvl(dr,0)=0 and pk_invoice_b<>'"+bpk+"' and csourcebid='"+pkSend+"'";
					UFDouble sumJz =  HgtsPubTool.getUFDoubleNullAsZero(getDao().executeQuery(condition, new ColumnProcessor()));
					UFDouble ygbnum=HgtsPubTool.getUFDoubleNullAsZero(jingz).add(sumJz);
					//回写发运通知单 
					sql = "update hgts_sendnoticebill_b set "
							+(!new UFBoolean(isDIn).booleanValue()|| jytype==1 || jytype==4 ? " yzxnum ="+ygbnum:" dynum ="+jingz)
							+" where  pk_sendnoticebill_b = '"+pkSend+"'";
					getDao().executeUpdate(sql);
//					//回写发货计量单
//					String sql2 = "update hgts_invoicesheet_b set syl ="+ygbnum+" where  csourcebid = '"+pkSend+"'";
//					getDao().executeUpdate(sql2);
				}

			}

		} catch (DAOException e) {
			e.printStackTrace();
			ExceptionUtils.wrappBusinessException("回写发运通知单失败"+e.getMessage());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

}
