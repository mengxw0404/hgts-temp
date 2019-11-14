package nc.ui.hgts.ponder_ly.ace.base;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nc.bs.logging.Logger;
//import nc.itf.hgts.queue.ICarQueueItf;
import nc.ui.hgts.pondUI.ace.Hardware.CommPortRead;
import nc.ui.hgts.ponder.ace.base.LedNumberPanel;
import nc.ui.hgts.ponder.ace.base.StatuDesginPanel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UILabel;
import nc.vo.hgts.bd.cal.CalParaVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.ContractVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.ui.pub.beans.UIRefPane;

public class MeasPanel extends UIPanel implements Observer{
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
	private WeightMonitor weightMonitor = null;
	private UIPanel northPanel;
	private UIPanel centerPanel;
	private UIPanel southPanel;
	private Queue<UFDouble> weightQueue = new LinkedList();
	CalParaVO measdoc = null;
	private CommPortRead reader = null;
	private CodeStack codeStack;
	private BillWorkPanel parents;
	int flag = 0;  
	FormulaParseTool tool=null;
	public MeasPanel()
	{
		initialize();
	}


	public MeasPanel(BillWorkPanel parents)
	{
		this.parents = parents;
		initialize();
	}



	private void initialize(){
		// setLayout(new BorderLayout());
		// setBackground(new Color(51, 51, 51));

		//   add(getNorthPanel(), "North");
		//   add(getCenterPanel(), "Center");
		//   add(getSouthPanel(), "South");

		try {
			this.measdoc = getParents().getMeasDoc();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		runPonderWeight();

		// 红外
		//getCodeStack();

	}

	public WeightMonitor getWeightMonitor()
	{
		if (this.weightMonitor == null)
		{
			this.weightMonitor = new WeightMonitor(getParents());
			this.weightMonitor.surf.start();
		}
		return this.weightMonitor;
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

	private void runPonderWeight()
	{
		//是否模拟读数
		if (Integer.parseInt(this.measdoc.getAttributeValue("anreading").toString()) == 1)
		{
			Timer timer = new Timer();

			timer.schedule(new RunClock(), 0L, 100L);
		}
		else
		{
			String com = "";
			try
			{
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

					this.reader = new CommPortRead(com, Integer.parseInt(bit[0]), Integer.parseInt(bit[1]), 
							Integer.parseInt(bit[2]),0,
							Integer.parseInt(this.measdoc.getAttributeValue("clockpara").toString()), // 时钟参数
							Integer.parseInt(this.measdoc.getAttributeValue("rdlen").toString()),		// 读数长度
							Integer.parseInt(this.measdoc.getAttributeValue("effelen").toString()),	// 有效长度
							this.measdoc.getAttributeValue("rdorder").toString().equals("1"), 		// 读数顺序
							this.measdoc.getAttributeValue("outformat").toString() == "2"? "BCD": "ASCII" ,// 输出格式
									this.measdoc.getAttributeValue("startchar").toString(),   // 开始字符
									this.measdoc.getAttributeValue("endchar").toString(),		// 终止字符
									Integer.parseInt(this.measdoc.getAttributeValue("resostart").toString()), // 解析起始位
									Integer.parseInt(this.measdoc.getAttributeValue("decontorl").toString()), // 小数点控制位
									Integer.parseInt(this.measdoc.getAttributeValue("calunit").toString()), "0");// 计量单位


					this.reader.initPortRead();
					this.reader.addObserver(this);
				}
			}
			catch (Exception e)
			{
				Logger.error(e.getMessage());
				JOptionPane.showMessageDialog(this, e.getMessage(), "端口参数错误", 0);

				return;
			}
		}
	}

	public CodeStack getCodeStack()
	{ 
		if(this.codeStack == null){
			// 启用红外
			UFBoolean israd = this.measdoc.getAttributeValue("isradition") == null ? UFBoolean.valueOf("N") : UFBoolean.valueOf(this.measdoc.getAttributeValue("isradition").toString());
			// 红外串口
			if ( null !=this.measdoc.getAttributeValue("infraredport")  && (israd.booleanValue()))
			{
				this.codeStack = new CodeStack(this,this.measdoc.getAttributeValue("infraredport").toString());
			}
		}
		return this.codeStack;
	}

	public void reFlashPonder() {}

	class RunClock
	extends TimerTask
	{
		boolean isImitateread = false;
		double maxWeight = 10.0D * (1.0D + Math.random());
		double currentWeight = 0.0D;

		public RunClock() {}
		int flag = 0;
		int timenum = 0;
		public void run(){ 

			this.currentWeight += Math.random();
			if(currentWeight<100 && timenum==0){
				this.currentWeight += Math.random();
			}else if(currentWeight>=100 && timenum==0){
				//this.currentWeight -= Math.random();
				this.currentWeight = 0.0D;    		
				timenum= 0;
			}else{
				this.currentWeight -= Math.random();
				timenum--;
			}

			UFDouble nweight = new UFDouble(this.currentWeight);
			/*if (MeasPanel.this.weightQueue.size() == 20) {
    		MeasPanel.this.weightQueue.remove();
    	}*/

			nweight = nweight.setScale(2, BigDecimal.ROUND_HALF_UP);
			if (MeasPanel.this.getParents().getBillModel() == -1){
				this.maxWeight = (10.0D * (1.0D + Math.random()));
				this.currentWeight = 0.0D;    	
				getParents().getParents().saveBTN.setEnabled(false);   	
				getParents().getParents().cancelBTN.setEnabled(false);  
				getParents().getParents().invoiceBTN.setEnabled(false);
				getParents().getParents().addBTN.setEnabled(true); 
				getParents().getParents().editBTN.setEnabled(true); 
				getParents().getParents().qryBTN.setEnabled(true); 
				getParents().getParents().deleteBTN.setEnabled(true);
				getParents().getParents().printBTN.setEnabled(true);
				getParents().getParents().approveBTN.setEnabled(true);
				getParents().getParents().unapproveBTN.setEnabled(true);

				getHeadCardPanel().getHeadItem("hmaozhong").setName("0.00");
				getHeadCardPanel().getHeadItem("hchaoqian").setName("0.00");
			}else{    		
				MeasPanel.this.getWeightField().setText(String.valueOf(nweight.doubleValue()));    
				MeasPanel.this.getWeightLedPanel().setDispnumber(nweight);
				AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO, 
						ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
				InvoicesheetHVO headervo=aggPonder.getParentVO();
				if(null!=headervo.getPrimaryKey() && MeasPanel.this.getParents().getBillModel() == 1){

					//String col_name=getHeadCardPanel().getHeadItem("hmaozhong").getName();

					/**地磅读数稳定判断 begin**/
					for (Iterator<UFDouble> iter = MeasPanel.this.weightQueue.iterator(); iter.hasNext();) {  
						UFDouble old = (UFDouble) iter.next();
						// 当前数值与前五组数值差 大于0.01
						if(Math.abs(nweight.sub(old).doubleValue()) > 0.01){
							flag++;
						}
					}
					MeasPanel.this.weightQueue.add(nweight);    			
					/**地磅读数稳定判断 --end
					 * **/
					if (MeasPanel.this.getParents().getFReceOrSend() == 3
							&& (MeasPanel.this.getParents().getBillModel() == 1 
							&&	null!=headervo.getPrimaryKey()))
					{//毛重
						getParents().getParents().saveBTN.setEnabled(true);   	
						getParents().getParents().cancelBTN.setEnabled(true);   
						getParents().getParents().addLineBTN.setEnabled(true);   	
						getParents().getParents().delLineBTN.setEnabled(true);
						getParents().getParents().insertLineBTN.setEnabled(true);
						getParents().getParents().invoiceBTN.setEnabled(true);

						//getBillCardPanel().getHeadItem("vnote").setName("检测报告号");
						getHeadCardPanel().getHeadItem("hmaozhong").setName(nweight+"");
						getHeadCardPanel().setHeadItem("hmaozhong", nweight);
						getHeadCardPanel().getHeadItem("hmaozhong").updateValue(); 

						int sel=getHeadCardPanel().getBillTable().getSelectedRow();
						if(sel>=0){
							//	sel=0;
							UFDouble biaozhong=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "bweight"));
							UFDouble zizhong=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "piz"));
							UFDouble zengzai=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "zengzai"));
							UFDouble chaoqian=nweight.sub(biaozhong).sub(zizhong).sub(zengzai);
							chaoqian = chaoqian.setScale(2, UFDouble.ROUND_HALF_UP);

							getHeadCardPanel().setHeadItem("curzcs", (sel+1));
							getHeadCardPanel().setHeadItem("targetton", biaozhong.add(zizhong));

							getHeadCardPanel().getHeadItem("hchaoqian").setName(chaoqian+"");
							getHeadCardPanel().setHeadItem("hchaoqian", chaoqian);
							getHeadCardPanel().getHeadItem("hchaoqian").updateValue();
							((UIRefPane)getHeadCardPanel().getHeadItem("hchaoqian").getComponent()).getUITextField().setForeground(Color.blue);//设置文本框字体颜色
							//((UILabel)getHeadCardPanel().getHeadItem("hchaoqian").getComponent()).setBackground(Color.blue);
							getHeadCardPanel().setHeadItem("hmaozhong", nweight);
							getHeadCardPanel().getHeadItem("hmaozhong").updateValue(); 
						}


					}

					else if (MeasPanel.this.getParents().getBillModel() == 2)
					{
					}
					if (MeasPanel.this.getParents().getBillModel() == -1) {
						getParents().afterEditNum();
					}    			
				}
			}
		}    
	}

	private BillWorkPanel getParents() {
		// TODO 自动生成的方法存根
		return this.parents;
	}


	protected HeadCardPanel getHeadCardPanel()
	{
		return getParents().getHeadCardPanel();
	}

	public void update(Observable observable, Object obj)
	{
		/*判断是否手动过磅
		 * String isOnhander = getHeadCardPanel().getHeadItem("isonhand").getValue();
    UFBoolean bOnhander = new UFBoolean(isOnhander);
    if (bOnhander.booleanValue()) {
      return;
    }*/
		if ((obj instanceof UFDouble))
		{
			UFDouble nweight = (UFDouble)obj;
			if (this.weightQueue.size() == 10) {
				this.weightQueue.remove();
			}
			//注释此句是为避免当前数据与自身比较
			//this.weightQueue.add(nweight);

			String valStr = nweight.toString();
			System.out.println(((UFDouble) obj).doubleValue());
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
			/*  if(MeasPanel.this.weightQueue.size() == 10 && flag > 0){
    	  flag = 0;
    	  getParents().getParents().saveBTN.setEnabled(false);
    	  return;
      }else if(MeasPanel.this.weightQueue.size() == 10 && flag == 0){*/
			/**地磅读数稳定判断 --end
			 * **/
			if (MeasPanel.this.getParents().getBillModel() == -1){
				getParents().getParents().saveBTN.setEnabled(false);   	
				getParents().getParents().cancelBTN.setEnabled(false);  
				getParents().getParents().addBTN.setEnabled(true); 
				getParents().getParents().editBTN.setEnabled(true); 
				getParents().getParents().qryBTN.setEnabled(true);
				getParents().getParents().deleteBTN.setEnabled(true);
				getParents().getParents().printBTN.setEnabled(true);
				getParents().getParents().approveBTN.setEnabled(true);
				getParents().getParents().unapproveBTN.setEnabled(true);
				getParents().getParents().invoiceBTN.setEnabled(false);

				getHeadCardPanel().getHeadItem("hmaozhong").setName("0.00");
				getHeadCardPanel().getHeadItem("hchaoqian").setName("0.00");
			}else{
				if (MeasPanel.this.getParents().getFReceOrSend() == 3 
						&& (MeasPanel.this.getParents().getBillModel() == 1)){//毛重

					AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO, 
							ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
					InvoicesheetHVO headervo=aggPonder.getParentVO();
					if(null!=headervo.getPrimaryKey()){
						int sel=getHeadCardPanel().getBillTable().getSelectedRow();
						if(sel>=0){

							UFDouble biaozhong=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "bweight"));
							UFDouble zizhong=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "piz"));
							UFDouble zengzai=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBillModel().getValueAt(sel, "zengzai"));
							UFDouble chaoqian=nweight.sub(biaozhong).sub(zizhong).sub(zengzai);

							getHeadCardPanel().getHeadItem("hmaozhong").setName(nweight==null?new UFDouble(0.00)+"":nweight.setScale(2, UFDouble.ROUND_HALF_UP).toString());
							getHeadCardPanel().setHeadItem("hmaozhong", nweight);
							getHeadCardPanel().getHeadItem("hmaozhong").updateValue(); 

							getHeadCardPanel().getHeadItem("hchaoqian").setName(chaoqian==null?new UFDouble(0.00)+"":chaoqian.setScale(2, UFDouble.ROUND_HALF_UP).toString());
							getHeadCardPanel().setHeadItem("hchaoqian", chaoqian.setScale(2, UFDouble.ROUND_HALF_UP));
							getHeadCardPanel().getHeadItem("hchaoqian").updateValue(); 
							((UIRefPane)getHeadCardPanel().getHeadItem("hchaoqian").getComponent()).getUITextField().setForeground(Color.blue);//设置文本框字体颜色

							getHeadCardPanel().setHeadItem("curzcs", (sel+1));
							getHeadCardPanel().setHeadItem("targetton", biaozhong.add(zizhong));
						}
					}  
					getParents().getParents().saveBTN.setEnabled(true);   	
					getParents().getParents().cancelBTN.setEnabled(true);   
					getParents().getParents().addLineBTN.setEnabled(true);   	
					getParents().getParents().delLineBTN.setEnabled(true);
					getParents().getParents().insertLineBTN.setEnabled(true);
					getParents().getParents().invoiceBTN.setEnabled(true);
				}

				else if (MeasPanel.this.getParents().getBillModel() == 2)
				{//复磅
					getHeadCardPanel().setBodyValueAt(nweight, 0, "fubs");
					getHeadCardPanel().getBodyItem("fubs").updateValue();  
				}
				if (MeasPanel.this.getParents().getBillModel() == -1) {
					getParents().afterEditNum();
				}
				getParents().getParents().saveBTN.setEnabled(true);

				/* }else{
    	  getParents().getParents().saveBTN.setEnabled(false);

      }*/
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
			//   this.northPanel.add(getTitleTextField(), "Center");
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

			// this.centerPanel.add(getHistortPanel());


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
}
