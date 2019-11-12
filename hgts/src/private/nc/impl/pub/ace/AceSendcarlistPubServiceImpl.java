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
	// 新增
	public AggSendCarListHVO[] pubinsertBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSendCarListHVO> transferTool = new BillTransferTool<AggSendCarListHVO>(
					clientFullVOs);
			// 调用BP
			AceSendcarlistInsertBP action = new AceSendcarlistInsertBP();
			AggSendCarListHVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceSendcarlistDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSendCarListHVO[] pubupdateBills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSendCarListHVO> transferTool = new BillTransferTool<AggSendCarListHVO>(
					clientFullVOs);
			AceSendcarlistUpdateBP bp = new AceSendcarlistUpdateBP();
			AggSendCarListHVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggSendCarListHVO[] pubsendapprovebills(
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills)
			throws BusinessException {
		AceSendcarlistSendApproveBP bp = new AceSendcarlistSendApproveBP();
		AggSendCarListHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggSendCarListHVO[] pubunsendapprovebills(
			AggSendCarListHVO[] clientFullVOs, AggSendCarListHVO[] originBills)
			throws BusinessException {
		AceSendcarlistUnSendApproveBP bp = new AceSendcarlistUnSendApproveBP();
		AggSendCarListHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggSendCarListHVO[] pubapprovebills(AggSendCarListHVO[] clientFullVOs,
			AggSendCarListHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceSendcarlistApproveBP bp = new AceSendcarlistApproveBP();
		AggSendCarListHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

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