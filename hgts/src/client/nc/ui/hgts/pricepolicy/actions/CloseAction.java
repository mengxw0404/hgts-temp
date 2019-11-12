package nc.ui.hgts.pricepolicy.actions;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.hgts.pricepolicy.PricepolicyHVO;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class CloseAction extends NCAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5182981624840748040L;

	public CloseAction(){
		super();
		super.setCode("closeAction");
		super.setBtnName("关闭");
	}

	private AbstractAppModel model;
	private BillForm editor;
	private RefreshSingleAction refreshAction;
	
	public RefreshSingleAction getRefreshAction() {
		return refreshAction;
	}

	public void setRefreshAction(RefreshSingleAction refreshAction) {
		this.refreshAction = refreshAction;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		AggPricepolicyHVO aggvo=(AggPricepolicyHVO) this.model.getSelectedData();
		PricepolicyHVO hvo =  aggvo.getParentVO();
		hvo.setAttributeValue("closeflag", "Y");
		hvo.setAttributeValue("dr", 0);
		// 2018-2-26 关闭人、关闭时间、打开人、打开时间
		hvo.setAttributeValue("def2", AppContext.getInstance().getPkUser());
		hvo.setAttributeValue("def3", AppContext.getInstance().getServerTime());
		hvo.setAttributeValue("def4", null);
		hvo.setAttributeValue("def5", null);
		HYPubBO_Client.update(hvo);
		
		this.refreshAction.doAction(arg0);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggPricepolicyHVO aggvo=(AggPricepolicyHVO) this.model.getSelectedData();
		PricepolicyHVO hvo=aggvo.getParentVO();
		return !ValueUtils.getUFBoolean(hvo.getAttributeValue("closeflag")).booleanValue();
	}

	
}
