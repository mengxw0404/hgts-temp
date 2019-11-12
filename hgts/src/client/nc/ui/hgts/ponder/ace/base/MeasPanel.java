package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nc.bs.logging.Logger;
//import nc.itf.hgts.queue.ICarQueueItf;
import nc.ui.hgts.pondUI.ace.Hardware.CommPortRead;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.bd.cal.CalParaVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
//import nc.vo.hgts.histtare.HistTareVO;
import nc.vo.hgts.invoicesheet.DoListSendnoticeAgg;
import nc.vo.hgts.invoicesheet.DoListInvoiceAgg;

public class MeasPanel   extends UIPanel  implements Observer{
	private static final long serialVersionUID = 1L;
	private JTextField titleTextField = null;
	//private QueueInfoTextField queueTextField = null;
	// private QueuePanel queuePanel = null;
	private LedNumberPanel ledPanel = null;
	private UIPanel statusPanel = null;
	private UIPanel histortPanel = null;
	private UITextField txtWaitingShow = null;
	private UITextField txtTareAvgShow = null;
	private JTextField txtTareMinShow = null;
	private JTextField txtMaxShow = null;
	private JTextField weightField = null;
	private StatuDesginPanel statusDesginPanel = null;
	private HardWarePanel parents = null;
	private WeightMonitor weightMonitor = null;
	private UIPanel northPanel;
	private UIPanel centerPanel;
	private UIPanel southPanel;
	private Queue<UFDouble> weightQueue = new LinkedList();
	CalParaVO measdoc = null;
	private CommPortRead reader = null;
	private CodeStack codeStack;
	int flag = 0;  

	int guil=0; // 归零个数

	FormulaParseTool tool=null;

	public MeasPanel(){
		initialize();
	}

	public MeasPanel(HardWarePanel parents){
		this.parents = parents;
		initialize();
	}

	private void initialize(){
		setLayout(new BorderLayout());
		setBackground(new Color(51, 51, 51));

		add(getNorthPanel(), "North");
		add(getCenterPanel(), "Center");
		add(getSouthPanel(), "South");

		this.measdoc = getBillWorkPanel().getMeasDoc();
		runPonderWeight();
		getCodeStack();
	}

	public WeightMonitor getWeightMonitor(){
		if (this.weightMonitor == null){
			this.weightMonitor = new WeightMonitor(getBillWorkPanel());
			this.weightMonitor.surf.start();
		}
		return this.weightMonitor;
	}

	private UIPanel getHistortPanel(){
		if (this.histortPanel == null){
			this.histortPanel = new UIPanel();
			this.histortPanel.setLayout(new GridLayout(2, 2));
			this.histortPanel.setBackground(new Color(51, 51, 51));


			UIPanel maxPanel = new UIPanel();
			maxPanel.setLayout(new FlowLayout());
			maxPanel.add(createLabel("最大值"));
			maxPanel.add(getTxtMaxShow());
			maxPanel.setBackground(new Color(51, 51, 51));
			this.histortPanel.add(maxPanel);


			UIPanel minTarePanel = new UIPanel();
			minTarePanel.add(createLabel("最小值"));
			minTarePanel.add(getTxtTareMinShow());
			minTarePanel.setBackground(new Color(51, 51, 51));
			this.histortPanel.add(minTarePanel);


			UIPanel avgTarePanel = new UIPanel();
			avgTarePanel.add(createLabel("平均值"));
			avgTarePanel.add(getTxtTareAvgShow());
			avgTarePanel.setBackground(new Color(51, 51, 51));
			this.histortPanel.add(avgTarePanel);


			UIPanel waitNumPanel = new UIPanel();
			waitNumPanel.add(createLabel("等  待"));
			waitNumPanel.add(gettxtWaitingShow());
			waitNumPanel.setBackground(new Color(51, 51, 51));
			this.histortPanel.add(waitNumPanel);
		}
		return this.histortPanel;
	}

