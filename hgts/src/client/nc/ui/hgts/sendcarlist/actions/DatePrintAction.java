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
	//������ѡ���ֶ�
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
			// ����ΰ��ʾ����applet��Ϊ�����壬��������򿪺�Ԥ��ʱ��������
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
			MessageDialog.showErrorDlg(null, "����", "δѡ���ӡ���ݣ���ѡ����ϸ�ɳ���Ϣ��");
		} else {
			newObj = this.getSelectedVO();
		}
		//����У�飺���ɳ����ӱ���Ϣ���޳�
		SendCarListBVO[] bodyvos  = (SendCarListBVO[]) newObj.getChildrenVO();
		List<SendCarListBVO> listbvos = new ArrayList<SendCarListBVO>();
		for(SendCarListBVO bodyvo:bodyvos){
			//
			if(HgtsPubTool.getUFBooleanNullAsFalse(bodyvo.getAttributeValue("isdispatch")).booleanValue()){
				listbvos.add(bodyvo);
			}	
		}
		//�ж��д�ӡ������
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
		// ����Ĭ�ϴ�ӡ������wushzh
		setDefaultPrintListener(list.toArray(new IMetaDataDataSource[0]));
		if (this.isPreview()) {
			this.printEntry.preview();
		} else {
			this.printEntry.print();
		}
	}

	/**
	 * ���������������õ�ѡ�����
	 */
	private void getSelectIndex() {
		// ��Ƭ����
		if (((ShowUpableBillForm) this.editor).isComponentVisible()) {
			BillCardPanel panel = this.editor.getBillCardPanel();
			this.selectIndex = panel.getBodyPanel().getTable().getSelectedRows();
		} else {// �б����
			BillListPanel panel = this.list.getBillListPanel();
			this.selectIndex = panel.getBodyTable().getSelectedRows();
		}
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
	 * ����ӡ��ť�ڣ�û��ע���ӡ������������Ĭ�ϵĴ�ӡ������
	 * 
	 * @throws BusinessException
	 */
	protected void setDefaultPrintListener(IMetaDataDataSource[] list)
			throws BusinessException {
		// ��ȡ����Դ��������ԴΪ�գ���ô����Ҫ����Ĭ�ϵĴ�ӡ��������
		if (ArrayUtils.isEmpty(list))
			return;
		// ��û��Ĭ�ϵļ����࣬����Ҫ����ӡ��������ô����һ��Ĭ�ϵļ�����
		// String templatid = this.printEntry.getTemplateID();
		String funnode = getModel().getContext().getNodeCode();
		if (StringUtils.isEmpty(funnode))
			return;
		if (getPrintListener() == null && isPrintLimit(funnode))
			getDefaultPrintListener();
		// ����Ĭ�ϵļ����࣬��Ҫ������������Դ
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
	 * ��ȡĬ�ϵļ����࣬��ע�뵽printEntry��
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
}
