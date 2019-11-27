package nc.impl.pub.ace;

import nc.bs.dao.BaseDAO;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetInsertBP;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.hgts.sendcarlist.SendCarListHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 * �ɳ��� --�ɳ� ����    ����������
 * @author cl
 * 2019��6��19��
 */
public class AceSendcarlistDispatchToInvoice {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public AceSendcarlistDispatchToInvoice() {
		// TODO �Զ����ɵĹ��캯�����
	}

	public void process(AggSendCarListHVO aggvo) {

		SendCarListHVO head = aggvo.getParentVO();
		// ���ͨ��
		if (Integer.parseInt(head.getAttributeValue("vbillstatus").toString()) == ApproveStatus.APPROVED) {

			SendnoticebillBVO sbvo = null;
			SendnoticebillHVO shvo = null;
			try {
				// �ɳ�����ͷ�Զ�Ӧ�ķ���֪ͨ���ӱ�����
				sbvo = (SendnoticebillBVO) this.getDao().retrieveByPK(
						SendnoticebillBVO.class,
						HgtsPubTool.getStringNullAsTrim(head
								.getAttributeValue("sendbillno")));

				String pk_sendnoticebill = sbvo.getPk_sendnoticebill();
				shvo = (SendnoticebillHVO) this.getDao().retrieveByPK(
						SendnoticebillHVO.class,
						HgtsPubTool.getStringNullAsTrim(pk_sendnoticebill));
				UFDouble syl = HgtsPubTool.getUFDoubleNullAsZero(
						sbvo.getAttributeValue("shul")).sub(
						HgtsPubTool.getUFDoubleNullAsZero(sbvo
								.getAttributeValue("yzxnum")));

				SendCarListBVO[] bodys = (SendCarListBVO[]) aggvo
						.getChildrenVO();

				// 1��Ŀ�굥�ݾۺ�vo������������
				AggInvoicesheetHVO[] bills = new AggInvoicesheetHVO[bodys.length];
				for (int i = 0; i < bodys.length; i++) {
					SendCarListBVO item = bodys[i];
					// 2��Ŀ�굥������vo
					InvoicesheetHVO hvo = new InvoicesheetHVO();
					hvo.setAttributeValue("dbilldate", AppContext.getInstance()
							.getServerTime());
					hvo.setAttributeValue("pk_dept",
							head.getAttributeValue("pk_dept"));
					hvo.setAttributeValue("pk_group",
							head.getAttributeValue("pk_group"));
					hvo.setAttributeValue("pk_org",
							head.getAttributeValue("pk_org"));
					hvo.setAttributeValue("pk_org_v",
							head.getAttributeValue("pk_org_v"));
					hvo.setAttributeValue("vbillstatus", ApproveStatus.FREE);
					hvo.setAttributeValue("pk_billtypeid", HgtsPubConst.FHJLD);
					hvo.setAttributeValue("pk_billtypecode", "YX11");
					hvo.setAttributeValue("pk_transporttype",
							HgtsPubConst.TRANSPORT_QY);
					hvo.setAttributeValue("creator", AppContext.getInstance()
							.getPkUser());
					hvo.setAttributeValue("creationtime", AppContext
							.getInstance().getServerTime());
					hvo.setAttributeValue("sendnoticebillno",
							shvo.getAttributeValue("vbillno"));
					hvo.setAttributeValue("pk_kc",
							head.getAttributeValue("pk_mine"));
					hvo.setAttributeValue("pk_cust",
							head.getAttributeValue("pk_cust"));
					hvo.setAttributeValue("pk_stordoc",
							head.getAttributeValue("pk_stordoc"));
					hvo.setAttributeValue("pk_balatype",
							shvo.getAttributeValue("pk_balatype"));// ���㷽ʽ
					hvo.setAttributeValue("pk_busitype",
							shvo.getAttributeValue("pk_busitype"));// ҵ������
					hvo.setAttributeValue("pk_supplier",
							shvo.getAttributeValue("pk_supplier"));// ���乫˾
					hvo.setAttributeValue("yfxycode",
							shvo.getAttributeValue("yfxycode"));// �����ͬ����
					hvo.setAttributeValue("zxxycode",
							shvo.getAttributeValue("zxxycode"));// װж��ͬ����
					hvo.setAttributeValue("def4",
							head.getAttributeValue("pk_inv"));
					hvo.setAttributeValue("jytype", "1");
					hvo.setAttributeValue("pk_fytzd", pk_sendnoticebill);
					// ֵ��Դ���ռƻ����˵�����
					hvo.setAttributeValue("def5",
							item.getAttributeValue("carno"));
					hvo.setAttributeValue("driver",
							item.getAttributeValue("drivername"));
					hvo.setAttributeValue("driveridcard",
							item.getAttributeValue("driveridcord"));

					// 3��Ŀ�굥���ӱ�vo ����=1
					InvoicesheetBVO[] bvos = new InvoicesheetBVO[1];
					InvoicesheetBVO bvo = new InvoicesheetBVO();
					bvo.setAttributeValue("crowno", 10);
					bvo.setAttributeValue("carno",item.getAttributeValue("carno"));
					bvo.setAttributeValue("pz",head.getAttributeValue("pk_inv"));
					bvo.setAttributeValue("syl", syl);
					bvo.setAttributeValue("fnum", syl);
					bvo.setAttributeValue("csrcbid", item.getPrimaryKey()); // �ɳ����ӱ�id
					bvo.setAttributeValue("csrcid", head.getPrimaryKey()); // �ɳ�������id
					bvo.setAttributeValue("csrccode",head.getAttributeValue("vbillno"));// �ɳ������ݺ�

					// ��ŷ���֪ͨ���������Ϣ������ �������ʱ���߼�
					bvo.setAttributeValue("vsourcecode",shvo.getAttributeValue("vbillno"));
					bvo.setAttributeValue("csourceid", pk_sendnoticebill);
					bvo.setAttributeValue("csourcebid", head.getAttributeValue("sendbillno")); // ��ŷ���֪ͨ���ӱ�����
					bvos[0] = bvo;

					bills[i] = new AggInvoicesheetHVO();
					bills[i].setParentVO(hvo);
					bills[i].setChildrenVO(bvos);

				}
				// 4���������ݿ�
				AceInvoicesheetInsertBP action = new AceInvoicesheetInsertBP();
				AggInvoicesheetHVO[] retvos = action.insert(bills);
				// 5����д�ɳ���
				// if(null !=retvos && retvos.length>0){
				for (AggInvoicesheetHVO bill : retvos) {
					InvoicesheetHVO hvo = (InvoicesheetHVO) bill.getParentVO();
					String vbillno = HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillno"));
					InvoicesheetBVO[] bvos = (InvoicesheetBVO[]) bill.getChildrenVO();
					for(InvoicesheetBVO bvo :bvos ){
						String sql = "update hgts_sendcarlist_b set invoicebillno='"+ vbillno 
								+ "' , isdispatch='Y'  where pk_sendcarlist_b='"+ bvo.getAttributeValue("csrcbid") + "'";
						getDao().executeUpdate(sql);
					}
					
				}

				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
