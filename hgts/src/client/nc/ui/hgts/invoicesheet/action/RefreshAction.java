package nc.ui.hgts.invoicesheet.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractAppModel;

public class RefreshAction extends NCAction{
	private static final long serialVersionUID = -5296203813087965366L;

	public RefreshAction(){
		super();
		ActionInitializer.initializeAction(this, "Refresh");
	}
	private BillForm editor;
	private AbstractAppModel model;
	private RefreshSingleAction cardRefreshAction;
	private DefaultRefreshAction listRefreshAction;

	@Override
	public void doAction(ActionEvent event) throws Exception {
		// 卡片界面刷新
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			cardRefreshAction.doAction(event);	
		} else {// 列表界面
			listRefreshAction.doAction(event);		
		}

	}
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	public RefreshSingleAction getCardRefreshAction() {
		return cardRefreshAction;
	}
	public void setCardRefreshAction(RefreshSingleAction cardRefreshAction) {
		this.cardRefreshAction = cardRefreshAction;
	}
	public DefaultRefreshAction getListRefreshAction() {
		return listRefreshAction;
	}
	public void setListRefreshAction(DefaultRefreshAction listRefreshAction) {
		this.listRefreshAction = listRefreshAction;
	}
	public AbstractAppModel getModel() {
		return model;
	}
	public void setModel(AbstractAppModel model) {
		this.model = model;
	}
}
