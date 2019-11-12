package nc.ui.hgts.sopact.ace.parent.handler;

import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;

public class ParentAceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent>{

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO 自动生成的方法存根
		int row=e.getRow();
		String pk_project=HgtsPubTool.getStringNullAsTrim(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "pk_project"));
		UFDouble minv=HgtsPubTool.getUFDoubleNullAsZero(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "minv")).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble maxv=HgtsPubTool.getUFDoubleNullAsZero(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "maxv")).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble bj=new UFDouble(0.5).setScale(1, UFDouble.ROUND_HALF_UP);
		UFDouble tjjs=new UFDouble(-8).setScale(0, UFDouble.ROUND_HALF_UP);
		// 1、灰分计算公式
		if(e.getKey().equals("pk_project") || e.getKey().equals("minv")
				|| e.getKey().equals("maxv")){			

			if(null !=pk_project && !"".equals(pk_project)
					&& null !=minv && null !=maxv
					//&& HgtsPubConst.HF.equals(pk_project)
					&& pk_project.contains("灰")
					){
				String jgfd="";
				// 价格浮动=（化验值-10）/步阶*（调价基数）
				// 如果值区间的最大值大于10，价格浮动=（化验值-值区间的最小值）/步阶*（调价基数）
				if(maxv.doubleValue()>10){
					jgfd="（化验值-"+minv+"）/"+bj+"*（"+tjjs+"）";
				}else{
					//   值区间的最大值小于10，价格浮动=（化验值-值区间的最大值）/步阶*（调价基数）
					jgfd="（化验值-"+maxv+"）/"+bj+"*（"+tjjs+"）";
				}

				e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").setValueAt(jgfd, row, "pricechange");
			}
		}
		
		BillModel model=e.getBillCardPanel().getBillModel("pk_pact_b");
		UFDouble gpprice=UFDouble.ZERO_DBL;
		UFDouble ton=UFDouble.ZERO_DBL;
		UFDouble price=UFDouble.ZERO_DBL;
		UFDouble rate=UFDouble.ZERO_DBL;
		UFDouble bmny=UFDouble.ZERO_DBL;
		UFDouble fkfsyh=UFDouble.ZERO_DBL;
		UFDouble qyyh=UFDouble.ZERO_DBL;
		UFDouble ysfsyh=UFDouble.ZERO_DBL;
		// 2、带出税率
		if(e.getKey().equals("inv")){
			String pk_inv=HgtsPubTool.getStringNullAsTrim(e.getValue());
			FormulaParseTool tool=new FormulaParseTool();
			rate=tool.getTaxrate(pk_inv);
			model.setValueAt(rate+"", row, "rate");
		}	
		// 数量
		else if(e.getKey().equals("ton")){
			ton=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			price=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "price"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "rate"));
			
			bmny=ton.multiply(price);			
			/*norateprice=price.div(rate.div(100).add(1)).setScale(2, UFDouble.ROUND_HALF_UP);
			bnoratemny=(price.div(rate.div(100).add(1)).multiply(ton)).setScale(2, UFDouble.ROUND_HALF_UP);
			bratemny=bmny.sub(bnoratemny);*/
			
			model.setValueAt(bmny, row, "bmny");
		/*	model.setValueAt(norateprice, row, "norateprice");
			model.setValueAt(bnoratemny, row, "bnoratemny");
			model.setValueAt(bratemny, row, "bratemny");*/
			
			this.setBodyValue(price, ton, rate, bmny, model, row);
		}
		// 含税单价
		else if(e.getKey().equals("price")){
			price=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			ton=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ton"));
		
			bmny=ton.multiply(price);			
			rate=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "rate"));
			
			fkfsyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "yhprice"));
			qyyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "qyyh"));
			ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ysfsyh"));
			gpprice=price.add(fkfsyh.abs()).add(qyyh.abs()).add(ysfsyh.abs());
			
			model.setValueAt(bmny, row, "bmny");
			model.setValueAt(gpprice, row, "gpprice");
			this.setBodyValue(price, ton, rate, bmny, model, row);
		}	
		// 编辑税率，算不含税单价、不含税金额
		else if(e.getKey().equals("rate")){
			rate=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			ton=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ton"));
			price=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "price"));
			bmny=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "bmny"));
			this.setBodyValue(price, ton, rate, bmny, model, row);
		}
		// 挂牌价
		else if(e.getKey().equals("gpprice")||e.getKey().equals("yhprice")){
			gpprice=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "gpprice"));
			fkfsyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "yhprice"));
			qyyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "qyyh"));
			ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ysfsyh"));
			ton=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ton"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "rate"));

			price=gpprice.sub(fkfsyh.abs()).sub(qyyh.abs()).sub(ysfsyh.abs());
			bmny=ton.multiply(price);
			model.setValueAt(price, row, "price");
			model.setValueAt(bmny, row, "bmny");
			this.setBodyValue(price, ton, rate, bmny, model, row);
		}
		// 编辑含税金额，反算数量
		else if(e.getKey().equals("bmny")){
			bmny=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			price=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "price"));
			rate=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "rate"));
			
			if(null !=price && price.doubleValue()>0){
				ton=bmny.div(price)/*.setScale(2, UFDouble.ROUND_HALF_UP)*/;
				ton=HgtsPubTool.getTon(ton);
				model.setValueAt(ton, row, "ton");
				this.setBodyValue(price, bmny.div(price), rate, bmny, model, row);
			}
		}
	}

	
	/**
	 * 为 无税单价、无税金额、税额 赋值
	 * @param price
	 * @param ton
	 * @param rate
	 * @param bmny
	 * @param model
	 * @param row
	 */
	public void setBodyValue(UFDouble price,UFDouble ton,UFDouble rate,UFDouble bmny,BillModel model,int row){
		UFDouble norateprice=price.div(rate.div(100).add(1)).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble bnoratemny=(price.div(rate.div(100).add(1)).multiply(ton)).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble bratemny=bmny.sub(bnoratemny);
		model.setValueAt(norateprice, row, "norateprice");
		model.setValueAt(bnoratemny, row, "bnoratemny");
		model.setValueAt(bratemny, row, "bratemny");
	}
}
