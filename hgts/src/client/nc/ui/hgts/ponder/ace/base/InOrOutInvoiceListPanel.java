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
 * Ҥ�� ����������
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
	DoListInvoiceAgg var_bodyVo =null; // 2018-4-16 ����ȡ��ʱʹ��
	int selindex=-1;	// �����д�����ʾ�󣬶�λ����ǰѡ�������
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
		setMultiSelect(false);//���ɶ�ѡ
		updateUI();
	}

	public void initData() {
		// TODO �Զ����ɵķ������
		try {
			String mineid = getParents().getMeasDoc().getAttributeValue("ofmine") == null ?"" : getParents().getMeasDoc().getAttributeValue("ofmine").toString();
			/*Object flag=getParents().getHeadCardPanel().getHeadItem("flag_data").getValueObject();
			String flag_data=flag==null || "".equals(flag)?"1":flag.toString();*/
			DoListInvoiceAgg[] agg = ponder.getDoListInvoiceAgg(mineid,null,"","");
			setBodyValueVO(new DoListInvoiceAgg[0]);
			setBodyValueVO(agg);
			getBodyBillModel().loadLoadRelationItemValue();
			getBodyBillModel().execLoadFormula();
			// ȥ������
			this.getHeadCardPanel().getBillTable().setSortEnabled(false);
			this.getInOrOutInvoiceListPanel().getBodyTable().setSortEnabled(false);

		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void bodyRowChange(BillEditEvent e){
		//��ռ�����ҳ������
		getHeadCardPanel().getBillData().setHeaderValueVO(new InvoicesheetBVO());
		getHeadCardPanel().getBillData().setBodyValueVO(null);
		getParents().getParents().getHardWarePanel().getMeasPanel().setWeightQueue();
		DoListInvoiceAgg bodyVo = (DoListInvoiceAgg) getBodyBillModel().getBodyValueRowVO(e.getRow(), DoListInvoiceAgg.class.getName());		
		//	Object flag=getParents().getHeadCardPanel().getHeadItem("flag_data").getValueObject();
		try {
			AggInvoicesheetHVO aggVO = (AggInvoicesheetHVO) ponder.getAggInvoiceVO(bodyVo);
			//˾��Ա����������
			// 0:�� ����ԭʼ���� ; 1:��: ���ݵ�ǰ����Ա ����˾��Ա�� ��������;2:��������
			if(getParents().getFReceOrSend() == 2){
				String pk_invoice_b=bodyVo.getPk_invoice_b();
				InvoicesheetBVO ibvo=(InvoicesheetBVO) HYPubBO_Client.queryByPrimaryKey(InvoicesheetBVO.class, pk_invoice_b);
				UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(ibvo.getAttributeValue("jingz"));
				if(null !=jingz && jingz.doubleValue()>0){
					showErrorMessage("���¹�ë��ʱ������ǰ��������������ά�������棬��[����]ֵ���");
					return;
				}				
				aggVO.getParentVO().setAttributeValue("sby", AppContext.getInstance().getPkUser());
				String pk_dept = new FormulaParseTool().getNameByID("bd_psnjob", "pk_dept", "pk_psndoc",  
						new FormulaParseTool().getNameByID("sm_user", "pk_psndoc", "cuserid",AppContext.getInstance().getPkUser()));
				aggVO.getParentVO().setAttributeValue("pk_dept",pk_dept);
				//aggVO.getParentVO().setAttributeValue("flag_data", flag==null || "".equals(flag)?"1":flag);

				if(getParents().getMeasDoc() != null){
					if(null!=jingz && jingz.doubleValue()>0){						
						//��ë�غ������
						aggVO.getParentVO().setAttributeValue("def1", getParents().getMeasDoc().getAttributeValue("calcode"));
					}else{
						// ��Ƥ�غ������
						aggVO.getParentVO().setAttributeValue("hengqno", getParents().getMeasDoc().getAttributeValue("calcode"));
					}
				}
				selindex=e.getRow();				
			}
			//ʵ��Ϊë��˾��Ա��Ϣ
			aggVO.getParentVO().setAttributeValue("approver",AppContext.getInstance().getPkUser());
			aggVO.getParentVO().setAttributeValue("tapprovetime", AppContext.getInstance().getServerTime());
			String pk_pz=bodyVo.getPz();
			aggVO.getParentVO().setAttributeValue("def4",pk_pz); // Ʒ�� 
			String carid=bodyVo.getCarno();
			aggVO.getParentVO().setAttributeValue("def5",carid); // ���� 
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
