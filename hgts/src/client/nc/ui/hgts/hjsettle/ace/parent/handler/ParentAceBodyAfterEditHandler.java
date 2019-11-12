package nc.ui.hgts.hjsettle.ace.parent.handler;

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

public class ParentAceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent>{

	public ParentAceBodyAfterEditHandler() {
		// TODO �Զ����ɵĹ��캯�����
	}

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO �Զ����ɵķ������

		BillModel bm = e.getBillCardPanel().getBillModel();
		int row=e.getRow();

		Object o_isks=e.getBillCardPanel().getHeadItem("isks").getValueObject();
		UFBoolean isks=o_isks==null||"N".equals(o_isks)?UFBoolean.FALSE:UFBoolean.TRUE;
		String settlezt=HgtsPubTool.getStringNullAsTrim(e.getBillCardPanel().getHeadItem("settlezt").getValueObject());

		UFDouble jz=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "jz"));
		UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "zhegl"));
		UFDouble huif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "huif"));
		
		UFDouble custton=UFDouble.ZERO_DBL;
		UFDouble custzhegl=UFDouble.ZERO_DBL;
		UFDouble custshuif=UFDouble.ZERO_DBL;
		UFDouble custhuif=UFDouble.ZERO_DBL;
		UFDouble settleton=UFDouble.ZERO_DBL;
		UFDouble settlezhegl=UFDouble.ZERO_DBL;
		UFDouble v_huif=UFDouble.ZERO_DBL;
		UFDouble price=UFDouble.ZERO_DBL;
		UFDouble rate=UFDouble.ZERO_DBL;
		// ����������ˮ��
		if(e.getKey().equals("custton") || e.getKey().equals("custshuif")
				|| e.getKey().equals("custhuif")){
			if(e.getKey().equals("custton")){				
				custton=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
				custshuif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custshuif"));
				custhuif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custhuif"));
			}else if(e.getKey().equals("custshuif")){
				custton=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custton"));
				custshuif=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
				custhuif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custhuif"));
			}else if(e.getKey().equals("custhuif")){
				custton=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custton"));
				custshuif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custshuif"));
				custhuif=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			}

			if(isks.booleanValue()){		
				if(custshuif.doubleValue()<=0){
					custzhegl=custton;
				}else{					
					custzhegl=(new UFDouble(100).sub(custshuif)).div(100-8).multiply(custton);
				}
			}else{
				custzhegl=custton;
			}
			
			settleton=this.getSettleData(settlezt, jz, custton);
			settlezhegl=this.getSettleData(settlezt, zhegl, custzhegl);
			v_huif=this.getSettleData(settlezt, huif, custhuif);			

			price=this.getJsprice(bm, row,v_huif);
			rate=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "rate"));			
			bm.setValueAt(custzhegl, row, "custzhegl"); 	// ���۸���
			bm.setValueAt(settleton, row, "settleton"); 	// �������
			bm.setValueAt(settlezhegl, row, "settlezhegl");	// �����۸���
			bm.setValueAt(jz.sub(custton), row, "tuh");		// ;��=��������-�Է�������
			bm.setValueAt(custton.sub(custzhegl), row, "kous");	// ��ˮ=�Է�������-�Է��۸���
			this.setBodyValue(bm, row, price, settlezhegl, rate);

		}else if(e.getKey().equals("custzhegl")){
			custzhegl=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			zhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "zhegl"));
			custhuif=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custhuif"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "rate"));
			custton=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custton"));
			
			settlezhegl=this.getSettleData(settlezt, zhegl, custzhegl);
			v_huif=this.getSettleData(settlezt, huif, custhuif);
			
			price=this.getJsprice(bm, row,v_huif);
			
			bm.setValueAt(settlezhegl, row, "settlezhegl");	// �����۸���
			bm.setValueAt(custton.sub(custzhegl), row, "kous");	// ��ˮ=�Է�������-�Է��۸���
			
			this.setBodyValue(bm, row, price, settlezhegl, rate);
			
		}else if(e.getKey().equals("jgzcprice")){
			UFDouble jgzcprice=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			zhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "zhegl"));
			custzhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custzhegl"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "rate"));
			
			settlezhegl=this.getSettleData(settlezt, zhegl, custzhegl);			
			UFDouble jgzcmny= jgzcprice.multiply(zhegl);			
			
			bm.setValueAt(jgzcmny, row, "jgzcmny");	// �۸����߼۸�
			this.setBodyValue(bm, row, jgzcprice, settlezhegl, rate);
		}
	
	}
	
	/**
	 * 
	 * @param settlezt����������
	 * @param maif �������۸���/ˮú��/�ҷ�
	 * @param cust�����۸���/ˮú��/�ҷ�
	 * @return
	 */
	public UFDouble getSettleData(String settlezt,UFDouble maif,UFDouble cust){
		if("".equals(settlezt) ||"2".equals(settlezt)){ // ����
			return maif;
		}else{
			return cust;
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
