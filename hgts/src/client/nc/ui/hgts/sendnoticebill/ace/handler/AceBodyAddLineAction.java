package nc.ui.hgts.sendnoticebill.ace.handler;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pubapp.uif2app.actions.BodyAddLineAction;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;

/**
 * 发运通知单子表增行
 * @author TR
 *
 */
public class AceBodyAddLineAction extends BodyAddLineAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1817527501572203694L;

	@Override
	public void doAction() {
		super.doAction();
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(this.getCardPanel().getHeadItem("pk_transporttype").getValueObject());
		if(null !=pk_transporttype && !"".equals(pk_transporttype)){
			if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){				
				Object weight=this.getWeight();
				this.getCardPanel().getBillModel("hgts_sendnoticebill_b").setValueAt(weight, 0, "carstrong");
				
				// 2018-3-8
				CommQs comm= new CommQs();
				String pk_cust=HgtsPubTool.getStringNullAsTrim(getCardPanel().getHeadItem("pk_cust").getValueObject());
				String kb=HgtsPubTool.getStringNullAsTrim(getCardPanel().getHeadItem("pk_fhkc").getValueObject());
				String[] str=comm.getInfo(pk_cust, kb);
				if(null !=str && str.length>0){
					getCardPanel().getBillModel("hgts_sendnoticebill_b").setValueAt(str[0], 0, "startstadion");
					getCardPanel().getBillModel("hgts_sendnoticebill_b").setValueAt(str[1], 0, "arrviestadion");
					getCardPanel().getBillModel("hgts_sendnoticebill_b").setValueAt(str[2], 0, "cyfee");
					getCardPanel().getBillModel("hgts_sendnoticebill_b").loadLoadRelationItemValue();
				}
			}
		}
		
		getCardPanel().getBillModel("hgts_sendnoticebill_b").setValueAt("blatest", 0, "Y");
	}

	public  Object getWeight() {
		Object  ton= "" ;
		IUAPQueryBS queryBS=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try {
			String sql="select standardweight from hgts_weightandbill where billtype = (select pk_billtypeid from bd_billtype where pk_billtypecode='YX04') and nvl(dr,0)=0 ";
			ton=queryBS.executeQuery(sql, new ColumnProcessor());

		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		return ton;

	}
}
