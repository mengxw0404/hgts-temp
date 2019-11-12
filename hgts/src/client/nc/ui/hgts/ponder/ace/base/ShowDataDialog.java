package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.ponder.IPonderItf;
import nc.ui.bd.ref.ToolVORefModel;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;

/**������ʷ���ݲ�ѯ
 */
public class ShowDataDialog extends UIDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private BillListPanel blp;
	private BillCardPanel bcp;
   
	private UIPanel buttonPanel;// ��ť���
	private UIButton cancelButton;// ȡ����ť
	private UIButton confirmButton;// ȷ�ϰ�ť
	private UIPanel QueryPanel;
	BillManageModel model;
	
	private UIButton btnQuery = null;
	
	private UIPanel jPanel = null;
	public JLabel begdateLab;
	public JComboBox<String> begdateRef;
	private JLabel lbywy = null;// �ͻ�
	private UIRefPane ywyText = null;
	private AggInvoicesheetHVO skdAggVOS;
	private AggInvoicesheetHVO results;

	private String pk_org;
	private  int uicode = 0;
	private HashMap<String, AggInvoicesheetHVO> exAggVOMap;
	
	public HashMap<String, AggInvoicesheetHVO> getExAggVOMap() {
		return exAggVOMap;
	}

	public void setExAggVOMap(HashMap<String, AggInvoicesheetHVO> exAggVOMap) {
		this.exAggVOMap = exAggVOMap;
	}

	public ShowDataDialog(Container parent,int UICODE, LoginContext context,String pk_org, HashMap<String, AggInvoicesheetHVO> exAggVOMap) {
		super(parent, "������");
		this.exAggVOMap = exAggVOMap;
		this.uicode = UICODE;
		this.pk_org=pk_org;
		//this.model=model;
		initialize();
	}

	public ShowDataDialog(Container parent, int UICODE ,LoginContext context,AggInvoicesheetHVO aggVOS,String pk_org) {
		super(parent, "������");
		this.skdAggVOS= aggVOS;
		this.uicode = UICODE;
		this.pk_org=pk_org;
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setLocation(300, 300);
		this.setSize(new Dimension(1000, 500));
		this.add(getJPanel(), BorderLayout.CENTER);
	//	this.add(getButtonPanel(), BorderLayout.SOUTH);
		
		//modify 2015-06-10  ��������ʱ�����̵߳ȴ���
		initDataForLazy();
	}

	public JPanel getJPanel() {

		if (jPanel == null) {
			jPanel = new UIPanel(new BorderLayout());
			jPanel.setPreferredSize(new Dimension(200, 90));
			//������
			jPanel.add(getQueryPanel(),BorderLayout.NORTH);
			//����չʾ��uicode=0����Ƥ�ؽ�����ʷ��ѯ
			if(this.uicode == 0){
				jPanel.add(getblp(), BorderLayout.CENTER);
			}else{
				jPanel.add(getbcp(), BorderLayout.CENTER);
			}
			
		}
		return jPanel;
	}
	
	//��������
	private UIRefPane getYwyText(){
		if(ywyText == null){
			ywyText = new UIRefPane();
			ywyText.setName("���ƺ�");
			ywyText.setBounds(150, 30, 150, 22);
			ywyText.setRefModel(new ToolVORefModel());
			ywyText.setVisible(true);		
			
		}
		return ywyText;
	}   

	//�ڼ�������������
	public JComboBox<String> getDateFromRef() {
		if (begdateRef == null) {
			begdateRef = new JComboBox<String>();			
			begdateRef.insertItemAt("һ��", 0);
			begdateRef.insertItemAt("����", 1);
			begdateRef.insertItemAt("һ��", 2);
			begdateRef.setName("begdateRef");
			begdateRef.setSelectedIndex(0);
			begdateRef.setBounds(150, 135, 150, 55);
			
		}
		return begdateRef;
	}
	
	public String getYwy(){
		return getYwyText().getRefPK()==null?"":getYwyText().getRefPK().toString();
	}
	
	private UIButton getBtnOk() {
		if (btnQuery == null) {
			btnQuery = new UIButton();
			btnQuery.setBounds(new Rectangle(100, 240, 68, 22));
			btnQuery.setText("��ѯ");
			btnQuery.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onBtnQuery();
				}
			});
		}
		return btnQuery;
	}
	
	private void onBtnQuery() {
		
			 StringBuffer where = new StringBuffer();
			 if(getYwy() != null && getYwy().length()!=0){
				 where.append("b.carno = '"+getYwy()+"'");
			 }else{
				 MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ���ƺ�");
				 return;
			 }
			 //�ж��ڼ䣬��д����
			 String piz="substr(b.piztime, 0, 10)";
			 String maoz="substr(b.maoztime, 0, 10)";
			 int index = getDateFromRef().getSelectedIndex();
			 UFDate newdata = new UFDate(new Date());
			if(index == 0){	
				UFDate lastdata = new UFDate(String.valueOf(newdata.getYear()-1)+"-"+newdata.getMonth()+"-"+String.valueOf(newdata.getDay()-1));
				if(this.uicode ==0)
					where.append("and "+piz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
				else if(this.uicode ==1 )
					where.append("and "+maoz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
			}else if(index == 1){
				UFDate lastdata = new UFDate(newdata.getYear()+"-"+String.valueOf(newdata.getMonth()-6)+"-"+String.valueOf(newdata.getDay()-1));
				if(newdata.getMonth() >= 1 && newdata.getMonth() <= 6){
					lastdata = new UFDate(String.valueOf(newdata.getYear()-1)+"-"+String.valueOf(newdata.getMonth()+6)+"-"+String.valueOf(newdata.getDay()-1));
				}
				if(this.uicode ==0)
					where.append("and "+piz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
				else if(this.uicode ==1 )
					where.append("and "+maoz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
			}else if(index == 2){	
				UFDate lastdata=null;
				if(String.valueOf(newdata.getMonth()-3).equals("0")){
					lastdata = new UFDate(newdata.getYear()+"-03-"+String.valueOf(newdata.getDay()-1));
				}else{
					
					lastdata = new UFDate(newdata.getYear()+"-"+String.valueOf(newdata.getMonth()-3)+"-"+String.valueOf(newdata.getDay()-1));
				}
				if(newdata.getMonth() >= 1 && newdata.getMonth() <= 3){
					lastdata = new UFDate(String.valueOf(newdata.getYear()-1)+"-"+String.valueOf(newdata.getMonth()+9)+"-"+String.valueOf(newdata.getDay()-1));
				}
				if(this.uicode ==0)
					where.append("and "+piz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
				else if(this.uicode ==1 )
					where.append("and "+maoz+" between '"+ lastdata.toString().substring(0, 10) +"' and '"+ newdata.toString().substring(0, 10) + "'");
			}
			/**�������ݲ�ѯչʾ*/
			IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);
			try {
				if(this.uicode == 0){
					getblp().getBodyBillModel().setBodyDataVO(ponder.OnQueryCarHistory(where.toString(),getYwy()));
					getblp().getBodyBillModel().execLoadFormula();
				}else{	
					getbcp().getBillModel().setBodyDataVO(ponder.OnQueryCarHistory(where.toString(),getYwy())); 
					getbcp().getBillModel().execLoadFormula();
				}
				
				
			} catch (BusinessException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
	
	}
	protected void onBtnCancle() {
		this.closeCancel();
	}
	private void initDataForLazy() {
		//���������¹��ܴ���getRefModel().getPkValue()
		InvoicesheetBVO[] body =  (InvoicesheetBVO[]) skdAggVOS.getChildrenVO();
		if(null!=body && body.length > 0){
			getYwyText().setPK(body[0].getAttributeValue("carno"));
		}
		onBtnQuery();

	}

	public String getpkField() {
		return "pk_invoice";
	}

	public BillListPanel getblp() {
		if (blp == null) {
				blp = new BillListPanel();
				String pk_user=AppContext.getInstance().getPkUser();
				blp.loadTemplet("40H1060302", null, pk_user,pk_org);
				blp.setParentMultiSelect(true); // ���岻����ֶ�ѡ��
		}
		return blp;
	}
	
	public BillCardPanel getbcp() {
		if (bcp == null) {
			bcp = new BillCardPanel();
				String pk_user=AppContext.getInstance().getPkUser();
				bcp.loadTemplet("40H1060302", null, pk_user,pk_org);
		}
		return bcp;
	}
	
	public UIPanel getQueryPanel() {
		if(QueryPanel == null){
			if(lbywy == null){
				lbywy = new JLabel();
				lbywy.setBounds(new Rectangle(85, 30, 100, 22));
				lbywy.setText("���ƺţ�");
				lbywy.setVisible(true);
			}
			if (begdateLab == null) {
				begdateLab = new JLabel();
				begdateLab.setText("�ڼ䣺");
				begdateLab.setBounds(60, 135, 100, 20);
			}	
			QueryPanel = new UIPanel(new FlowLayout());
			QueryPanel.add(lbywy,null);
			QueryPanel.add(getYwyText(),null);
			QueryPanel.add(begdateLab,null);
			QueryPanel.add(getDateFromRef(),null); 	
			QueryPanel.add(getBtnOk(), null);
			
		}
		return QueryPanel;
	}
	
	
	public UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new UIPanel(new FlowLayout());
			buttonPanel.setPreferredSize(new Dimension(200, 30));
			buttonPanel.add(getConfirmButton());
			buttonPanel.add(getCancelButton());
			
		}
		return buttonPanel;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton();
			cancelButton.setText("ȡ��");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public UIButton getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = new UIButton();
			confirmButton.setText("ȷ��");
			confirmButton.addActionListener(this);
		}
		return confirmButton;
	}

	public AggInvoicesheetHVO getResults() {
		return results;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)  {
		if (e.getSource().equals(getConfirmButton())) {
			try {
				boolean issel=onConfirm();
				if(issel){
					this.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageDialog.showWarningDlg(null, "��ʾ", e1.getMessage());
			}
		} else if (e.getSource().equals(getCancelButton())) {
			closeCancel();
		} 
	}

	private boolean onConfirm() throws Exception {
		return true;
		/*
		boolean issel=false;		
		AggregatedValueObject[] selectedBillVOs = getblp().getMultiSelectedVOs(AggInvoicesheetHVO.class.getName(),
				InvoicesheetHVO.class.getName(), InvoicesheetBVO.class.getName());

		if (selectedBillVOs == null || selectedBillVOs.length == 0) {
			MessageDialog.showErrorDlg(this, "����", "δѡ���κ����ݣ�");
			issel=false;
		}else{		
			if(selectedBillVOs.length>1){
				int num=0;
				for(int i=0;i<selectedBillVOs.length-1;i++){
					String pk_busitype=HgtsPubTool.getStringNullAsTrim(selectedBillVOs[i].getParentVO().getAttributeValue("pk_busitype"));
					for(int j=i+1;j<selectedBillVOs.length;j++){
						String j_pk_busitype=HgtsPubTool.getStringNullAsTrim(selectedBillVOs[j].getParentVO().getAttributeValue("pk_busitype"));
						if(!pk_busitype.equals(j_pk_busitype)){
							num=num+1;
							break;
						}
					}
				}
				if(num>0){
					MessageDialog.showErrorDlg(this, "����", "ѡ������ݣ�������ͬһҵ�����ͣ�");
					issel=false;
				}else{
					issel=true;
				}
			}else{
				issel=true;
			}
			
			if(!issel){
				issel=false;
				return issel;
			}
			results=(AggInvoicesheetHVO[]) selectedBillVOs;
			issel=true;
		}
		return issel;
	*/}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		
	}

}
