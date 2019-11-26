package nc.ui.hgts.ponder.ace.base;

import java.awt.CardLayout;
import java.awt.Panel;
import java.awt.event.WindowEvent;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import nc.bs.pub.im.exception.BusinessException;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;

public abstract class BaseContainer extends ToftPanel{

	private static final long serialVersionUID = 1L;
	protected ButtonObject Def_ButtonGroup[];//��ť��
	private HardWarePanel hardWarePanel = null;//Ӳ����ҳ�������
	protected BillWorkPanel billWorkPanel = null;//����
	private JSplitPane spanel = null;
	private Panel cardLayoutPanel = null;
	private CardLayout cardLayout = new CardLayout();
	// private VedioPanel fullscreenVideoPanel = null;//��Ƶ
	
	
	protected ButtonObject tareBTN;
	protected ButtonObject grossBTN;
	protected ButtonObject saveBTN;
	protected ButtonObject printBTN;
	protected ButtonObject deleteBTN;
	protected ButtonObject cancelBTN;
	protected ButtonObject refreshBTN;
	protected ButtonObject fubBTN; 
	protected ButtonObject carHBTN;
	protected ButtonObject carWeightBTN;
	protected ButtonObject queryBTN;


	public BaseContainer()
	{

		initialize();  
	}

	public void setBillButtonShow(int ReceOrSend ,int BillModel) {
		// TODO �Զ����ɵķ������
		if(BillModel== -1){ //��ʼ
			grossBTN.setEnabled(false);
			tareBTN.setEnabled(false);
			saveBTN.setEnabled(false);
			printBTN.setEnabled(true);
			deleteBTN.setEnabled(false);
			cancelBTN.setEnabled(false); 
			carWeightBTN.setEnabled(false);
			queryBTN.setEnabled(true);
		}
		else if(BillModel == 0){//�༭
			if(ReceOrSend == 0){
				tareBTN.setEnabled(true);
				deleteBTN.setEnabled(false);
				carWeightBTN.setEnabled(true);
			}else if(ReceOrSend == 1){
				grossBTN.setEnabled(true);
				deleteBTN.setEnabled(true);
				fubBTN.setEnabled(true);
			}
			saveBTN.setEnabled(true);
			printBTN.setEnabled(false);
			cancelBTN.setEnabled(true);  
			queryBTN.setEnabled(true);
		}
		else if(BillModel == 1){//��Ҫ��������������

		}
	}

	private void initialize()
	{
		super.setButtons(getButtons());
		getBillWorkPanel();
		getCardLayoutPanel().add("splitpanel", getSpanel());
		add(getCardLayoutPanel());

		getSpanel().setDividerLocation(335);
		//����ͷ����
		 if (false) {//getBillWorkPanel().getMeasDoc().getDef2().booleanValue()
		      SwingUtilities.invokeLater(new Runnable()
		      {
		        public void run()
		        {
		          try
		          {
		            BaseContainer.this.getHardWarePanel().getVedioPanel().startRealPlay(false, 0);
		          }
		          catch (BusinessException e)
		          {
		            FmpubLogger.error(e.getMessage(), e);
		            BaseContainer.this.showErrorMessage(e.getMessage());
		          }
		        }
		      });
		    }
	}

	public abstract BillWorkPanel getBillWorkPanel();

	public HardWarePanel getHardWarePanel()
	{
		if (this.hardWarePanel == null) {
			this.hardWarePanel = new HardWarePanel(this);
		}
		return this.hardWarePanel;
	}
	
