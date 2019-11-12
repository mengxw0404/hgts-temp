package nc.ui.hgts.hjsettle.actions;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.hgts.common.ISysDocValues;
import nc.itf.hgts.settle.INumColResult;
import nc.itf.hgts.settle.IPriceSettle;
import nc.ui.hgts.hjsettle.yh.comm.QuaDj;
import nc.ui.hgts.hjsettle_ly.actions.QryCarloadingplanAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pc.BdInfoVOForSettle;
import nc.vo.hgts.pc.NumColResult;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.util.VOSortUtils;

/**
 * 汽运 --结算单 参照 过磅单 
 */
public class QryBDAction extends nc.ui.pubapp.uif2app.actions.AddAction {

	private static final long serialVersionUID = 7473834006638663348L;
	FormulaParseTool tool =new FormulaParseTool();
	public QryBDAction(){
		super();
		super.setCode("qryBDAction");
		super.setBtnName("参照过磅单");
	}

	private AbstractAppModel model;
	private ShowUpableBillForm editor;

	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public HashMap bufferCondition = new HashMap();//存放上一次查询的数据，作为下一次查询时的缓存

	public HashMap getBufferCondition() {
		return bufferCondition;
	}

	public void setBufferCondition(HashMap bufferCondition) {
		this.bufferCondition = bufferCondition;
	}

