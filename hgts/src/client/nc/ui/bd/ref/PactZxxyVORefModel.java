package nc.ui.bd.ref;

import java.util.HashSet;
import java.util.Set;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.hgts.pub.HgtsPubConst;

public class PactZxxyVORefModel extends AbstractRefModel {
	private Set fomulaSet;
	public PactZxxyVORefModel() {
		super();
		fomulaSet = new HashSet();
		init();
	}
	
	private void init(){
	
		setRefNodeName("����װжЭ��");
		setRefTitle("����װжЭ��");
		setFieldCode(new String[] {
		" contcode " ,
		" vbillno ",	
		" hgts_sopact.def4 ",	
		"  nvl((select name from org_orgs s where nvl(dr, 0) = 0 and isbusinessunit = 'Y' and s.pk_org = hgts_sopact.pk_org), '') pk_org ",
	    "  nvl((select name from hgts_mine where nvl(dr, 0) = 0 and hgts_mine.id = hgts_pact_b.kuang), '') kuang ",
	    "  nvl((select name from bd_defdoc where nvl(dr, 0) = 0 and pk_defdoc = hgts_sopact.transport), '') transport "
		 });
		setFieldName(new String[] {
		"��ͬ���",
		"���ݺ�",
		"Э������",
		"��֯",
		"���",
		"���䷽ʽ"	
		});
		setDefaultFieldCount(4);
	
		//����ʵ����
		setHiddenFieldCode(new String[] {
				"hgts_pact_b.pk_pact_b"
		});
		setPkFieldCode("hgts_pact_b.pk_pact_b");
		
		setTableName("hgts_sopact inner join hgts_pact_b on hgts_sopact.pk_pact=hgts_pact_b.pk_pact");
		setWherePart("1=1 and pk_billtypeid='"+HgtsPubConst.CONTRACT_ZXXY+"' and nvl(hgts_sopact.dr,0)=0 and nvl(hgts_pact_b.dr,0)=0 and hgts_sopact.approvestatus=1 ");
		
		setRefCodeField("vbillno");
		setRefNameField("hgts_sopact.def4");
	    resetFieldName();
	   
	}
	
}