	public JPanel getStatusPanel()
	{
		if (this.statusPanel == null)
		{
			this.statusPanel = new UIPanel();
			this.statusPanel.setLayout(new BorderLayout());


			this.statusPanel.setBackground(new Color(51, 51, 51));
			this.statusPanel.add(getStatusDesginPanel(), "North");
		}
		return this.statusPanel;
	}

	public StatuDesginPanel getStatusDesginPanel()
	{
		if (this.statusDesginPanel == null) {
			this.statusDesginPanel = new StatuDesginPanel();
		}
		return this.statusDesginPanel;
	}

	private UITextField gettxtWaitingShow()
	{
		if (this.txtWaitingShow == null) {
			this.txtWaitingShow = createHistoryText();
		}
		return this.txtWaitingShow;
	}

	private UITextField createHistoryText()
	{
		UITextField field = new UITextField();
		field.setFont(new Font("宋体", 1, 12));
		field.setForeground(Color.black);
		field.setText("0.00");
		field.setColumns(12);
		field.setEditable(false);
		field.setHorizontalAlignment(4);
		field.setBackground(new Color(51, 51, 51));

		return field;
	}

	private UITextField getTxtTareAvgShow()
	{
		if (this.txtTareAvgShow == null) {
			this.txtTareAvgShow = createHistoryText();
		}
		return this.txtTareAvgShow;
	}

	private JTextField getTxtTareMinShow()
	{
		if (this.txtTareMinShow == null) {
			this.txtTareMinShow = createHistoryText();
		}
		return this.txtTareMinShow;
	}

	private JTextField getTxtMaxShow()
	{
		if (this.txtMaxShow == null) {
			this.txtMaxShow = createHistoryText();
		}
		return this.txtMaxShow;
	}

	private JTextField getTitleTextField()
	{
		if (this.titleTextField == null)
		{
			this.titleTextField = new JTextField();
			this.titleTextField.setFont(new Font("宋体", 1, 32));
			this.titleTextField.setForeground(Color.lightGray);
			this.titleTextField.setText("磅房计量");
			this.titleTextField.setEditable(false);
			this.titleTextField.setColumns(4);
			this.titleTextField.setHorizontalAlignment(0);
			this.titleTextField.setBackground(new Color(102, 102, 102));
		}
		return this.titleTextField;
	}

	/* public QueueInfoTextField getQueueTextField()
  {
    if (this.queueTextField == null)
    {
      this.queueTextField = new QueueInfoTextField();
      this.queueTextField.setBounds(new Rectangle(6, 245, 322, 25));
      this.queueTextField.setFont(new Font("宋体", 1, 12));
      this.queueTextField.setForeground(Color.ORANGE);
      this.queueTextField.setEditable(false);
      this.queueTextField.setBackground(new Color(102, 102, 102));
      this.queueTextField.setHorizontalAlignment(2);

      ICarQueueItf carQueueItf = (ICarQueueItf)NCLocator.getInstance().lookup(ICarQueueItf.class);
      try
      {
        this.queueTextField.setCarQueue(Arrays.asList(carQueueItf.getUpListResult()));
      }
      catch (BusinessException e)
      {
        FmpubLogger.error(e.getMessage(), e);
      }
      this.queueTextField.setCarInfo();
    }
    return this.queueTextField;
  }

  public void setQueueText(String txt)
  {
    getQueueTextField().setText(txt);
  }*/

	private JLabel createLabel(String text)
	{
		JLabel label = new JLabel();
		label.setFont(new Font("宋体", 1, 12));
		label.setLayout(new FlowLayout());
		label.setText(text);
		label.setBackground(new Color(51, 51, 51));
		label.setForeground(Color.green);

		return label;
	}

	public JTextField getWeightField()
	{
		if (this.weightField == null)
		{
			this.weightField = new JTextField();
			this.weightField.setText("0.00");
			this.weightField.setFont(new Font("宋体", 1, 70));
			this.weightField.setForeground(Color.green);
			this.weightField.setEditable(false);
			this.weightField.setBackground(new Color(102, 102, 102));
			this.weightField.setHorizontalAlignment(4);
		}
		return this.weightField;
	}