	protected ImpQueryDlg getConditionDlg() {
		ImpQueryDlg rtnValue = new ImpQueryDlg(this.getBufferCondition(),this.model);
		return rtnValue;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ImpQueryDlg dlg = this.getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String pk_org=null;
			//弹出过磅单数据界面
			String[] whereBuffer = dlg.getQuerycondition();
			IPriceSettle settle=(IPriceSettle) NCLocator.getInstance().lookup(IPriceSettle.class.getName());
			AggInvoicesheetHVO[] skdAggVOS =settle.getAggvos(whereBuffer);
			if(null==skdAggVOS || skdAggVOS.length==0){
				MessageDialog.showWarningDlg(null, "提示", "没有符合条件的数据");
				return;
			}
			ShowDataDialog buffDataDlalog=new ShowDataDialog(null, getModel().getContext(),skdAggVOS, pk_org, dlg.isImp);
			buffDataDlalog.setVisible(true);
			AggInvoicesheetHVO[] aggvos=buffDataDlalog.getResults();
			if(null !=aggvos && aggvos.length>0){
				//过磅单生成结算信息
				AggHjsettleHVO aggvo=data(aggvos);
				if(null!=aggvo){					// 新增单据
					this.model.setUiState(UIState.ADD);
					this.model.setAppUiState(AppUiState.ADD);
				//	this.getEditor().getBillCardPanel().setBillValueVO(aggvo);
					this.getEditor().setValue(aggvo);
					this.getEditor().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
					//this.getEditor().getBillCardPanel().getBillTable().setSortEnabled(false);
					this.ishaveflag=false;
				}
			}
		}
	}

	/**
	 * 同一 组织+客户+矿别+煤种+发运通知单号+质检批次+过磅日期的过磅单合并成一条数据
	 * 过磅日期+质检批次（质检报告单号）+发运通知单号+发运通知单体id  进行合并:相同的合并形成表体的一行
	 * @param aggvos
	 * @throws SQLException 
	 * @throws BusinessException 
	 */
	boolean ishaveflag=false;  // 是否有拆行标识
	public AggHjsettleHVO data(AggInvoicesheetHVO[] aggvos) throws Exception{
		AggHjsettleHVO aggHjsettleHVO=new AggHjsettleHVO();
		Map<String,List<AggInvoicesheetHVO>> map=new TreeMap<String,List<AggInvoicesheetHVO>>();
		String pk_zxxy ="";//装卸协议主键
		String pk_ysxy ="";//运输协议主键
		UFBoolean type =UFBoolean.FALSE;//是否一票制
		
		// TreeMap 默认根据key升序
		for(int i=0;i<aggvos.length;i++){
			InvoicesheetHVO hvo=aggvos[i].getParentVO();
			String pk_invoice=hvo.getPrimaryKey();
			hvo=(InvoicesheetHVO) HYPubBO_Client.queryByPrimaryKey(InvoicesheetHVO.class, pk_invoice);
			pk_zxxy = HgtsPubTool.getStringNullAsTrim( hvo.getAttributeValue("zxxycode"));
			String ida  = HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_fytzd"));
			type=HgtsPubTool.getUFBooleanNullAsFalse(tool.getNameByID("hgts_sendnoticebill", "type", "pk_sendnoticebill", ida));
			
			pk_ysxy = HgtsPubTool.getStringNullAsTrim( hvo.getAttributeValue("yfxycode"));
			InvoicesheetBVO[] items=(InvoicesheetBVO[]) aggvos[i].getChildrenVO();
			if(null == items || items.length==0 ){
				
				items=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");		
			}
			
			aggvos[i].setChildrenVO(items);
			String gbdate=hvo.getAttributeValue("dbilldate").toString().substring(0, 10);
			String zjpc=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_qualityreport"));
			String sendnoticebillno=hvo.getAttributeValue("sendnoticebillno").toString();
			String pk_send_b=HgtsPubTool.getStringNullAsTrim(items[0].getAttributeValue("csourcebid"));

			String key=pk_send_b+sendnoticebillno+zjpc+gbdate;
			//过磅日期+质检批次（质检报告单号）+发运通知单号+发运通知单体id  VO合并
			if(map.containsKey(key)){
				map.get(key).add(aggvos[i]);
			}else{
				List<AggInvoicesheetHVO> list=new ArrayList<AggInvoicesheetHVO>();
				list.add(aggvos[i]);
				map.put(key, list);
			}
		}
		
		// 质量指标使用 begin
		Map<String,UFDouble> map_dates=new TreeMap<String,UFDouble>();
		if(map !=null && map.size()>0){
			for(String key:map.keySet()){
				UFDouble sum_jz=UFDouble.ZERO_DBL;
				List<AggInvoicesheetHVO> list=map.get(key);
				String gbdate=key.substring(key.length()-10, key.length());
				if(null !=list && list.size()>0){
					for(int i=0;i<list.size();i++){						
						AggInvoicesheetHVO aggvo=list.get(i);
						InvoicesheetBVO[] ibvos=(InvoicesheetBVO[]) aggvo.getChildrenVO();
						if(null ==ibvos || ibvos.length==0){
							String pk_invoice=aggvo.getParentVO().getPrimaryKey();
							ibvos=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");
						}
						for(int j=0;j<ibvos.length;j++){
							UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("jingz"));
							sum_jz=sum_jz.add(jingz);
						}						
					}					
				}
	
				if(map_dates.containsKey(gbdate)){
					map_dates.get(gbdate).add(sum_jz);
				}else{					
					map_dates.put(gbdate, sum_jz);
				}				
			}
		}
		
		// end
		
		if(map !=null && map.size()>0){
			HjsettleHVO hjsettleHVO=new HjsettleHVO();
			List<HjsettleBVO[]> list_bvos=new ArrayList<HjsettleBVO[]>();
			String srchpk="";	//2017-12-22 由之前存放发货计量单主表pk，改为单据号
			Map<String,UFDouble> s_map=new HashMap<String,UFDouble>();
			String lastsrcbpks="";		// 存放 发运通知单子表主键、结算重量，用来回写
			
			for(String key:map.keySet()){
				List<AggInvoicesheetHVO> list=map.get(key);
				if(null !=list && list.size()>0){
					BdInfoVOForSettle infor =new  BdInfoVOForSettle() ;
					int carnum=list.size();	// 车数
					UFDouble jingz =UFDouble.ZERO_DBL;
					UFDouble custton=UFDouble.ZERO_DBL;
					String mz="";
					String vsourcecode="";
					String vsourcerowno="";
					String csourcebid="";
					String pk_balatype="";
					for(int i=0;i<list.size();i++){
						AggInvoicesheetHVO aggvo=list.get(i);
						// 1、质检批次、过磅日期、矿别、煤种 
						InvoicesheetHVO hvo=aggvo.getParentVO();
						String pk_group =hvo.getAttributeValue("pk_group").toString();
						String pk_org =hvo.getAttributeValue("pk_org").toString();
						String pk_org_v =hvo.getAttributeValue("pk_org_v").toString();
						String pk_busitype =hvo.getAttributeValue("pk_busitype").toString();
						String pk_cust =hvo.getAttributeValue("pk_cust").toString();
						String qcbillid =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_qualityreport"));
						String mineid =hvo.getAttributeValue("pk_kc").toString();
						UFDate gbdate =AppContext.getInstance().getBusiDate();
						pk_balatype =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_balatype"));
						hjsettleHVO.setAttributeValue("pk_group", pk_group);
						hjsettleHVO.setAttributeValue("pk_org", pk_org);
						hjsettleHVO.setAttributeValue("pk_org_v", pk_org_v);
						hjsettleHVO.setAttributeValue("pk_cust", pk_cust);
						hjsettleHVO.setAttributeValue("pk_busitype", pk_busitype);		
						hjsettleHVO.setAttributeValue("dbilldate", gbdate);
						hjsettleHVO.setAttributeValue("pk_billtype", HgtsPubConst.XSHJD_QY);
						hjsettleHVO.setAttributeValue("vbillstatus", BillStatusEnum.FREE.value());
						hjsettleHVO.setAttributeValue("pk_balatype", pk_balatype);

						// 2017-12-13 取发运通知单部门 begin
						String sendnoticebillno =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("sendnoticebillno"));
						String pk_dept=tool.getNameByID("hgts_sendnoticebill", "pk_dept", "vbillno", sendnoticebillno);

						hjsettleHVO.setAttributeValue("isks", hvo.getAttributeValue("isks"));
						hjsettleHVO.setAttributeValue("pk_dept", pk_dept);
						hjsettleHVO.setAttributeValue("settlezt", hvo.getAttributeValue("settlezt"));
						// 2017-12-13 取发运通知单部门 end

						// 来源hpk，用来回写
						if(!"".equals(srchpk)){
							srchpk=srchpk+",'"+hvo.getAttributeValue("vbillno")+"'";
						}else{
							srchpk="'"+hvo.getAttributeValue("vbillno")+"'";
						}
						hjsettleHVO.setAttributeValue("srchpks", srchpk);

						InvoicesheetBVO[] ibvos=(InvoicesheetBVO[]) aggvo.getChildrenVO();
						if(null ==ibvos || ibvos.length==0){
							String pk_invoice=aggvo.getParentVO().getPrimaryKey();
							ibvos=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");
						}

						for(int j=0;j<ibvos.length;j++){
							UFDouble jsweight=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("jingz"));
							jingz=jingz.add(jsweight);

							UFDouble custjingz=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("custjingz"));
							custton=custton.add(custjingz);

							mz =ibvos[j].getAttributeValue("pz").toString();

							String pk_sendnoticebill_b=HgtsPubTool.getStringNullAsTrim(ibvos[j].getAttributeValue("csourcebid"));
							if(s_map.containsKey(pk_sendnoticebill_b)){
								jsweight=s_map.get(pk_sendnoticebill_b).add(jsweight);
								s_map.put(pk_sendnoticebill_b, jsweight);
							}else{
								s_map.put(pk_sendnoticebill_b, jsweight);
							}	
						}

						vsourcecode=HgtsPubTool.getStringNullAsTrim(ibvos[0].getAttributeValue("vsourcecode")); // 发货通知单号
						csourcebid=HgtsPubTool.getStringNullAsTrim(ibvos[0].getAttributeValue("csourcebid"));
					
						infor.setPk_org(pk_org);
						infor.setPk_cust(pk_cust);
						infor.setQcbillid(qcbillid);
						infor.setMineid(mineid);
						infor.setGbdate(new UFDate(hvo.getAttributeValue("dbilldate").toString()));//过磅日期
						infor.setPk_invid(mz);						
						infor.setJingz(jingz);
						infor.setCustjingz(custton);
						
					}

					// 质检批次、过磅日期、矿别、煤种、煤种组合、净重
					HjsettleBVO hjsettleBVO=new HjsettleBVO();
					hjsettleBVO.setAttributeValue("rowno", 10);
					hjsettleBVO.setAttributeValue("zjpc", infor.getQcbillid());
					hjsettleBVO.setAttributeValue("gbdate", infor.getGbdate());
					hjsettleBVO.setAttributeValue("kb", infor.getMineid());
					hjsettleBVO.setAttributeValue("mz", mz);
				
					hjsettleBVO.setAttributeValue("jz", jingz);
					hjsettleBVO.setAttributeValue("custton", custton);

					hjsettleBVO.setAttributeValue("fytzdh", vsourcecode);//发货通知单号

					vsourcerowno=tool.getNameByID("hgts_sendnoticebill_b", "rowno", "pk_sendnoticebill_b", csourcebid);
					hjsettleBVO.setAttributeValue("fytzdrowno",vsourcerowno);//发货通知单行号
					hjsettleBVO.setAttributeValue("csourcebid",csourcebid);//发货通知单体id
					hjsettleBVO.setAttributeValue("pk_balatype",pk_balatype);
					hjsettleBVO.setAttributeValue("carnum", carnum);
					
					// 3、净重、灰分、水分、当月累计结算  价格计算
					HjsettleBVO[] bvos=datacol(hjsettleHVO,hjsettleBVO,infor,ishaveflag,map_dates);

					list_bvos.add(bvos);
				}
			}

			if(null !=s_map && s_map.size()>0){
				UFDouble s_jsweight=UFDouble.ZERO_DBL;
				for(String s_key:s_map.keySet()){
					s_jsweight=s_map.get(s_key);
					if(!"".equals(lastsrcbpks)){
						lastsrcbpks = lastsrcbpks+";"+s_key+","+s_jsweight;
					}else{
						lastsrcbpks=s_key+","+s_jsweight;
					}
				}
			}

			hjsettleHVO.setAttributeValue("def1", lastsrcbpks);

			aggHjsettleHVO.setParent(hjsettleHVO);
			List<HjsettleBVO> list_hjbvos=new ArrayList<HjsettleBVO>();
			if(null !=list_bvos && list_bvos.size()>0){
				for(int i=0;i<list_bvos.size();i++){
					HjsettleBVO[] items=list_bvos.get(i);
					if(null !=items && items.length>0){						
						for(int j=0;j<items.length;j++){
							HjsettleBVO item=items[j];
							list_hjbvos.add(item);
						}
					}
				}
			}
			//
			HjsettleBVO[] bvos=list_hjbvos.toArray(new HjsettleBVO[0]);
			VOSortUtils.ascSort(bvos, new String[]{"fytzdh","zjpc","gbdate"});
			UFDouble zxton = UFDouble.ZERO_DBL;//装卸重量
			UFDouble M_mny = UFDouble.ZERO_DBL;//结算总煤款
			for(int i=0;i<bvos.length;i++){
				zxton = zxton.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("jz")));
				M_mny = M_mny.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("jsmny")));
				if(i!=0){
					// 取第i行的净重、折干量
					UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("zhegl"));
					// 取第（i-1）行的当月结算累计
					UFDouble curzhegllj=HgtsPubTool.getUFDoubleNullAsZero(bvos[i-1].getAttributeValue("zhegllj"));//折干量累计
					UFDouble grpzgllj=HgtsPubTool.getUFDoubleNullAsZero(bvos[i-1].getAttributeValue("grpzgllj"));//分组折干量累计
					UFDouble i_curzhegllj=curzhegllj.add(zhegl);
					UFDouble i_grpzgllj=grpzgllj.add(zhegl);
					bvos[i].setAttributeValue("zhegllj", i_curzhegllj);
					bvos[i].setAttributeValue("grpzgllj", i_grpzgllj);
					bvos[i].setAttributeValue("rowno", (i+1)*10);
				}					
			}
			
			//根据整理后的结算子信息，做重量合计并计算装卸费
			HjsettleBVO zxbvo = new  HjsettleBVO();
			String pk_material=tool.getNameByID("bd_material", "pk_material", "code", "0901");//获取装车费信息
			IPriceSettle Ipactser= NCLocator.getInstance().lookup(IPriceSettle.class);
			PactBVO  zxpvo =Ipactser.getZXPactB(hjsettleHVO.getAttributeValue("pk_org"), bvos[0].getAttributeValue("kb"),HgtsPubConst.TRANSPORT_QY ,pk_zxxy);
			zxbvo.setAttributeValue("rowno", (bvos.length+1)*10);
			zxbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
			zxbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//矿厂
			zxbvo.setAttributeValue("mz", pk_material);//煤种-装车费
			zxbvo.setAttributeValue("jz", zxton);
			zxbvo.setAttributeValue("settleton",zxton); 	// 结算吨数	
			zxbvo.setAttributeValue("settlezhegl",zxton); 	// 结算折干量(装卸费按实际重量计算)
			UFDouble price = HgtsPubTool.getUFDoubleNullAsZero(zxpvo.getAttributeValue("price"));
			zxbvo.setAttributeValue("jsprice",price);//;//结算单价根据发运通知单中的装卸协议装卸单价
			zxbvo.setAttributeValue("jsmny", price.multiply(zxton));//结算金额
			zxbvo.setAttributeValue("rate",HgtsPubTool.getUFDoubleNullAsZero(zxpvo.getAttributeValue("rate")).setScale(0, 4));//税率：根据发运通知单中的装卸协议装卸税率
			zxbvo.setAttributeValue("def16", "Y");//是否传发票
			List<HjsettleBVO> nList = new ArrayList<HjsettleBVO>();
			for(HjsettleBVO bo:bvos){
				bo.setAttributeValue("def16", "Y");
				nList.add(bo);
			}
			nList.add(zxbvo);
			//判断是否为一票制单据
			if(!pk_ysxy.equals("") && type.booleanValue() ){//&& type.booleanValue()
				//根据整理后的结算子信息，做重量合计并计算运输费
				HjsettleBVO ysbvo = new  HjsettleBVO();
				String  ys=tool.getNameByID("bd_material", "pk_material", "code", "0902");//获取装车费信息
				PactBVO yspvo =Ipactser.getYSPactB(hjsettleHVO.getAttributeValue("pk_org"), bvos[0].getAttributeValue("kb"),HgtsPubConst.TRANSPORT_QY ,pk_ysxy);
				ysbvo.setAttributeValue("rowno", (bvos.length+2)*10);
				ysbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
				ysbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//矿厂
				ysbvo.setAttributeValue("mz", ys);//煤种-运输费
				ysbvo.setAttributeValue("jz", zxton);
				ysbvo.setAttributeValue("settleton",zxton); 	// 结算吨数	
				ysbvo.setAttributeValue("settlezhegl",zxton); 	// 结算折干量(装卸费按实际重量计算)
				UFDouble price1 = HgtsPubTool.getUFDoubleNullAsZero(yspvo.getAttributeValue("price"));
				ysbvo.setAttributeValue("jsprice",price1);//;//结算单价根据发运通知单中的运输协议装卸单价
				ysbvo.setAttributeValue("jsmny", price1.multiply(zxton));//结算金额
				ysbvo.setAttributeValue("rate",HgtsPubTool.getUFDoubleNullAsZero(yspvo.getAttributeValue("rate")).setScale(0, 4));//税率：根据发运通知单中的装卸协议装卸税率
				//
				nList.add(ysbvo);
				
				//一票制总，使用 （结算总款 减 运输费 ）/总重量 ==新煤单价
				HjsettleBVO mbvo = new  HjsettleBVO();
				mbvo.setAttributeValue("rowno", (bvos.length+3)*10);
				mbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
				mbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//矿厂
				mbvo.setAttributeValue("mz", bvos[0].getAttributeValue("mz"));//煤种 			
				UFDouble rate = HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("rate"));
				mbvo.setAttributeValue("rate", HgtsPubTool.getUFDoubleNullAsZero(rate).setScale(0, 4));//税率
				mbvo.setAttributeValue("jz", zxton);
				mbvo.setAttributeValue("settleton",zxton); 	// 结算吨数	
				mbvo.setAttributeValue("settlezhegl",zxton); 	// 结算折干量(装卸费按实际重量计算)
				UFDouble mmny = M_mny.sub(price1.multiply(zxton));
				mbvo.setAttributeValue("jsprice",mmny.div(zxton).setScale(2, 4));//;//结算单价根据发运通知单中的运输协议装卸单价
				mbvo.setAttributeValue("jsmny", mmny );//结算金额
				UFDouble norateprice=mmny.div(zxton).div(rate.div(100).add(1));
				mbvo.setAttributeValue("norateprice",norateprice);//无税单价
				mbvo.setAttributeValue("noratemny", norateprice.multiply(zxton) );//无税金额
				mbvo.setAttributeValue("def14", mmny.sub(norateprice.multiply(zxton)) );//税额
				//
				nList.add(mbvo);
			}
			 
		
			
			aggHjsettleHVO.setChildrenVO(nList.toArray(new HjsettleBVO[0]));
		}
		return aggHjsettleHVO;
	}



	/**
	 * 表体数据的相关计算
	 * @param aggvo
	 * @param hjsettleHVO
	 * @param infor
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public HjsettleBVO[] datacol(HjsettleHVO hjsettleHVO,HjsettleBVO hjsettleBVO,
			BdInfoVOForSettle infor,boolean ishaveflag,Map<String,UFDouble> map_dates) throws BusinessException, SQLException{
		INumColResult result = new NumColResult();
		IPriceSettle settle=(IPriceSettle) NCLocator.getInstance().lookup(IPriceSettle.class.getName());
		// 获取折干量保留小数位数
		ISysDocValues isdv=NCLocator.getInstance().lookup(ISysDocValues.class);		
		int digits=isdv.getDecDigits(infor.getPk_cust());
		// 2、净重、灰分、水分、当月累计结算 计算
		UFDate jsdate=(UFDate) hjsettleHVO.getAttributeValue("dbilldate"); // 结算日期
		Object isks=hjsettleHVO.getAttributeValue("isks"); // 是否扣水
		result=settle.numCol(infor,jsdate,isks);
		if(null !=result){	
			hjsettleBVO.setAttributeValue("huif", result.getHf());
			hjsettleBVO.setAttributeValue("shuif", result.getSf());
			hjsettleBVO.setAttributeValue("custhuif", result.getCustHf());
			hjsettleBVO.setAttributeValue("custshuif", result.getCustSf());

			hjsettleBVO.setAttributeValue("zhegl", result.getZgNum().setScale(digits, UFDouble.ROUND_HALF_UP));
			hjsettleBVO.setAttributeValue("custzhegl", result.getCustZgNum().setScale(digits, UFDouble.ROUND_HALF_UP));
	
			// 2018-01-17  级别
			QryCarloadingplanAction qca=new QryCarloadingplanAction();
			String jibie=qca.getJiBie(result.getHf());
			if(null !=jibie && !"".equals(jibie)){						
				hjsettleBVO.setAttributeValue("jib", Integer.parseInt(jibie));
			}
		}

		// 3、价格计算		
		String pk_busitype=HgtsPubTool.getStringNullAsTrim(hjsettleHVO.getAttributeValue("pk_busitype"));
		String settlezt=HgtsPubTool.getStringNullAsTrim(hjsettleHVO.getAttributeValue("settlezt"));
		HjsettleBVO[] hjbvos=this.getLastVO(hjsettleBVO,pk_busitype,settlezt,result,isks,infor,map_dates);
		return hjbvos;
	}

	@Override
	protected boolean isActionEnable() {
		return this.model.getUiState() == UIState.NOT_EDIT;
	}

	/**
	 * 
	 * @param hjsettleBVO
	 * @throws UifException
	 */
	public HjsettleBVO[] getLastVO(HjsettleBVO hjsettleBVO,String pk_busitype,String settlezt,
			INumColResult result,Object isks,BdInfoVOForSettle infor,Map<String,UFDouble> map_dates) throws UifException{
		HjsettleBVO[] hjsettleBVOs=new HjsettleBVO[1];
		if(null !=hjsettleBVO){			
			String sendnoticebillno=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("fytzdh"));
			String pk_send_b=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("csourcebid"));
			UFDouble huif=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("huif"));
			UFDouble custhuif=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custhuif"));
			
			FormulaParseTool tool =new FormulaParseTool();
			SendnoticebillHVO[] sbillHVO=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+sendnoticebillno+"'");
			String pk_send_h=sbillHVO[0].getPrimaryKey();
			// 货物合同号
			String pk_pact=HgtsPubTool.getStringNullAsTrim(sbillHVO[0].getAttributeValue("contcode"));
			String contcode="";	
			if(!"".equals(pk_pact) && null !=pk_pact){	
				contcode=tool.getNameByID("hgts_sopact", "contcode", "pk_pact", pk_pact);		
			}
			// 指标取值方式：1：单批次；2：加权平均
			String zbqztype=HgtsPubTool.getStringNullAsTrim(sbillHVO[0].getAttributeValue("zbqztype"));
			SendnoticebillBVO bvo=(SendnoticebillBVO) HYPubBO_Client.queryByPrimaryKey(SendnoticebillBVO.class, pk_send_b);
			if(null !=bvo){
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
				UFDouble tz_paytype=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("fkfsyh"));
				UFDouble tz_qyyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("qyyh"));
				UFDouble tz_ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("def13"));
				String vnote=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("vnote"));
				String gbdate=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("gbdate"));
				UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("zhegl"));
				UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("jz"));				
				UFDouble custton=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custton"));
				UFDouble custzhegl=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custzhegl"));
				//算价格政策价格、价格政策金额、结算金额、结算价格
				// 付款方式优惠、区域优惠是下调，从发运通知单取出来的调整价格是正数，故做减法；
				UFDouble price=gpprice.sub(tz_paytype.abs()).sub(tz_qyyh.abs())
						.sub(tz_ysfsyh.abs())/*.sub(def15.abs())*/;
				
				QuaDj dj=new QuaDj();
				if(null !=result){	
					UFDouble tz_zlzb=UFDouble.ZERO_DBL;
					
					String isCurPrj=dj.isCurPrj();					
					if("1".equals(isCurPrj)){
						// 2019年3月7日 modify 取出质量指标  调整价格   由之前从价格政策上取，现在统一从通知单上取
						//	tz_zlzb=getTz_quaindex(pk_pricepolicy, jgz, huif,pk_busitype);
						if(null !=settlezt && !"".equals(settlezt)){
							if("1".equals(settlezt)){ // 买方						
								tz_zlzb=this.getTz_quaindex(pk_send_h, tool, custhuif);
							}else{ // 卖方
								tz_zlzb=this.getTz_quaindex(pk_send_h, tool, huif);
							}
						}else{ // 卖方					
							tz_zlzb=this.getTz_quaindex(pk_send_h, tool, huif);
						}

						if(null !=map && map.size()>0){
							String rowno=map.get("rowno");
							vnote=vnote+"质量指标("+rowno+")"+tz_zlzb.setScale(2, UFDouble.ROUND_HALF_UP);
						}
						map=new HashMap<String,String>();
					
						// 质量指标：根据灰分算出的值如果是正数，表示含杂质少，得上调，负数下调，故加法	
						price=price.add(tz_zlzb);
					}else{						
						// 1、售价
						UFDouble u_price=dj.getTz_quaindex_sj(infor, pk_send_h, settlezt,
								gbdate,zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);
						System.out.println("售价："+u_price);
						// 2、扣款
						UFDouble u_kk=dj.getTz_quaindex_kk(infor, pk_send_h,settlezt,gbdate,
								zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);
						System.out.println("扣款："+u_kk);
						// 3、扣吨
						UFDouble u_kd=dj.getTz_quaindex_kd(infor, pk_send_h,settlezt,gbdate,
								zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);					
						System.out.println("扣吨："+u_kd);
						// 说明综合到厂价为0
						if(null==u_price || null==u_kk){
							price=UFDouble.ZERO_DBL;
						}else{							
							if(u_price.doubleValue()>0){
								price=u_price.sub(u_kk);
							}else{
								price=price.sub(u_kk);
							}
						}

						if(null !=settlezt && !"".equals(settlezt)){
							if("1".equals(settlezt)){ 			// 买方
								custzhegl=custton.sub(custton.multiply(u_kd).div(100));
							}else if("2".equals(settlezt)){ //2： 卖方								
								zhegl=jingz.sub(jingz.multiply(u_kd).div(100));								
							}else if("3".equals(settlezt)){ //3：单月买双月卖
								UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
								if(isD.booleanValue()){ // 单月
									custzhegl=custton.sub(custton.multiply(u_kd).div(100));
								}else{
									zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
								}
							}else { //4:单月卖双月买
								UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
								if(isD.booleanValue()){
									zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
								}else{
									custzhegl=custton.sub(custton.multiply(u_kd).div(100));
								}
							}
						}else{
							zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
						}
						
						// 该批次全部扣除了
						if(zhegl.doubleValue()<0){
							zhegl=UFDouble.ZERO_DBL;
						}
						if(custzhegl.doubleValue()<0){
							custzhegl=UFDouble.ZERO_DBL;
						}
					}
				}
				
				zhegl=zhegl.setScale(3, UFDouble.ROUND_HALF_UP);
				UFDouble totalmny=price.multiply(zhegl);

				// TODO 税率、无税单价、无税金额、税额				
				String pk_material=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("mz"));
				UFDouble rate=tool.getTaxrate(pk_material);//"16%"; 
				UFDouble norateprice=price.div((rate.div(100).add(1)));						

				UFDouble settleton=UFDouble.ZERO_DBL;
				UFDouble settlezhegl=UFDouble.ZERO_DBL;
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ // 买方						
						settleton=custton;
						settlezhegl=custzhegl;
					}else if("2".equals(settlezt)){ // 卖方
						settleton=jingz;
						settlezhegl=zhegl;
					}else if("3".equals(settlezt)){ //3：单月买双月卖
						UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
						if(isD.booleanValue()){ // 单月
							settleton=custton;
							settlezhegl=custzhegl;
						}else{
							settleton=jingz;
							settlezhegl=zhegl;
						}
					}else { //4:单月卖双月买
						UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
						if(isD.booleanValue()){
							settleton=jingz;
							settlezhegl=zhegl;
						}else{
							settleton=custton;
							settlezhegl=custzhegl;
						}
					}
				}else{ // 卖方
					settleton=jingz;
					settlezhegl=zhegl;
				}

				UFDouble jsmny=price.multiply(settlezhegl);				
				UFDouble noratemny=norateprice.multiply(settlezhegl);				
				UFDouble ntaxratemny=price.multiply(settlezhegl).sub(noratemny);

				hjsettleBVO.setAttributeValue("zhegl", zhegl);				
				hjsettleBVO.setAttributeValue("custzhegl", custzhegl);
				
				hjsettleBVO.setAttributeValue("rate", rate);
				hjsettleBVO.setAttributeValue("jgzcprice", price);
				hjsettleBVO.setAttributeValue("jgzcmny", totalmny);
				hjsettleBVO.setAttributeValue("jsmny", jsmny);//结算金额
				hjsettleBVO.setAttributeValue("jsprice", price);//结算单价
				hjsettleBVO.setAttributeValue("norateprice", norateprice);
				hjsettleBVO.setAttributeValue("noratemny", noratemny);	// 无税金额
				hjsettleBVO.setAttributeValue("def14", ntaxratemny); 	// 税额
				hjsettleBVO.setAttributeValue("settleton",settleton); 	// 结算吨数	
				hjsettleBVO.setAttributeValue("settlezhegl",settlezhegl); 	// 结算折干量	
				hjsettleBVO.setAttributeValue("vnote", vnote);
				//途耗=过磅数量-对方过磅数
				//扣水=对方过磅数-对方折干数
				hjsettleBVO.setAttributeValue("tuh", jingz.sub(custton));
				hjsettleBVO.setAttributeValue("kous", custton.sub(custzhegl));
				hjsettleBVO.setAttributeValue("contcode", contcode);
				hjsettleBVOs[0]=hjsettleBVO;
			}
		}
		return hjsettleBVOs;
	}

	/**
	 * 取质量指标的调整值
	 * @throws UifException 
	 */
	Map<String,String> map=new HashMap<String,String>();
	public UFDouble getTz_quaindex(String pk_send_h,FormulaParseTool tool,UFDouble huif) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		SendYzyjBVO[] items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"'");
		if(null !=items && items.length>0){
			UFDouble min = null;
			UFDouble max = null;
			String rowno="";
			String jgfd=""; // 价格计算公式
			for(int i=0;i<items.length;i++){
				min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
				max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
				if(max.doubleValue()==0){
					// 不输入默认为最大值
					if(huif.compareTo(min)>=0){
						jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						rowno=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("rowno"));
						map.put("rowno", rowno);
						break;
					}
				}else{
					if(huif.doubleValue()>=min.doubleValue() && huif.doubleValue()<max.doubleValue()){
						jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						rowno=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("rowno"));
						map.put("rowno", rowno);
						break;
					}
				}
			}

			if(null !=jgfd && !"".equals(jgfd) && jgfd.indexOf("化验值")>0){
				jgfd=jgfd.replace("化验值", "hyz").replaceAll("（", "(").replaceAll("）", ")");
				//｛（化验值-9.52）/0.5｝*（-8） ｛｝内计算取整，不是整数进一
				// 如：（化验值-9.52）/0.5 = 1.1，则取值为2，若=1.0，则取值为1
				String[] str=jgfd.split("[*]");
				FormulaParse fromula = new FormulaParse();		
				fromula.addVariable("hyz", huif);
				fromula.setExpress(str[0]);
				String value=fromula.getValue();
				int i_value=0;
				if(null==value || "".equals(value)){
					i_value=0;					
				}else{
					UFDouble d_value=new UFDouble(value);
					if(d_value.doubleValue()<0){					
						i_value=Integer.valueOf(d_value.abs().toString().split("[.]")[0]).intValue()*(-1);
					}else{
						i_value=Integer.valueOf(d_value.toString().split("[.]")[0]).intValue();
					}

					String s=str[1].replace("(", "").replace(")", "");
					if(i_value==d_value.doubleValue()){
						jgfd=(d_value.multiply(new UFDouble(s))).toString();
					}else{
						if(i_value<=0){	
							if(d_value.doubleValue()>0){
								jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
							}else{								
								jgfd=((new UFDouble(i_value).abs().add(1)).multiply(-1).multiply(new UFDouble(s))).toString();
							}
						}else{
							jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
						}
					}
					fdzvalue=HgtsPubTool.getUFDoubleNullAsZero(jgfd);
				}
			}		
		}
		return fdzvalue;
	}

}
