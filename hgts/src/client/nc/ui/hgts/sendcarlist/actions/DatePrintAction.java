package nc.ui.hgts.sendcarlist.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.printcheck.IPrintLog;
import nc.ui.bd.print.printlog.Printlistenner;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.print.IMetaDataDataSource;
import nc.ui.pubapp.uif2app.actions.BaseMetaDataBasedPrintAction;
import nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.tool.BillIndex;
import nc.vo.trade.checkrule.VOChecker;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DatePrintAction extends BaseMetaDataBasedPrintAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6468173030699824364L;
	private nc.ui.pub.print.PrintEntry printEntry;
	private BillForm editor;
	private BillListView list;
	//自数据选中字段
	private int[] selectIndex = null;
	
	public interface IBeforePrintDataProcess extends
			BaseMetaDataBasedPrintAction.IBeforePrintDataProcess {
		Object[] processData(Object[] datas);
	}

	public interface IDataSplit extends BaseMetaDataBasedPrintAction.IDataSplit {
		Object[] splitData(Object[] datas);
	}

	@Override
	public BaseMetaDataBasedPrintAction.IBeforePrintDataProcess getBeforePrintDataProcess() {
		if (beforePrintDataProcess == null) {
			return super.getBeforePrintDataProcess();
		}
		return beforePrintDataProcess;
	}

	public void setBeforePrintDataProcess(
			DatePrintAction.IBeforePrintDataProcess beforeProcessor) {
		if (beforeProcessor instanceof BaseMetaDataBasedPrintAction.IBeforePrintDataProcess) {
			super.setBeforePrintDataProcess(beforeProcessor);
		} else {
			this.beforePrintDataProcess = (DatePrintAction.IBeforePrintDataProcess) beforeProcessor;
		}
	}

	@Override
	public BaseMetaDataBasedPrintAction.IDataSplit getDataSplit() {
		if (this.dataSplit == null) {
			return super.dataSplit;
		}
		return dataSplit;
	}

	public void setDataSplit(DatePrintAction.IDataSplit dataSplit) {
		if (dataSplit instanceof BaseMetaDataBasedPrintAction.IDataSplit) {
			super.setDataSplit(dataSplit);
		} else {
			this.dataSplit = (DatePrintAction.IDataSplit) dataSplit;
		}

	}

	protected nc.ui.pub.print.PrintEntry getPrintEntry() {
		if (this.getParent() == null) {
			// 刘晨伟提示，用applet作为父窗体，对于联查打开后预览时会有问题
			this.setParent(this.getModel().getContext().getEntranceUI());
		}
		this.printEntry = new nc.ui.pub.print.PrintEntry(this.getParent(), null);
		LoginContext ctx = this.getModel().getContext();
		this.printEntry.setTemplateID(ctx.getPk_group(), ctx.getNodeCode(),
				ctx.getPk_loginUser(), null, this.getNodeKey());

		if (getPrintListener() != null) {
			this.printEntry.setPrintListener(getPrintListener());
		}
		return this.printEntry;

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (this.getPrintEntry().selectTemplate() != 1) {
			return;
		}
		
		this.getSelectIndex();
		AggSendCarListHVO newObj = null;
		if (null == selectIndex || selectIndex.length == 0) {
			MessageDialog.showErrorDlg(null, "错误", "未选择打印数据，请选中详细派车信息！");
		} else {
			newObj = this.getSelectedVO();
		}
		//数据校验：已派车的子表信息被剔除
		SendCarListBVO[] bodyvos  = (SendCarListBVO[]) newObj.getChildrenVO();
		List<SendCarListBVO> listbvos = new ArrayList<SendCarListBVO>();
		for(SendCarListBVO bodyvo:bodyvos){
			//
			if(HgtsPubTool.getUFBooleanNullAsFalse(bodyvo.getAttributeValue("isdispatch")).booleanValue()){
				listbvos.add(bodyvo);
			}	
		}
		//判断有打印的数据
		List<IMetaDataDataSource> list = new ArrayList<IMetaDataDataSource>();
		if(listbvos.size() > 0){
			IMetaDataDataSource[] defaultDataSource = this.getDefaultMetaDataSource();
			if (!VOChecker.isEmpty(defaultDataSource)) {
				for (IMetaDataDataSource dataSourceItem : defaultDataSource) {
					((AggSendCarListHVO)dataSourceItem.getMDObjects()[0]).setChildrenVO(listbvos.toArray(new SendCarListBVO[0]));
					this.printEntry.setDataSource(dataSourceItem);
					this.printEntry.setAdjustable(isAdjustable());
					list.add(dataSourceItem);
				}
			} else {
				return;
			}
		}
		// 设置默认打印监听，wushzh
		setDefaultPrintListener(list.toArray(new IMetaDataDataSource[0]));
		if (this.isPreview()) {
			this.printEntry.preview();
		} else {
			this.printEntry.print();
		}
	}

	/**
	 * 方法功能描述：得到选择的行
	 */
	private void getSelectIndex() {
		// 卡片界面
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			BillCardPanel panel = this.editor.getBillCardPanel();
			this.selectIndex = panel.getBodyPanel().getTable().getSelectedRows();
		} else {// 列表界面
			BillListPanel panel = this.list.getBillListPanel();
			this.selectIndex = panel.getBodyTable().getSelectedRows();
		}
	}
	/**
	 * 方法功能描述：得到当前界面上选择的数据VO
	 */
	private AggSendCarListHVO getSelectedVO() {
		AggSendCarListHVO vo = null;
		// 卡片界面
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			vo = this.getCloseVOFromBillView(1);
		} else {// 列表界面
			vo = this.getCloseVOFromBillView(2);
		}
		return vo;
	}
	
	/**
	 * 方法功能描述：如果是卡片界面，从卡片获取表体数据
	 * @param billtype :1==卡片 ，2==列表
	 */
	private AggSendCarListHVO getCloseVOFromBillView(int billtype) {
		if (ArrayUtils.isEmpty(this.selectIndex)) {
			return null;
		}
		// 获得旧VO，然后将表体设为选择的VO，得到选择数据VO
		AggSendCarListHVO oldVO = (AggSendCarListHVO) this.getModel().getSelectedData();
		if (null == oldVO) {
			return null;
		}
		AggSendCarListHVO vo = (AggSendCarListHVO) oldVO.clone();
		BillIndex index = new BillIndex(new AggSendCarListHVO[] { vo });
		IVOMeta meta = vo.getMetaData().getVOMeta(SendCarListBVO.class);
		SendCarListBVO[] itemVOs = new SendCarListBVO[this.selectIndex.length];
		for (int i = 0; i < this.selectIndex.length; ++i) {
			String pk_sendcarlist_b = "";
			//1==卡片 ，2==列表
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
	 * 若打印按钮内，没有注册打印监听器，设置默认的打印监听器
	 * 
	 * @throws BusinessException
	 */
	protected void setDefaultPrintListener(IMetaDataDataSource[] list)
			throws BusinessException {
		// 获取数据源，若数据源为空，那么不需要设置默认的打印监听类了
		if (ArrayUtils.isEmpty(list))
			return;
		// 若没有默认的监听类，且需要检查打印次数，那么设置一个默认的监听类
		// String templatid = this.printEntry.getTemplateID();
		String funnode = getModel().getContext().getNodeCode();
		if (StringUtils.isEmpty(funnode))
			return;
		if (getPrintListener() == null && isPrintLimit(funnode))
			getDefaultPrintListener();
		// 若是默认的监听类，需要重新设置数据源
		if (this.getPrintListener() != null
				&& this.getPrintListener() instanceof Printlistenner) {
			((Printlistenner) getPrintListener()).setDatasource(list);
			((Printlistenner) getPrintListener()).setTemplatid(null);
			((Printlistenner) getPrintListener()).setFuncode(funnode);
		}
	}

	private boolean isPrintLimit(String funnode) throws BusinessException {
		return NCLocator.getInstance().lookup(IPrintLog.class)
				.isAddPrintListenerByTemplatid(funnode);
	}

	/**
	 * 获取默认的监听类，并注入到printEntry内
	 */
	private void getDefaultPrintListener() {
		this.setPrintListener(new Printlistenner());
		this.printEntry.setPrintListener(getPrintListener());

	}
	
	/**
	 * @return list
	 */
	public BillListView getList() {
		return list;
	}

	/**
	 * @param list 要设置的 list
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
	 * @param editor 要设置的 editor
	 */
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
}