	public LedNumberPanel getWeightLedPanel()
	{
		if (this.ledPanel == null) {
			this.ledPanel = new LedNumberPanel(6, 2);
		}
		return this.ledPanel;
	}

	/* public QueuePanel getQueuePanel()
  {
    if (this.queuePanel == null) {
      this.queuePanel = new QueuePanel();
    }
    return this.queuePanel;
  }*/

	/* public void setHardDriverStatus(int radio, int vedio, int ir, int com, int trans, int hander, int tare, int gross)
  {
    if (radio == 0) {
      getStatusDesginPanel().getRadioFreFiled().setForeground(Color.GREEN);
    } else if (radio == 1) {
      getStatusDesginPanel().getRadioFreFiled().setForeground(Color.RED);
    } else if (radio == 2) {
      getStatusDesginPanel().getRadioFreFiled().setForeground(Color.YELLOW);
    }
    if (vedio == 0) {
      getStatusDesginPanel().getVideoFiled().setForeground(Color.GREEN);
    } else if (vedio == 1) {
      getStatusDesginPanel().getVideoFiled().setForeground(Color.RED);
    } else if (vedio == 2) {
      getStatusDesginPanel().getVideoFiled().setForeground(Color.YELLOW);
    }
    if (ir == 0) {
      getStatusDesginPanel().getInfraredFiled().setForeground(Color.GREEN);
    } else if (ir == 1) {
      getStatusDesginPanel().getInfraredFiled().setForeground(Color.RED);
    } else if (ir == 2) {
      getStatusDesginPanel().getInfraredFiled().setForeground(Color.YELLOW);
    }
    if (com == 0) {
      getStatusDesginPanel().getSerialPortFiled().setForeground(
        Color.GREEN);
    } else if (com == 1) {
      getStatusDesginPanel().getSerialPortFiled().setForeground(Color.RED);
    } else if (com == 2) {
      getStatusDesginPanel().getSerialPortFiled().setForeground(
        Color.YELLOW);
    }
    if (trans == 0) {
      getStatusDesginPanel().getTransferField().setForeground(Color.GREEN);
    } else if (trans == 1) {
      getStatusDesginPanel().getTransferField().setForeground(Color.RED);
    } else if (trans == 2) {
      getStatusDesginPanel().getTransferField().setForeground(
        Color.YELLOW);
    }
    if (hander == 0) {
      getStatusDesginPanel().getHandWorkField().setForeground(Color.GREEN);
    } else if (hander == 1) {
      getStatusDesginPanel().getHandWorkField().setForeground(Color.RED);
    } else if (hander == 2) {
      getStatusDesginPanel().getHandWorkField().setForeground(
        Color.YELLOW);
    }
    if (tare == 0) {
      getStatusDesginPanel().getSkipWeightField().setForeground(
        Color.GREEN);
    } else if (tare == 1) {
      getStatusDesginPanel().getSkipWeightField().setForeground(Color.RED);
    } else if (tare == 2) {
      getStatusDesginPanel().getSkipWeightField().setForeground(
        Color.YELLOW);
    }
    if (gross == 0) {
      getStatusDesginPanel().getGrossWeightField().setForeground(
        Color.GREEN);
    } else if (gross == 1) {
      getStatusDesginPanel().getGrossWeightField().setForeground(
        Color.RED);
    } else if (gross == 2) {
      getStatusDesginPanel().getGrossWeightField().setForeground(
        Color.YELLOW);
    }
  }*/

	public void setHistoryValue(UFDouble max, UFDouble min, UFDouble avg, UFDouble wait)
	{
		max = max == null ? UFDouble.ZERO_DBL : max;
		min = min == null ? UFDouble.ZERO_DBL : min;
		avg = avg == null ? UFDouble.ZERO_DBL : avg;

		getTxtMaxShow().setText(String.valueOf(max.doubleValue()));
		getTxtTareMinShow().setText(String.valueOf(min.doubleValue()));
		getTxtTareAvgShow().setText(String.valueOf(avg.doubleValue()));
	}

