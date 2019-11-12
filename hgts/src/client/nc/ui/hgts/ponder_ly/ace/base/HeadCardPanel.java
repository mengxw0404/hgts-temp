package nc.ui.hgts.ponder_ly.ace.base;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.ponder.IPonderItf;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
//import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItemEvent;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
/**
 * 火车衡计量
 * @author 
 *
 */
public class HeadCardPanel extends BillCardPanel
implements BillEditListener, BillCardBeforeEditListener/*,ActionListener*/{
	private String m_userid = null;
	protected String m_corpid = null;
	protected BillWorkPanel parents = null;
	IUAPQueryBS dao = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);

	public HeadCardPanel(BillWorkPanel parents)
	{
		this.parents = parents;
		initlize();
	}

	private void initlize()
	{
		setAutoExecHeadEditFormula(true);
		this.m_userid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		this.m_corpid = ClientEnvironment.getInstance().getUser().getPk_org();


		loadTemplet("40H10605", null, this.m_userid, this.m_corpid);
		addEditListener(this);
		addBillEditListenerHeadTail(this);
		//addActionListener((BillActionListener) this);
		
		setBillBeforeEditListenerHeadTail(this);
		setBodyMultiSelect(false);//不可多选
		setBillData(getBillData());
		updateUI();
	}

	public void setBillValueByVo(InvoicesheetHVO vo)
	{
		AggInvoicesheetHVO aggvo = new AggInvoicesheetHVO();
		aggvo.setParentVO(vo);
		setBillValueVO(aggvo);
		execHeadLoadFormulas();
		updateUI();
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		/*int sel=this.parents.getHeadCardPanel().getBillTable().getSelectedRow();
		UFDouble maozhong=HgtsPubTool.getUFDoubleNullAsZero(this.parents.getHeadCardPanel().getHeadItem("hmaozhong"));
		UFDouble biaozhong=HgtsPubTool.getUFDoubleNullAsZero(this.parents.getHeadCardPanel().getBillModel().getValueAt(sel, "bweight"));
		UFDouble zizhong=HgtsPubTool.getUFDoubleNullAsZero(this.parents.getHeadCardPanel().getBillModel().getValueAt(sel, "piz"));
		UFDouble zengzai=HgtsPubTool.getUFDoubleNullAsZero(this.parents.getHeadCardPanel().getBillModel().getValueAt(sel, "zengzai"));
		UFDouble chaoqian=maozhong.sub(biaozhong).sub(zizhong).sub(zengzai);
		this.parents.getHeadCardPanel().setHeadItem("hbiaozhong", biaozhong);*/
	}

	public void afterEdit(BillEditEvent e)
	{
		this.parents.afterEdit(e);
	}

	public boolean beforeEdit(BillItemEvent e)
	{
		return this.parents.beforeEdit(e);
	}

	/*@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		
		this.parents.getHeadCardPanel().getBillModel().setValueAt("", 0, "");
	}*/

	/*private void SelectHeadData(String pk_invoice_b) {
		// TODO 自动生成的方法存根
		String sql2 = "select * from hgts_invoicesheet h left join hgts_invoicesheet_b b "
				+ " on h.pk_invoice = b.pk_invoice where b.pk_invoice_b = '"+ pk_invoice_b +"'";

		List<InvoicesheetHVO> result = null;
		try {
			result = (ArrayList)dao.executeQuery(sql2, new BeanListProcessor(InvoicesheetHVO.class));
			getBillData().setHeaderValueVO(result.get(0));
			getBillModel().loadLoadRelationItemValue();
		}catch (BusinessException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
	}*/
}
