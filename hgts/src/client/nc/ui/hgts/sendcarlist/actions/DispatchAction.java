package nc.ui.hgts.sendcarlist.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.ISendcarlistMaintain;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.EditAction;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.tool.BillIndex;

import org.apache.commons.lang.ArrayUtils;

public class DispatchAction extends EditAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8463826150149979675L;
	public DispatchAction(){
		super();
		super.setCode("DispatchAction");
		super.setBtnName("�ɳ�");
	}
	private BillForm editor;
	private BillListView list;
	
	private RefreshSingleAction cardRefreshAction;
	//������ѡ���ֶ�
	private int[] selectIndex = null;

	@Override
	public void doAction(ActionEvent event) throws Exception {
		// TODO �Զ����ɵķ������
		this.getSelectIndex();
		AggSendCarListHVO newObj = null;
		if (null == selectIndex || selectIndex.length == 0) {
			newObj = getOldVO();
		} else {
			newObj = this.getSelectedVO();
		}
		//����У�飺���ɳ����ӱ���Ϣ���޳�
		SendCarListBVO[] bodyvos  = (SendCarListBVO[]) newObj.getChildrenVO();
		List<SendCarListBVO> listbvos = new ArrayList<SendCarListBVO>();
		for(SendCarListBVO bodyvo:bodyvos){
			//
			if(!HgtsPubTool.getUFBooleanNullAsFalse(bodyvo.getAttributeValue("isdispatch")).booleanValue()){
				listbvos.add(bodyvo);
			}	
		}
		//�ж��Ƿ����Ϊ�ɳ���
		if(listbvos.size() > 0){
			newObj.setChildrenVO(listbvos.toArray(new SendCarListBVO[0]));
			ISendcarlistMaintain service = (ISendcarlistMaintain) NCLocator
					.getInstance().lookup(ISendcarlistMaintain.class.getName());
			//���ɹ�����
			service.dispatchToInvoice(newObj);
		}else{
			MessageDialog.showHintDlg(null, "����", "��ѡ�����Ѿ��ɳ��������ظ��ɳ���");
		}
		//ˢ��
		cardRefreshAction.doAction(event);
	}

	/**
	 * ���������������õ���ǰ������ѡ�������VO
	 */
	private AggSendCarListHVO getSelectedVO() {
		AggSendCarListHVO vo = null;
		// ��Ƭ����
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			vo = this.getCloseVOFromBillView(1);
		} else {// �б����
			vo = this.getCloseVOFromBillView(2);
		}
		return vo;
	}
	
	/**
	 * ������������������ǿ�Ƭ���棬�ӿ�Ƭ��ȡ��������
	 * @param billtype :1==��Ƭ ��2==�б�
	 */
	private AggSendCarListHVO getCloseVOFromBillView(int billtype) {
		if (ArrayUtils.isEmpty(this.selectIndex)) {
			return null;
		}
		// ��þ�VO��Ȼ�󽫱�����Ϊѡ���VO���õ�ѡ������VO
		AggSendCarListHVO oldVO = this.getOldVO();
		if (null == oldVO) {
			return null;
		}
		AggSendCarListHVO vo = (AggSendCarListHVO) oldVO.clone();
		BillIndex index = new BillIndex(new AggSendCarListHVO[] { vo });
		IVOMeta meta = vo.getMetaData().getVOMeta(SendCarListBVO.class);
		SendCarListBVO[] itemVOs = new SendCarListBVO[this.selectIndex.length];
		for (int i = 0; i < this.selectIndex.length; ++i) {
			String pk_sendcarlist_b = "";
			//1==��Ƭ ��2==�б�
			if(billtype == 1){
				pk_sendcarlist_b = (String) this.editor.getBillCardPanel()
						.getBodyValueAt(this.selectIndex[i], "pk_sendcarlist_b");
			}else{
				pk_sendcarlist_b = (String) this.list.getBillListPanel().getBodyBillModel()
						.getValueAt(this.selectIndex[i],"pk_sendcarlist_b");
			}
			SendCarListBVO itemVO = (SendCarListBVO) index.get(meta, pk_sendcarlist_b);
			if (null == itemVO) {
				return null;
			}
			itemVOs[i] = itemVO;
		}
		vo.setChildrenVO(itemVOs);
		return vo;
	}
	/**
	 * ���������������õ�ѡ�����
	 */
	private void getSelectIndex() {
		// ��Ƭ����
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			BillCardPanel panel = this.editor.getBillCardPanel();
			this.selectIndex = panel.getBodyPanel().getTable()
					.getSelectedRows();
		} else {// �б����
			BillListPanel panel = this.list.getBillListPanel();
			this.selectIndex = panel.getBodyTable().getSelectedRows();
		}
	}
	/**
	 * ���������������õ�model�еľ�VO
	 */
	private AggSendCarListHVO getOldVO() {
		AggSendCarListHVO vo = (AggSendCarListHVO) this.getModel().getSelectedData();
		return vo;
	}
	

	/**
	 * @return list
	 */
	public BillListView getList() {
		return list;
	}

	/**
	 * @param list Ҫ���õ� list
	 */
	public void setList(BillListView list) {
		this.list = list;
	}
	/**
	 * @return editor
	 */
	public BillForm getEditor() {
		return editor;
	}

	/**
	 * @param editor Ҫ���õ� editor
	 */
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	
	public RefreshSingleAction getCardRefreshAction() {
		return cardRefreshAction;
	}

	public void setCardRefreshAction(RefreshSingleAction cardRefreshAction) {
		this.cardRefreshAction = cardRefreshAction;
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getUiState()==UIState.NOT_EDIT){
			if(getModel().getSelectedData()==null)
				return false;
			AggSendCarListHVO billVO=(AggSendCarListHVO) getModel().getSelectedData();
			if(null!=billVO.getParentVO().getAttributeValue("vbillstatus") 
					&& billVO.getParentVO().getAttributeValue("vbillstatus").toString().equals("1")){
				return true;
			}
		}
	   return false;
	}
}