	public void setWaitingNum(Integer custWaitingNum, Integer totalWaitingNum)
	{
		String showText = "";
		if (custWaitingNum == null) {
			showText = showText + "NA";
		} else {
			showText = showText + custWaitingNum.intValue();
		}
		if (totalWaitingNum == null) {
			showText = showText + "/0";
		} else {
			showText = showText + "/" + totalWaitingNum.intValue();
		}
		gettxtWaitingShow().setText(showText);
	}

	public UFDouble getCurrentWeight()
	{
		String weight = getWeightField().getText();
		if ((weight != null) && (!weight.equals(""))) {
			return new UFDouble(weight);
		}
		return new UFDouble(0);
	}

	private void runPonderWeight(){
		//是否模拟读数
		if (Integer.parseInt(this.measdoc.getAttributeValue("anreading").toString()) == 1){
			Timer timer = new Timer();
			timer.schedule(new RunClock(), 0L, 100L);
		}
		else{
			String com = "";
			try{
				if(this.measdoc.getAttributeValue("portcode")!=null){
					com = "COM" +this.measdoc.getAttributeValue("portcode").toString();
				}
				String pk_protpara=HgtsPubTool.getStringNullAsTrim(this.measdoc.getAttributeValue("portpara"));

				if(null !=pk_protpara && !"".equals(pk_protpara)){
					tool=new FormulaParseTool();
					String protpara=tool.getNameByID("bd_defdoc", "name", "pk_defdoc", pk_protpara);							

					String[] bit = new String[3];
					bit[0] = protpara.substring(0, protpara.indexOf("."));
					bit[1] = protpara.substring(protpara.indexOf(".") + 1, protpara.lastIndexOf("."));
					bit[2] = protpara.substring(protpara.lastIndexOf(".") + 1);

					this.reader = new CommPortRead(com, Integer.parseInt(bit[0]), Integer.parseInt(bit[1]), Integer.parseInt(bit[2]),0,
							Integer.parseInt(this.measdoc.getAttributeValue("clockpara").toString()), Integer.parseInt(this.measdoc.getAttributeValue("rdlen").toString()),
							Integer.parseInt(this.measdoc.getAttributeValue("effelen").toString()), this.measdoc.getAttributeValue("rdorder").toString().equals("1"), 
							this.measdoc.getAttributeValue("outformat").toString() == "2"? "BCD": "ASCII" ,
									this.measdoc.getAttributeValue("startchar").toString(), this.measdoc.getAttributeValue("endchar").toString(),
									Integer.parseInt(this.measdoc.getAttributeValue("resostart").toString()), 
									Integer.parseInt(this.measdoc.getAttributeValue("decontorl").toString()), Integer.parseInt(this.measdoc.getAttributeValue("calunit").toString()), "0");


					this.reader.initPortRead();
					this.reader.addObserver(this);
				}
			}catch (Exception e){
				Logger.error(e.getMessage());
				JOptionPane.showMessageDialog(this, e.getMessage(), "端口参数错误", 0);

				return;
			}
		}
	}

	public CodeStack getCodeStack()
	{ 
		if(this.codeStack == null){
			UFBoolean israd = this.measdoc.getAttributeValue("isradition") == null ? UFBoolean.valueOf("N") : UFBoolean.valueOf(this.measdoc.getAttributeValue("isradition").toString());
			if ( null !=this.measdoc.getAttributeValue("infraredport")  && (israd.booleanValue()))
			{
				this.codeStack = new CodeStack(this,this.measdoc.getAttributeValue("infraredport").toString());
			}
		}
		return this.codeStack;
	}

	public void reFlashPonder() {}

	class RunClock extends TimerTask{
		boolean isImitateread = false;
		double maxWeight = 10.0D * (1.0D + Math.random());
		double currentWeight = 0.0D;

