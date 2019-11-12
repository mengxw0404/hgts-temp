package nc.ui.hgts.invoicesheet.action;

import java.awt.event.ActionEvent;

import nc.ui.hgts.ff.pub.AddRefAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

public class AddSendNoticeAction extends AddRefAction {



	/**
	 * 
	 */
	private static final long serialVersionUID = 3286430955995336466L;

	@Override
	protected String getCurrBilltype() {
		// TODO 自动生成的方法存根
		return "YX11";
	}


	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根
		super.doAction(arg0);
	}


	@Override
	public void fieldsControll() {
		Object sendnoticebillno=getEditor().getBillCardPanel().getHeadItem("sendnoticebillno").getValueObject();
		Object pk_sendnoticebill=getEditor().getBillCardPanel().getHeadItem("pk_fytzd").getValueObject();
		try {
			SendnoticebillHVO[] hvos=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+sendnoticebillno+"' and pk_sendnoticebill='"+pk_sendnoticebill+"'");
			if(null !=hvos && hvos.length>0){
				
				getEditor().getBillCardPanel().getHeadItem("dbilldate").setValue(AppContext.getInstance().getServerTime());
				getEditor().getBillCardPanel().getHeadItem("pk_dept").setValue(hvos[0].getAttributeValue("pk_dept"));
				getEditor().getBillCardPanel().getHeadItem("pk_group").setValue(hvos[0].getAttributeValue("pk_group"));
				getEditor().getBillCardPanel().getHeadItem("pk_org").setValue(hvos[0].getAttributeValue("pk_org"));
				getEditor().getBillCardPanel().getHeadItem("pk_org_v").setValue(hvos[0].getAttributeValue("pk_org_v"));
				
				getEditor().getBillCardPanel().getHeadItem("vbillstatus").setValue(ApproveStatus.FREE);
				getEditor().getBillCardPanel().getHeadItem("pk_billtypeid").setValue( HgtsPubConst.FHJLD);
				getEditor().getBillCardPanel().getHeadItem("pk_billtypecode").setValue("YX11");
				getEditor().getBillCardPanel().getHeadItem("pk_transporttype").setValue(HgtsPubConst.TRANSPORT_QY);
				getEditor().getBillCardPanel().getTailItem("creator").setValue(AppContext.getInstance().getPkUser());
				getEditor().getBillCardPanel().getTailItem("creationtime").setValue(AppContext.getInstance().getServerTime());
				getEditor().getBillCardPanel().getHeadItem("sendnoticebillno").setValue(hvos[0].getAttributeValue("vbillno"));
				getEditor().getBillCardPanel().getHeadItem("pk_kc").setValue(hvos[0].getAttributeValue("pk_fhkc"));
				getEditor().getBillCardPanel().getHeadItem("pk_cust").setValue(hvos[0].getAttributeValue("pk_cust"));
				getEditor().getBillCardPanel().getHeadItem("pk_stordoc").setValue(hvos[0].getAttributeValue("pk_stordoc"));
				getEditor().getBillCardPanel().getHeadItem("pk_balatype").setValue(hvos[0].getAttributeValue("pk_balatype"));//结算方式
				getEditor().getBillCardPanel().getHeadItem("pk_busitype").setValue(hvos[0].getAttributeValue("pk_busitype"));//业务类型
				getEditor().getBillCardPanel().getHeadItem("pk_supplier").setValue(hvos[0].getAttributeValue("pk_supplier"));//运输公司
				getEditor().getBillCardPanel().getHeadItem("yfxycode").setValue(hvos[0].getAttributeValue("yfxycode"));//运输协议
				getEditor().getBillCardPanel().getHeadItem("zxxycode").setValue(hvos[0].getAttributeValue("zxxycode"));//装卸协议
				getEditor().getBillCardPanel().getHeadItem("def4").setValue(hvos[0].getAttributeValue("pk_inv"));
				getEditor().getBillCardPanel().getHeadItem("pk_fytzd").setValue(hvos[0].getPrimaryKey());//发运通知单主键
				
				getEditor().getBillCardPanel().getHeadItem("jytype").setValue(hvos[0].getAttributeValue("jytype"));
				getEditor().getBillCardPanel().getHeadItem("settlezt").setValue(hvos[0].getAttributeValue("settlezt"));
				getEditor().getBillCardPanel().getHeadItem("isks").setValue(hvos[0].getAttributeValue("isks"));
			

				
				String hpk=hvos[0].getPrimaryKey();
				
				SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, " nvl(dr,0)=0 and nvl(rowcloseflag,'N')='N' and nvl(blatest ,'N')='Y' and pk_sendnoticebill='"+hpk+"'");
				if(null !=bvos && bvos.length>0){
					InvoicesheetBVO bvo=new InvoicesheetBVO();
					UFDouble syl=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("shul")).sub(HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("yzxnum")));

					bvo.setAttributeValue("crowno", 10);
					bvo.setAttributeValue("pz", bvos[0].getAttributeValue("pz"));
					bvo.setAttributeValue("syl", syl);
					bvo.setAttributeValue("fnum", syl);
					// 存放发运通知单的相关信息，保持 后面结算时的逻辑
					bvo.setAttributeValue("vsourcecode",  hvos[0].getAttributeValue("vbillno")); 
					bvo.setAttributeValue("csourceid", bvos[0].getPk_sendnoticebill());
					bvo.setAttributeValue("csourcebid", bvos[0].getPrimaryKey()); // 存放发运通知单子表主键

					InvoicesheetBVO[] items=new InvoicesheetBVO[1];
					items[0]=bvo;
					getEditor().getBillCardPanel().getBillModel().setBodyDataVO(items);
					getEditor().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
				}
			}
		} catch (UifException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}


	}

}
