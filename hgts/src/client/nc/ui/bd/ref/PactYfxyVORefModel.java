package nc.ui.bd.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.hgts.pub.HgtsPubConst;

public class PactYfxyVORefModel extends AbstractRefModel {

	public PactYfxyVORefModel() {
		super();
		init();
	}

	private void init(){

		setRefNodeName("运费协议");
		setRefTitle("运费协议");
		//查询字段-显示
		setFieldCode(new String[] {
				" contcode",
				" vbillno",
				" hgts_sopact.def4 ",
				" nvl((select name from org_orgs s where nvl(dr,0)=0 and isbusinessunit='Y' and s.pk_org=hgts_sopact.pk_org),'') pk_org",
			    " nvl((select name from hgts_mine where nvl(dr, 0) = 0 and hgts_mine.id = hgts_pact_b.kuang), '') kuang ",
				" nvl((select name from bd_customer  where nvl(dr, 0) = 0  and pk_customer = hgts_sopact.cust),  '') cust ",	
			    " nvl((select name from bd_defdoc where nvl(dr, 0) = 0 and pk_defdoc = hgts_sopact.transport), '') transport "	,
			    " nvl((select name from bd_supplier  where nvl(dr, 0) = 0  and bd_supplier.pk_supplier = hgts_sopact.pk_supplier), '')  supplier "	
			  //"nvl((select name from bd_defdoc where nvl(dr,0)=0 and pk_defdoc=pk_balatype),'') pk_balatype"			
		});
		//显示字段说明
		setFieldName(new String[] {
				"合同编号",
				"单据号",
				"协议名称",
				"组织",
				"矿别",
				"买受人",
				"运输方式",
				"运输公司"
				//"结算方式"
		});
		setHiddenFieldCode(new String[] {
				"hgts_pact_b.pk_pact_b"				
		});
		setPkFieldCode("hgts_pact_b.pk_pact_b");
		setWherePart("1=1 and pk_billtypeid='"+HgtsPubConst.CONTRACT_YFXY+"' and nvl(hgts_sopact.dr,0)=0 and nvl(hgts_pact_b.dr,0)=0 and hgts_sopact.approvestatus=1 ");
		setTableName("hgts_sopact inner join hgts_pact_b on hgts_sopact.pk_pact=hgts_pact_b.pk_pact");
		setRefCodeField("vbillno");
		setRefNameField("vbillno");

	}

}