	public ButtonObject[] getButtons()
	{

		tareBTN = new ButtonObject("��Ƥ��", "", 0, "tare");
		tareBTN.setEnabled(false);
		grossBTN = new ButtonObject("��ë��", "", 0, "gross");
		grossBTN.setEnabled(false);
		saveBTN = new ButtonObject("����", "", 0, "save");
		saveBTN.setEnabled(false);
		printBTN = new ButtonObject("��ӡ", "", 0, "print");
		printBTN.setEnabled(false);
		deleteBTN = new ButtonObject("����", "", 0, "delete");
		deleteBTN.setEnabled(false);
		cancelBTN = new ButtonObject("ȡ��", "", 0, "cancel");
		cancelBTN.setEnabled(false);
		refreshBTN = new ButtonObject("ˢ��", "", 0, "refresh");

		fubBTN = new ButtonObject("����", "", 0, "refresh");
		fubBTN.setEnabled(false);

		carHBTN = new ButtonObject("������ʷ����", "", 0, "carhistory");
		carHBTN.setEnabled(true);

		carWeightBTN = new ButtonObject("������Ƥ", "", 0, "carweight");
		carWeightBTN.setEnabled(false);
		
		queryBTN = new ButtonObject("��ѯ","",0,"query");
		queryBTN.setEnabled(true);
			
		return  Def_ButtonGroup = (new ButtonObject[] {
				tareBTN, grossBTN,fubBTN,
				saveBTN, cancelBTN,
				printBTN, deleteBTN,
				refreshBTN , carHBTN,
				carWeightBTN,queryBTN,
			//	nBTN,wBTN,aBTN
				
		});

	}
	

	//��ť�¼�
	public void onButtonClicked(ButtonObject bo) {
		if(bo == tareBTN)//Ƥ��
			onOK();
		else if(bo == grossBTN)//ë��
			onOK();
		else if(bo == saveBTN)
			getBillWorkPanel().OnSave();
		else if(bo == printBTN)//��ӡ
			getBillWorkPanel().onPrint();
		else if(bo == deleteBTN)//����
			getBillWorkPanel().onDelete();
		else if(bo == cancelBTN)//ȡ��
		{
			getBillWorkPanel().onCancel();
		}	 
		else if(bo == refreshBTN)//ˢ��
			getBillWorkPanel().onRefresh();
		else if(bo == carHBTN)//������ʷ����
			getBillWorkPanel().onCarHistory();
		else if(bo == carWeightBTN){//������Ƥ
			getBillWorkPanel().onCarWeight();  
		}else if(bo==queryBTN){ // ��ѯ
			getBillWorkPanel().onQuery();
		}
	}

	public abstract String getTitle();

	public abstract String getBillTypeCode();

	public void afterEdit() {}

	public void setButtonStatues() {}

	public void setBillSatues() {}

	public void windowClosed(WindowEvent arg0)
	{
		super.windowClosed(arg0);
		if (getHardWarePanel().getCodeStacker()!= null && getHardWarePanel().getCodeStacker().getIrDAreader()!= null) {
			getHardWarePanel().getCodeStacker().getIrDAreader().closeport();
		}
		if (getHardWarePanel().getReader() != null) {
			getHardWarePanel().getReader().closeport();
		}
		getHardWarePanel().getMeasPanel().getWeightMonitor().surf.stop();
		if (getHardWarePanel().getMeasPanel().getWeightMonitor().getMonitorDlg() != null) {
			getHardWarePanel().getMeasPanel().getWeightMonitor().getMonitorDlg().getMonitor().surf.stop();
		}
	}

	public boolean onClosing()
	{
		//������ѯ    getHardWarePanel().getMeasPanel().getQueuePanel().windowCLosed();

		if (getHardWarePanel().getCodeStacker()!= null && getHardWarePanel().getCodeStacker().getIrDAreader()!= null) {
			getHardWarePanel().getCodeStacker().getIrDAreader().closeport();
		}
		if (getHardWarePanel().getReader() != null) {
			getHardWarePanel().getReader().closeport();
		}

		getHardWarePanel().getMeasPanel().getWeightMonitor().surf.stop();
		if (getHardWarePanel().getMeasPanel().getWeightMonitor().getMonitorDlg() != null) {
			getHardWarePanel().getMeasPanel().getWeightMonitor().getMonitorDlg().getMonitor().surf.stop();
		}

		return super.onClosing();
	}

	public JSplitPane getSpanel()
	{
		if (this.spanel == null) {
			this.spanel = new JSplitPane(1, true, getHardWarePanel(), this.billWorkPanel);
		}
		return this.spanel;
	}

	public Panel getCardLayoutPanel()
	{
		if (this.cardLayoutPanel == null)
		{
			this.cardLayoutPanel = new Panel();
			this.cardLayoutPanel.setLayout(this.cardLayout);
		}
		return this.cardLayoutPanel;
	}

	public void switchSplitAndVideo()
	{
		((CardLayout)getCardLayoutPanel().getLayout()).next(getCardLayoutPanel());
	}
}
