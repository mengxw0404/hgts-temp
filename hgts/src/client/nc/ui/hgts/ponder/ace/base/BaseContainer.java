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
	protected ButtonObject Def_ButtonGroup[];//按钮组
	private HardWarePanel hardWarePanel = null;//硬件，页面左侧区
	protected BillWorkPanel billWorkPanel = null;//工作
	private JSplitPane spanel = null;
	private Panel cardLayoutPanel = null;
	private CardLayout cardLayout = new CardLayout();
	// private VedioPanel fullscreenVideoPanel = null;//视频
	
	
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
		// TODO 自动生成的方法存根
		if(BillModel== -1){ //初始
			grossBTN.setEnabled(false);
			tareBTN.setEnabled(false);
			saveBTN.setEnabled(false);
			printBTN.setEnabled(true);
			deleteBTN.setEnabled(false);
			cancelBTN.setEnabled(false); 
			carWeightBTN.setEnabled(false);
			queryBTN.setEnabled(true);
		}
		else if(BillModel == 0){//编辑
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
		else if(BillModel == 1){//需要复磅，暂无需求

		}
	}

	private void initialize()
	{
		super.setButtons(getButtons());
		getBillWorkPanel();
		getCardLayoutPanel().add("splitpanel", getSpanel());
		add(getCardLayoutPanel());

		getSpanel().setDividerLocation(335);
		//摄像头开发
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

		tareBTN = new ButtonObject("过皮重", "", 0, "tare");
		tareBTN.setEnabled(false);
		grossBTN = new ButtonObject("过毛重", "", 0, "gross");
		grossBTN.setEnabled(false);
		saveBTN = new ButtonObject("保存", "", 0, "save");
		saveBTN.setEnabled(false);
		printBTN = new ButtonObject("打印", "", 0, "print");
		printBTN.setEnabled(false);
		deleteBTN = new ButtonObject("作废", "", 0, "delete");
		deleteBTN.setEnabled(false);
		cancelBTN = new ButtonObject("取消", "", 0, "cancel");
		cancelBTN.setEnabled(false);
		refreshBTN = new ButtonObject("刷新", "", 0, "refresh");

		fubBTN = new ButtonObject("复磅", "", 0, "refresh");
		fubBTN.setEnabled(false);

		carHBTN = new ButtonObject("车辆历史数据", "", 0, "carhistory");
		carHBTN.setEnabled(true);

		carWeightBTN = new ButtonObject("车辆标皮", "", 0, "carweight");
		carWeightBTN.setEnabled(false);
		
		queryBTN = new ButtonObject("查询","",0,"query");
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
	

	//按钮事件
	public void onButtonClicked(ButtonObject bo) {
		if(bo == tareBTN)//皮重
			onOK();
		else if(bo == grossBTN)//毛重
			onOK();
		else if(bo == saveBTN)
			getBillWorkPanel().OnSave();
		else if(bo == printBTN)//打印
			getBillWorkPanel().onPrint();
		else if(bo == deleteBTN)//作废
			getBillWorkPanel().onDelete();
		else if(bo == cancelBTN)//取消
		{
			getBillWorkPanel().onCancel();
		}	 
		else if(bo == refreshBTN)//刷新
			getBillWorkPanel().onRefresh();
		else if(bo == carHBTN)//车辆历史数据
			getBillWorkPanel().onCarHistory();
		else if(bo == carWeightBTN){//车辆标皮
			getBillWorkPanel().onCarWeight();  
		}else if(bo==queryBTN){ // 查询
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
		//车辆查询    getHardWarePanel().getMeasPanel().getQueuePanel().windowCLosed();

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
