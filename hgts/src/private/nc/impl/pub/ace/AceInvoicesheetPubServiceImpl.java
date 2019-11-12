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
	// ����
	public AggInvoicesheetHVO[] pubinsertBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(
					clientFullVOs);
			// ����BP
			AceInvoicesheetInsertBP action = new AceInvoicesheetInsertBP();
			AggInvoicesheetHVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceInvoicesheetDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggInvoicesheetHVO[] pubupdateBills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(
					clientFullVOs);
			AceInvoicesheetUpdateBP bp = new AceInvoicesheetUpdateBP();
			InvoicesheetBVO[] bvos=(InvoicesheetBVO[]) originBills[0].getChildrenVO();
			if(null !=bvos && bvos.length>0){
				UFDouble maoz=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("maoz"));
				InvoicesheetHVO hvo=clientFullVOs[0].getParentVO();
				String upnote=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("upnote"));
				if(null==upnote || "".equals(upnote)){
					upnote="��ʷë�أ�";
				}
				upnote=upnote+maoz+"��";
				
				hvo.setAttributeValue("upnote", upnote);
				hvo.setAttributeValue("upflag", "Y");
			}
			
			AggInvoicesheetHVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
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
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	// �ύ
	public AggInvoicesheetHVO[] pubsendapprovebills(
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills)
			throws BusinessException {
		AceInvoicesheetSendApproveBP bp = new AceInvoicesheetSendApproveBP();
		AggInvoicesheetHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public AggInvoicesheetHVO[] pubunsendapprovebills(
			AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills)
			throws BusinessException {
		AceInvoicesheetUnSendApproveBP bp = new AceInvoicesheetUnSendApproveBP();
		AggInvoicesheetHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public AggInvoicesheetHVO[] pubapprovebills(AggInvoicesheetHVO[] clientFullVOs,
			AggInvoicesheetHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceInvoicesheetApproveBP bp = new AceInvoicesheetApproveBP();
		AggInvoicesheetHVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����

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
	 * ��������֪ͨ��
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 */
	public AggInvoicesheetHVO[] invalid(AggInvoicesheetHVO[] clientFullVOs, AggInvoicesheetHVO[] originBills) throws BusinessException {
		try {
			return new AceInvoicesheetInvalidBP().invalid(clientFullVOs,originBills);
			/*
			// ���� + ���ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(clientFullVOs);
			AggInvoicesheetHVO[] retvos = new AceInvoicesheetInvalidBP().invalid(clientFullVOs,originBills);
				// ���췵������
			return transferTool.getBillForToClient(retvos);
			*/	
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	public  AggInvoicesheetHVO[] invalid(AggInvoicesheetHVO[] clientFullVOs) throws BusinessException {
		try {
			/*// ���� + ���ts
			BillTransferTool<AggInvoicesheetHVO> transferTool = new BillTransferTool<AggInvoicesheetHVO>(clientFullVOs);
			AggInvoicesheetHVO[] retvos = new AceInvoicesheetInvalidBP().invalid(clientFullVOs);
						// ���췵������
			return transferTool.getBillForToClient(retvos);*/
			return new AceInvoicesheetInvalidBP().invalid(clientFullVOs);
	
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}


}