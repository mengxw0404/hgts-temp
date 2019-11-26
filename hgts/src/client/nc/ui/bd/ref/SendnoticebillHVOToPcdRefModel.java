package nc.ui.bd.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.hgts.pub.HgtsPubConst;

/**
 * �ɳ��� ��
 * @author cl
 *
 */
public class SendnoticebillHVOToPcdRefModel extends AbstractRefModel {

	public SendnoticebillHVOToPcdRefModel() {
		super();
		init();
	}

	private void init(){

		setRefNodeName("����֪ͨ��");
		setRefTitle("����֪ͨ��");
		setFieldCode(new String[] {
				"vbillno",
				"nvl((select name from bd_customer where nvl(dr,0)=0 and pk_customer=pk_cust),'') pk_cust",
				"nvl((select name from hgts_mine where nvl(dr,0)=0 and id=hgts_sendnoticebill.pk_fhkc),'') pk_fhkc",
				"nvl((select name from hgts_stordoc where nvl(dr,0)=0 and pk_stordoc=hgts_sendnoticebill.pk_stordoc),'') pk_stordoc",
				"nvl((select name from bd_material where nvl(dr,0)=0 and pk_material=hgts_sendnoticebill_b.pz),'') pz",
				"nvl(shul,0) shul",
				"nvl(yzxnum,0) yzxnum",
				"nvl(hgts_sendnoticebill_b.shul,0)-nvl(hgts_sendnoticebill_b.yzxnum,0) syl"
		});
		setFieldName(new String[] {
				"���ݺ�",
				"�ͻ�",
				"������",
				"�ֿ�",
				"ú��",
				"����",
				"�ѹ�������",
				"ʣ����"
		});
		setHiddenFieldCode(new String[] {
				"pk_sendnoticebill_b"
		});
		setPkFieldCode("pk_sendnoticebill_b");
		setWherePart(" 1=1 and nvl(hgts_sendnoticebill.dr,0)=0 "
				+ " and nvl(hgts_sendnoticebill_b.dr,0)=0 "
				+ " and vbillstatus=1 "
				+ " and pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'"
				+ " and nvl(hgts_sendnoticebill_b.shul,0)>nvl(hgts_sendnoticebill_b.yzxnum,0) "
				+ " and nvl(rowcloseflag,'N')='N'");
		setTableName("hgts_sendnoticebill hgts_sendnoticebill inner join hgts_sendnoticebill_b hgts_sendnoticebill_b on hgts_sendnoticebill.pk_sendnoticebill=hgts_sendnoticebill_b.pk_sendnoticebill");
		setRefCodeField("vbillno");
		setRefNameField("vbillno");

	}

}