package nc.itf.hgts;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.pub.BusinessException;

public interface ISendcarlistMaintain {

	public void delete(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] insert(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] update(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggSendCarListHVO[] save(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] unsave(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] approve(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;

	public AggSendCarListHVO[] unapprove(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException;
	
	public void dispatchToInvoice(AggSendCarListHVO clientFullVOs) throws BusinessException;
}