		public RunClock() {}
		int flag = 0;
		public void run()
		{
			/*判断是否手动过磅
			 *  String isOnhander = MeasPanel.this.getHeadCardPanel().getHeadItem("isonhand").getValue();
      UFBoolean bOnhander = new UFBoolean(isOnhander);
      if (bOnhander.booleanValue()) {
        return;
      }*/
			if(currentWeight<=0){
				setGuil(1);
			}

			if ((MeasPanel.this.getBillWorkPanel().getBillModel() == 1) 
					||  (MeasPanel.this.getBillWorkPanel().getBillModel() == 2))
			{
				if (this.currentWeight < this.maxWeight + 50.0D) {
					this.currentWeight += Math.random();
				}
			}
			else if (this.currentWeight < this.maxWeight) {
				this.currentWeight += Math.random();
			}
			UFDouble nweight = new UFDouble(this.currentWeight);
			if (MeasPanel.this.weightQueue.size() == 20) {
				MeasPanel.this.weightQueue.remove();
			}
			nweight = nweight.setScale(2, BigDecimal.ROUND_HALF_UP);
			//    MeasPanel.this.weightQueue.add(nweight);
			//			if (MeasPanel.this.getBillWorkPanel().getBillModel() == -1)
			//			{//皮重
			//				this.maxWeight = (10.0D * (1.0D + Math.random()));
			//				this.currentWeight = 0.0D;    	
			//				if(currentWeight<=0){
			//					setGuil(1);
			//				}
			//			}
			MeasPanel.this.getWeightField().setText(String.valueOf(nweight.doubleValue()));    
			MeasPanel.this.getWeightLedPanel().setDispnumber(nweight);
			/**地磅读数稳定判断 begin**/

			for (Iterator<UFDouble> iter = MeasPanel.this.weightQueue.iterator(); iter.hasNext();) {  
				UFDouble old = (UFDouble) iter.next();
				// 当前数值与前五组数值差 大于0.01
				if(Math.abs(nweight.sub(old).doubleValue()) > 0.01){
					flag++;
				}
				//System.out.println(nweight.sub(old).doubleValue());
			}
			MeasPanel.this.weightQueue.add(nweight);
			//如果五次有三次数据大于1 ，则不回写数值；否则
			//当数据不稳定时，应该清空数据并设置保存按钮不可用

			if(MeasPanel.this.weightQueue.size() == 20 && flag > 0){
				flag = 0;
				getParents().getParents().saveBTN.setEnabled(false);   	  
				return;
			}else if(MeasPanel.this.weightQueue.size() == 20 && flag == 0 ){
				/**地磅读数稳定判断 --end
				 * **/
				if (MeasPanel.this.getBillWorkPanel().getFReceOrSend() == 1 && MeasPanel.this.getBillWorkPanel().getBillModel() == 0)
				{//毛重

					qusByDiffBui(1,nweight);

				}
				else if (MeasPanel.this.getBillWorkPanel().getFReceOrSend() == 0 && MeasPanel.this.getBillWorkPanel().getBillModel() == 0)
				{//皮重    	

					qusByDiffBui(0,nweight);

				}else if(MeasPanel.this.getBillWorkPanel().getFReceOrSend() == 2 && MeasPanel.this.getBillWorkPanel().getBillModel() == 0 ){
					// 窑街 项目
					qusByDiffBui(2,nweight);
				}
				else if (MeasPanel.this.getBillWorkPanel().getBillModel() == 2)
				{
				}
				if (MeasPanel.this.getBillWorkPanel().getBillModel() == -1) {
					getBillWorkPanel().afterEditNum();
				}else{        	  
					getParents().getParents().saveBTN.setEnabled(true);
				}
			}else{
				getParents().getParents().saveBTN.setEnabled(false);
			}
		}



	}

	public HardWarePanel getParents()
	{
		return this.parents;
	}

	protected BillWorkPanel getBillWorkPanel()
	{
		return getParents().getParents().getBillWorkPanel();
	}

	protected HeadCardPanel getHeadCardPanel()
	{
		return getBillWorkPanel().getHeadCardPanel();
	}

