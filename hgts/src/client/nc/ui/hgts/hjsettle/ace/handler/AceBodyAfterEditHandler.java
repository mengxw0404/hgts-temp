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
 *单据表体字段编辑后事件
 * 
 * @since 6.0
 * @version 2011-7-12 下午08:17:33
 * @author duy
 */
public class AceBodyAfterEditHandler extends ParentAceBodyAfterEditHandler /*implements IAppEventHandler<CardBodyAfterEditEvent>*/ {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if(e.getKey().equals("def6")){
			BillModel bm = e.getBillCardPanel().getBillModel();
			int row=e.getRow();
			//数量：结算金额/结算单价jsmny /jsprice
			UFDouble num = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "jsmny")).div( HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "jsprice"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			UFDouble newDef6 = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, e.getKey())).setScale(2, BigDecimal.ROUND_HALF_UP);//调整后含税单价
			UFDouble rate = HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row,"rate")).setScale(2, BigDecimal.ROUND_HALF_UP);//获取税率
			UFDouble newDef7 = newDef6.div(UFDouble.ONE_DBL.add(rate.div(100))).setScale(2, BigDecimal.ROUND_HALF_UP);//无税单价：含税/1+R
			UFDouble newDef8 = newDef6.multiply(num).setScale(2, BigDecimal.ROUND_HALF_UP);//价税合计 ：含税*数量
			UFDouble newDef9 = newDef7.multiply(num).setScale(2, BigDecimal.ROUND_HALF_UP);//无税金额 ：无税*数量
			UFDouble newDef10 = newDef8.sub(newDef9).setScale(2, BigDecimal.ROUND_HALF_UP);//税额:价税合计-无税金额
			bm.setValueAt(newDef7,row,"def7");			//调价后无税单价
			bm.setValueAt(newDef8,row,"def8");		    //调价后价税合计 
			bm.setValueAt(newDef9,row,"def9");	        //调价后无税金额
			bm.setValueAt(newDef10,row,"def10");		//调价后税额
		}else{
			super.handleAppEvent(e);
		}

	} 

	public void setBodyValue(BillModel bm,int row,UFDouble price,UFDouble settlezhegl,UFDouble rate){
		UFDouble jsmny=price.multiply(settlezhegl);		
		UFDouble norateprice=price.div((rate.div(100).add(1)));
		UFDouble noratemny=norateprice.multiply(settlezhegl);				
		UFDouble ntaxratemny=price.multiply(settlezhegl).sub(noratemny);
		bm.setValueAt(price,row,"jsprice");				//结算单价
		bm.setValueAt(jsmny,row,"jsmny");				//结算金额
		bm.setValueAt(norateprice,row,"norateprice");	//无税单价
		bm.setValueAt(noratemny,row,"noratemny");		//无税金额
		bm.setValueAt(ntaxratemny,row,"def14"); 		//税额
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

				// 2019年3月7日 modify 取出质量指标  调整价格   由之前从价格政策上取，现在统一从通知单上取
				//	tz_zlzb=getTz_quaindex(pk_pricepolicy, jgz, huif,pk_busitype);
				FormulaParseTool tool =new FormulaParseTool();
				String pk_send_h=tool.getNameByID("hgts_sendnoticebill", "pk_sendnoticebill", "vbillno", sendnoticebillno);
				try {					
					tz_zlzb=new QryBDAction().getTz_quaindex(pk_send_h, tool, v_huif);
				} catch (UifException e) {
					e.printStackTrace();
				}
				//算价格政策价格、价格政策金额、结算金额、结算价格
				// 付款方式优惠、区域优惠是下调，从发运通知单取出来的调整价格是正数，故做减法；
				// 质量指标：根据灰分算出的值如果是正数，表示含杂质少，得上调，负数下调，故加法
				UFDouble price=gpprice.sub(tz_paytype.abs()).sub(tz_qyyh.abs()).sub(tz_ysfsyh.abs()).add(tz_zlzb);
				return price;
			}
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		return UFDouble.ZERO_DBL;
	}
}
