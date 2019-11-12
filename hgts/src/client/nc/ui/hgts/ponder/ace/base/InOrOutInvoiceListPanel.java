package nc.ui.hgts.ponder.ace.base;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.ponder.IPonderItf;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.pub.bill.IBillModelRowStateChangeEventListener;
import nc.ui.pub.bill.RowStateChangeEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.DoListInvoiceAgg;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 * 窑街 计量进、出
 * @author cl
 *
 */
public class InOrOutInvoiceListPanel extends BillListPanel implements BillTableMouseListener, BillEditListener,
IBillModelRowStateChangeEventListener {
	private String m_userid = null;
	private String m_corpid = null;
	private BillWorkPanel parents = null;
	private int billModel = -1;
	IUAPQueryBS dao = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);
	DoListInvoiceAgg var_bodyVo =null; // 2018-4-16 计量取数时使用
	int selindex=-1;	// 保存有错误提示后，定位到当前选择的数据
	public InOrOutInvoiceListPanel(BillWorkPanel parents) {
		this.parents = parents;
		initlize();
	}

	public InOrOutInvoiceListPanel() {
		initlize();	
		initData();
	}

	private void initlize() {
		this.m_userid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		this.m_corpid = ClientEnvironment.getInstance().getUser().getPk_org();
		loadTemplet("40H10607", null, this.m_userid, this.m_corpid);
		addMouseListener(this);
		addHeadEditListener(this);
		addBodyEditListener(this);
		getHeadBillModel().addRowStateChangeEventListener(this);
		setMultiSelect(false);//不可多选
		updateUI();
	}

	public void initData() {
		// TODO 自动生成的方法存根
		try {
			String mineid = getParents().getMeasDoc().getAttributeValue("ofmine") == null ?"" : getParents().getMeasDoc().getAttributeValue("ofmine").toString();
			/*Object flag=getParents().getHeadCardPanel().getHeadItem("flag_data").getValueObject();
			String flag_data=flag==null || "".equals(flag)?"1":flag.toString();*/
			DoListInvoiceAgg[] agg = ponder.getDoListInvoiceAgg(mineid,null,"","");
			setBodyValueVO(new DoListInvoiceAgg[0]);
			setBodyValueVO(agg);
			getBodyBillModel().loadLoadRelationItemValue();
			getBodyBillModel().execLoadFormula();
			// 去掉排序
			this.getHeadCardPanel().getBillTable().setSortEnabled(false);
			this.getInOrOutInvoiceListPanel().getBodyTable().setSortEnabled(false);

		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void bodyRowChange(BillEditEvent e){
		//清空计量单页面数据
		getHeadCardPanel().getBillData().setHeaderValueVO(new InvoicesheetBVO());
		getHeadCardPanel().getBillData().setBodyValueVO(null);
		getParents().getParents().getHardWarePanel().getMeasPanel().setWeightQueue();
		DoListInvoiceAgg bodyVo = (DoListInvoiceAgg) getBodyBillModel().getBodyValueRowVO(e.getRow(), DoListInvoiceAgg.class.getName());		
		//	Object flag=getParents().getHeadCardPanel().getHeadItem("flag_data").getValueObject();
		try {
			AggInvoicesheetHVO aggVO = (AggInvoicesheetHVO) ponder.getAggInvoiceVO(bodyVo);
			//司磅员及所属部门
			// 0:进 保留原始数据 ; 1:出: 根据当前操作员 设置司磅员及 所属部门;2:计量进出
			if(getParents().getFReceOrSend() == 2){
				String pk_invoice_b=bodyVo.getPk_invoice_b();
				InvoicesheetBVO ibvo=(InvoicesheetBVO) HYPubBO_Client.queryByPrimaryKey(InvoicesheetBVO.class, pk_invoice_b);
				UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(ibvo.getAttributeValue("jingz"));
				if(null !=jingz && jingz.doubleValue()>0){
					showErrorMessage("重新过毛重时，请先前往【发货计量单维护】界面，将[净重]值清空");
					return;
				}				
				aggVO.getParentVO().setAttributeValue("sby", AppContext.getInstance().getPkUser());
				String pk_dept = new FormulaParseTool().getNameByID("bd_psnjob", "pk_dept", "pk_psndoc",  
						new FormulaParseTool().getNameByID("sm_user", "pk_psndoc", "cuserid",AppContext.getInstance().getPkUser()));
				aggVO.getParentVO().setAttributeValue("pk_dept",pk_dept);
				//aggVO.getParentVO().setAttributeValue("flag_data", flag==null || "".equals(flag)?"1":flag);

				if(getParents().getMeasDoc() != null){
					if(null!=jingz && jingz.doubleValue()>0){						
						//过毛重衡器编号
						aggVO.getParentVO().setAttributeValue("def1", getParents().getMeasDoc().getAttributeValue("calcode"));
					}else{
						// 过皮重衡器编号
						aggVO.getParentVO().setAttributeValue("hengqno", getParents().getMeasDoc().getAttributeValue("calcode"));
					}
				}
				selindex=e.getRow();				
			}
			//实际为毛重司磅员信息
			aggVO.getParentVO().setAttributeValue("approver",AppContext.getInstance().getPkUser());
			aggVO.getParentVO().setAttributeValue("tapprovetime", AppContext.getInstance().getServerTime());
			String pk_pz=bodyVo.getPz();
			aggVO.getParentVO().setAttributeValue("def4",pk_pz); // 品种 
			String carid=bodyVo.getCarno();
			aggVO.getParentVO().setAttributeValue("def5",carid); // 车号 
			getHeadCardPanel().setBillValueVO(aggVO);
			getHeadCardPanel().getBillModel().loadLoadRelationItemValue();
			getHeadCardPanel().getBillModel().execLoadFormula();

			var_bodyVo=bodyVo;

			if(getParents().getFReceOrSend() == 0 || getParents().getFReceOrSend() == 1 ){
				getParents().setBillModel(-1);
			}else{
				getParents().setBillModel(0);
				this.addBodyEditListener(getInOrOutInvoiceListPanel());
			}
			getParents().setBillStates();			
			getHeadCardPanel().updateUI();
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
	}

	public void setBillModel(int billModel)
	{
		this.billModel = billModel;
	}

	public void mouse_doubleclick(BillMouseEnent e) {
	}

	public void afterEdit(BillEditEvent e) {
	}

	public BillWorkPanel getParents() {
		return this.parents;
	}

	protected InOrOutInvoiceListPanel getInOrOutInvoiceListPanel() {
		return getParents().getInOrOutInvoiceListPanel();
	}

	protected HeadCardPanel getHeadCardPanel() {
		return getParents().getHeadCardPanel();
	}

	private void showErrorMessage(String mesg) {
		getParents().showErrorMessage(mesg);
	}

	public void valueChanged(RowStateChangeEvent event) {
		afterEditSelect(event.getRow());
	}

	private void afterEditSelect(int row) {}

	public int getSelindex() {
		return selindex;
	}

	public void setSelindex(int selindex) {
		this.selindex = selindex;
	}
}
