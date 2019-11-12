package nc.impl.pub.ace;

import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetApproveBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetDeleteBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetInsertBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetInvalidBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetSendApproveBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetUnApproveBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetUnSendApproveBP;
import nc.bs.hgts.invoicesheet.ace.bp.AceInvoicesheetUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceInvoicesheetPubServiceImpl {
	// 新增
	public AggInvoicesheetHVO[] pubinsertBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(
					clientFullVOs);
			// 调用BP
			AceInvoicesheetInsertBP action = new AceInvoicesheetInsertBP();
			AggInvoicesheetHVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceInvoicesheetDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggInvoicesheetHVO[] pubupdateBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(
					clientFullVOs);
			AceInvoicesheetUpdateBP bp = new AceInvoicesheetUpdateBP();
			InvoicesheetBVO[] bvos=(InvoicesheetBVO[]) originBills[0].getChildrenVO();
			if(null !=bvos && bvos.length>0){
				UFDouble maoz=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("maoz"));
				InvoicesheetHVO hvo=clientFullVOs[0].getParentVO();
				String upnote=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("upnote"));
				if(null==upnote || "".equals(upnote)){
					upnote="历史毛重：";
				}
				upnote=upnote+maoz+"、";
				
				hvo.setAttributeValue("upnote", upnote);
				hvo.setAttributeValue("upflag", "Y");
			}
			
			AggInvoicesheetHVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggInvoicesheetHVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggInvoicesheetHVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggInvoicesheetHVO> query = new BillLazyQuery<AggInvoicesheetHVO>(
					AggInvoicesheetHVO.class);
			bills = query.query(queryScheme, " order by vbillno");
		
			
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
	public AggInvoicesheetHVO[] pubsendapprovebills(
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills)
			throws BusinessException {
		AceInvoicesheetSendApproveBP bp = new AceInvoicesheetSendApproveBP();
		AggInvoicesheetHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggInvoicesheetHVO[] pubunsendapprovebills(
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills)
			throws BusinessException {
		AceInvoicesheetUnSendApproveBP bp = new AceInvoicesheetUnSendApproveBP();
		AggInvoicesheetHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggInvoicesheetHVO[] pubapprovebills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceInvoicesheetApproveBP bp = new AceInvoicesheetApproveBP();
		AggInvoicesheetHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggInvoicesheetHVO[] pubunapprovebills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceInvoicesheetUnApproveBP bp = new AceInvoicesheetUnApproveBP();
		AggInvoicesheetHVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

	/**
	 * 撤换发运通知单
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 */
	public AggInvoicesheetHVO[] invalid(AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			return new AceInvoicesheetInvalidBP().invalid(clientFullVOs,originBills);
			/*
			// 加锁 + 检查ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(clientFullVOs);
			AggInvoicesheetHVO[] retvos = new AceInvoicesheetInvalidBP().invalid(clientFullVOs,originBills);
				// 构造返回数据
			return transferTool.getBillForToClient(retvos);
			*/	
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	public  AggInvoicesheetHVO[] invalid(AggInvoicesheetHVO[] clientFullVOs) throws BusinessException {
		try {
			/*// 加锁 + 检查ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(clientFullVOs);
			AggInvoicesheetHVO[] retvos = new AceInvoicesheetInvalidBP().invalid(clientFullVOs);
						// 构造返回数据
			return transferTool.getBillForToClient(retvos);*/
			return new AceInvoicesheetInvalidBP().invalid(clientFullVOs);
	
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}


}