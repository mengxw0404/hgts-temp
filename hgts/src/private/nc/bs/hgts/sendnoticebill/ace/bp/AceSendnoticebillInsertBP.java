package nc.bs.hgts.sendnoticebill.ace.bp;

import nc.bs.hgts.sendnoticebill.plugin.bpplugin.SendnoticebillPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.bs.hgts.sendnoticebill.ace.rule.AfterInsertRule;
import nc.bs.hgts.sendnoticebill.ace.rule.BeforeSaveRule;
import nc.bs.hgts.sendnoticebill.ace.rule.ContractBeforeSaveRule;
/**
 * ��׼��������BP
 */
public class AceSendnoticebillInsertBP {

	public AggSendnoticebillHVO[] insert(AggSendnoticebillHVO[] bills) {

		InsertBPTemplate<AggSendnoticebillHVO> bp = new InsertBPTemplate<AggSendnoticebillHVO>(
				SendnoticebillPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSendnoticebillHVO> processor) {
		// TODO ���������
		IRule<AggSendnoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("YX04");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
		.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
		.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		
		rule=new AfterInsertRule();
		processor.addAfterRule(rule);
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSendnoticebillHVO> processer) {
		// TODO ����ǰ����
		IRule<AggSendnoticebillHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("YX04");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
		.setCodeItem("vbillno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
		.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		
//		rule=new BeforeSaveRule();
//		processer.addBeforeRule(rule);
//		 У�� ֪ͨ�����ú�ͬ�������Ƿ񳬶�
//		rule=new ContractBeforeSaveRule();
//		processer.addBeforeRule(rule);
		
	}
}
