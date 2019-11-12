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
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO 自动生成的方法存根

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
		// 买方数量、买方水分
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
			bm.setValueAt(custzhegl, row, "custzhegl"); 	// 买方折干量
			bm.setValueAt(settleton, row, "settleton"); 	// 结算吨数
			bm.setValueAt(settlezhegl, row, "settlezhegl");	// 结算折干量
			bm.setValueAt(jz.sub(custton), row, "tuh");		// 途耗=过磅数量-对方过磅数
			bm.setValueAt(custton.sub(custzhegl), row, "kous");	// 扣水=对方过磅数-对方折干数
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
			
			bm.setValueAt(settlezhegl, row, "settlezhegl");	// 结算折干量
			bm.setValueAt(custton.sub(custzhegl), row, "kous");	// 扣水=对方过磅数-对方折干数
			
			this.setBodyValue(bm, row, price, settlezhegl, rate);
			
		}else if(e.getKey().equals("jgzcprice")){
			UFDouble jgzcprice=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			zhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "zhegl"));
			custzhegl=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "custzhegl"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(bm.getValueAt(row, "rate"));
			
			settlezhegl=this.getSettleData(settlezt, zhegl, custzhegl);			
			UFDouble jgzcmny= jgzcprice.multiply(zhegl);			
			
			bm.setValueAt(jgzcmny, row, "jgzcmny");	// 价格政策价格
			this.setBodyValue(bm, row, jgzcprice, settlezhegl, rate);
		}
	
	}
	
	/**
	 * 
	 * @param settlezt：结算主体
	 * @param maif ：卖方折干量/水煤量/灰分
	 * @param cust：买方折干量/水煤量/灰分
	 * @return
	 */
	public UFDouble getSettleData(String settlezt,UFDouble maif,UFDouble cust){
		if("".equals(settlezt) ||"2".equals(settlezt)){ // 卖方
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
