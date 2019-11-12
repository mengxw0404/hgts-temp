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
		// TODO �Զ����ɵķ������
		int row=e.getRow();
		String pk_project=HgtsPubTool.getStringNullAsTrim(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "pk_project"));
		UFDouble minv=HgtsPubTool.getUFDoubleNullAsZero(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "minv")).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble maxv=HgtsPubTool.getUFDoubleNullAsZero(e.getBillCardPanel().getBillModel("pk_cont_yzyj_b").getValueAt(row, "maxv")).setScale(2, UFDouble.ROUND_HALF_UP);
		UFDouble bj=new UFDouble(0.5).setScale(1, UFDouble.ROUND_HALF_UP);
		UFDouble tjjs=new UFDouble(-8).setScale(0, UFDouble.ROUND_HALF_UP);
		// 1���ҷּ��㹫ʽ
		if(e.getKey().equals("pk_project") || e.getKey().equals("minv")
				|| e.getKey().equals("maxv")){			

			if(null !=pk_project && !"".equals(pk_project)
					&& null !=minv && null !=maxv
					//&& HgtsPubConst.HF.equals(pk_project)
					&& pk_project.contains("��")
					){
				String jgfd="";
				// �۸񸡶�=������ֵ-10��/����*�����ۻ�����
				// ���ֵ��������ֵ����10���۸񸡶�=������ֵ-ֵ�������Сֵ��/����*�����ۻ�����
				if(maxv.doubleValue()>10){
					jgfd="������ֵ-"+minv+"��/"+bj+"*��"+tjjs+"��";
				}else{
					//   ֵ��������ֵС��10���۸񸡶�=������ֵ-ֵ��������ֵ��/����*�����ۻ�����
					jgfd="������ֵ-"+maxv+"��/"+bj+"*��"+tjjs+"��";
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
		// 2������˰��
		if(e.getKey().equals("inv")){
			String pk_inv=HgtsPubTool.getStringNullAsTrim(e.getValue());
			FormulaParseTool tool=new FormulaParseTool();
			rate=tool.getTaxrate(pk_inv);
			model.setValueAt(rate+"", row, "rate");
		}	
		// ����
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
		// ��˰����
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
		// �༭˰�ʣ��㲻��˰���ۡ�����˰���
		else if(e.getKey().equals("rate")){
			rate=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			ton=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "ton"));
			price=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "price"));
			bmny=HgtsPubTool.getUFDoubleNullAsZero(model.getValueAt(row, "bmny"));
			this.setBodyValue(price, ton, rate, bmny, model, row);
		}
		// ���Ƽ�
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
		// �༭��˰����������
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
	 * Ϊ ��˰���ۡ���˰��˰�� ��ֵ
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
