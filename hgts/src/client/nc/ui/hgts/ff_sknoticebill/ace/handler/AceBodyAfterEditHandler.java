package nc.ui.hgts.ff_sknoticebill.ace.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.NumberToCN;
import nc.vo.pub.lang.UFDouble;

public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent>{

	public AceBodyAfterEditHandler() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent arg0) {
		// TODO 自动生成的方法存根
		int rowcount=arg0.getBillCardPanel().getBillModel().getRowCount();
		UFDouble skmny=UFDouble.ZERO_DBL;//收款金额
		UFDouble sknum=UFDouble.ZERO_DBL;//收款数量
		//填写数量
		if("ton".equals(arg0.getKey())){
			UFDouble ton=HgtsPubTool.getUFDoubleNullAsZero(arg0.getValue());
			UFDouble price=HgtsPubTool.getUFDoubleNullAsZero(arg0.getBillCardPanel().getBillModel().getValueAt(arg0.getRow(), "price"));
			UFDouble local_money_cr=HgtsPubTool.getUFDoubleNullAsZero(arg0.getBillCardPanel().getBillModel().getValueAt(arg0.getRow(), "local_money_cr"));
			if(local_money_cr.doubleValue() > 0){
				UFDouble nprice=local_money_cr.div(ton);	
				arg0.getBillCardPanel().getBillModel().setValueAt(nprice, arg0.getRow(), "price");
			}
			else if(price.doubleValue() > 0){
				UFDouble mny=ton.multiply(price);	
				arg0.getBillCardPanel().getBillModel().setValueAt(mny, arg0.getRow(), "local_money_cr");
			} 
			for(int i=0;i<rowcount;i++){
				skmny=skmny.add(HgtsPubTool.getUFDoubleNullAsZero(arg0.getBillCardPanel().getBillModel().getValueAt(i, "local_money_cr")));
				sknum=sknum.add(ton);
			}
			
			this.setHeadValue(arg0, skmny,sknum);
			
		}else if(arg0.getKey().equals("local_money_cr")){
			UFDouble local_money_cr=HgtsPubTool.getUFDoubleNullAsZero(arg0.getValue());
			UFDouble price=HgtsPubTool.getUFDoubleNullAsZero(arg0.getBillCardPanel().getBillModel().getValueAt(arg0.getRow(), "price"));
			UFDouble ton= HgtsPubTool.getUFDoubleNullAsZero(arg0.getBillCardPanel().getBillModel().getValueAt(arg0.getRow(), "ton"));
			if(ton.doubleValue() > 0){
				UFDouble nprice=local_money_cr.div(ton);	
				arg0.getBillCardPanel().getBillModel().setValueAt(nprice, arg0.getRow(), "price");
			}
			else if(price.doubleValue() > 0){
				ton =local_money_cr.div(price);	
				arg0.getBillCardPanel().setBodyValueAt(ton, arg0.getRow(), "ton");
			} 
			for(int i=0;i<rowcount;i++){
				skmny=skmny.add(local_money_cr);
				sknum=sknum.add(ton);
			}
			
			this.setHeadValue(arg0, skmny,sknum);
		}
	}
	//反写表头‘收款金额’
	public void setHeadValue(CardBodyAfterEditEvent arg0,UFDouble skmny,UFDouble shnum){
		NumberToCN cn=new NumberToCN();
		String dx=cn.numberCNMontrayUnit(skmny.toBigDecimal());
		arg0.getBillCardPanel().setHeadItem("skmny", skmny);
		arg0.getBillCardPanel().setHeadItem("shnum", shnum);
		arg0.getBillCardPanel().setHeadItem("def0", dx);
	}

}
