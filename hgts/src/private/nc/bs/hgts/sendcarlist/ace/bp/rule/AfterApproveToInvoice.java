package nc.bs.hgts.sendcarlist.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetInsertBP;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.hgts.sendcarlist.SendCarListHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 * �ɳ���  ��˺�����    ����������
 * @author cl
 * 2019��6��19��
 */
public class AfterApproveToInvoice implements IRule<AggSendCarListHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public AfterApproveToInvoice() {
		// TODO �Զ����ɵĹ��캯�����
	}

	@Override
	public void process(AggSendCarListHVO[] vos) {
		if(null !=vos && vos.length>0){
			FormulaParseTool tool=new FormulaParseTool();
			for(AggSendCarListHVO aggvo:vos){
				SendCarListHVO head=aggvo.getParentVO();
				// ���ͨ��
				if(Integer.parseInt(head.getAttributeValue("vbillstatus").toString())==ApproveStatus.APPROVED){

					SendnoticebillBVO sbvo=null;
					try {
						//�ɳ�����ͷ�Զ�Ӧ�ķ���֪ͨ���ӱ�����
						sbvo = (SendnoticebillBVO) this.getDao().retrieveByPK(SendnoticebillBVO.class,HgtsPubTool.getStringNullAsTrim(head.getAttributeValue("sendbillno")));
					} catch (DAOException e1) {
						e1.printStackTrace();
					}
					String pk_sendnoticebill=sbvo.getPk_sendnoticebill();
					String sendbillno=tool.getBsNameByID("hgts_sendnoticebill", "vbillno", "pk_sendnoticebill",pk_sendnoticebill);
					UFDouble syl=HgtsPubTool.getUFDoubleNullAsZero(sbvo.getAttributeValue("shul")).sub(HgtsPubTool.getUFDoubleNullAsZero(sbvo.getAttributeValue("yzxnum")));
					
					SendCarListBVO[] bodys=(SendCarListBVO[]) aggvo.getChildrenVO();

					// 1��Ŀ�굥�ݾۺ�vo������������
					AggInvoicesheetHVO[] bills=new AggInvoicesheetHVO[bodys.length]; 
					for(int i=0;i<bodys.length;i++){
						SendCarListBVO item=bodys[i];
						// 2��Ŀ�굥������vo
						InvoicesheetHVO hvo=new InvoicesheetHVO();
						hvo.setAttributeValue("dbilldate", AppContext.getInstance().getServerTime());
						hvo.setAttributeValue("pk_dept", head.getAttributeValue("pk_dept"));
						hvo.setAttributeValue("pk_group", head.getAttributeValue("pk_group"));
						hvo.setAttributeValue("pk_org", head.getAttributeValue("pk_org"));
						hvo.setAttributeValue("pk_org_v", head.getAttributeValue("pk_org_v"));
						hvo.setAttributeValue("vbillstatus", ApproveStatus.FREE);
						hvo.setAttributeValue("pk_billtypeid", HgtsPubConst.FHJLD);
						hvo.setAttributeValue("pk_billtypecode", "YX11");
						hvo.setAttributeValue("pk_transporttype", HgtsPubConst.TRANSPORT_QY);
						hvo.setAttributeValue("creator", AppContext.getInstance().getPkUser());
						hvo.setAttributeValue("creationtime", AppContext.getInstance().getServerTime());
						hvo.setAttributeValue("sendnoticebillno", sendbillno);
						hvo.setAttributeValue("pk_kc", head.getAttributeValue("pk_mine"));
						hvo.setAttributeValue("pk_cust", head.getAttributeValue("pk_cust"));
						hvo.setAttributeValue("pk_stordoc", head.getAttributeValue("pk_stordoc"));
						hvo.setAttributeValue("pk_balatype", head.getAttributeValue("pk_balatype"));//���㷽ʽ
						hvo.setAttributeValue("pk_busitype", head.getAttributeValue("pk_busitype"));//ҵ������
						hvo.setAttributeValue("def4", head.getAttributeValue("pk_inv"));
						hvo.setAttributeValue("jytype", "1");
						hvo.setAttributeValue("pk_fytzd",head.getAttributeValue("sendbillno"));
						// ֵ��Դ���ռƻ����˵�����						
						hvo.setAttributeValue("def5", item.getAttributeValue("carno"));
						hvo.setAttributeValue("driver",item.getAttributeValue("drivername"));
						hvo.setAttributeValue("driveridcard",item.getAttributeValue("driveridcord"));

						// 3��Ŀ�굥���ӱ�vo ����=1
						InvoicesheetBVO[] bvos=new InvoicesheetBVO[1];
						InvoicesheetBVO bvo=new InvoicesheetBVO();
						bvo.setAttributeValue("crowno", 10);
						bvo.setAttributeValue("carno", item.getAttributeValue("carno"));
						bvo.setAttributeValue("pz", head.getAttributeValue("pk_inv"));
						bvo.setAttributeValue("syl", syl);
						bvo.setAttributeValue("fnum", syl);
						bvo.setAttributeValue("csrcbid", item.getPrimaryKey()); // �ɳ����ӱ�id
						bvo.setAttributeValue("csrcid", head.getPrimaryKey());	// �ɳ�������id
						bvo.setAttributeValue("csrccode", head.getAttributeValue("vbillno"));// �ɳ������ݺ�
						
						// ��ŷ���֪ͨ���������Ϣ������ �������ʱ���߼�
						bvo.setAttributeValue("vsourcecode", sendbillno); 
						bvo.setAttributeValue("csourceid", pk_sendnoticebill);
						bvo.setAttributeValue("csourcebid", head.getAttributeValue("sendbillno")); // ��ŷ���֪ͨ���ӱ�����
						bvos[0]=bvo;

						bills[i]=new AggInvoicesheetHVO();
						bills[i].setParentVO(hvo);
						bills[i].setChildrenVO(bvos);

					}

					try {
						// 4���������ݿ�
						AceInvoicesheetInsertBP action=new AceInvoicesheetInsertBP();
						AggInvoicesheetHVO[] retvos = action.insert(bills);
						// 5����д�ɳ���
						if(null !=retvos && retvos.length>0){
							for(AggInvoicesheetHVO bill:retvos){
								InvoicesheetHVO hvo=(InvoicesheetHVO) bill.getParentVO();
								String vbillno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillno"));
								InvoicesheetBVO[] bvos=(InvoicesheetBVO[]) bill.getChildrenVO();

								String sql="update hgts_sendcarlist_b set invoicebillno='"+vbillno+"' where pk_sendcarlist_b='"+bvos[0].getAttributeValue("csrcbid")+"'";
								getDao().executeUpdate(sql);
							}
							
							
							// 6����ӡ������
							
							/*IPrintEntryProxy proxy=new PrintEntryProxyImpl();
							proxy.setDataSource(new CardPonderPanelPRTS("40H10603",null));
							proxy.setTemplateID(head.getAttributeValue("pk_org").toString(), "40H10601", AppContext.getInstance().getPkUser(), null);
							proxy.selectTemplate();
							proxy.preview();*/
							
							/*IDataSource dataSource = new CardPonderPanelPRTS("40H10603",  null);
							if(new MetaDataBasedPrintAction().getBeforePrintDataProcess()!=null){
								new MetaDataBasedPrintAction().getBeforePrintDataProcess().processData(retvos);
							}
							PrintEntry print = new PrintEntry(null,dataSource);							
							print.setTemplateID(AppContext.getInstance().getPkGroup(), "40H10601", AppContext.getInstance().getPkUser(), null, "ot");
							print.selectTemplate();					
							print.preview();*/

						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
