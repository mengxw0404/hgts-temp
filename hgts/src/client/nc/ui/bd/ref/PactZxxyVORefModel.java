package nc.ui.bd.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.hgts.pub.HgtsPubConst;

public class PactZxxyVORefModel extends AbstractRefModel {

	public PactZxxyVORefModel() {
		super();
		init();
	}
	
	private void init(){
	
		setRefNodeName("参照装卸协议");
		setRefTitle("参照装卸协议");
		setFieldCode(new String[] {
		" contcode " ,
		" vbillno ",	
		" hgts_sopact.def4 ",	
		"  nvl((select name from org_orgs s where nvl(dr, 0) = 0 and isbusinessunit = 'Y' and s.pk_org = hgts_sopact.pk_org), '') pk_org ",
	    "  nvl((select name from hgts_mine where nvl(dr, 0) = 0 and hgts_mine.id = hgts_pact_b.kuang), '') kuang ",
	    "  nvl((select name from bd_defdoc where nvl(dr, 0) = 0 and pk_defdoc = hgts_sopact.transport), '') transport "
		 });
		setFieldName(new String[] {
		"合同编号",
		"单据号",
		"协议名称",
		"组织",
		"矿别",
		"运输方式"	
		});
		
		setHiddenFieldCode(new String[] {
				"hgts_pact_b.pk_pact_b"				
		});
		setPkFieldCode("hgts_pact_b.pk_pact_b");
		
		setWherePart("1=1 and pk_billtypeid='"+HgtsPubConst.CONTRACT_ZXXY+"' and nvl(hgts_sopact.dr,0)=0 and nvl(hgts_pact_b.dr,0)=0 and hgts_sopact.approvestatus=1 ");
		setTableName("hgts_sopact inner join hgts_pact_b on hgts_sopact.pk_pact=hgts_pact_b.pk_pact");
		setRefCodeField("vbillno");
		setRefNameField("vbillno");
	
	}
	
}