package nc.ui.hgts.ponder.ace.base;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;

@SuppressWarnings("serial")
public class QueryDlg extends UIDialog {

	private JPanel jPanel = null;
	private UIButton btnOk = null;
	private UIButton btnCancle = null;

	public JLabel begdateLab;
	public JLabel enddateLab;

	public UIRefPane begdateRef;
	public UIRefPane enddateRef;

	private JLabel lbywy = null;// �ͻ�
	private UIRefPane ywyText = null;

	private JLabel lbmz=null; //ú��
	private UIRefPane mzText = null;	

	public String[] querycondition= new String[3];
	public StringBuffer b_qrycondition=new StringBuffer();
	

	public StringBuffer getB_qrycondition() {
		return b_qrycondition;
	}

	public void setB_qrycondition(StringBuffer b_qrycondition) {
		this.b_qrycondition = b_qrycondition;
	}


	public String[] getQuerycondition() {
		return querycondition;
	}

	public void setQuerycondition(String[] querycondition) {
		this.querycondition = querycondition;
	}

	public HashMap bufferCondition = new HashMap();//��������رմ����һ�εĲ�ѯ����


	@SuppressWarnings("deprecation")
	public QueryDlg() {
		super();
		initialize();
	}

	@SuppressWarnings("deprecation")
	public QueryDlg(HashMap bufferCondition){
		super();
		this.setBufferCondition(bufferCondition);
		initialize();
	}

	private void initialize() {
		this.setSize(new Dimension(400, 300));
		this.setContentPane(getJPanel());
		this.setTitle("��ѯ����");
		setResizable(false);
	}

	public JPanel getJPanel() {
		if (jPanel == null) {

			lbywy = new JLabel();
			lbywy.setBounds(new Rectangle(85, 30, 100, 22));
			lbywy.setText("�ͻ���");
			lbywy.setVisible(true);				

			lbmz = new JLabel();
			lbmz.setBounds(new Rectangle(85, 65, 100, 22));
			lbmz.setText("ú�֣�");
			lbmz.setVisible(true);

			jPanel = new JPanel();
			jPanel.setLayout(null);

			jPanel.add(lbywy,null);
			jPanel.add(getYwyText(),null);

			jPanel.add(lbmz,null);
			jPanel.add(getMzText(),null);

			jPanel.add(getBtnOk(), null);
			jPanel.add(getBtnCancle(), null);

		}
		return jPanel;
	}

	/**
	 * ҵ��Ա
	 * @return
	 */
	private UIRefPane getYwyText(){
		if(ywyText == null){
			ywyText = new UIRefPane();
			ywyText.setName("�ͻ�");
			ywyText.setRefNodeName("�ͻ�����");
			ywyText.setPK(bufferCondition.get("pk_cust"));
			ywyText.setBounds(150, 30, 150, 22);
			ywyText.setVisible(true);
			/*ywyText.addRefEditListener(new RefEditListener() {				
				@Override
				public boolean beforeEdit(RefEditEvent arg0) {
					PsndocDefaultRefModel psnref = (PsndocDefaultRefModel) ywyText.getRefModel();
					String pk_org=QueryDlgUtils.getDefaultOrgUnit();
					psnref.setPk_org(pk_org);
					return true;
				}
			});*/
		}
		return ywyText;
	}      

	private UIRefPane getMzText(){
		if(mzText == null){
			mzText = new UIRefPane();
			mzText.setName("ú��");
			mzText.setRefNodeName("���ϣ���汾��");
			mzText.setBounds(150, 65, 150, 22);
			mzText.setVisible(true);   
			mzText.setPK(bufferCondition.get("pk_inv"));
		}
		return mzText;
	}

	


	/**
	 * �����ǵõ�ѡ���
	 * ��ʼ���ڡ���������
	 * @return
	 */
	
	public String getYwy(){
		return getYwyText().getRefPK()==null?"":getYwyText().getRefPK().toString();
	}


	public String getMz(){
		return getMzText().getRefPK()==null?"":getMzText().getRefPK().toString();
	}

	@SuppressWarnings("unchecked")
	private void onBtnOk() {

		//querycondition.append(" 1=1 ");
		if(getYwy() != null && getYwy().length()!=0){
			querycondition[0]="'"+getYwy()+"'";
			bufferCondition.put("pk_cust", getYwy());
		}/*else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ��ͻ�");
			return;
		}*/
		
		if(getMz() !=null && getMz().length() !=0){
			querycondition[1]="'"+getMz()+"'";
			bufferCondition.put("pk_inv", getMz());
			//b_qrycondition.append(" and mz='"+getMz()+"'");
		}
		
		this.closeOK();
	}

	private UIButton getBtnOk() {
		if (btnOk == null) {
			btnOk = new UIButton();
			btnOk.setBounds(new Rectangle(100, 240, 68, 22));
			btnOk.setText("ȷ��");
			btnOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onBtnOk();
				}
			});
		}
		return btnOk;
	}

	private UIButton getBtnCancle() {
		if (btnCancle == null) {
			btnCancle = new UIButton();
			btnCancle.setBounds(new Rectangle(220, 240, 68, 22));
			btnCancle.setText("ȡ��");
			btnCancle.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onBtnCancle();
				}
			});
		}
		return btnCancle;
	}

	protected void onBtnCancle() {
		this.closeCancel();
	}

	public HashMap getBufferCondition() {
		return bufferCondition;
	}

	public void setBufferCondition(HashMap bufferCondition) {
		this.bufferCondition = bufferCondition;
	}
}
