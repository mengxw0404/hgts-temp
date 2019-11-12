package nc.impl.pub.ace;

import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistInsertBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistUpdateBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistDeleteBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistSendApproveBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistUnSendApproveBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistApproveBP;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceSendcarlistPubServiceImpl {
	// ����
	public AggSendCarListHVO[] pubinsertBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggSendCarListHVO> transferTool = new BillTransferTool<AggSendCarListHVO>(
					clientFullVOs);
			// ����BP
			AceSendcarlistInsertBP action = new AceSendcarlistInsertBP();
			AggSendCarListHVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceSendcarlistDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggSendCarListHVO[] pubupdateBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggSendCarListHVO> transferTool = new BillTransferTool<AggSendCarListHVO>(
					clientFullVOs);
			AceSendcarlistUpdateBP bp = new AceSendcarlistUpdateBP();
			AggSendCarListHVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSendCarListHVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSendCarListHVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSendCarListHVO> query = new BillLazyQuery<AggSendCarListHVO>(
					AggSendCarListHVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	// �ύ
	public AggSendCarListHVO[] pubsendapprovebills(
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills)
			throws BusinessException {
		AceSendcarlistSendApproveBP bp = new AceSendcarlistSendApproveBP();
		AggSendCarListHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public AggSendCarListHVO[] pubunsendapprovebills(
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills)
			throws BusinessException {
		AceSendcarlistUnSendApproveBP bp = new AceSendcarlistUnSendApproveBP();
		AggSendCarListHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public AggSendCarListHVO[] pubapprovebills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceSendcarlistApproveBP bp = new AceSendcarlistApproveBP();
		AggSendCarListHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����

	public AggSendCarListHVO[] pubunapprovebills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceSendcarlistUnApproveBP bp = new AceSendcarlistUnApproveBP();
		AggSendCarListHVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}