	public void update(Observable observable, Object obj){
		if ((obj instanceof UFDouble)){

			UFDouble nweight = (UFDouble)obj;

			if(null==nweight || nweight.doubleValue()<=0){
				setGuil(1);
			}

			if (this.weightQueue.size() == 10) {
				this.weightQueue.remove();
			}
			//注释此句是为避免当前数据与自身比较
			//this.weightQueue.add(nweight);
			String valStr = nweight.toString();
			System.out.println("地磅仪表传过来的数值："+((UFDouble) obj).doubleValue());
			getWeightField().setText(valStr);
			getWeightLedPanel().setDispnumber(nweight);		  

			/**地磅读数稳定判断 begin**/

			for (Iterator<UFDouble> iter = MeasPanel.this.weightQueue.iterator(); iter.hasNext();) {  
				UFDouble old = (UFDouble) iter.next();
				// 当前数值与前五组数值差 大于 0.1
				if(Math.abs(nweight.sub(old).doubleValue()) > 0.1){
					flag++;
				}
				//  System.out.println(nweight.sub(old).doubleValue());
			}
			MeasPanel.this.weightQueue.add(nweight);
			//如果五次有三次数据大于1 ，则不回写数值；否则
			if(MeasPanel.this.weightQueue.size() == 10 && flag > 0){
				flag = 0;
				getParents().getParents().saveBTN.setEnabled(false);
				return;
			}else if(MeasPanel.this.weightQueue.size() == 10 && flag == 0){
				/**地磅读数稳定判断 --end
				 * **/
				if (MeasPanel.this.getBillWorkPanel().getFReceOrSend() == 1 && MeasPanel.this.getBillWorkPanel().getBillModel() == 0)
				{//毛重
					if(null==nweight || nweight.doubleValue()<=0){ // 有出现0
						this.setGuil(1);
					}

					qusByDiffBui(1,nweight);

				}
				else if (MeasPanel.this.getBillWorkPanel().getFReceOrSend() == 0 && MeasPanel.this.getBillWorkPanel().getBillModel() == 0)
				{//皮重

					if(null==nweight || nweight.doubleValue()<=0){ // 有出现0
						this.setGuil(1);
					}

					qusByDiffBui(0,nweight);


				}
				else if (MeasPanel.this.getBillWorkPanel().getBillModel() == 2)
				{//复磅
					getHeadCardPanel().setBodyValueAt(nweight, 0, "fubs");
					getHeadCardPanel().getBodyItem("fubs").updateValue();  
				}
				if (MeasPanel.this.getBillWorkPanel().getBillModel() == -1) {
					getBillWorkPanel().afterEditNum();

					UFDouble weight= (UFDouble)obj;
					System.out.println("地磅 稳定 保存后 仪表传过来的数值："+weight);
					if(null==weight || weight.doubleValue()<=0){
						setGuil(1);
					}
				}else{        	  
					getParents().getParents().saveBTN.setEnabled(true);
				}

			}else{
				getParents().getParents().saveBTN.setEnabled(false);

				if (MeasPanel.this.getBillWorkPanel().getBillModel() == -1) {
					UFDouble weight= (UFDouble)obj;
					System.out.println("地磅  仪表传过来的数值："+weight);
					if(null==weight || weight.doubleValue()<=0){
						setGuil(1);
					}
				}      
			}      
		}
	}

	public CommPortRead getReader()
	{
		return this.reader;
	}

	public CodeStack getCodeStacker()
	{
		return this.codeStack;
	}


	/* public HistTareVO getHistTareValue()
  {
    HistTareVO tareVO = new HistTareVO();
    tareVO.setNmaxtare(new UFDouble(this.txtMaxShow.getText()));
    tareVO.setNmintare(new UFDouble(this.txtTareMinShow.getText()));
    tareVO.setNaveragetare(new UFDouble(this.txtTareAvgShow.getText()));
    return tareVO;
  }*/

