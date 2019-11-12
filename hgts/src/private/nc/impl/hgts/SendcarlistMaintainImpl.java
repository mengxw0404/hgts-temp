package nc.impl.hgts;

import nc.impl.pub.ace.AceSendcarlistDispatchToInvoice;
import nc.impl.pub.ace.AceSendcarlistPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.itf.hgts.ISendcarlistMaintain;
import nc.vo.pub.BusinessException;

public class SendcarlistMaintainImpl extends AceSendcarlistPubServiceImpl
		implements ISendcarlistMaintain {

	@Override
	public void delete(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] insert(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] update(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggSendCarListHVO[] save(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] unsave(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] approve(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggSendCarListHVO[] unapprove(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	/**
	 * 派车--生成过磅单
	 */
	@Override
	public void dispatchToInvoice(AggSendCarListHVO clientFullVOs)
			throws BusinessException {
		new AceSendcarlistDispatchToInvoice().process(clientFullVOs);;
	}

}
