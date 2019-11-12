package nc.ui.hgts.hjsettle.actions;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.ui.bd.ref.MineVORefModel;
import nc.ui.hgts.hjsettle_yfee.actions.QueryConditonDlg;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.calendar.CalendarValueChangeEvent;
import nc.ui.pub.beans.calendar.ICalendarValueChangeListener;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;

@SuppressWarnings("serial")
public class ImpQueryDlg extends UIDialog {

	private JPanel jPanel = null;
	private UIButton btnOk = null;
	private UIButton btnCancle = null;

	public JLabel begdateLab;
	public JLabel enddateLab;

	public UIRefPane begdateRef;
	public UIRefPane enddateRef;

	private JLabel lborg = null;// �ͻ�
	private UIRefPane orgText = null;
	
	private JLabel lbywy = null;// �ͻ�
	private UIRefPane ywyText = null;

	private JLabel lbtype = null;// ���㷽ʽ
	private UIRefPane typeText = null;
	
	private JLabel lbjsfs=null; //���
	private UIRefPane jsfsText = null;

	private JLabel lbmz=null; //ú��
	private UIRefPane mzText = null;	
	
	private AbstractAppModel model;
	public String[]  querycondition= new String[3];
	public StringBuffer b_qrycondition=new StringBuffer();
	public boolean isImp;    

	public StringBuffer getB_qrycondition() {
		return b_qrycondition;
	}

	public void setB_qrycondition(StringBuffer b_qrycondition) {
		this.b_qrycondition = b_qrycondition;
	}

	public boolean isImp() {
		return isImp;
	}

	public void setImp(boolean isImp) {
		this.isImp = isImp;
	}

	public String[] getQuerycondition() {
		return querycondition;
	}

	public void setQuerycondition(String[] querycondition) {
		this.querycondition = querycondition;
	}

	public HashMap bufferCondition = new HashMap();//��������رմ����һ�εĲ�ѯ����


	@SuppressWarnings("deprecation")
	public ImpQueryDlg(AbstractAppModel model) {
		super();
		this.model = model;
		initialize();
	}

