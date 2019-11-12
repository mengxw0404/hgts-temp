package nc.ui.hgts.bd.coaltype.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Coaltype_config extends AbstractJavaBeanDefinition {
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

	public nc.ui.pubapp.pub.smart.SmartBatchAppModelService getBatchModelModelService() {
		if (context.get("batchModelModelService") != null)
			return (nc.ui.pubapp.pub.smart.SmartBatchAppModelService) context
					.get("batchModelModelService");
		nc.ui.pubapp.pub.smart.SmartBatchAppModelService bean = new nc.ui.pubapp.pub.smart.SmartBatchAppModelService();
		context.put("batchModelModelService", bean);
		bean.setServiceItf("nc.itf.hgts.ICoaltypeMaintain");
		bean.setVoClass("nc.vo.hgts.bd.coaltype.CoaltypeVO");
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

	public nc.ui.pubapp.uif2app.model.BatchBillTableModel getBatchModel() {
		if (context.get("batchModel") != null)
			return (nc.ui.pubapp.uif2app.model.BatchBillTableModel) context
					.get("batchModel");
		nc.ui.pubapp.uif2app.model.BatchBillTableModel bean = new nc.ui.pubapp.uif2app.model.BatchBillTableModel();
		context.put("batchModel", bean);
		bean.setContext(getContext());
		bean.setService(getBatchModelModelService());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BatchModelDataManager getBatchModelModelDataManager() {
		if (context.get("batchModelModelDataManager") != null)
			return (nc.ui.pubapp.uif2app.model.BatchModelDataManager) context
					.get("batchModelModelDataManager");
		nc.ui.pubapp.uif2app.model.BatchModelDataManager bean = new nc.ui.pubapp.uif2app.model.BatchModelDataManager();
		context.put("batchModelModelDataManager", bean);
		bean.setModel(getBatchModel());
		bean.setService(getBatchModelModelService());
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

	public nc.ui.pubapp.uif2app.view.OrgPanel getViewa() {
		if (context.get("viewa") != null)
			return (nc.ui.pubapp.uif2app.view.OrgPanel) context.get("viewa");
		nc.ui.pubapp.uif2app.view.OrgPanel bean = new nc.ui.pubapp.uif2app.view.OrgPanel();
		context.put("viewa", bean);
		bean.setModel(getBatchModel());
		bean.setDataManager(getBatchModelModelDataManager());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable getBatchBillTable() {
		if (context.get("batchBillTable") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable) context
					.get("batchBillTable");
		nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable bean = new nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable();
		context.put("batchBillTable", bean);
		bean.setModel(getBatchModel());
		bean.setNodekey("bt");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_59b637());
		bean.setActions(getManagedList1());
		bean.setEditActions(getManagedList2());
		bean.setModel(getBatchModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_59b637() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#59b637") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#59b637");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#59b637", bean);
		bean.setUp(getCNode_f64287());
		bean.setDown(getCNode_189e5fa());
		bean.setDividerLocation(74.0f);
		bean.setName("");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_f64287() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#f64287") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#f64287");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#f64287", bean);
		bean.setComponent(getViewa());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_189e5fa() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#189e5fa") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#189e5fa");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#189e5fa", bean);
		bean.setComponent(getBatchBillTable());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getBatchEditAction());
		list.add(getBatchRefreshAction());
		list.add(getBatchAddLineAction());
		return list;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getBatchSaveAction());
		list.add(getBatchCancelAction());
		list.add(getBatchAddLineActiona());
		return list;
	}

	public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context
					.get("InitDataListener");
		nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
		context.put("InitDataListener", bean);
		bean.setModel(getBatchModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.hgts.bd.coaltype.CoaltypeVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBatchModelEventMediator() {
		if (context.get("batchModelEventMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context
					.get("batchModelEventMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("batchModelEventMediator", bean);
		bean.setModel(getBatchModel());
		bean.setHandlerGroup(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		return list;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchEditAction getBatchEditAction() {
		if (context.get("batchEditAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchEditAction) context
					.get("batchEditAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchEditAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchEditAction();
		context.put("batchEditAction", bean);
		bean.setModel(getBatchModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction getBatchRefreshAction() {
		if (context.get("batchRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction) context
					.get("batchRefreshAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction();
		context.put("batchRefreshAction", bean);
		bean.setModel(getBatchModel());
		bean.setModelManager(getBatchModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineAction getBatchAddLineAction() {
		if (context.get("batchAddLineAction") != null)
			return (nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineAction) context
					.get("batchAddLineAction");
		nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineAction bean = new nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineAction();
		context.put("batchAddLineAction", bean);
		bean.setModel(getBatchModel());
		bean.setVoClassName("nc.vo.hgts.bd.coaltype.CoaltypeVO");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction getBatchDelLineAction() {
		if (context.get("batchDelLineAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction) context
					.get("batchDelLineAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction();
		context.put("batchDelLineAction", bean);
		bean.setModel(getBatchModel());
		bean.setBatchBillTable(getBatchBillTable());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction getBatchSaveAction() {
		if (context.get("batchSaveAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction) context
					.get("batchSaveAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction();
		context.put("batchSaveAction", bean);
		bean.setModel(getBatchModel());
		bean.setEditor(getBatchBillTable());
		bean.setValidationService(getBatchBillTableValidateService());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getBatchCancelAction() {
		if (context.get("batchCancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction) context
					.get("batchCancelAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
		context.put("batchCancelAction", bean);
		bean.setModel(getBatchModel());
		bean.setEditor(getBatchBillTable());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineActiona getBatchAddLineActiona() {
		if (context.get("batchAddLineActiona") != null)
			return (nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineActiona) context
					.get("batchAddLineActiona");
		nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineActiona bean = new nc.ui.hgts.bd.coaltype.action.CoaltypeAddLineActiona();
		context.put("batchAddLineActiona", bean);
		bean.setModel(getBatchModel());
		bean.setVoClassName("nc.vo.hgts.bd.coaltype.CoaltypeVO");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction getBatchDelLineActiona() {
		if (context.get("batchDelLineActiona") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction) context
					.get("batchDelLineActiona");
		nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction();
		context.put("batchDelLineActiona", bean);
		bean.setModel(getBatchModel());
		bean.setBatchBillTable(getBatchBillTable());
		bean.setExceptionHandler(getExceptionHandler());
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

	public nc.ui.uif2.model.DefaultBatchValidationService getBatchBillTableValidateService() {
		if (context.get("batchBillTableValidateService") != null)
			return (nc.ui.uif2.model.DefaultBatchValidationService) context
					.get("batchBillTableValidateService");
		nc.ui.uif2.model.DefaultBatchValidationService bean = new nc.ui.uif2.model.DefaultBatchValidationService();
		context.put("batchBillTableValidateService", bean);
		bean.setEditor(getBatchBillTable());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
