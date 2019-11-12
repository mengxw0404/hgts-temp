package nc.ui.bd.ref;


public class SendnoticebillHVORefModel extends AbstractRefModel {

	public SendnoticebillHVORefModel() {
		super();
		init();
	}
	
	private void init(){	
		setRefNodeName("发运通知单");
		setRefTitle("发运通知单");
		setFieldCode(new String[] {
		"vbillno",
		"substr(startdate,0,10)",
		"substr(enddate,0,10)",
	//	"dbilldate",
		"nvl((select name from bd_customer where nvl(dr,0)=0 and pk_customer=pk_cust),'')  pk_cust",
		"nvl((select name from hgts_mine where nvl(dr,0)=0 and id=hgts_sendnoticebill.pk_fhkc),'') pk_fhkc",
		"nvl((select name from bd_customer where nvl(dr,0)=0 and pk_customer=receiver),'')  receiver",
		"nvl((select name from bd_material where nvl(dr,0)=0 and pk_material=pz),'')  pz",
		"carnum",
		"hgts_sendnoticebill_b.def6",
		"nvl(carnum,0)-nvl(hgts_sendnoticebill_b.def6,0)",
		"nvl((select name from hgts_station where nvl(dr,0)=0 and id=startstadion),'') startstadion",
		"nvl((select name from hgts_station where nvl(dr,0)=0 and id=arrviestadion),'') arrviestadion"
				});
		setFieldName(new String[] {
		"单据号",
		//"单据日期",
		"开始日期",
		"结束日期",
		"客户",
		"矿场",
		"收货人",
		"煤种",
		"车数",
		"已装车数",
		"剩余车数",
		"发站",
		"到站"
				});
		setHiddenFieldCode(new String[] {
		"hgts_sendnoticebill.pk_sendnoticebill"
		
			});
		setPkFieldCode("hgts_sendnoticebill.pk_sendnoticebill");
		setWherePart(" 1=1 and nvl(hgts_sendnoticebill.dr,0)=0 "
				+ " and nvl(hgts_sendnoticebill_b.dr,0)=0 "
				+ " and vbillstatus=1 "
				//+ " and nvl(closeflag,'N')='N' "
				// TODO 1101 注释
				//+ " and pk_transporttype='"+HgtsPubConst.TRANSPORT_LY+"'"
				+ " and nvl(carnum,0)-nvl(hgts_sendnoticebill_b.def6,0)>0"
				//+ " and nvl(hgts_sendnoticebill_b.shul,0)>nvl(hgts_sendnoticebill_b.yzxnum,0) "
				+ " and nvl(rowcloseflag,'N')='N'");
		setTableName("hgts_sendnoticebill hgts_sendnoticebill inner join hgts_sendnoticebill_b hgts_sendnoticebill_b on hgts_sendnoticebill.pk_sendnoticebill=hgts_sendnoticebill_b.pk_sendnoticebill");
		setRefCodeField("vbillno");
		setRefNameField("pk_cust");
		setDefaultFieldCount(15);
	}
	
}