	public UIPanel getNorthPanel()
	{
		if (this.northPanel == null)
		{
			this.northPanel = new UIPanel();
			this.northPanel.setLayout(new BorderLayout());
			this.northPanel.add(getTitleTextField(), "Center");
			this.northPanel.add(getStatusPanel(), "South");
		}
		return this.northPanel;
	}

	public UIPanel getCenterPanel()
	{
		if (this.centerPanel == null)
		{
			this.centerPanel = new UIPanel();
			this.centerPanel.setLayout(new GridLayout(3, 1));

			this.centerPanel.add(getHistortPanel());


			this.centerPanel.add(getWeightLedPanel());

			this.centerPanel.add(getWeightMonitor());
		}
		return this.centerPanel;
	}

	public UIPanel getSouthPanel()
	{
		if (this.southPanel == null)
		{
			this.southPanel = new UIPanel();
			this.southPanel.setLayout(new BorderLayout());
			//   this.southPanel.add(getQueuePanel(), "Center");
		}
		return this.southPanel;
	}

	public Queue<UFDouble> getWeightQueue()
	{
		return this.weightQueue;
	}

	/**
	 * 根据不同的业务类型取数
  // 2018-4-13 调入洗： 调出业务：先空车、后重车；调入业务：先重车、后空车
  // 调出业务：当前电脑计量参数档案上配置的矿场与发货矿场一致；
  // 调入业务：当前电脑计量参数档案上配置的矿场与调入矿场一致。
	 * 
	 * @param receOrSend:0:计量-进；1=计量-出
	 * @param nweight:计量上读取的数值
	 */
	public void qusByDiffBui(int receOrSend,UFDouble nweight){
		// 计量器具参数上的磅房对应的矿场
		try {
			String ofmine=HgtsPubTool.getStringNullAsTrim(measdoc.getAttributeValue("ofmine")); 
			DoListInvoiceAgg aggvo= this.getBillWorkPanel().getNewTListPanel().var_bodyVo ;
			String vbillno= aggvo.getSendnoticebillno();
			SendnoticebillHVO[] shvo = (SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+vbillno+"'");
			String	jytype=shvo[0].getAttributeValue("jytype")==null?"1":shvo[0].getAttributeValue("jytype").toString();// 交易类型
			String pk_fhkc=HgtsPubTool.getStringNullAsTrim(shvo[0].getAttributeValue("pk_fhkc"));	// 发货矿场
			String pk_drkc=HgtsPubTool.getStringNullAsTrim(shvo[0].getAttributeValue("pk_drkc"));	// 调入矿场
			if(receOrSend==0){		  
				if("2".equals(jytype)){
					//  调入洗 业务类型
					// 调出业务：当前电脑计量参数档案上配置的矿场与发货矿场一致；
					// 调入业务：当前电脑计量参数档案上配置的矿场与调入矿场一致。
					if(null !=ofmine && !"".equals(ofmine)){
						if(ofmine.equals(pk_fhkc)){ // 调出 ：先空车、后重车
							// 原始逻辑
							// 皮重
							in_piz_konche(nweight);

						}else if(ofmine.equals(pk_drkc)){ // 调入：先重车、后空车
							// 毛重
							maoz(nweight);
						}
					}
				}else if("1".equals(jytype) || "4".equals(jytype)) {
					//jytype==1 || jytype==4 :商品煤、其它-出
					// 原始逻辑   先空车、后重车
					// 皮重
					in_piz_konche(nweight);

				}else if("3".equals(jytype)){ // 其它-入: 先重车、后空车
					// 毛重
					maoz(nweight);			  
				}

			}else if(receOrSend==1){
				if("2".equals(jytype)){
					//  调入洗 业务类型
					// 调出业务：当前电脑计量参数档案上配置的矿场与发货矿场一致；
					// 调入业务：当前电脑计量参数档案上配置的矿场与调入矿场一致。
					if(null !=ofmine && !"".equals(ofmine)){
						if(ofmine.equals(pk_fhkc)){ // 调出 ：先空车、后重车
							// 原始逻辑
							// 毛重
							out_maoz_zhongche(nweight);

						}else if(ofmine.equals(pk_drkc)){ // 调入：先重车、后空车
							// 皮重
							inOrOut(nweight);
						}
					}
				}else if("1".equals(jytype)|| "4".equals(jytype)){
					// 原始逻辑
					// 毛重
					out_maoz_zhongche(nweight);

				}else if("3".equals(jytype)){ // 其它-出 ：先重车，后空车
					// 皮重
					inOrOut(nweight);
				}

			}else if(receOrSend==2){
				//DoListInvoiceAgg aggvo= this.getBillWorkPanel().getInOrOutInvoiceListPanel().var_bodyVo ;
				if(HgtsPubTool.getUFDoubleNullAsZero(aggvo.getPiz()).doubleValue()<=0){
					in_piz_konche(nweight);
				}else{
					out_maoz_zhongche(nweight);
				}
			}
		} catch (UifException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	// 毛重
	public void maoz(UFDouble nweight){
		// 皮重
		getHeadCardPanel().setBodyValueAt(null, 0, "piz");
		getHeadCardPanel().getBodyItem("piz").updateValue();

		getHeadCardPanel().setBodyValueAt(nweight, 0, "maoz");
		getHeadCardPanel().getBodyItem("maoz").updateValue(); 
	}
	/**
	 * 计量-进：空车 
	 * 取 皮重  原始逻辑
	 * @param nweight
	 */
	public void in_piz_konche(UFDouble nweight){
		// 皮重
		getHeadCardPanel().setBodyValueAt(nweight, 0, "piz");
		getHeadCardPanel().getBodyItem("piz").updateValue();
		Object standardweight =getHeadCardPanel().getBodyValueAt(0, "carno.standardweight");
		if ((standardweight != null) && (!standardweight.toString().equals("")))
		{
			getHeadCardPanel().setBodyValueAt(nweight.sub(new UFDouble(standardweight.toString())), 0, "wucha");    
			getHeadCardPanel().getBodyItem("wucha").updateValue();
		}
	}

	/**
	 * 计量-出：重车
	 * 取 毛重  原始逻辑
	 * @param nweight
	 */
	public void out_maoz_zhongche(UFDouble nweight){
		// 毛重
		getHeadCardPanel().setBodyValueAt(nweight, 0, "maoz");
		getHeadCardPanel().getBodyItem("maoz").updateValue(); 

		Object piz =getHeadCardPanel().getBodyValueAt(0, "piz");
		if ((piz != null) && (!piz.toString().equals("")))
		{
			getHeadCardPanel().setBodyValueAt(nweight.sub(new UFDouble(piz.toString())), 0, "jingz");    
			getHeadCardPanel().getBodyItem("jingz").updateValue();

		}
	}

	/**
	 * 计量-进、计量-出
	 * 先进重车，后出空车
	 * @param nweight
	 */
	public void inOrOut(UFDouble nweight){
		getHeadCardPanel().setBodyValueAt(nweight, 0, "piz");
		getHeadCardPanel().getBodyItem("piz").updateValue(); 

		UFDouble maoz =HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "maoz"));
		if (maoz != null && maoz.doubleValue()>0){
			getHeadCardPanel().setBodyValueAt(maoz.sub(nweight), 0, "jingz");    
			getHeadCardPanel().getBodyItem("jingz").updateValue();			    		    
		}

		Object standardweight =getHeadCardPanel().getBodyValueAt(0, "carno.standardweight");
		if ((standardweight != null) && (!standardweight.toString().equals("")))
		{
			getHeadCardPanel().setBodyValueAt(nweight.sub(new UFDouble(standardweight.toString())), 0, "wucha");    
			getHeadCardPanel().getBodyItem("wucha").updateValue();
		}
	}
	public void setWeightQueue(){

		weightQueue= new LinkedList();

	}


	public int getGuil() {
		return guil;
	}


	public void setGuil(int guil) {
		this.guil = guil;
	}





}
