package nc.ui.hgts.ponder.ace.base;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hgts.common.YGUtils;
import nc.itf.hgts.ponder.IKeyFileReader;
import nc.itf.hgts.ponder.IPonderItf;
import nc.itf.hgts.ponder.IRfid;
import nc.itf.hgts.ponder.RFIDFactory;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.busibean.ISysInitQry;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pub.hgts.consts.SysParamConsts;
import nc.pub.hgts.encryption.Decoder;
import nc.pub.hgts.encryption.Encoder;
import nc.ui.hgts.pub.comm.ClientContext;
import nc.ui.hgts.pub.scale.PubTsBillScaleTool;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.para.SysInitBO_Client;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.bd.ToolVO;
import nc.vo.hgts.bd.cal.CalParaVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.ContractVO;
import nc.vo.hgts.invoicesheet.DoListInvoiceAgg;
import nc.vo.hgts.invoicesheet.DoListSendnoticeAgg;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.AppContext;
import nc.vo.trade.checkrule.ICheckRules;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public abstract class BillWorkPanel extends UIPanel implements ICheckRules, Observer,AWTEventListener{
	private static final long serialVersionUID = 1L;
	private HeadCardPanel headCardPanel = null;
//	private SendListPanel tailListPanel = null;
	private InvoiceListPanel newTListPanel = null;
	private InOrOutInvoiceListPanel inOrOutTListPanel = null;
	private BaseContainer parents = null;
	private int billModel = -1;//单据状态-1初始；0新增；1修改；2刷新？？
	private ClientEnvironment ce = ClientEnvironment.getInstance();
	
	private boolean isICCardUsed = false;
	private String m_corpid = this.ce.getUser().getPk_org();
	private IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);
	private String pk_busitype = null;
	private static String equip = null;
	protected CalParaVO measDoc = null;
	private static IRfid reader = null;
	private Integer numScale = null;
	private UFBoolean isNeedQueue = null;
	private UFBoolean isUseLadeCoal = null;
	private UFDouble checkTolerance = null;
	private UFBoolean isSendCoalBureau = null;
	private UFBoolean isNeedCheckGrossWeight = null;
	private UFDouble emptySaveTolerance = null;
	private String key = null;
	private Decoder decoder = null;
	private Encoder encoder = null;

	private UFBoolean isEncryptLade = UFBoolean.TRUE;
	private boolean bIsShowDialog = false;
	FormulaParseTool tool=new FormulaParseTool();

	public BillWorkPanel(){
		try{
			initialize();
		}catch (BusinessException e){
			FmpubLogger.error(e.getMessage(), e);
			showErrorMessage(e.getMessage());
		}
	}

	public BillWorkPanel(Container container){
		this.parents = ((BaseContainer)container);
		try{
			initialize();
		}catch (BusinessException e){
			showErrorMessage(e.getMessage());
		}
	}

	private void initialize() throws BusinessException{
		initSysCode();//查询参数设置

		setLayout(new BorderLayout());

		UISplitPane splitPane = new UISplitPane(0);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(450);  

		UISplitPane topSplitPane = null;
		if(getFReceOrSend() == 0 || getFReceOrSend() == 1){
			topSplitPane = new UISplitPane(0);
			topSplitPane.setOneTouchExpandable(false);
			topSplitPane.setTopComponent(getHeadCardPanel());//卡片头
			topSplitPane.setBottomComponent(getNewTListPanel());//已过磅计量单
			topSplitPane.setDividerSize(10);
			topSplitPane.setDividerLocation(300);

		}else if(getFReceOrSend() == 2){
			topSplitPane = new UISplitPane(0);
			topSplitPane.setOneTouchExpandable(false);
			topSplitPane.setTopComponent(getHeadCardPanel());//卡片头
			topSplitPane.setBottomComponent(getInOrOutInvoiceListPanel());//窑街 过磅进出计量单
			topSplitPane.setDividerSize(10);
			topSplitPane.setDividerLocation(300);
		}

//		UISplitPane bottomSplitPane = new UISplitPane(0);
//		if(getFReceOrSend() == 0){
//			bottomSplitPane.setOneTouchExpandable(false); 
//			bottomSplitPane.setTopComponent(getTailListPanel());//发运通知单
//			bottomSplitPane.setDividerSize(10);
//			bottomSplitPane.setDividerLocation(150);
//		}
//		if(getFReceOrSend() == 0){
//			splitPane.setBottomComponent(bottomSplitPane);
//		}
		splitPane.setTopComponent(topSplitPane);
	

		add(splitPane, "Center");

		setBillStates();

		//addEventListener();

		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		
		requestFocus();

		timer();
	}

	private void setBillButtonShow() {
		// TODO 自动生成的方法存根
		getParents().setBillButtonShow(getFReceOrSend(), getBillModel());
	}

	private void initSysCode() throws BusinessException{
		try{
			/*判断是否IC卡用户
			 *  String[] initcodes = { "TS0101", "TS0102", "TS0103" };
			 *  HashMap<String, String> map = ponder.getSysInits(initcodes,"公司ID");

      String iccardUsed = (String)map.get("TS0103");
      if ((iccardUsed != null) && (!iccardUsed.trim().equals("")) && 
        (iccardUsed.trim().equalsIgnoreCase("Y"))) {
        this.isICCardUsed = true;
      }*/
			this.measDoc = ponder.getDateVO( new ClientContext().getConfigName(), this.m_corpid);
			if (this.measDoc == null)
			{
				JOptionPane.showMessageDialog(null, "没有读取到本机IP对应的计量参数信息，请检查计量参数档案。", 
						"ERROR", 0);
				throw new BusinessException("没有读取到本机IP对应的计量参数信息，请检查计量参数档案。");
			}
			/*  if ((this.measDoc.getDef2() != null) && (this.measDoc.getDef2().booleanValue()) && (YGUtils.getTSXYG06() == null))
      {
        JOptionPane.showMessageDialog(null, "本机启用了视频，但是参数TSXYG06【 图片本地保存路径】 为空，请检查参数配置。", 
          "ERROR", 0);
        this.measDoc = null;
        throw new BusinessException("本机启用了视频，但是参数TSXYG06【 图片本地保存路径】 为空，请检查参数配置。");
      }*/
		}catch (BusinessException e){
			throw new BusinessException("没有读取到本机IP对应的计量参数信息，请检查计量参数档案。");
		}
	}


	public HeadCardPanel getHeadCardPanel(){
		if (this.headCardPanel == null) {
			this.headCardPanel = new HeadCardPanel(this);
		}
		return this.headCardPanel;
	}

