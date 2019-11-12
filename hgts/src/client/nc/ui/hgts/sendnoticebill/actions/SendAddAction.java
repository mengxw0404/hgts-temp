package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.UIState;

public class SendAddAction extends nc.ui.pubapp.uif2app.actions.AddAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -910526722362133860L;

	public SendAddAction(){
		super();
		super.setCode("addAction");
		super.setBtnName("����");
	}
	
	private ShowUpableBillForm editor = null;
	
	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO �Զ����ɵķ������
		super.doAction(e);
		this.getEditor().getBillCardPanel().getHeadItem("contcode").setEnabled(false);
		
	}

	@Override
	protected boolean isActionEnable() {
		// TODO �Զ����ɵķ������
		return this.model.getUiState() == UIState.NOT_EDIT;
	}

	
}
