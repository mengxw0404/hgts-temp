package nc.impl.pub.ace;

import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactInsertBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactUpdateBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactDeleteBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactSendApproveBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactUnSendApproveBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactApproveBP;
import nc.bs.hgts.sopact_yfxy.ace.bp.AceSopactUnApproveBP;

import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.sopact.AggPactVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceSopactPubServiceImpl {
	// 新增
	public AggPactVO[] pubinsertBills(AggPactVO[] clientFullVOs,
			AggPactVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggPactVO> transferTool = new BillTransferTool<AggPactVO>(
					clientFullVOs);

			AggPactVO[] retvos=null;
			Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
			if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
				//销售合同
				nc.bs.hgts.sopact.ace.bp.AceSopactInsertBP action=new nc.bs.hgts.sopact.ace.bp.AceSopactInsertBP();
				retvos=action.insert(clientFullVOs);
			}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){				
				// 调用BP运费合同
				AceSopactInsertBP action = new AceSopactInsertBP();
				retvos = action.insert(clientFullVOs);
			}else{
				//装卸协议
				nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactInsertBP action=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactInsertBP();
				retvos=action.insert(clientFullVOs);
			}
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggPactVO[] clientFullVOs,
			AggPactVO[] originBills) throws BusinessException {
		try {
			Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
			if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
				new nc.bs.hgts.sopact.ace.bp.AceSopactDeleteBP().delete(clientFullVOs);
			}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){
				// 调用BP
				new AceSopactDeleteBP().delete(clientFullVOs);

			}else{
				new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactDeleteBP().delete(clientFullVOs);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggPactVO[] pubupdateBills(AggPactVO[] clientFullVOs,
			AggPactVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggPactVO> transferTool = new BillTransferTool<AggPactVO>(
					clientFullVOs);

			AggPactVO[] retvos =null;
			Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
			if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
				nc.bs.hgts.sopact.ace.bp.AceSopactUpdateBP bp=new nc.bs.hgts.sopact.ace.bp.AceSopactUpdateBP();
				retvos=bp.update(clientFullVOs, originBills);
			}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){				
				AceSopactUpdateBP bp = new AceSopactUpdateBP();
				retvos = bp.update(clientFullVOs, originBills);
			}else{
				nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUpdateBP bp=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUpdateBP();
				retvos=bp.update(clientFullVOs, originBills);
			}

			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggPactVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggPactVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggPactVO> query = new BillLazyQuery<AggPactVO>(
					AggPactVO.class);
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
	public AggPactVO[] pubsendapprovebills(
			AggPactVO[] clientFullVOs, AggPactVO[] originBills)
					throws BusinessException {

		AggPactVO[] retvos =null;
		Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
		if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
			nc.bs.hgts.sopact.ace.bp.AceSopactSendApproveBP bp=new nc.bs.hgts.sopact.ace.bp.AceSopactSendApproveBP();
			retvos=bp.sendApprove(clientFullVOs, originBills);
		}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){			
			AceSopactSendApproveBP bp = new AceSopactSendApproveBP();
			retvos = bp.sendApprove(clientFullVOs, originBills);
		}else{
			nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactSendApproveBP bp=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactSendApproveBP();
			retvos=bp.sendApprove(clientFullVOs, originBills);
		}

		return retvos;
	}

	// 收回
	public AggPactVO[] pubunsendapprovebills(
			AggPactVO[] clientFullVOs, AggPactVO[] originBills)
					throws BusinessException {
		AggPactVO[] retvos=null;
		Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
		if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
			nc.bs.hgts.sopact.ace.bp.AceSopactUnSendApproveBP bp=new nc.bs.hgts.sopact.ace.bp.AceSopactUnSendApproveBP();
			retvos=bp.unSend(clientFullVOs, originBills);
		}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){			
			AceSopactUnSendApproveBP bp = new AceSopactUnSendApproveBP();
			retvos = bp.unSend(clientFullVOs, originBills);
		}else{
			nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUnSendApproveBP bp=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUnSendApproveBP();
			retvos=bp.unSend(clientFullVOs, originBills);
		}
		return retvos;
	};

	// 审批
	public AggPactVO[] pubapprovebills(AggPactVO[] clientFullVOs,
			AggPactVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}

		AggPactVO[] retvos=null;
		Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
		if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
			nc.bs.hgts.sopact.ace.bp.AceSopactApproveBP bp=new nc.bs.hgts.sopact.ace.bp.AceSopactApproveBP();
			retvos=bp.approve(clientFullVOs, originBills);
		}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){

			AceSopactApproveBP bp = new AceSopactApproveBP();
			retvos = bp.approve(clientFullVOs, originBills);
		}else{
			nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactApproveBP bp=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactApproveBP();
			retvos=bp.approve(clientFullVOs, originBills);
		}

		return retvos;
	}

	// 弃审

	public AggPactVO[] pubunapprovebills(AggPactVO[] clientFullVOs,
			AggPactVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggPactVO[] retvos=null;
		Object billtype=clientFullVOs[0].getParentVO().getAttributeValue("pk_billtypeid");
		if(HgtsPubConst.CONTRACT_SALE.equals(billtype)){
			nc.bs.hgts.sopact.ace.bp.AceSopactUnApproveBP bp=new nc.bs.hgts.sopact.ace.bp.AceSopactUnApproveBP();
			retvos=bp.unApprove(clientFullVOs, originBills);
		}else if(HgtsPubConst.CONTRACT_YFXY.equals(billtype)){

			AceSopactUnApproveBP bp = new AceSopactUnApproveBP();			
			retvos = bp.unApprove(clientFullVOs, originBills);
		}else{
			nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUnApproveBP bp=new nc.bs.hgts.sopact_zxxy.ace.bp.AceSopactUnApproveBP();
			retvos=bp.unApprove(clientFullVOs, originBills);
		}

		return retvos;
	}

}