	@SuppressWarnings("deprecation")
	public ImpQueryDlg(HashMap bufferCondition,AbstractAppModel model){
		super();
		this.setBufferCondition(bufferCondition);
		this.model = model;
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

			lborg = new JLabel();
			lborg.setBounds(new Rectangle(85, 30, 100, 22));
			lborg.setText("��֯��");
			lborg.setVisible(true);		
			
			lbywy = new JLabel();
			lbywy.setBounds(new Rectangle(85, 65, 100, 22));
			lbywy.setText("�ͻ���");
			lbywy.setVisible(true);			

			lbjsfs = new JLabel();
			lbjsfs.setBounds(new Rectangle(85, 100, 100, 22));
			lbjsfs.setText("���");
			lbjsfs.setVisible(true);
			
			lbtype = new JLabel();
			lbtype.setBounds(new Rectangle(85, 135, 100, 22));
			lbtype.setText("���㷽ʽ��");
			lbtype.setVisible(true);

			lbmz = new JLabel();
			lbmz.setBounds(new Rectangle(85, 170, 100, 22));
			lbmz.setText("ú�֣�");
			lbmz.setVisible(true);


			getDateFromLab();
			getDateToLab();


			jPanel = new JPanel();
			jPanel.setLayout(null);

			jPanel.add(lborg,null);
			jPanel.add(getOrgText(),null);
			
			jPanel.add(lbywy,null);
			jPanel.add(getYwyText(),null);

			jPanel.add(lbjsfs,null);
			jPanel.add(getJsfsText(),null);
			
			jPanel.add(lbtype,null);
			jPanel.add(getBalatypeText(),null);

			jPanel.add(lbmz,null);
			jPanel.add(getMzText(),null);


			jPanel.add(getBtnOk(), null);
			jPanel.add(getBtnCancle(), null);

			jPanel.add(begdateLab,null);
			jPanel.add(getDateFromRef(),null); 

			jPanel.add(enddateLab,null);
			jPanel.add(getDateToRef(),null); 



		}
		return jPanel;
	}

	/**
	 * ��֯
	 * @return
	 */
	private UIRefPane getOrgText(){
		if(orgText == null){
			orgText = new UIRefPane();
			orgText.setName("��֯");
			orgText.setRefNodeName("ҵ��Ԫ");
			orgText.setPK(bufferCondition.get("pk_org"));
			orgText.setBounds(150, 30, 150, 22);
			orgText.setVisible(true);
			//�༭�¼�����
//			orgText.addRefEditListener(new RefEditListener() {
//				@Override
//				public boolean beforeEdit(RefEditEvent refeditevent) {
//					// TODO �Զ����ɵķ������
//					if(getOrgText().getRefPK()!=null){
//						ywyText.getRefModel().setPk_org(getOrgText().getRefPK().toString());
//						mzText.getRefModel().setPk_org(getOrgText().getRefPK().toString());
//					}
//					return true;
//				}
//			});
			//�༭���¼�����
			orgText.addValueChangedListener(new ValueChangedListener() {	
				@Override
				public void valueChanged(ValueChangedEvent valuechangedevent) {
					// ��ѡ����֮֯�󣬽��ͻ������ϵ�������˾����Ϊ��ѡ�����֯
					if(getOrgText().getRefPK()!=null){
						ywyText.getRefModel().setPk_org(getOrgText().getRefPK().toString());
						mzText.getRefModel().setPk_org(getOrgText().getRefPK().toString());
					}
				}
			});
		}
		return orgText;
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
			ywyText.setBounds(150, 65, 150, 22);
			ywyText.setVisible(true);
		}
		return ywyText;
	}   
	
	private UIRefPane getJsfsText(){
		if(jsfsText == null){
			jsfsText = new UIRefPane();
			jsfsText.setName("���");
			jsfsText.setRefModel(new MineVORefModel());
			jsfsText.setBounds(150, 100, 150, 22);
			jsfsText.setVisible(true);   
			jsfsText.setPK(bufferCondition.get("pk_kc"));
		}
		return jsfsText;
	}
	
	private UIRefPane getBalatypeText(){
		if(typeText == null){
			typeText = new UIRefPane();
			typeText.setName("���㷽ʽ");
			typeText.setRefNodeName("���ʽ(�Զ��嵵��)");
			typeText.setPK(bufferCondition.get("pk_balatype"));
			typeText.setBounds(150, 135, 150, 22);
			typeText.setVisible(true);
		}
		return typeText;
	}   

	private UIRefPane getMzText(){
		if(mzText == null){
			mzText = new UIRefPane();
			mzText.setName("ú��");
			mzText.setRefNodeName("���ϣ���汾��");
			mzText.setBounds(150, 170, 150, 22);
			mzText.setVisible(true);   
			mzText.setPK(bufferCondition.get("pk_inv"));
		}
		return mzText;
	}

	public JLabel getDateFromLab() {
		if (begdateLab == null) {
			begdateLab = new JLabel();
			begdateLab.setText("��ʼ���ڣ�");
			begdateLab.setBounds(60, 205, 100, 20);
		}
		return begdateLab;
	}

	public UIRefPane getDateFromRef() {
		if (begdateRef == null) {
			begdateRef = new UIRefPane();
			begdateRef.setName("begdateRef");
			begdateRef.setBounds(150, 205, 150, 22);
			begdateRef.setRefNodeName("����");
			Object datefrom=bufferCondition.get("datefrom");
			if(null==datefrom){
				//begdateRef.setValueObj(new UFDateTime(System.currentTimeMillis()));
				QueryConditonDlg qg=new QueryConditonDlg();
				begdateRef.setValueObj(qg.getDays()[0]);
			}else{
				begdateRef.setValueObj(datefrom);
			}
		}
		return begdateRef;
	}

	public JLabel getDateToLab(){
		if (enddateLab == null) {
			enddateLab = new JLabel();
			enddateLab.setText("�������ڣ�");
			enddateLab.setBounds(60, 240, 100, 20);
		}
		return enddateLab;
	}
	public UIRefPane getDateToRef() {
		if (enddateRef == null) {
			enddateRef = new UIRefPane();
			enddateRef.setName("enddateRef");
			enddateRef.setBounds(150, 240, 150, 22);
			enddateRef.setRefNodeName("����");
			Object dateto=bufferCondition.get("dateto");
			if(null==dateto){
				enddateRef.setValueObj(new UFDateTime(System.currentTimeMillis()));
			}else{
				enddateRef.setValueObj(dateto);
			}
		}
		return enddateRef;
	}


	/**
	 * �����ǵõ�ѡ���
	 * ��ʼ���ڡ���������
	 * @return
	 */
	public String getDateFrom(){
		return getDateFromRef().getRefName()==null?"":
			getDateFromRef().getRefName().toString();
	}

	public String getDateTo(){
		return getDateToRef().getRefName()==null?"":
			getDateToRef().getRefName().toString();
	}

	public String getOrg(){
		
		return getOrgText().getRefPK()==null?"":getOrgText().getRefPK().toString();
	}

	public String getYwy(){
		return getYwyText().getRefPK()==null?"":getYwyText().getRefPK().toString();
	}

	public String getJsfs(){
		return getJsfsText().getRefPK()==null?"":getJsfsText().getRefPK().toString();
	}

	public String getMz(){
		return getMzText().getRefPK()==null?"":getMzText().getRefPK().toString();
	}

	public String getBalatype(){
		return getBalatypeText().getRefPK()==null?"":getBalatypeText().getRefPK();
	}
	@SuppressWarnings("unchecked")
	private void onBtnOk() {

		//querycondition.append(" 1=1 ");
		if(getOrg() != null && getOrg().length()!=0){
			querycondition[0]=" and pk_org='"+getOrg()+"'";
			querycondition[2]=" and pk_org='"+getOrg()+"'"; // �жϸû��Ƿ���Ҫ�����ʼ�ʹ��
			bufferCondition.put("pk_org", getOrg());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ����֯");
			return;
		}

		//querycondition.append(" 1=1 ");
		if(getYwy() != null && getYwy().length()!=0){
			querycondition[0]=" and pk_cust='"+getYwy()+"'";
			querycondition[2]=" and pk_cust='"+getYwy()+"'"; // �жϸû��Ƿ���Ҫ�����ʼ�ʹ��
			bufferCondition.put("pk_cust", getYwy());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ��ͻ�");
			return;
		}

		if(getJsfs() != null && getJsfs().length()!=0){
			querycondition[0]=querycondition[0]+" and pk_kc='"+getJsfs()+"'";
			bufferCondition.put("pk_kc", getJsfs());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ���");
			return;
		}

		if(getBalatype() != null && getBalatype().length()!=0){
			querycondition[0]=querycondition[0]+" and pk_balatype='"+getBalatype()+"'";
			bufferCondition.put("pk_balatype", getBalatype());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ����㷽ʽ");
			return;
		}

		if(getDateFrom() != null && getDateFrom().length()!=0){
			querycondition[0]=querycondition[0]+" and substr(dbilldate,0,10) >='"+getDateFrom()+"'";
			bufferCondition.put("datefrom", getDateFrom());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ������");
			return;
		}

		if(getDateTo() != null && getDateTo().length()!=0){
			if(getDateFrom() != null && getDateFrom().length()!=0){
				String bdate=new UFDate(getDateFrom()).getYear()+"-"+new UFDate(getDateFrom()).getMonth();
				String edate=new UFDate(getDateTo()).getYear()+"-"+new UFDate(getDateTo()).getMonth();
				
				// ��ȡ��ǰ��¼����
				UFDate cdate=new UFDate(AppContext.getInstance().getBusiDate().toString().substring(0, 10));
				UFDate e_date=new UFDate(getDateTo().substring(0, 10));
				if(cdate.beforeDate(e_date)){
					MessageDialog.showWarningDlg(null, "��ʾ", "�������ڱ���С�ڵ��ڵ�ǰ��¼����");
					return; 
				}
			}
			querycondition[0]=querycondition[0]+" and substr(dbilldate,0,10) <='"+getDateTo()+"'";
			bufferCondition.put("dateto", getDateTo());
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ������");
			return;
		}

		if(getMz() !=null && getMz().length() !=0){
			querycondition[1]=" and pz='"+getMz()+"'";
			bufferCondition.put("pk_inv", getMz());
			//b_qrycondition.append(" and mz='"+getMz()+"'");
		}else{
			MessageDialog.showWarningDlg(null, "��ʾ", "��ѡ��ú��");
			return;
		}

		this.closeOK();
	}

	private UIButton getBtnOk() {
		if (btnOk == null) {
			btnOk = new UIButton();
			btnOk.setBounds(new Rectangle(100, 270, 68, 22));
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
			btnCancle.setBounds(new Rectangle(220, 270, 68, 22));
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
