package nc.ui.hgts.sendnoticebill.ace.handler;

import nc.ui.bd.ref.PactYfxyVORefModel;
import nc.ui.bd.ref.PactZxxyVORefModel;
import nc.ui.bd.ref.StordocVORefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent ;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.FormulaParseTool;

public class AceHeadTailbeforeEditHandler implements IAppEventHandler<CardHeadTailBeforeEditEvent>{

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent arg0) {
		// TODO 自动生成的方法存根
		Object pk_org=arg0.getBillCardPanel().getHeadItem("pk_org").getValueObject();
		Object pk_fhkc=arg0.getBillCardPanel().getHeadItem("pk_fhkc").getValueObject();
		Object pk_transporttype=arg0.getBillCardPanel().getHeadItem("pk_transporttype").getValueObject();
		int rowcount=arg0.getBillCardPanel().getBillModel("hgts_sendnoticebill_b").getRowCount();
		String pk_pz="";
		if(rowcount>0){					
			String pk_pact_b=HgtsPubTool.getStringNullAsTrim(arg0.getBillCardPanel().getBillModel("hgts_sendnoticebill_b").getValueAt(0, "csourcebid"));
			pk_pz = new FormulaParseTool().getBsNameByID("hgts_pact_b", "inv", "pk_pact_b", pk_pact_b); 
		}
		StringBuffer swhere = new StringBuffer();
		swhere.append( "".equals(pk_org) || null ==pk_org?"":" and hgts_sopact.pk_org='"+pk_org+"'");
		swhere.append( "".equals(pk_fhkc)|| null ==pk_fhkc?"":" and hgts_pact_b.kuang='"+pk_fhkc+"'");//矿厂
		//swhere+="".equals(pk_pz)?"":" and inv='"+pk_pz+"'";煤种	
		//增加矿别

		if(arg0.getKey().equals("yfxycode")){
			UIRefPane refpane=(UIRefPane) arg0.getBillCardPanel().getHeadItem(arg0.getKey()).getComponent();
			PactYfxyVORefModel model=new PactYfxyVORefModel();
			model.addWherePart(swhere.toString());
			refpane.setRefModel(model);			
		}else if(arg0.getKey().equals("zxxycode")){
			UIRefPane refpane=(UIRefPane) arg0.getBillCardPanel().getHeadItem(arg0.getKey()).getComponent();
			PactZxxyVORefModel model=new PactZxxyVORefModel();
			swhere.append("".equals(pk_transporttype)|| null ==pk_transporttype?"":" and hgts_sopact.transport='"+pk_transporttype+"'");//运输方式
			model.addWherePart(swhere.toString());
			refpane.setRefModel(model);
		}else if(arg0.getKey().equals("pk_stordoc") || arg0.getKey().equals("pk_instordoc") ){
			String pk_kc="";
			UIRefPane refpane=(UIRefPane) arg0.getBillCardPanel().getHeadItem(arg0.getKey()).getComponent();
			if(arg0.getKey().equals("pk_stordoc")){				
				pk_kc=HgtsPubTool.getStringNullAsTrim(arg0.getBillCardPanel().getHeadItem("pk_fhkc").getValueObject());
			}else{
				pk_kc=HgtsPubTool.getStringNullAsTrim(arg0.getBillCardPanel().getHeadItem("pk_drkc").getValueObject());
			}
			
			if(null !=pk_kc && !"".equals(pk_kc)){				
				StordocVORefModel model=new StordocVORefModel();
				model.addWherePart(" and pk_mine='"+pk_kc+"'");
				refpane.setRefModel(model);
			}			
		}

		arg0.setReturnValue(true);
	}

}