//	public SendListPanel getTailListPanel(){
//		if (this.tailListPanel == null){
//			this.tailListPanel = new SendListPanel(this);
//
//			PubTsBillScaleTool billScaleTool = new PubTsBillScaleTool(null, this.tailListPanel, ClientEnvironment.getInstance());
//			billScaleTool.setScale();
//		}
//		return this.tailListPanel;
//	}

	public InvoiceListPanel getNewTListPanel(){
		if (this.newTListPanel == null){
			this.newTListPanel = new InvoiceListPanel(this);

			PubTsBillScaleTool billScaleTool = new PubTsBillScaleTool(null, this.newTListPanel, ClientEnvironment.getInstance());
			billScaleTool.setScale();
		}
		return this.newTListPanel;
	}

	public InOrOutInvoiceListPanel getInOrOutInvoiceListPanel(){
		if(this.inOrOutTListPanel==null){
			this.inOrOutTListPanel=new InOrOutInvoiceListPanel(this);

			PubTsBillScaleTool billScaleTool = new PubTsBillScaleTool(null, this.inOrOutTListPanel, ClientEnvironment.getInstance());
			billScaleTool.setScale();
		}

		return this.inOrOutTListPanel;
	}

	public BaseContainer getParents(){
		return this.parents;
	}

	protected abstract void beforOnTare();

	public void update(Observable arg0, Object arg1){
		if ((arg0 instanceof IRfid))
		{
			if (arg1 == null) {
				return;
			}

		}
	}

	//读卡器
	private void openReader() throws BusinessException{
		reader = RFIDFactory.getInstance().build();
		((Observable)reader).addObserver(this);
		reader.open();
	}

	protected void startReadRFID() throws BusinessException{
		openReader();
	}

	protected void validRfid(String iccode) throws BusinessException{
		equip = iccode;
		setBillCellEdit();
	}

	protected void showErrorMessage(String mesg){
		getParents().showErrorMessage(mesg);
	}

	protected abstract void beforeGross();

	protected void clearUIData(){
		getHeadCardPanel().setBillValueByVo(new InvoicesheetHVO());
//		getTailListPanel().setHeaderValueVO(new SendnoticebillHVO[0]);
//		getTailListPanel().setBodyValueVO(null);
		getNewTListPanel().setBodyValueVO(new DoListInvoiceAgg[0]);

		updateUI();
	}

	protected void setDataOnTareForCoalBureau()
	{
		System.out.print("setDataOnTareForCoalBureau"); 
		/*
    if (!getIsSendCoalBureau().booleanValue()) {
      return;
    }
    getBodyListPanel().getBillModel().setBodyDataVO(null);
    String pk_corp = ClientEnvironment.getInstance().getUser().getPk_org();
    DataCollectionVO lorryInfoVo = null;
    try
    {
      CarInfoUtil carinfo = new CarInfoUtil();
      lorryInfoVo = carinfo.getEnterSrc();
    }
    catch (Exception e)
    {
      showErrorMessage("得到车卡信息出错。请联系管理员。" + e);
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
    if ((lorryInfoVo != null) && (lorryInfoVo.getvReturnDescr() != null) && 
      (lorryInfoVo.getvReturnDescr().trim().length() != 0))
    {
      showErrorMessage("读卡错误。" + lorryInfoVo.getvReturnDescr());
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
    if ((lorryInfoVo != null) && (lorryInfoVo.getCiccardid() != null) && 
      (lorryInfoVo.getCiccardid().trim().length() != 0))
    {
      setLorryInfoVo(lorryInfoVo);
      String carpk = "";
      try
      {
        carpk = ponder.getLorryInfoPk(
          lorryInfoVo.getCardInfo(), pk_corp);
      }
      catch (Exception e)
      {
        showErrorMessage("得到车辆信息时发生错误。" + e);
        ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
          .getComponent()).getUITextField().requestFocus();
        return;
      }
      String cph = getProtites(lorryInfoVo.getCardInfo(), "车牌号码");
      if ((carpk != null) && (carpk.trim().length() != 0))
      {
        getHeadCardPanel().setHeadItem("cvechmanid", 
          carpk == null ? null : carpk);
        getHeadCardPanel().execHeadLoadFormulas();

        getActPanel().setHeadItem("cvechmanid", 
          carpk == null ? null : carpk);
        getActPanel().execHeadLoadFormulas();
        getActPanel().setHeadItem("veichcode", cph);

        getActPanel().setHeadItem("vmtjcarno", cph);

     //   onQuery("cvechmanid");
        afterEditNeedQuery("cvechmanid");
        setBillCellEdit();
        if (getIsUseLadeCoal().booleanValue()) {
          ((UIRefPane)getHeadCardPanel().getBodyItem("ladecoalbillno").getComponent()).getUITextField().requestFocus(true);
        }
        getHeadCardPanel().setHeadItem("coaliccardid", 
          lorryInfoVo.getCiccardid());
        getActPanel().setHeadItem("coaliccardid", 
          lorryInfoVo.getCiccardid());
      }
      else
      {
        showErrorMessage("得到车辆信息时发生错误。");
        ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
          .getComponent()).getUITextField().requestFocus();
      }
    }
    else
    {
      showErrorMessage("没有得到该卡epc编码。");
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
		 */}

	protected void setDataOnGrossForCoalBureau()
	{
		System.out.print("setDataOnGrossForCoalBureau");
		/*
    if (!getIsSendCoalBureau().booleanValue()) {
      return;
    }
    getHeadCardPanel().setBillValueByVo(null);
    getBodyListPanel().getBillModel().setBodyDataVO(null);
    DataCollectionVO carpccode = null;
    try
    {
      CarInfoUtil carinfo = new CarInfoUtil();
      carpccode = carinfo.getLeaveSrc();
    }
    catch (Exception e)
    {
      showErrorMessage("得到车卡信息出错。请联系管理员。" + e);

      setLorryInfoVo(null);
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
    if ((carpccode != null) && (carpccode.getvReturnDescr() != null) && 
      (carpccode.getvReturnDescr().trim().length() != 0))
    {
      showErrorMessage("读卡错误。" + carpccode.getvReturnDescr());
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
    if ((carpccode != null) && (carpccode.getCiccardid().trim().length() != 0))
    {
      setLorryInfoVo(carpccode);
      getHeadCardPanel().setHeadItem("coaliccardid", 
        carpccode.getCiccardid());
      getActPanel().setHeadItem("coaliccardid", 
        carpccode.getCiccardid());
      String carpk = "";
      try
      {
        carpk = ponder.getCarPkByEpc(carpccode.getCiccardid());
      }
      catch (Exception e)
      {
        showErrorMessage("查找已过磅车辆发生错误。");
        return;
      }
      if ((carpk != null) && (carpk.trim().length() != 0))
      {
        getButtonAndNumPanel().getHeadCardPanel().getBodyItem("cvechmanid").setValue(carpk);
        getHeadCardPanel().setHeadItem("cvechmanid", 
          carpk == null ? null : carpk);
        getHeadCardPanel().execHeadLoadFormulas();
        getActPanel().setHeadItem("cvechmanid", 
          carpk == null ? null : carpk);
        getActPanel().execHeadLoadFormulas();

        onQuery("cvechmanid");
        afterEditNeedQuery("cvechmanid");
      }
      else
      {
        showErrorMessage("没有找到该车的入矿单据。");
        ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
          .getComponent()).getUITextField().requestFocus();
      }
    }
    else
    {
      showErrorMessage("得到车辆信息时发生错误");
      ((UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
        .getComponent()).getUITextField().requestFocus();
      return;
    }
		 */}


	private static String getProtites(String info, String proname)
	{
		int fromIndex = info.indexOf(proname + ":") + proname.length() + 1;
		String str = info.substring(fromIndex, info.indexOf(";", fromIndex));
		return str;
	}

	private void dataNotNullValidate()  throws BusinessException
	{
		getHeadCardPanel().dataNotNullValidate();
	}

	private void checkICCard(String iccardid)throws BusinessException
	{}


	private boolean isEmpty(Object obj)
	{
		if ((obj != null) && (obj.toString().length() > 0)) {
			return false;
		}
		return true;
	}

	public void afterEditNum() {/*

	    Object ntareweight =  getHeadCardPanel().getBodyValueAt(0, "maoz");
	    Object ngrossweight = getHeadCardPanel().getBodyValueAt(0, "piz");
	    if ((ntareweight != null) && (ngrossweight != null) && 
	      (ntareweight.toString().length() > 0) && 
	      (ngrossweight.toString().length() > 0))
	    {
	      UFDouble nnetweight = new UFDouble(ngrossweight.toString()).sub(new UFDouble(ntareweight.toString()));

	      getActPanel().getHeadItem("nnetweight").setValue(nnetweight);
	      getHeadCardPanel().getHeadItem("nnetweight").setValue(nnetweight);

	      PonderBVO[] bodys = (PonderBVO[])getBodyListPanel().getBillModel()
	        .getBodyValueVOs(ContractVO.PONDERVOBVO);
	      for (int i = 0; i < bodys.length; i++) {
	        for (int j = i + 1; j < bodys.length; j++) {
	          if (bodys[i].getNcouldrecnum().compareTo(
	            bodys[j].getNcouldrecnum()) > 0)
	          {
	            temp = new PonderBVO();
	            temp = (PonderBVO)bodys[i].clone();
	            bodys[i] = ((PonderBVO)bodys[j].clone());
	            bodys[j] = ((PonderBVO)temp.clone());
	            temp = null;
	          }
	        }
	      }
	      UFDouble receNum = new UFDouble(0);
	      if (bodys.length == 1)
	      {
	        getBodyListPanel().getBillModel().setValueAt(
	          nnetweight.sub(receNum), 0, "nnetweight"); return;
	      }
	      PonderBVO[] arrayOfPonderBVO1;
	      PonderBVO localPonderBVO1 = (arrayOfPonderBVO1 = bodys).length;
	      for (PonderBVO temp = 0; temp < localPonderBVO1; temp++)
	      {
	        PonderBVO body = arrayOfPonderBVO1[temp];
	        if (nnetweight.sub(receNum).compareTo(body.getNcouldrecnum()) >= 0)
	        {
	          body.setNnetweight(body.getNcouldrecnum());
	        }
	        else
	        {
	          body.setNnetweight(nnetweight.sub(receNum));
	          break;
	        }
	        receNum = receNum.add(body.getNnetweight());
	      }
	    }

	 */}

	public void setBillStates(){
		if (this.billModel == -1){
			getHeadCardPanel().setEnabled(false);
			//getTailListPanel().setEnabled(false);
			if (getFReceOrSend() == 0 || getFReceOrSend() == 1){				
				getNewTListPanel().setEnabled(false);
			}else{
				getInOrOutInvoiceListPanel().setEnabled(false);
			}
		}
		//0-进；1-出
		if (getFReceOrSend() == 1){
			//已过磅
			if (this.billModel == 1){
				getHeadCardPanel().setEnabled(true);
				getHeadCardPanel().getHeadItem("vbillno").setEdit(false);

				getHeadCardPanel().getBodyItem("carno").setEdit(false);
				getHeadCardPanel().getBodyItem("piz").setEdit(false);
				getHeadCardPanel().getBodyItem("bnote").setEdit(true);
				getHeadCardPanel().getBodyItem("ches").setEdit(true);
				getHeadCardPanel().getBodyItem("maoz").setEdit(true);
				getHeadCardPanel().getBodyItem("fubs").setEdit(true);
			}else if (this.billModel == 0){
				getHeadCardPanel().setEnabled(true);
				getHeadCardPanel().getHeadItem("vbillno").setEdit(false);

				getHeadCardPanel().getBodyItem("carno").setEdit(false);
				getHeadCardPanel().getBodyItem("piz").setEdit(false);
				getHeadCardPanel().getBodyItem("bnote").setEdit(true);
				getHeadCardPanel().getBodyItem("ches").setEdit(true);
				getHeadCardPanel().getBodyItem("maoz").setEdit(true);
				getHeadCardPanel().getBodyItem("fubs").setEdit(true);
			}

		}else if (this.billModel == 1){
			getHeadCardPanel().setEnabled(true);
			getHeadCardPanel().getHeadItem("vbillno").setEdit(false);

			getHeadCardPanel().getBodyPanel().setEnabled(true);
			getHeadCardPanel().getBodyItem("bnote").setEdit(true);
			getHeadCardPanel().getBodyItem("ches").setEdit(true);
			getHeadCardPanel().getBodyItem("piz").setEdit(true);
			getHeadCardPanel().getBodyItem("maoz").setEdit(false);
			getHeadCardPanel().getBodyItem("fubs").setEdit(false);

		}else if (this.billModel == 0){
			getHeadCardPanel().setEnabled(true);
			getHeadCardPanel().getHeadItem("vbillno").setEdit(false);

			getHeadCardPanel().getBodyPanel().setEnabled(true);
			getHeadCardPanel().getBodyItem("bnote").setEdit(true);

			getHeadCardPanel().getBodyItem("piz").setEdit(true);
			getHeadCardPanel().getBodyItem("maoz").setEdit(false);
			getHeadCardPanel().getBodyItem("fubs").setEdit(false);

		}else if(this.billModel == 2){

			getHeadCardPanel().setEnabled(true);
			getHeadCardPanel().getHeadItem("vbillno").setEdit(false);

			getHeadCardPanel().getBodyPanel().setEnabled(true);
			getHeadCardPanel().getBodyItem("bnote").setEdit(false);
			getHeadCardPanel().getBodyItem("ches").setEdit(false);
			getHeadCardPanel().getBodyItem("piz").setEdit(false);
			getHeadCardPanel().getBodyItem("maoz").setEdit(false);
			getHeadCardPanel().getBodyItem("fubs").setEdit(true); 
		}
		setBillButtonShow();

		setDefualtValue();
		updateUI();
		getParents().updateUI();
	}

	public void afterEdit(BillEditEvent e){
		String skey = e.getKey();
		if (YGUtils.isXiaoyugou()) {
			setBillStates();
		}
		setBillCellEdit();

		// 2018年12月24日
		if(skey.equals("carno")){
			Object obj=e.getValue();
			String carno=obj==null?"":obj.toString();
			if(!"".equals(carno)){			  
				String standardweight=tool.getNameByID("hgts_tool", "standardweight", "id", carno);
				String pk_supplier= tool.getNameByID("hgts_tool", "pk_supplier", "id", carno);
				String drivername= tool.getNameByID("hgts_tool", "drivername", "id", carno);
				this.getHeadCardPanel().setBodyValueAt(standardweight, e.getRow(),"def8");
				this.getHeadCardPanel().setBodyValueAt(drivername, e.getRow(),"bdriver");
				this.getHeadCardPanel().setHeadItem("pk_supplier", pk_supplier); // 供应商
				this.getHeadCardPanel().setHeadItem("driver", drivername);// 司机姓名
			}
		}

		if(e.getPos()==0){

			if(skey.equals("flag_data")){ // 数据标识： N=1：正常销售的数据；W=2：异常数据（超重）；A=3：所有数据

				String flag=HgtsPubTool.getStringNullAsTrim(e.getValue());			
				String mineid = this.getMeasDoc().getAttributeValue("ofmine") == null ?"" : this.getMeasDoc().getAttributeValue("ofmine").toString();
//				if(this.getFReceOrSend()==0){
//					//清空计量单页面表体数据
//					getHeadCardPanel().getBillData().setBodyValueVO(null);
//
//					DoListSendnoticeAgg[] aggs =null;
//
//					try {
////						if("".equals(flag)){
////							flag="1";						
////						}
//						aggs = ponder.SelectSendnoticeVOs(AppContext.getInstance().getServerTime().getDate().toString().substring(0, 10), mineid,null,"","");	
//
//					} catch (BusinessException e1) {
//						e1.printStackTrace();
//					}
//
//					getTailListPanel().setBodyValueVO(new DoListSendnoticeAgg[0]);
//					getTailListPanel().setBodyValueVO(aggs);
//					getTailListPanel().getBodyBillModel().loadLoadRelationItemValue();
//					getTailListPanel().getBodyTable().setSortEnabled(false); // 去掉表尾排序
//				}
//				if(this.getFReceOrSend()==1){
					DoListInvoiceAgg[] aggInvoice = null;
					try {
						aggInvoice=ponder.getDoListInvoiceAgg(mineid,null,"","");
						/*BillItem[] items=getHeadCardPanel().getHeadItems();
						for(BillItem item:items){
							if(!item.getKey().equals(skey)){
								getHeadCardPanel().setHeadItem(item.getKey(), null);
							}
						}*/
						getHeadCardPanel().getBillData().setBodyValueVO(null);

						getNewTListPanel().setBodyValueVO(new DoListInvoiceAgg[0]);
						getNewTListPanel().setBodyValueVO(aggInvoice);
						getNewTListPanel().getBodyBillModel().loadLoadRelationItemValue();
						getNewTListPanel().getBodyBillModel().execLoadFormula();
						getNewTListPanel().getBodyTable().setSortEnabled(false);
					} catch (BusinessException e1) {
						e1.printStackTrace();
					}
//				}


			}else if(skey.equals("driveridcard")){
				String driveridcard=HgtsPubTool.getStringNullAsTrim(e.getValue());
				String ofmine =HgtsPubTool.getStringNullAsTrim( this.getMeasDoc().getAttributeValue("ofmine"));
				String pcdate=AppContext.getInstance().getBusiDate().toString();
				try {
					AggInvoicesheetHVO aggvo=(AggInvoicesheetHVO) ponder.getNewAggInvoiceVO(driveridcard, ofmine, pcdate);
					aggvo.getParentVO().setAttributeValue("hengqno", getMeasDoc().getAttributeValue("calcode"));
					aggvo.getParentVO().setAttributeValue("driveridcard", driveridcard);
					getHeadCardPanel().setBillValueVO(aggvo);
					getHeadCardPanel().getBillModel().loadLoadRelationItemValue();
					getHeadCardPanel().getBillModel().execLoadFormula();
					setBillModel(0);
					setBillStates();
					getHeadCardPanel().updateUI();
				} catch (BusinessException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	protected void setDefualtValue()
	{
		//getTailListPanel().InitData();
		if(this.getFReceOrSend()==0 || this.getFReceOrSend()==1){
			getNewTListPanel().initData();
		}else{			
			getInOrOutInvoiceListPanel().initData();
		}
	}

	public void setBillCellEdit()
	{}

	public boolean updateQueueInfo(String wsdl, String arg0, String arg1, String arg2, String arg3)
	{
		Logger.debug("告诉防作弊系统已过皮，updateQueueInfo:<wsdl>" + wsdl + "<arg0>" + arg0 + "<arg1>" + arg1 + "<arg2>" + arg2 + "<arg3>" + arg3);
		Service service = new Service();
		try
		{
			Call call = (Call)service.createCall();

			call.setTimeout(Integer.valueOf(5000));
			call.setTargetEndpointAddress(new URL(wsdl));
			call.setOperationName(new QName("http://ws.ddgl.jbfs.com/", "updateQueueInfo"));
			call.addParameter("arg0", new QName("http://ws.ddgl.jbfs.com/", "getPlateNumber"), ParameterMode.IN);
			call.addParameter("arg1", new QName("http://ws.ddgl.jbfs.com/", "getPlateNumber"), ParameterMode.IN);
			call.addParameter("arg2", new QName("http://ws.ddgl.jbfs.com/", "getPlateNumber"), ParameterMode.IN);
			call.addParameter("arg3", new QName("http://ws.ddgl.jbfs.com/", "getPlateNumber"), ParameterMode.IN);
			call.invoke(new Object[] { arg0, arg1, arg2, arg3 });
			return true;
		}
		catch (Exception e)
		{
			Logger.debug("访问防作弊系统异常，updateQueueInfo:" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}



	protected void reflashBillVo(BillEditEvent e)
	{/*
    Vector v = new Vector();
    ToDoListVO[] tailVos = (ToDoListVO[])getTailListPanel()
      .getHeadBillModel().getBodyValueVOs(ContractVO.TODOLISTVO);
    if (tailVos == null) {
      return;
    }
    UIRefPane refPane = (UIRefPane)e.getSource();
    String pk_value = refPane.getRefPK();
    for (ToDoListVO tailVo : tailVos) {
      if (e.getKey().equals("ccumandoc"))
      {
        if ((tailVo.getPk_cumandoc() == null) || 
          (tailVo.getPk_cumandoc().equals(pk_value))) {
          v.add(tailVo);
        }
      }
      else if (e.getKey().equals("cinvmandoc"))
      {
        if ((tailVo.getCinvmandoc() == null) || 
          (tailVo.getCinvmandoc().equals(pk_value)) || 
          (pk_value == null)) {
          v.add(tailVo);
        }
      }
      else if ((e.getKey().equals("pk_calbody")) && (
        (tailVo.getCoutcalbody() == null) || 
        (tailVo.getCoutcalbody().equals(pk_value)))) {
        v.add(tailVo);
      }
    }
    ToDoListVO[] viewVos = new ToDoListVO[v.size()];
    v.toArray(viewVos);
    getTailListPanel().getHeadBillModel().setBodyDataVO(viewVos);
    getTailListPanel().getHeadBillModel().execLoadFormula();
    if (viewVos.length < 1)
    {
      getBodyListPanel().getBillModel().setBodyDataVO(null);
      getBodyListPanel().updateUI();
    }
    for (int i = 0; i < getBodyListPanel().getRowCount(); i++)
    {
      getBodyListPanel().getBillModel().setValueAt(pk_value, i, 
        "ccumandoc");
      getBodyListPanel().getBillModel().execEditFormulaByKey(i, 
        "ccumandoc");
    }
	 */}

	//？？？？未知？？查询过磅数量？？
	public void setWaitingNum()
	{
		IUAPQueryBS queryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = "select count(1) from hgts_ponder_h where vechistatus <> 6 and pk_billtype = '" + 
				getParents().getBillTypeCode() + 
				"' and pk_corp = '" +  this.m_corpid + "'";
		sql = sql + " and isfromoperate = 'Y' and tintime is not null and touttime is null and isnull(dr,0) = 0";
		Integer totalWaitingNum = null;
		try
		{
			totalWaitingNum = (Integer)queryBS.executeQuery(sql,  new ColumnProcessor());
		}
		catch (BusinessException e)
		{
			Logger.error(e.getMessage(), e);
		}
		Integer custWaitingNum = null;
		String cubasdoc = null;//(String)getHeadCardPanel().getBodyItem("ccubasdoc").getValueObject();
		if (!StringUtil.isEmpty(cubasdoc))
		{
			sql = "select count (h.hid) from (";
			sql = sql + " select distinct hgts_ponder_h.cponderhid hid from hgts_ponder_h inner join hgts_ponder_b on hgts_ponder_h.cponderhid = hgts_ponder_b.cponderhid ";
			sql = sql + " where vechistatus <> 6 and pk_billtype = '" + 
					getParents().getBillTypeCode() + 
					"' and hgts_ponder_h.pk_corp = '" + this.m_corpid + "'";
			sql = sql + " and hgts_ponder_b.ccubasdoc = '" + cubasdoc + "' ";
			sql = sql + " and hgts_ponder_h.isfromoperate = 'Y' ";
			sql = sql + " and tintime is not null and touttime is null ";
			sql = sql + " and isnull(hgts_ponder_h.dr,0) = 0 and isnull(hgts_ponder_b.dr,0) = 0 ";
			sql = sql + ") h";
			try
			{
				custWaitingNum = (Integer)queryBS.executeQuery(sql, 
						new ColumnProcessor());
			}
			catch (BusinessException e)
			{
				Logger.error(e.getMessage(), e);
			}
		}
		getParents().getHardWarePanel().getMeasPanel().setWaitingNum(custWaitingNum, totalWaitingNum);
	}

	private int getWatingNumByInv()
	{
		String cinvbasdoc = null;// (String)getHeadCardPanel().getBodyItem("cinvbasdoc").getValueObject();
		IUAPQueryBS queryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = "select count(1) from hgts_ponder_h where vechistatus = 2 and pk_billtype = '" + 

      getParents().getBillTypeCode() + "' and pk_corp = '" + 
      this.m_corpid + "'";
		sql = sql + " and isfromoperate = 'Y' and tintime is not null and touttime is null and isnull(dr,0) = 0";
		sql = sql + " and cinvbasdoc = '" + cinvbasdoc + "' ";

		String cponderhid = (String)getHeadCardPanel().getHeadItem(
				"cponderhid").getValueObject();
		if (!StringUtil.isEmpty(cponderhid)) {
			sql = sql + " and cponderhid <> '" + cponderhid + "' ";
		}
		Integer totalWaitingNum = null;
		try
		{
			totalWaitingNum = (Integer)queryBS.executeQuery(sql, 
					new ColumnProcessor());
		}
		catch (BusinessException e)
		{
			Logger.error(e.getMessage(), e);
		}
		if (totalWaitingNum == null) {
			return 0;
		}
		return totalWaitingNum.intValue();
	}

	private void checkCanTare() throws BusinessException
	{
		checkCarQueue();

		checkBlackList();
	}

	private void checkBlackList()
			throws BusinessException
			{/*
    UIRefPane refPane = (UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
      .getComponent();
    String vechCode = refPane.getText();
    ICarQueueItf carQueueItf = (ICarQueueItf)NCLocator.getInstance().lookup(
      ICarQueueItf.class);
    boolean isInBlackList = carQueueItf.isInBlackList(vechCode);
    if (isInBlackList)
    {
      if ((getFReceOrSend() == 1) && (
        (getBillModel() == 1) || 
        (getBillModel() == 2))) {
        throw new BusinessException("车辆(" + vechCode + ")已被列入黑名单，不允许过毛。");
      }
      throw new BusinessException("车辆(" + vechCode + ")已被列入黑名单，不允许入场。");
    }
			 */}

	private void checkCarQueue() throws BusinessException
	{/*
    if (!isNeedQueue()) {
      return;
    }
    UIRefPane refPane = (UIRefPane)getHeadCardPanel().getBodyItem("cvechmanid")
      .getComponent();
    String vechCode = refPane.getText();
    ICarQueueItf carQueueItf = (ICarQueueItf)NCLocator.getInstance().lookup(
      ICarQueueItf.class);
    CarQueueVO[] carQueue = carQueueItf.getUpListResult();
    if ((carQueue == null) || (carQueue.length <= 0)) {
      throw new BusinessException("当前车辆没有在排队系统中，请先在排队系统中录入。");
    }
    if (!vechCode.equals(carQueue[0].getCarno())) {
      throw new BusinessException("车辆(" + vechCode + ")插队，当前排队车辆应为 " + 
        carQueue[0].getCarno() + "。");
    }
	 */}

	public boolean isNeedCheckGrossWeight()
	{
		if (this.isNeedCheckGrossWeight == null)
		{
			this.isNeedCheckGrossWeight = UFBoolean.FALSE;
			try
			{
				this.isNeedCheckGrossWeight = SysInitBO_Client.getParaBoolean(this.ce
						.getUser().getPk_org(), "TSBO10");
			}
			catch (BusinessException e)
			{
				return false;
			}
		}
		return this.isNeedCheckGrossWeight.booleanValue();
	}

	private boolean isNeedQueue()
	{
		if (this.isNeedQueue == null)
		{
			this.isNeedQueue = UFBoolean.FALSE;
			try
			{
				this.isNeedQueue = SysInitBO_Client.getParaBoolean(this.ce
						.getUser().getPk_org(), "TSBO11");
			}
			catch (BusinessException e)
			{
				return false;
			}
		}
		return this.isNeedQueue.booleanValue();
	}

	public int getBillModel()
	{
		return this.billModel;
	}

	public void setBillModel(int billModel)
	{
		//磅房各个串口显示状态
		/* if (billModel == -1) {
      getParents().getHardWarePanel().getMeasPanel().getStatusDesginPanel().setStatus(-1, -1, 
        -1, -1, 
        -1, -1, 
        -1, -1);
    } else if (billModel == 1) {
      getParents().getHardWarePanel().getMeasPanel().getStatusDesginPanel().setStatus(-1, 0, 
        -1, -1, 
        -1, -1, 
        -1, -1);
    } else if (billModel == 0) {
      getParents().getHardWarePanel().getMeasPanel().getStatusDesginPanel().setStatus(0, -1, 
        -1, -1, 
        -1, -1, 
        -1, -1);
    } else {
      getParents().getHardWarePanel().getMeasPanel().getStatusDesginPanel().setStatus(-1, -1, 
        -1, -1, 
        -1, -1, 
        -1, -1);
    }*/
		this.billModel = billModel;
	}

	protected abstract int getFReceOrSend();

	public UFBoolean getParaBoolean(String paramCode)
			throws BusinessException
			{
		UFBoolean value = SysinitAccessor.getInstance().getParaBoolean( ClientEnvironment.getInstance().getUser().getPk_org(), 
				paramCode);
		if (value == null) {
			throw new BusinessException("未找到参数代码为" + value + 
					"的系统参数，请到[客户化]->[参数设置]中查看是否分配了此参数。");
		}
		return value;
			}

	public UFDouble getParaDbl(String paramCode)
			throws BusinessException
			{
		UFDouble value = SysinitAccessor.getInstance().getParaDbl(
				ClientEnvironment.getInstance().getUser().getPk_org(),paramCode);
		if (value == null) {
			throw new BusinessException("未找到参数代码为" + value + 
					"的系统参数，请到[客户化]->[参数设置]中查看是否分配了此参数。");
		}
		return value;
			}

	protected void beforQuery(String skey)
	{
		if (this.measDoc == null)
		{
			getParents().showErrorMessage("没有读取到本机IP对应的计量参数信息，请检查计量参数档案。");
			return;
		}
	}

	public boolean beforeEdit(BillItemEvent e)
	{

		return true;
	}

	public UFBoolean getIsUseLadeCoal()
	{
		if (this.isUseLadeCoal == null) {
			try
			{
				this.isUseLadeCoal = getParaBoolean(SysParamConsts.B_USE_LADE_COAL);
			}
			catch (BusinessException e)
			{
				showErrorMessage(e.getMessage());
				return UFBoolean.FALSE;
			}
		}
		return this.isUseLadeCoal;
	}

	public UFDouble getCheckTolerance()
	{
		if (this.checkTolerance == null) {
			try
			{
				this.checkTolerance = getParaDbl(SysParamConsts.N_CHECK_TOLERANCE);
			}
			catch (BusinessException e)
			{
				showErrorMessage(e.getMessage());
				return UFDouble.ZERO_DBL;
			}
		}
		return this.checkTolerance;
	}

	public UFBoolean getIsSendCoalBureau()
	{
		if (this.isSendCoalBureau == null) {
			try
			{
				this.isSendCoalBureau = getParaBoolean(SysParamConsts.B_SEND_COAL_BUREAU);
			}
			catch (BusinessException e)
			{
				showErrorMessage(e.getMessage());
				return UFBoolean.FALSE;
			}
		}
		return this.isSendCoalBureau;
	}

	public UFDouble getEmptySaveTolerance()
	{
		if (this.emptySaveTolerance == null) {
			try
			{
				this.emptySaveTolerance = getParaDbl(SysParamConsts.N_EMPTY_SAVE_TOLERANCE);
			}
			catch (BusinessException e)
			{
				showErrorMessage(e.getMessage());
				return UFDouble.ZERO_DBL;
			}
		}
		return this.emptySaveTolerance;
	}

	public String getKey()
	{
		if (this.key == null) {
			try
			{
				this.key = (NCLocator.getInstance().lookup(IKeyFileReader.class)).getKey();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		return this.key;
	}

	public IRfid getReader()
	{
		return reader;
	}  

	public Decoder getDecoder()
	{
		if (this.decoder == null) {
			this.decoder = new Decoder(getKey());
		}
		return this.decoder;
	}

	public Encoder getEncoder(){
		if (this.encoder == null) {
			this.encoder = new Encoder(getKey());
		}
		return this.encoder;
	}



	public CalParaVO getMeasDoc(){
		return this.measDoc;
	}
	protected void stopReadRFID()  throws BusinessException{
		if (reader != null) {
			reader.close();
		}
	}

	public void OnSave(){
		//设置非编辑态
		getHeadCardPanel().stopEditing();
		//保存过磅单数据Recieve=0;Send=1
		try {
			int guil=this.parents.getHardWarePanel().getMeasPanel().getGuil();
			if(guil<=0){
				//TODO 2019年7月23日 并行时暂时注释，正式用时再放开此代码
				/*showErrorMessage("系统未归零，请重新上磅！");
				return;*/
			}

			// 2018年12月19日 进、出红外 
			/*String in_red="N";
			String out_red="N";*/
			/*1、判断红外是否启用*/
			if(this.measDoc.getAttributeValue("isradition").toString().equals("Y")){ 
				if (this.measDoc.getAttributeValue("isvia").toString().equals("N")) {//判断红外是否通过
					showErrorMessage("车辆上磅姿势不正确，请调整！");
					return;
				}
				/*if(getFReceOrSend() == 0){
					// TODO 进 红外启用：Y
					in_red="Y";
				}else{
					out_red="Y";
				}*/
			}

			//获取过磅单数据，保存
			AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO, 
					ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
			InvoicesheetBVO[] bodys = (InvoicesheetBVO[])getHeadCardPanel().getBillModel().getBodyValueVOs(ContractVO.InvoicesheetBVO);
			if(null == bodys || bodys.length==0 ){//没有子表数据，不可保存
				showErrorMessage("未读取到车辆信息");
				return;
			}

			// 2018-9-4  当前保存所关联的通知单行是否关闭：关闭了，不允许保存
			String pkSend_b=((String)bodys[0].getAttributeValue("csourcebid"));
			String rowcloseflag=HgtsPubTool.getStringNullAsTrim(tool.getNameByID("hgts_sendnoticebill_b", "rowcloseflag", "pk_sendnoticebill_b", pkSend_b));
			if(null !=rowcloseflag && !"".equals(rowcloseflag) && "Y".equals(rowcloseflag)){
				showErrorMessage("通知单行状态已经关闭，不允许保存，请刷新当前界面，重新选择或撤换通知单！");
				return;
			}

			// 2018-4-13 add
			String ofmine=HgtsPubTool.getStringNullAsTrim(this.measDoc.getAttributeValue("ofmine"));
			boolean isDIn=ponder.isDrxIn(ofmine,aggPonder);
			int jytype=aggPonder.getParentVO().getAttributeValue("jytype")==null?1:Integer.parseInt(aggPonder.getParentVO().getAttributeValue("jytype").toString());

			// 0：进
			if(getFReceOrSend() == 0){
				Object carid=bodys[0].getAttributeValue("carno");
				if(null == carid){
					showErrorMessage("请先选择车辆");
					return;
				}

				// 调入洗的调出业务、商品煤、其它-出
				if(!isDIn || jytype==1 || jytype==4 ){
					if(null ==bodys[0].getAttributeValue("piz")){
						showErrorMessage("车辆皮重未读取，请先点击“过皮重”按钮！");
						return;
					}
					// 皮重<=0 不允许保存
					UFDouble pizhong=HgtsPubTool.getUFDoubleNullAsZero(bodys[0].getAttributeValue("piz"));
					if(pizhong.doubleValue()<=0){
						showErrorMessage("未读取到皮重信息，当前皮重为"+pizhong);
						return;
					}
					//判断时候选择车辆信息
					if(null != carid){
						ToolVO toolVO= (ToolVO) HYPubBO_Client.queryByPrimaryKey(ToolVO.class, carid.toString());
						Object standardweight  = toolVO.getAttributeValue("standardweight");

						//判断车辆隶属次数
						IPonderItf ponder = NCLocator.getInstance().lookup(IPonderItf.class);
						try {
							InvoicesheetHVO headervo=aggPonder.getParentVO();
							String pk_kc= HgtsPubTool.getStringNullAsTrim(headervo.getAttributeValue("pk_kc"));
							int num = ponder.OnQueryCarHnum(carid,pk_kc/*this.ce.getUser().getPk_org()*/);
							if (standardweight == null || standardweight.toString().equals("")) {
								if(num >= 2){
									showErrorMessage("车辆未设定标皮，请先设定该车辆的标皮");
									return;
								} 
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if ((standardweight != null) && (!standardweight.toString().equals("")) 
								&& getHeadCardPanel().getBodyValueAt(0, "wucha") != null){
							UFDouble wucha=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "wucha"));
							if(null !=wucha && wucha.doubleValue()>0.2){
								/*showErrorMessage("车辆标重与本次皮重误差较大，允许最大误差为0.2，本次误差为"+wucha);
								return;*/
							}else{
								// 小于等于0.2，给出提示，但允许保存
								//showErrorMessage("皮重与车辆标重，本次误差为"+wucha);
							}
						}
					}
					// 2018-4-10
					InvoicesheetHVO headervo=aggPonder.getParentVO();
					String hpk=headervo.getPrimaryKey();
					String pk_kc= HgtsPubTool.getStringNullAsTrim(headervo.getAttributeValue("pk_kc"));
					boolean isHave=ponder.onQueryFreeCarnoNum(hpk, pk_kc, carid.toString());
					if(!isHave){
						showErrorMessage("同一矿厂，回皮时、车牌号不允许录入多次");
						return;
					}
					//headervo.setAttributeValue("def11", in_red);
					aggPonder.setParentVO(headervo);
				}
			}

			// 1-出
			if(getFReceOrSend() == 1){
				int selindex=getNewTListPanel().getSelindex();//.selindex;
				if(null ==bodys[0].getAttributeValue("maoz")){
					showErrorMessage("车辆毛重未读取，请先点击“过毛重”按钮！");
					// 重新定位到选择行
					getNewTListPanel().getBodyTable().setRowSelectionInterval(selindex, selindex);
					return;
				}
				UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "jingz"));
				if(jingz.doubleValue()<0){
					showErrorMessage("净重小于0，不允许保存");
					getNewTListPanel().getBodyTable().setRowSelectionInterval(selindex, selindex);
					return;
				}

				// 发运通知单子表pk
				String csourcebid=HgtsPubTool.getStringNullAsTrim(getHeadCardPanel().getBodyValueAt(0, "csourcebid"));
				try {
					SendnoticebillBVO sendbvo=(SendnoticebillBVO) HYPubBO_Client.queryByPrimaryKey(SendnoticebillBVO.class, csourcebid);
					UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(sendbvo.getAttributeValue("shul"));
					UFDouble yzxnum=HgtsPubTool.getUFDoubleNullAsZero(sendbvo.getAttributeValue("yzxnum"));
					// 2019年2月15日 剩余量进行四舍五入
					UFDouble syl=shul.sub(yzxnum).setScale(3, UFDouble.ROUND_HALF_UP); // 过重车之前的剩余量

					UFDouble next_syl=syl.sub(jingz); // 本次剩余

					if(!isDIn || jytype==1 || jytype==4 ){

						if(next_syl.doubleValue()<0){
							showErrorMessage("该发运通知单,剩余量不足,不可过磅!");
							getNewTListPanel().getBodyTable().setRowSelectionInterval(selindex, selindex);
							return;
						}else{
							// 如果 毛重=皮重  直接保存，不再校验 称重异常。
							UFDouble piz=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "piz"));
							UFDouble maoz=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "maoz"));
							UFDouble hezs=HgtsPubTool.getUFDoubleNullAsZero(getHeadCardPanel().getBodyValueAt(0, "carno.def2"));
							// 毛重不允许超 核载数
							if(maoz.doubleValue()>hezs.doubleValue() &&  hezs.doubleValue()>0){
								showErrorMessage("毛重超过核载数，不可过磅！");
								getNewTListPanel().getBodyTable().setRowSelectionInterval(selindex, selindex);
								return;
							}

							if(piz.doubleValue() != maoz.doubleValue()){
								// 检验称重异常数据
								InvoicesheetHVO headervo=aggPonder.getParentVO();
								String pk_kc= HgtsPubTool.getStringNullAsTrim(headervo.getAttributeValue("pk_kc"));
								String carno=HgtsPubTool.getStringNullAsTrim(bodys[0].getAttributeValue("carno"));
								String pk_material=HgtsPubTool.getStringNullAsTrim(bodys[0].getAttributeValue("pz"));
								Object note=this.controlErrorData(pk_kc, carno, jingz,pk_material,selindex);
								// note 不为null时 ，保存数据
								if(null ==note){
									return;
								}

								if(!"Y".equals(note)){							  
									headervo.setAttributeValue("def2", note); // 记录称重异常信息							  
									aggPonder.setParentVO(headervo);
								}else{
									//headervo.setAttributeValue("def12", out_red);
									aggPonder.setParentVO(headervo);
								}
							}else{
								//InvoicesheetHVO headervo=aggPonder.getParentVO();
								//headervo.setAttributeValue("def12", out_red);
								//aggPonder.setParentVO(headervo);
							}
							getHeadCardPanel().setBodyValueAt(next_syl, 0, "syl");
							bodys[0].setAttributeValue("syl", next_syl);
						}
					}
				} catch (UifException e) {
					e.printStackTrace();
				}

				getNewTListPanel().setSelindex(-1);
			}
			aggPonder.setChildrenVO(bodys);
			bodys =  ponder.insertInvoicesheetVO(aggPonder,getFReceOrSend());	

			//增加打印功能
			//  onPrint();
			setBillModel(-1);
			setBillStates();
			updateUI();
			try {
				String pk_invoice = bodys[0].getPk_invoice()==null?bodys[0].getAttributeValue("pk_invoice").toString():bodys[0].getPk_invoice();
				InvoicesheetHVO hVO = (InvoicesheetHVO) HYPubBO_Client.queryByPrimaryKey(InvoicesheetHVO.class, pk_invoice);
				aggPonder = new AggInvoicesheetHVO();
				aggPonder.setParentVO(hVO);
				aggPonder.setChildrenVO(bodys);
				getHeadCardPanel().setBillValueVO(aggPonder);

				guil=0;
				this.parents.getHardWarePanel().getMeasPanel().setGuil(guil);

			} catch (UifException e) {
				e.printStackTrace();
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void onDelete() {
		try {
			//获取过磅单数据，保存
			AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO, 
					ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
			InvoicesheetBVO[] bodys = (InvoicesheetBVO[])getHeadCardPanel().getBillModel().getBodyValueVOs(ContractVO.InvoicesheetBVO);
			if(null == bodys || bodys.length<1 ){//没有子表数据，不可保存
				showErrorMessage("没有读取车辆信息!");
				return;
			}
			if(null ==bodys[0].getAttributeValue("csourcebid")){
				showErrorMessage("数据有误，无法回写发运通知单!");
				return;
			}
			aggPonder.setChildrenVO(bodys);

			ponder.DeleteInvoiceVO(aggPonder);
			//页面数据刷新 
			clearUIData();
			setBillModel(-1);
			setBillStates();
		} catch (BusinessException e) {

			e.printStackTrace();
		}
	}

	public void onCancel() {
		if(getFReceOrSend() == 0){
			/*getHeadCardPanel().setBodyValueAt("", 0, "piz");
			getHeadCardPanel().setBodyValueAt("", 0, "jingz");*/

			clearUIData();
		}else{
			/*if(null != getHeadCardPanel().getBodyValueAt(0,"fubs") && (!getHeadCardPanel().getBodyValueAt(0,"fubs").equals(""))){
				getHeadCardPanel().setBodyValueAt("", 0, "fubs");
			}else{
				getHeadCardPanel().setBodyValueAt("", 0, "maoz");
				getHeadCardPanel().setBodyValueAt("-"+getHeadCardPanel().getBodyValueAt(0,"piz"), 0, "jingz");  
			}*/ 	

			clearUIData();
		}
		setBillModel(-1);
		setBillStates();
	}

	public void onRefresh() {
		clearUIData();
		setBillModel(-1);
		getParents().getHardWarePanel().getMeasPanel().setHistoryValue(null, null, null, null);
		getParents().getHardWarePanel().getMeasPanel().setWaitingNum(null, null);
		setBillStates();
		if (YGUtils.isAutoReadRFID()) {
			try{
				stopReadRFID();
			} catch (BusinessException e){
				FmpubLogger.error(e.getMessage(), e);
				showErrorMessage(e.getMessage());
				return;
			}
		}
	}

	public void onPrint() {

		IDataSource dataSource = new CardPonderPanelPRTS("40H10603",  headCardPanel);
		PrintEntry print = new PrintEntry(getParents(), dataSource);
		// print.setDataSource(dataSource);
		String question="该磅单已进行打印，是否确定再次打印？";
		String vbillno=HgtsPubTool.getStringNullAsTrim(this.getHeadCardPanel().getHeadItem("vbillno").getValueObject());
		InvoicesheetHVO[] hvos=null;
		try {
			hvos=(InvoicesheetHVO[]) HYPubBO_Client.queryByCondition(InvoicesheetHVO.class, " nvl(dr,0)=0 and vbillno='"+vbillno+"'");
		} catch (UifException e) {
			e.printStackTrace();
		}

		if(null==hvos ||hvos.length==0){
			return;
		}

		if(getFReceOrSend() == 0 ){
			Object printnum =this.getHeadCardPanel().getHeadItem("printnum").getValueObject();
			if(null !=printnum && Integer.parseInt(printnum.toString())>=1){
				if(MessageDialog.showOkCancelDlg(this.parents, "询问", question)==1){ // 确定					
					print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10601", AppContext.getInstance().getPkUser(), null, "ot");
					print.selectTemplate();					
					print.preview();

					/*BarCoder bc =BarCoder.createBarCode(BarCoder.TYPE_PDF417);
					bc.setShowMsg(true);
					bc.setMsgPosition(2);
					Image image = bc.getBarCode(1920, 1080, "12");
					image.getGraphics();*/

				}
			}else{
				this.getHeadCardPanel().setHeadItem("printnum", "1");
				print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10601", AppContext.getInstance().getPkUser(), null, "ot");
				print.selectTemplate();					
				print.preview();

				hvos[0].setAttributeValue("dr", 0);
				hvos[0].setAttributeValue("printnum", 1);
				try {
					HYPubBO_Client.updateAry(hvos);
				} catch (UifException e) {
					e.printStackTrace();
				}

			}
		}
		if(getFReceOrSend() == 1){
			Object printnum =this.getHeadCardPanel().getHeadItem("cprintnum").getValueObject();
			if(null !=printnum && Integer.parseInt(printnum.toString())>=1){
				if(MessageDialog.showOkCancelDlg(this.parents, "询问", question)==1){ // 确定					
					print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10604", AppContext.getInstance().getPkUser(), null, "ot");	
					print.selectTemplate();					
					print.preview();
				}
			}else{
				this.getHeadCardPanel().setHeadItem("cprintnum", "1");
				print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10604", AppContext.getInstance().getPkUser(), null, "ot");
				print.selectTemplate();					
				print.preview();

				hvos[0].setAttributeValue("dr", 0);
				hvos[0].setAttributeValue("cprintnum", 1);
				try {
					HYPubBO_Client.updateAry(hvos);
				} catch (UifException e) {
					e.printStackTrace();
				}
			}
		}
		if(getFReceOrSend() == 2){
			if(HgtsPubTool.getUFDoubleNullAsZero(this.getHeadCardPanel().getBodyValueAt(0, "piz")).doubleValue() >0
					&& HgtsPubTool.getUFDoubleNullAsZero(this.getHeadCardPanel().getBodyValueAt(0, "maoz")).doubleValue() >0){				
				print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10604", AppContext.getInstance().getPkUser(), null, "ot");	
			}else{
				print.setTemplateID( AppContext.getInstance().getPkGroup(), "40H10601", AppContext.getInstance().getPkUser(), null, "ot");	
			}
			print.selectTemplate();					
			print.preview();
		}
	}

	//车辆历史数据查询
	public void onCarHistory() {
		AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO,  ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
		ShowDataDialog Dialog = new ShowDataDialog(getParents(),getFReceOrSend(),null,aggPonder,this.ce.getUser().getPk_org());
		Dialog.show();
	}

	//车辆标重
	public void onCarWeight() {
		// TODO 自动生成的方法存根
		AggInvoicesheetHVO aggPonder = (AggInvoicesheetHVO)getHeadCardPanel().getBillValueVO(ContractVO.AggInvoicesheetVO,  ContractVO.InvoicesheetHVO, ContractVO.InvoicesheetBVO);
		InvoicesheetBVO[] body = (InvoicesheetBVO[]) aggPonder.getChildrenVO();
		try {

			if(body[0].getAttributeValue("piz")== null){
				showErrorMessage("皮重未稳定，待稳定后标皮 !");
				return;
			}
			if(body[0].getAttributeValue("carno")== null){
				showErrorMessage("未选择车辆 ,请选择需标皮车辆 !");
				return;
			}  
			ponder.getCarWeight(aggPonder);
			Object piz=body[0].getAttributeValue("piz");
			getHeadCardPanel().setBodyValueAt(piz,0, "carno.standardweight");
			getHeadCardPanel().setBodyValueAt(0, 0, "wucha");

			MessageDialog.showWarningDlg(this,"提示","标皮成功！");

		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理称重异常数据
	 * 2018-3-24
	 * chengli
	 */
	public Object controlErrorData(String pk_kc,String carno,UFDouble jingz,String pk_material,int selindex){
		try {
			// 参数：是否检查异常
			ISysInitQry init=(ISysInitQry) NCLocator.getInstance().lookup(ISysInitQry.class.getName());
			SysInitVO sysInitVO = init.queryByParaCode(AppContext.getInstance().getPkGroup(),"FF10");
			String value=sysInitVO.getValue();
			// 检查
			if(null !=value && !"".equals(value) && "是".equals(value)){				  
				Object[] obj=ponder.contErrorData(pk_kc, carno, jingz,pk_material);
				//称重数据有异常
				if(null !=obj && obj.length>0){
					String question="本次称重数据异常，请查明原因后方可保存数据，是否保存数据？";
					getNewTListPanel().getBodyTable().setRowSelectionInterval(selindex, selindex);
					if(MessageDialog.showYesNoCancelDlg(this.parents, "询问", question)==4){ // 是
						ErrorReasonDlg dlg=new ErrorReasonDlg(this.parents,"本次异常原因",obj);
						dlg.setVisible(true);

						String note=dlg.getNote();
						Object vnote=obj[3]+"；本次异常原因："+note;
						return vnote;
					}
				}else{
					//无异常， 正常保存
					return "Y";
				}
			}else{ // 不检查， 正常保存
				return "Y";
			}
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		// 询问时，点击了按钮“否”，不保存
		return null;
	}

	/*private void addEventListener(){	
		this.getHeadCardPanel().getBillTable().addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO 自动生成的方法存根
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// 按下 F7 键，“数据标识”隐藏
				if(e.getKeyCode()==KeyEvent.VK_F7){
					onHidden();
					onHiddenTail();
				}
				// 按下 F8 键，“数据标识”显示
				if(e.getKeyCode()==KeyEvent.VK_F8){
					onShow();
					onShowTail();
				}

				//  按下 F6 键，弹出“打印”界面
				if(e.getKeyCode()==KeyEvent.VK_F6 && getBillModel()==-1 ){
					onPrint();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO 自动生成的方法存根

			}

		});
		
		this.getTailListPanel().getBodyTable().addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO 自动生成的方法存根
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO 自动生成的方法存根
				if(e.getKeyCode()==KeyEvent.VK_F7){
					onHidden();
					onHiddenTail();
				}
				
				if(e.getKeyCode()==KeyEvent.VK_F8){
					onShow();
					onShowTail();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO 自动生成的方法存根
				
			}
			
		});
	}*/

	public void onHidden(){
		this.getHeadCardPanel().hideHeadItem(new String[]{"flag_data"});
	}

	public void onShow(){
		this.getHeadCardPanel().showHeadItem(new String[]{"flag_data"});

	}

//	public void onHiddenTail(){
//		parents.nBTN.setEnabled(false);
//		parents.nBTN.setVisible(false);
//		parents.nBTN.setName("");
//		
//		parents.wBTN.setEnabled(false);
//		parents.wBTN.setVisible(false);
//		parents.wBTN.setName("");
//		
//		parents.aBTN.setEnabled(false);
//		parents.aBTN.setVisible(false);
//		parents.aBTN.setName("");
//		
//	}
//
//	public void onShowTail(){
//		parents.nBTN.setEnabled(true);
//		parents.nBTN.setVisible(true);
//		parents.nBTN.setName("N");
//		
//		parents.wBTN.setEnabled(true);
//		parents.wBTN.setVisible(true);
//		parents.wBTN.setName("W");
//		
//		parents.aBTN.setEnabled(true);
//		parents.aBTN.setVisible(true);
//		parents.aBTN.setName("A");
//	}
	/**
	 * 生成包含字符串信息的二维码图片
	 * @param outputStream 文件输出流路径
	 * @param content 二维码携带信息
	 * @param qrCodeSize 二维码图片大小
	 * @param imageFormat 二维码的格式
	 * @throws WriterException 
	 * @throws IOException 
	 */
	/*public static boolean createQrCode(OutputStream outputStream, String content, int qrCodeSize, String imageFormat) throws WriterException, IOException{  
		//设置二维码纠错级别ＭＡＰ
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // 矫错级别  
		hintMap.put(EncodeHintType.CHARACTER_SET,"utf-8");
		QRCodeWriter qrCodeWriter = new QRCodeWriter();  
		//创建比特矩阵(位矩阵)的QR码编码的字符串  
		BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);  
		// 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
		int matrixWidth = byteMatrix.getWidth();  
		BufferedImage image = new BufferedImage(matrixWidth-200, matrixWidth-200, BufferedImage.TYPE_INT_RGB);  
		image.createGraphics();  
		//绘制二维码图片
		Graphics2D graphics = (Graphics2D) image.getGraphics();  
		graphics.setColor(Color.WHITE);  
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);  
		// 使用比特矩阵画并保存图像
		graphics.setColor(Color.BLACK);  
		for (int i = 0; i < matrixWidth; i++){
			for (int j = 0; j < matrixWidth; j++){
				if (byteMatrix.get(i, j)){
					graphics.fillRect(i-100, j-100, 1, 1);  
				}
			}
		}
		return ImageIO.write(image, imageFormat, outputStream);  
	}*/

	/**
	 * 读二维码并输出携带的信息
	 */
	/*public static void readQrCode(InputStream inputStream){
		//从输入流中获取字符串信息
		try {
			BufferedImage image = ImageIO.read(inputStream);
			//将图像转换为二进制位图源
			LuminanceSource source = new BufferedImageLuminanceSource(image);  
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
			QRCodeReader reader = new QRCodeReader(); 
			Hashtable hints = new Hashtable();
			//解码设置编码方式为：utf-8
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			Result result = null ;  
			try {
				result = reader.decode(bitmap,hints);  
				result.getText();
			} catch (ReaderException e) {
				e.printStackTrace();  
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
	}*/

	public HashMap bufferCondition = new HashMap();//存放上一次查询的数据，作为下一次查询时的缓存

	public HashMap getBufferCondition() {
		return bufferCondition;
	}

	public void setBufferCondition(HashMap bufferCondition) {
		this.bufferCondition = bufferCondition;
	}

	protected QueryDlg getConditionDlg() {
		QueryDlg rtnValue = new QueryDlg(this.getBufferCondition());
		return rtnValue;
	}

	/**
	 * 查询
	 */
	public void onQuery(){
		QueryDlg dlg = getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String[] whereBuffer = dlg.getQuerycondition();
			String ofmine = HgtsPubTool.getStringNullAsTrim(this.getMeasDoc().getAttributeValue("ofmine"));
			try {
				if(getFReceOrSend() == 0){
					DoListSendnoticeAgg[] aggs=ponder.SelectSendnoticeVOs(AppContext.getInstance().getServerTime().getDate().toString().substring(0, 10), 
							ofmine, "1", whereBuffer[1], whereBuffer[0]); // 内部
					if(null==aggs || aggs.length==0){
						aggs = ponder.SelectSendnoticeVOs(AppContext.getInstance().getServerTime().getDate().toString().substring(0, 10),
								ofmine,"2",whereBuffer[1], whereBuffer[0]); // 外部
					}
					getHeadCardPanel().setBillValueByVo(new InvoicesheetHVO());
//					getHeadCardPanel().getBillData().setBodyValueVO(null);
//					getTailListPanel().setBodyValueVO(new DoListSendnoticeAgg[0]);
//					getTailListPanel().setBodyValueVO(aggs);
//					getTailListPanel().getBodyBillModel().loadLoadRelationItemValue();
//					getTailListPanel().getBodyTable().setSortEnabled(false); // 去掉表尾排序
					getHeadCardPanel().setBillValueByVo(new InvoicesheetHVO());
					getNewTListPanel().setBodyValueVO(new DoListInvoiceAgg[0]);
					getNewTListPanel().setBodyValueVO(aggs);
					getNewTListPanel().getBodyBillModel().loadLoadRelationItemValue();
					getNewTListPanel().getBodyBillModel().execLoadFormula();
					getNewTListPanel().getBodyTable().setSortEnabled(false);
					getHeadCardPanel().updateUI();
					updateUI();
					getParents().updateUI();
				}else if(getFReceOrSend() == 1){
					DoListInvoiceAgg[] aggs=ponder.getDoListInvoiceAgg(ofmine,"1", whereBuffer[1], whereBuffer[0]);
					if(null==aggs || aggs.length==0){
						aggs=ponder.getDoListInvoiceAgg(ofmine,"2", whereBuffer[1], whereBuffer[0]);
					}
					getHeadCardPanel().setBillValueByVo(new InvoicesheetHVO());
					getNewTListPanel().setBodyValueVO(new DoListInvoiceAgg[0]);
					getNewTListPanel().setBodyValueVO(aggs);
					getNewTListPanel().getBodyBillModel().loadLoadRelationItemValue();
					getNewTListPanel().getBodyBillModel().execLoadFormula();
					getNewTListPanel().getBodyTable().setSortEnabled(false);
					getHeadCardPanel().updateUI();
				}
			} catch (BusinessException e) {				
				e.printStackTrace();
			}
		}
	}

	public void onNBtn(){}

	public void onWBtn(){}

	public void onABtn(){}

	@Override
	public void eventDispatched(AWTEvent event) {
		// TODO 自动生成的方法存根
		KeyEvent e=(KeyEvent) event;
		if(e.getKeyCode()==KeyEvent.VK_F7){
			onHidden();
		//	onHiddenTail();
		}
		
		if(e.getKeyCode()==KeyEvent.VK_F8){
			onShow();
		//	onShowTail();
		}
		
		/*if (e.getID() == KeyEvent.KEY_PRESSED) {
			
			InputEvent input=(InputEvent) event;
			if(e.getKeyCode()==KeyEvent.VK_F7 && input.isControlDown()){
				onHidden();
				onHiddenTail();
				
			}
			if(e.getKeyCode()==KeyEvent.VK_F8 && input.isControlDown()){
				onShow();
				onShowTail();
			
			}
		}*/
	}
	
	/**
	 * 定时向数据库保存数据
	 */
	ScheduledExecutorService service=null;
	public void timer(){
		Runnable runnable=new Runnable(){

			@Override
			public void run() {
				// 保存按钮可用时
				if(parents.saveBTN.isEnabled() && getBillModel()!=-1){					
					//OnSave();
				}
			}
		};		
		if(null !=service){
			service.shutdownNow();
			service=null;
		}
		service=Executors.newSingleThreadScheduledExecutor();
		if(null !=service){
			
			// 延迟3s启动，每隔15s执行一次，是前一个任务开始时就开始计算时间间隔，但是会等上一个任务结束在开始下一个
			service.scheduleAtFixedRate(runnable, 3, 10, java.util.concurrent.TimeUnit.SECONDS);
		}
	}
	
}
