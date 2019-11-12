package nc.ui.hgts.hjsettle.ace.handler;

import java.math.BigDecimal;

import nc.ui.hgts.hjsettle.ace.parent.handler.ParentAceBodyAfterEditHandler;
import nc.ui.hgts.hjsettle.actions.QryBDAction;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 *���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
public class AceBodyAfterEditHandler extends ParentAceBodyAfterEditHandler /*implements IAppEventHandler<CardBodyAfterEditEvent>*/ {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if(e.getKey().equals("def6")){
			BillModel bm = e.getBillCardPanel().getBillModel();
			int row=e.getRow();
			//������������/���㵥��jsmny /jsprice
			UFDouble num = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "jsmny")).div( HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "jsprice"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			UFDouble newDef6 = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, e.getKey())).setScale(2, BigDecimal.ROUND_HALF_UP);//������˰����
			UFDouble rate = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row,"rate")).setScale(2, BigDecimal.ROUND_HALF_UP);//��ȡ˰��
			UFDouble newDef7 = newDef6.div(UFDouble.ONE_DBL.add(rate.div(100))).setScale(2, BigDecimal.ROUND_HALF_UP);//��˰���ۣ���˰/1+R
			UFDouble newDef8 = newDef6.multiply(num).setScale(2, BigDecimal.ROUND_HALF_UP);//��˰�ϼ� ����˰*����
			UFDouble newDef9 = newDef7.multiply(num).setScale(2, BigDecimal.ROUND_HALF_UP);//��˰��� ����˰*����
			UFDouble newDef10 = newDef8.sub(newDef9).setScale(2, BigDecimal.ROUND_HALF_UP);//˰��:��˰�ϼ�-��˰���
			bm.setValueAt(newDef7,row,"def7");			//���ۺ���˰����
			bm.setValueAt(newDef8,row,"def8");		    //���ۺ��˰�ϼ� 
			bm.setValueAt(newDef9,row,"def9");	        //���ۺ���˰���
			bm.setValueAt(newDef10,row,"def10");		//���ۺ�˰��
		}else{
			super.handleAppEvent(e);
		}

	} 

	public void setBodyValue(BillModel bm,int row,UFDouble price,UFDouble settlezhegl,UFDouble rate){
		UFDouble jsmny=price.multiply(settlezhegl);		
		UFDouble norateprice=price.div((rate.div(100).add(1)));
		UFDouble noratemny=norateprice.multiply(settlezhegl);				
		UFDouble ntaxratemny=price.multiply(settlezhegl).sub(noratemny);
		bm.setValueAt(price,row,"jsprice");				//���㵥��
		bm.setValueAt(jsmny,row,"jsmny");				//������
		bm.setValueAt(norateprice,row,"norateprice");	//��˰����
		bm.setValueAt(noratemny,row,"noratemny");		//��˰���
		bm.setValueAt(ntaxratemny,row,"def14"); 		//˰��
	}
	
	public UFDouble getJsprice(BillModel bm,int row,UFDouble v_huif){
		String sendnoticebillno=HgtsPubTool.getStringNullAsTrim(bm.getValueAt(row,"fytzdh"));
		String pk_send_b=HgtsPubTool.getStringNullAsTrim(bm.getValueAt(row, "csourcebid"));		
		try {
			SendnoticebillBVO bvo = (SendnoticebillBVO) HYPubBO_Client.queryByPrimaryKey(SendnoticebillBVO.class, pk_send_b);
			if(null !=bvo){
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
				UFDouble tz_paytype=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("fkfsyh"));
				UFDouble tz_qyyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("qyyh"));
				UFDouble tz_ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("def13"));
				UFDouble tz_zlzb=UFDouble.ZERO_DBL;

				// 2019��3��7�� modify ȡ������ָ��  �����۸�   ��֮ǰ�Ӽ۸�������ȡ������ͳһ��֪ͨ����ȡ
				//	tz_zlzb=getTz_quaindex(pk_pricepolicy, jgz, huif,pk_busitype);
				FormulaParseTool tool =new FormulaParseTool();
				String pk_send_h=tool.getNameByID("hgts_sendnoticebill", "pk_sendnoticebill", "vbillno", sendnoticebillno);
				try {					
					tz_zlzb=new QryBDAction().getTz_quaindex(pk_send_h, tool, v_huif);
				} catch (UifException e) {
					e.printStackTrace();
				}
				//��۸����߼۸񡢼۸����߽����������۸�
				// ���ʽ�Żݡ������Ż����µ����ӷ���֪ͨ��ȡ�����ĵ����۸�������������������
				// ����ָ�꣺���ݻҷ������ֵ�������������ʾ�������٣����ϵ��������µ����ʼӷ�
				UFDouble price=gpprice.sub(tz_paytype.abs()).sub(tz_qyyh.abs()).sub(tz_ysfsyh.abs()).add(tz_zlzb);
				return price;
			}
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		return UFDouble.ZERO_DBL;
	}
}
