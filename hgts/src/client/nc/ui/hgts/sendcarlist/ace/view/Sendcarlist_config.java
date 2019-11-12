package nc.ui.hgts.sendcarlist.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Sendcarlist_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.uif2.LoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.uif2.LoginContext) context.get("context");
		nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.sendcarlist.ace.serviceproxy.AceSendcarlistMaintainProxy getBmModelModelService() {
		if (context.get("bmModelModelService") != null)
			return (nc.ui.hgts.sendcarlist.ace.serviceproxy.AceSendcarlistMaintainProxy) context
					.get("bmModelModelService");
		nc.ui.hgts.sendcarlist.ace.serviceproxy.AceSendcarlistMaintainProxy bean = new nc.ui.hgts.sendcarlist.ace.serviceproxy.AceSendcarlistMaintainProxy();
		context.put("bmModelModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory() {
		if (context.get("BOAdapterFactory") != null)
			return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory) context
					.get("BOAdapterFactory");
		nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
		context.put("BOAdapterFactory", bean);
		bean.setMode("MD");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillManageModel getBmModel() {
		if (context.get("bmModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context
					.get("bmModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("bmModel", bean);
		bean.setContext(getContext());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getBmModelModelDataManager() {
		if (context.get("bmModelModelDataManager") != null)
			return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager) context
					.get("bmModelModelDataManager");
		nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
		context.put("bmModelModelDataManager", bean);
		bean.setModel(getBmModel());
		bean.setService(getBmModelModelService());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.pubapp.uif2app.view.TemplateContainer) context
					.get("templateContainer");
		nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
		context.put("templateContainer", bean);
		bean.setContext(getContext());
		bean.setNodeKeies(getManagedList0());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add("bt");
		return list;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getViewa() {
		if (context.get("viewa") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell) context
					.get("viewa");
		nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
		context.put("viewa", bean);
		bean.setQueryAreaCreator(getDefaultQueryAction());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getBillListView() {
		if (context.get("billListView") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView) context
					.get("billListView");
		nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
		context.put("billListView", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setMultiSelectionEnable(false);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb() {
		if (context.get("viewb") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel) context
					.get("viewb");
		nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
		context.put("viewb", bean);
		bean.setModel(getBmModel());
		bean.setTitleAction(getReturnAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction() {
		if (context.get("returnAction") != null)
			return (nc.ui.pubapp.uif2app.actions.UEReturnAction) context
					.get("returnAction");
		nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
		context.put("returnAction", bean);
		bean.setGoComponent(getBillListView());
		bean.setSaveAction(getSaveScriptAction());
		bean.setModel(getBmModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBillForm getBillForm() {
		if (context.get("billForm") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBillForm) context
					.get("billForm");
		nc.ui.pubapp.uif2app.view.ShowUpableBillForm bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillForm();
		context.put("billForm", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setBodyLineActions(getManagedList1());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getBodyAddLineAction_1f9c2fa());
		list.add(getBodyInsertLineAction_6fccec());
		list.add(getBodyDelLineAction_1f6d5ac());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.BodyAddLineAction getBodyAddLineAction_1f9c2fa() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1f9c2fa") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyAddLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1f9c2fa");
		nc.ui.pubapp.uif2app.actions.BodyAddLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyAddLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1f9c2fa",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyInsertLineAction getBodyInsertLineAction_6fccec() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#6fccec") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyInsertLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#6fccec");
		nc.ui.pubapp.uif2app.actions.BodyInsertLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyInsertLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#6fccec",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyDelLineAction getBodyDelLineAction_1f6d5ac() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f6d5ac") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyDelLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f6d5ac");
		nc.ui.pubapp.uif2app.actions.BodyDelLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyDelLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f6d5ac",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_14914f2());
		bean.setActions(getManagedList3());
		bean.setEditActions(getManagedList4());
		bean.setModel(getBmModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_14914f2() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#14914f2") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context
					.get("nc.ui.uif2.tangramlayout.node.TBNode#14914f2");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#14914f2", bean);
		bean.setTabs(getManagedList2());
		bean.setName("cardLayout");
		bean.setShowMode("CardLayout");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getHSNode_3608bb());
		list.add(getVSNode_161b09());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_3608bb() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#3608bb") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context
					.get("nc.ui.uif2.tangramlayout.node.HSNode#3608bb");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#3608bb", bean);
		bean.setLeft(getCNode_463c52());
		bean.setRight(getCNode_12232d0());
		bean.setDividerLocation(215.0f);
		bean.setName("列表");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_463c52() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#463c52") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#463c52");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#463c52", bean);
		bean.setComponent(getViewa());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_12232d0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#12232d0") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#12232d0");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#12232d0", bean);
		bean.setComponent(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_161b09() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#161b09") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#161b09");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#161b09", bean);
		bean.setUp(getCNode_1776270());
		bean.setDown(getCNode_dfc175());
		bean.setDividerLocation(43.0f);
		bean.setName("卡片");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1776270() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1776270") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#1776270");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1776270", bean);
		bean.setComponent(getViewb());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_dfc175() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#dfc175") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#dfc175");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#dfc175", bean);
		bean.setComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getAddMenuGroup());
		list.add(getEditAction());
		list.add(getDeleteScriptAction());
		list.add(getDefaultQueryAction());
		list.add(getCopyAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getCommitScriptAction());
		list.add(getUnCommitScriptAction());
		list.add(getApproveScriptAction());
		list.add(getUNApproveScriptAction());
		list.add(getSeparatorAction());
		list.add(getMetaDataBasedPrintAction());
		list.add(getMetaDataBasedPrintActiona());
		list.add(getOutputAction());
		list.add(getSeparatorAction());
		list.add(getPFApproveStatusInfoAction());
		list.add(getSeparatorAction());
		list.add(getDispatchAction());
		return list;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getSaveScriptAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context
					.get("InitDataListener");
		nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
		context.put("InitDataListener", bean);
		bean.setModel(getBmModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.hgts.sendcarlist.AggSendCarListHVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.pubapp.common.validateservice.ClosingCheck) context
					.get("ClosingListener");
		nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
		context.put("ClosingListener", bean);
		bean.setModel(getBmModel());
		bean.setSaveAction(getSaveScriptAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator() {
		if (context.get("bmModelEventMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context
					.get("bmModelEventMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("bmModelEventMediator", bean);
		bean.setModel(getBmModel());
		bean.setHandlerGroup(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getEventHandlerGroup_d976a8());
		list.add(getEventHandlerGroup_1f10336());
		list.add(getEventHandlerGroup_975119());
		list.add(getEventHandlerGroup_b1bc0e());
		list.add(getEventHandlerGroup_1a9fef7());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_d976a8() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d976a8") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d976a8");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d976a8", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
		bean.setHandler(getAceOrgChangeHandler_fe4ab0());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_fe4ab0() {
		if (context
				.get("nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler#fe4ab0") != null)
			return (nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler) context
					.get("nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler#fe4ab0");
		nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler bean = new nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler();
		context.put(
				"nc.ui.hgts.sendcarlist.ace.handler.AceOrgChangeHandler#fe4ab0",
				bean);
		bean.setBillForm(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1f10336() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1f10336") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1f10336");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1f10336",
				bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
		bean.setHandler(getAceAddHandler_fd2698());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler getAceAddHandler_fd2698() {
		if (context
				.get("nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler#fd2698") != null)
			return (nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler) context
					.get("nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler#fd2698");
		nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler bean = new nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler();
		context.put("nc.ui.hgts.sendcarlist.ace.handler.AceAddHandler#fd2698",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_975119() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#975119") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#975119");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#975119", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
		bean.setHandler(getAceBodyBeforeEditHandler_9e8de());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler getAceBodyBeforeEditHandler_9e8de() {
		if (context
				.get("nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler#9e8de") != null)
			return (nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler) context
					.get("nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler#9e8de");
		nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler bean = new nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler();
		context.put(
				"nc.ui.hgts.sendcarlist.ace.handler.AceBodyBeforeEditHandler#9e8de",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_b1bc0e() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#b1bc0e") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#b1bc0e");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#b1bc0e", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
		bean.setHandler(getAceBodyAfterEditHandler_4d4be5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler getAceBodyAfterEditHandler_4d4be5() {
		if (context
				.get("nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler#4d4be5") != null)
			return (nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler) context
					.get("nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler#4d4be5");
		nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler bean = new nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler();
		context.put(
				"nc.ui.hgts.sendcarlist.ace.handler.AceBodyAfterEditHandler#4d4be5",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1a9fef7() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9fef7") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9fef7");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9fef7",
				bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent");
		bean.setHandler(getAceHeadTailAfterEditHandler_b171db());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler getAceHeadTailAfterEditHandler_b171db() {
		if (context
				.get("nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler#b171db") != null)
			return (nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler) context
					.get("nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler#b171db");
		nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler bean = new nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler();
		context.put(
				"nc.ui.hgts.sendcarlist.ace.handler.AceHeadTailAfterEditHandler#b171db",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader getBillLazilyLoader() {
		if (context.get("billLazilyLoader") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader) context
					.get("billLazilyLoader");
		nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader bean = new nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader();
		context.put("billLazilyLoader", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getBmModelLasilyLodadMediator() {
		if (context.get("bmModelLasilyLodadMediator") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager) context
					.get("bmModelLasilyLodadMediator");
		nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
		context.put("bmModelLasilyLodadMediator", bean);
		bean.setModel(getBmModel());
		bean.setLoader(getBillLazilyLoader());
		bean.setLazilyLoadSupporter(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getCardPanelLazilyLoad_1a23717());
		list.add(getListPanelLazilyLoad_cef4fa());
		return list;
	}

	private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_1a23717() {
		if (context
				.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1a23717") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1a23717");
		nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
		context.put(
				"nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1a23717",
				bean);
		bean.setBillform(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_cef4fa() {
		if (context
				.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#cef4fa") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#cef4fa");
		nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
		context.put(
				"nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#cef4fa",
				bean);
		bean.setListView(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator() {
		if (context.get("rowNoMediator") != null)
			return (nc.ui.pubapp.uif2app.view.RowNoMediator) context
					.get("rowNoMediator");
		nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
		context.put("rowNoMediator", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context
					.get("mouseClickShowPanelMediator");
		nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setListView(getBillListView());
		bean.setShowUpComponent(getBillForm());
		bean.setHyperLinkColumn("vbillno");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.bill.BillCodeMediator getBillCodeMediator() {
		if (context.get("billCodeMediator") != null)
			return (nc.ui.pubapp.bill.BillCodeMediator) context
					.get("billCodeMediator");
		nc.ui.pubapp.bill.BillCodeMediator bean = new nc.ui.pubapp.bill.BillCodeMediator();
		context.put("billCodeMediator", bean);
		bean.setBillForm(getBillForm());
		bean.setBillCodeKey("vbillno");
		bean.setBillType("YPCD");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.PfAddInfoLoader getPfAddInfoLoader() {
		if (context.get("pfAddInfoLoader") != null)
			return (nc.ui.pubapp.uif2app.actions.PfAddInfoLoader) context
					.get("pfAddInfoLoader");
		nc.ui.pubapp.uif2app.actions.PfAddInfoLoader bean = new nc.ui.pubapp.uif2app.actions.PfAddInfoLoader();
		context.put("pfAddInfoLoader", bean);
		bean.setBillType("YPCD");
		bean.setModel(getBmModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.AddMenuAction getAddMenuGroup() {
		if (context.get("addMenuGroup") != null)
			return (nc.ui.pubapp.uif2app.actions.AddMenuAction) context
					.get("addMenuGroup");
		nc.ui.pubapp.uif2app.actions.AddMenuAction bean = new nc.ui.pubapp.uif2app.actions.AddMenuAction();
		context.put("addMenuGroup", bean);
		bean.setActions(getManagedList7());
		bean.setPfAddInfoLoader(getPfAddInfoLoader());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getSeparatorAction());
		list.add(getAddDayPlayAction());
		return list;
	}

	public nc.ui.pubapp.uif2app.actions.AddAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.pubapp.uif2app.actions.AddAction) context
					.get("addAction");
		nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
		context.put("addAction", bean);
		bean.setModel(getBmModel());
		bean.setInterceptor(getCompositeActionInterceptor_af6ec5());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_af6ec5() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#af6ec5") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#af6ec5");
		nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
		context.put(
				"nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#af6ec5",
				bean);
		bean.setInterceptors(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getShowUpComponentInterceptor_1355e04());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_1355e04() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1355e04") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1355e04");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put(
				"nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1355e04",
				bean);
		bean.setShowUpComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.sendcarlist.actions.AddDayPlayAction getAddDayPlayAction() {
		if (context.get("AddDayPlayAction") != null)
			return (nc.ui.hgts.sendcarlist.actions.AddDayPlayAction) context
					.get("AddDayPlayAction");
		nc.ui.hgts.sendcarlist.actions.AddDayPlayAction bean = new nc.ui.hgts.sendcarlist.actions.AddDayPlayAction();
		context.put("AddDayPlayAction", bean);
		bean.setSourceBillType("YDPS");
		bean.setSourceBillName("日计划发运");
		bean.setFlowBillType(false);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setTransferViewProcessor(getTransferProcessorforVP());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.pubapp.uif2app.actions.EditAction) context
					.get("editAction");
		nc.ui.pubapp.uif2app.actions.EditAction bean = new nc.ui.pubapp.uif2app.actions.EditAction();
		context.put("editAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction getDeleteScriptAction() {
		if (context.get("deleteScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction) context
					.get("deleteScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction();
		context.put("deleteScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("DELETE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer() {
		if (context.get("defaultQueryActionQueryTemplateContainer") != null)
			return (nc.ui.uif2.editor.QueryTemplateContainer) context
					.get("defaultQueryActionQueryTemplateContainer");
		nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
		context.put("defaultQueryActionQueryTemplateContainer", bean);
		bean.setNodeKey("qt");
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getDefaultQueryAction() {
		if (context.get("defaultQueryAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction) context
					.get("defaultQueryAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
		context.put("defaultQueryAction", bean);
		bean.setModel(getBmModel());
		bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
		bean.setNodeKey("qt");
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.CopyAction getCopyAction() {
		if (context.get("copyAction") != null)
			return (nc.ui.pubapp.uif2app.actions.CopyAction) context
					.get("copyAction");
		nc.ui.pubapp.uif2app.actions.CopyAction bean = new nc.ui.pubapp.uif2app.actions.CopyAction();
		context.put("copyAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getDefaultRefreshAction() {
		if (context.get("defaultRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction) context
					.get("defaultRefreshAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
		context.put("defaultRefreshAction", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction() {
		if (context.get("cardRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction) context
					.get("cardRefreshAction");
		nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
		context.put("cardRefreshAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction getCommitScriptAction() {
		if (context.get("commitScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction) context
					.get("commitScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction();
		context.put("commitScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("SAVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction getUnCommitScriptAction() {
		if (context.get("unCommitScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction) context
					.get("unCommitScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction();
		context.put("unCommitScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("UNSAVEBILL");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction getApproveScriptAction() {
		if (context.get("approveScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction) context
					.get("approveScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction();
		context.put("approveScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("APPROVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction getUNApproveScriptAction() {
		if (context.get("uNApproveScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction) context
					.get("uNApproveScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction();
		context.put("uNApproveScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("UNAPPROVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.LinkQueryAction getLinkQueryAction() {
		if (context.get("linkQueryAction") != null)
			return (nc.ui.pubapp.uif2app.actions.LinkQueryAction) context
					.get("linkQueryAction");
		nc.ui.pubapp.uif2app.actions.LinkQueryAction bean = new nc.ui.pubapp.uif2app.actions.LinkQueryAction();
		context.put("linkQueryAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintAction() {
		if (context.get("metaDataBasedPrintAction") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context
					.get("metaDataBasedPrintAction");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("metaDataBasedPrintAction", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Preview");
		bean.setActionname("预览");
		bean.setPreview(true);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.sendcarlist.actions.DatePrintAction getMetaDataBasedPrintActiona() {
		if (context.get("metaDataBasedPrintActiona") != null)
			return (nc.ui.hgts.sendcarlist.actions.DatePrintAction) context
					.get("metaDataBasedPrintActiona");
		nc.ui.hgts.sendcarlist.actions.DatePrintAction bean = new nc.ui.hgts.sendcarlist.actions.DatePrintAction();
		context.put("metaDataBasedPrintActiona", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Print");
		bean.setActionname("打印");
		bean.setPreview(false);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.pubapp.uif2app.actions.OutputAction) context
					.get("outputAction");
		nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
		context.put("outputAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction getPFApproveStatusInfoAction() {
		if (context.get("pFApproveStatusInfoAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction) context
					.get("pFApproveStatusInfoAction");
		nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction bean = new nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction();
		context.put("pFApproveStatusInfoAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction getSaveScriptAction() {
		if (context.get("saveScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction) context
					.get("saveScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction();
		context.put("saveScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("YPCD");
		bean.setFilledUpInFlow(true);
		bean.setActionName("SAVEBASE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.CancelAction) context
					.get("cancelAction");
		nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() {
		if (context.get("separatorAction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context
					.get("separatorAction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("separatorAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
		if (context.get("exceptionHandler") != null)
			return (nc.ui.uif2.DefaultExceptionHanler) context
					.get("exceptionHandler");
		nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(
				getContainer());
		context.put("exceptionHandler", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.sendcarlist.actions.DispatchAction getDispatchAction() {
		if (context.get("dispatchAction") != null)
			return (nc.ui.hgts.sendcarlist.actions.DispatchAction) context
					.get("dispatchAction");
		nc.ui.hgts.sendcarlist.actions.DispatchAction bean = new nc.ui.hgts.sendcarlist.actions.DispatchAction();
		context.put("dispatchAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setList(getBillListView());
		bean.setCardRefreshAction(getCardRefreshAction());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.sendcarlist.actions.RefreshAction getRefreshAction() {
		if (context.get("refreshAction") != null)
			return (nc.ui.hgts.sendcarlist.actions.RefreshAction) context
					.get("refreshAction");
		nc.ui.hgts.sendcarlist.actions.RefreshAction bean = new nc.ui.hgts.sendcarlist.actions.RefreshAction();
		context.put("refreshAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setCardRefreshAction(getCardRefreshAction());
		bean.setListRefreshAction(getDefaultRefreshAction());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.billref.dest.TransferViewProcessor getTransferProcessorforVP() {
		if (context.get("transferProcessorforVP") != null)
			return (nc.ui.pubapp.billref.dest.TransferViewProcessor) context
					.get("transferProcessorforVP");
		nc.ui.pubapp.billref.dest.TransferViewProcessor bean = new nc.ui.pubapp.billref.dest.TransferViewProcessor();
		context.put("transferProcessorforVP", bean);
		bean.setList(getBillListView());
		bean.setTransferLogic(getTransferLogicfordj());
		bean.setBillForm(getBillForm());
		bean.setCancelAction(getCancelAction());
		bean.setSaveAction(getSaveScriptAction());
		bean.setActionContainer(getActionsOfList());
		bean.setCardActionContainer(getActionsOfCard());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.billref.dest.DefaultBillDataLogic getTransferLogicfordj() {
		if (context.get("transferLogicfordj") != null)
			return (nc.ui.pubapp.billref.dest.DefaultBillDataLogic) context
					.get("transferLogicfordj");
		nc.ui.pubapp.billref.dest.DefaultBillDataLogic bean = new nc.ui.pubapp.billref.dest.DefaultBillDataLogic();
		context.put("transferLogicfordj", bean);
		bean.setBillForm(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfList() {
		if (context.get("actionsOfList") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context
					.get("actionsOfList");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getBillListView());
		context.put("actionsOfList", bean);
		bean.setModel(getBmModel());
		bean.setActions(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getAddMenuGroup());
		list.add(getEditAction());
		list.add(getDeleteScriptAction());
		list.add(getDefaultQueryAction());
		list.add(getCopyAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getCommitScriptAction());
		list.add(getUnCommitScriptAction());
		list.add(getApproveScriptAction());
		list.add(getUNApproveScriptAction());
		list.add(getSeparatorAction());
		list.add(getMetaDataBasedPrintAction());
		list.add(getMetaDataBasedPrintActiona());
		list.add(getOutputAction());
		list.add(getSeparatorAction());
		list.add(getPFApproveStatusInfoAction());
		list.add(getSeparatorAction());
		list.add(getDispatchAction());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard() {
		if (context.get("actionsOfCard") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context
					.get("actionsOfCard");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getBillForm());
		context.put("actionsOfCard", bean);
		bean.setModel(getBmModel());
		bean.setActions(getManagedList10());
		bean.setEditActions(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getAddMenuGroup());
		list.add(getEditAction());
		list.add(getDeleteScriptAction());
		list.add(getDefaultQueryAction());
		list.add(getCopyAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getCommitScriptAction());
		list.add(getUnCommitScriptAction());
		list.add(getApproveScriptAction());
		list.add(getUNApproveScriptAction());
		list.add(getSeparatorAction());
		list.add(getMetaDataBasedPrintAction());
		list.add(getMetaDataBasedPrintActiona());
		list.add(getOutputAction());
		list.add(getSeparatorAction());
		list.add(getPFApproveStatusInfoAction());
		list.add(getSeparatorAction());
		list.add(getDispatchAction());
		return list;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getSaveScriptAction());
		list.add(getCancelAction());
		return list;
	}

}
