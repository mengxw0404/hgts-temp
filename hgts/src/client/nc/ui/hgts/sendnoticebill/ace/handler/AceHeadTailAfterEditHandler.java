package nc.ui.hgts.sendnoticebill.ace.handler;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.itf.hgts.qry.cust.mny.ICustBalanceInfo;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent>{

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();
		String pk_org=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_org").getValueObject());
		String pk_cust=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_cust").getValueObject());
		String pk_busitypeid=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_busitype").getValueObject());
		String kb=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_fhkc").getValueObject());
		String pk_dept=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_dept").getValueObject());
		String pk_balatype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_balatype").getValueObject());
		// 2017-10-17 发运通知单 按“开始日期”为准取价格政策
		String dbilldate=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("startdate").getValueObject());
		String contcode=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("contcode").getValueObject());
		//是否自提
	    if(e.getKey().equals("type") && e.getValue().toString().equals("true")){
			e.getBillCardPanel().getHeadItem("yfxycode").setNull(true);
	     }
				
		
		// 参照合同,价格直接从合同上带出，无需取价格政策
		if(null==contcode || "".equals(contcode)){		
			if(e.getKey().equals("pk_org") || e.getKey().equals("pk_cust")
					|| e.getKey().equals("pk_fhkc") || e.getKey().equals("pk_busitype")
					|| e.getKey().equals("dbilldate")|| e.getKey().equals("pk_balatype")
					|| e.getKey().equals("pk_transporttype")){//|| e.getKey().equals("enddate") ||  e.getKey().equals("startdate")
				if(/*!"".equals(pk_org) && */ !"".equals(pk_cust) && !"".equals(pk_busitypeid) && !"".equals(kb)){
					int rowcount=panel.getBillModel("hgts_sendnoticebill_b").getRowCount();
					if(rowcount>0){				
						for(int i=0;i<rowcount;i++){
							String pz=HgtsPubTool.getStringNullAsTrim(panel.getBillModel("hgts_sendnoticebill_b").getBodyItems());//getValueAt(i, "pz"));					
							if(!"".equals(pz)){					
								String base=" where nvl(dr,0)=0 and kbie='"+kb+"' "
										+" and typegrp='"+pz+"'" 
										+" and sktj='"+pk_busitypeid+"' ";
								String coditions=" select gpprice from hgts_pricepolicy_b "
										+ base
										+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
										+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
										+ " and zxtime is not null "
									//	+ " and pk_org='"+pk_org+"' "
										+ " and substr(zxtime,1,10) <='"+dbilldate.substring(0, 10)+"'"
										+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b "
										+ base+" )";
								String codition=" and pk_cust='"+pk_cust+"' ";
								String orderfiled=" order by zxtime desc ) where rownum=1) ";
								
								String sql=coditions+codition+orderfiled;
								IUAPQueryBS bs=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
								try {
									// 1、客户价格
									List<PricepolicyBVO> list=(List<PricepolicyBVO>) bs.executeQuery(sql, new BeanListProcessor(PricepolicyBVO.class));
									UFDouble gpprice=new UFDouble();
									PricepolicyBVO bvo=null;
									if(null !=list && list.size()>0){
										bvo=list.get(0);
										gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));

									}else{
										// 2、客户分类价格
										String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pk_cust+"')";
										String ssql=coditions+strWhere+orderfiled;
										list=(List<PricepolicyBVO>) bs.executeQuery(ssql, new BeanListProcessor(PricepolicyBVO.class));
										if(null !=list && list.size()>0){
											bvo=list.get(0);
											gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
										}else{
											// 3、公共价格:客户分类为空的
											String s_sql=coditions+" and pk_cust='~' and (def1 is null or def1='~') "+orderfiled;
											list=(List<PricepolicyBVO>) bs.executeQuery(s_sql, new BeanListProcessor(PricepolicyBVO.class));
											if(null !=list && list.size()>0){
												bvo=list.get(0);
												gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
											}else{
												MessageDialog.showHintDlg(null, "提示", "未定义对应的价格政策");
											}
										}
									}
									if(gpprice.doubleValue() !=0){	
										AggSendnoticebillHVO aggvo=(AggSendnoticebillHVO) panel.getBillValueVO(AggSendnoticebillHVO.class.getName(), SendnoticebillHVO.class.getName(), SendnoticebillBVO.class.getName());
										IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
										aggvo=(AggSendnoticebillHVO) col.colPrice(aggvo);
										panel.getBillModel("hgts_sendnoticebill_b").setBodyDataVO(aggvo.getTableVO("hgts_sendnoticebill_b"));
									}else{
										String[] skey=new String[]{"gpprice","fkfsyh","ljyh","qyyh","def13","zxprice","jstotal"};
										for(String key:skey){
											panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, i, key);
										}
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

	
		if(e.getKey().equals("pk_transporttype")){		// 运输方式：汽运、路运
			int rowc=panel.getRowCount();
			BillItem[] items=panel.getBillModel("hgts_sendnoticebill_b").getBodyItems();
			CommQs comm= new CommQs();
			if(e.getValue().equals(HgtsPubConst.TRANSPORT_QY)){
				if(rowc >=1){	
					for(int i=0;i<rowc;i++){				
						String[] skey=new String[]{"startstadion","arrviestadion","carstrong","carnum","cyfee","def11","def12"};
						for(String key:skey){
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, i, key);
						}

						UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(i, "shul"));

						UFDouble zxprice=comm.getZxprice(i, panel);

						UFDouble jstotal=shul.multiply(zxprice);

						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zxprice, i, "zxprice");
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal, i, "jstotal");
					}
				}
				for(int j=0;j<items.length;j++){

					if(items[j].getKey().equals("startstadion") 
							|| items[j].getKey().equals("arrviestadion")
							|| items[j].getKey().equals("carstrong")
							|| items[j].getKey().equals("carnum")
							|| items[j].getKey().equals("cyfee")
							|| items[j].getKey().equals("def11")
							|| items[j].getKey().equals("def12")){
						panel.hideBodyTableCol(items[j].getKey());
					}
				}
			}else if(e.getValue().equals(HgtsPubConst.TRANSPORT_LY)){
				for(int j=0;j<items.length;j++){
					if(items[j].getKey().equals("startstadion") 
							|| items[j].getKey().equals("arrviestadion")
							|| items[j].getKey().equals("carstrong")
							|| items[j].getKey().equals("carnum")
							|| items[j].getKey().equals("cyfee")
							|| items[j].getKey().equals("def11")
							|| items[j].getKey().equals("def12")){
						panel.showBodyTableCol(items[j].getKey());

					}
				}				

				if(rowc >=1){	
					for(int i=0;i<rowc;i++){	
						//panel.getBillModel().setValueAt(null, i, "zcfee"); // 汽车装车费

						// 2018.01.29 add begin
						String[] str=comm.getInfo(pk_cust, kb);
						if(null !=str && str.length>0){
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[0], i, "startstadion");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[1], i, "arrviestadion");
							//	panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[2], i, "cyfee");
						}
						// 2018.01.29 add end

						String pz=HgtsPubTool.getStringNullAsTrim(panel.getBodyValueAt(i, "pz"));
						if(null !=pz && !"".equals(pz)){
						
							// 数量
							UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(i, "shul"));

							// 执行价格
							UFDouble zxprice=comm.getZxprice(i, panel)/*.add(cyfee).add(zyxfee).add(qscfee)*/;

							UFDouble jstotal=shul.multiply(zxprice);
							//panel.getBillModel().setValueAt(zyxfee, i, "def11");	// 专用线费单价
							//panel.getBillModel().setValueAt(qscfee, i, "def12");	// 取送车费单价
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zxprice, i, "zxprice");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal, i, "jstotal");
						}

						// 取 标准车重
						this.calNum(i,panel);
					}
				}
			}
		}

		// 2019年4月19日 去掉部门条件
		if(!"".equals(pk_org) &&  !"".equals(pk_cust) /*&& !"".equals(pk_dept)*/ && !"".equals(pk_balatype)){
			String hpk=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_sendnoticebill").getValueObject());
			String pk_billtype=HgtsPubConst.FHTZD;
			ICustBalanceInfo ic=(ICustBalanceInfo) NCLocator.getInstance().lookup(ICustBalanceInfo.class.getName());
			try {
				UFDouble[] info = ic.getBalanceInfo(hpk, pk_billtype, pk_org, pk_cust, pk_balatype, pk_dept, "2019-04-01"); // TODO
				if(null !=info && info.length>0){			
					// 信用额度
					UFDouble xyed=info[0];
					// 现汇、承兑 余额
					UFDouble zye=info[1];
					// 已占用金额
					UFDouble sendmny=info[2];			
					// 可用额度
					UFDouble balance=info[3];
					// 客户余额
					UFDouble custbalance=info[4];

					if(pk_balatype.equals(HgtsPubConst.paytype_xh)){
						panel.setHeadItem("xhmny", zye);
					}else{
						panel.setHeadItem("cdmny", zye);
					}

					panel.setHeadItem("xyed", xyed);
					panel.setHeadItem("ywzymny", sendmny);
					panel.setHeadItem("kymny", balance);
					panel.setHeadItem("def8", custbalance);
				}
			} catch (BusinessException be) {
				be.printStackTrace();
			}

		}

		if(e.getKey().equals("yfxycode")){
			// 通知单 运费/装卸协议		
			String pk_pactb_yf=HgtsPubTool.getStringNullAsTrim(e.getValue());
			try {
//				PactVO pactVO_yf=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_pact_yf);
//				PactBVO[] pactBVO_yf=(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact_yf+"'");
				//String pk_supplier = new FormulaParseTool().getBsNameByID("hgts_sopact", "pk_supplier", "pk_pact", HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("yfxycode").getValueObject())); 
				//panel.getHeadItem("pk_supplier").setValue(pk_supplier);
				PactBVO pactBVO_yf=(PactBVO) HYPubBO_Client.queryByPrimaryKey(PactBVO.class,pk_pactb_yf );
				if(null !=pactBVO_yf){			
					
					if(null==contcode || "".equals(contcode)){						
						panel.getBillModel("hgts_sendnoticebill_b").addLine();
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(10, 0, "rowno");
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(pactBVO_yf.getAttributeValue("inv"), 0, "pz"); //TODO 煤种手工选择还是自动带出？
					}
					
					int rowcount=panel.getRowCount("pk_xy_b");

					panel.getBillModel("pk_xy_b").addLine();
					String xyvbillno = new FormulaParseTool().getBsNameByID("hgts_sopact", "vbillno", "pk_pact", HgtsPubTool.getStringNullAsTrim(pactBVO_yf.getPrimaryKey())); 

					// 分别对应 协议单据号、运费/装卸类型、运费单价、税率、亏吨比例、亏吨单价、单车亏吨、单车亏吨单价
					panel.getBillModel("pk_xy_b").setValueAt((rowcount+1)*10, rowcount, "rowno");
					panel.getBillModel("pk_xy_b").setValueAt(xyvbillno, rowcount, "xyvbillno");
					panel.getBillModel("pk_xy_b").setValueAt(1, rowcount, "xytype");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("pz"), rowcount, "xypz");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("price"), rowcount, "price");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("rate"), rowcount, "rate");						
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("kdrate"), rowcount, "kdrate");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("kdprice"), rowcount, "kdprice");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("dckd"), rowcount, "dckd");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_yf.getAttributeValue("kdprice2"), rowcount, "kdprice2");


					panel.getBillModel("hgts_sendnoticebill_b").loadLoadRelationItemValue();
					//panel.getBillModel("pk_quality_b").loadLoadRelationItemValue();
					//panel.getBillModel("pk_yzyj_b").loadLoadRelationItemValue();
					panel.getBillModel("pk_xy_b").loadLoadRelationItemValue();
				}

			} catch (UifException e1) {
				e1.printStackTrace();
			}

		}else if(e.getKey().equals("zxxycode")){
			String pk_pactb_zx=HgtsPubTool.getStringNullAsTrim(e.getValue());
			try {
//				PactVO pactVO_zx=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_pact_zx);
//				PactBVO[] pactBVO_zx=(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact_zx+"'");
				PactBVO pactBVO_zx=(PactBVO) HYPubBO_Client.queryByPrimaryKey(PactBVO.class,pk_pactb_zx );
				if(null !=pactBVO_zx ){
					int rowcount=panel.getRowCount("pk_xy_b");
					String xyvbillno = new FormulaParseTool().getBsNameByID("hgts_sopact", "vbillno", "pk_pact", HgtsPubTool.getStringNullAsTrim(pactBVO_zx.getPrimaryKey())); 

					panel.getBillModel("pk_xy_b").addLine();
					String zxpz=HgtsPubTool.getStringNullAsTrim(pactBVO_zx.getAttributeValue("pz"));
					if(!"".equals(zxpz)){
						panel.getBillModel("pk_xy_b").setValueAt(2, rowcount, "xytype");
//						if(HgtsPubConst.PZ_ZCF.equals(zxpz)){ // TODO 记得改成正式环境的pk								
//							panel.getBillModel("pk_xy_b").setValueAt(2, (rowcount), "xytype");
//						}else if(HgtsPubConst.PZ_DMF.equals(zxpz)){
//							panel.getBillModel("pk_xy_b").setValueAt(3, (rowcount), "xytype");
//						}
					}
					// 分别对应 协议单据号、运费/装卸类型、运费单价、税率、亏吨比例、亏吨单价、单车亏吨、单车亏吨单价
					panel.getBillModel("pk_xy_b").setValueAt((rowcount+1)*10, (rowcount), "rowno");
					panel.getBillModel("pk_xy_b").setValueAt(xyvbillno, (rowcount), "xyvbillno");
					panel.getBillModel("pk_xy_b").setValueAt(zxpz, (rowcount), "xypz");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("price"), (rowcount), "price");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("rate"), (rowcount), "rate");						
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("kdrate"), (rowcount), "kdrate");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("kdprice"), (rowcount), "kdprice");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("dckd"), (rowcount), "dckd");
					panel.getBillModel("pk_xy_b").setValueAt(pactBVO_zx.getAttributeValue("kdprice2"), (rowcount), "kdprice2");

					panel.getBillModel("hgts_sendnoticebill_b").loadLoadRelationItemValue();
					//panel.getBillModel("pk_quality_b").loadLoadRelationItemValue();
					//panel.getBillModel("pk_yzyj_b").loadLoadRelationItemValue();
					panel.getBillModel("pk_xy_b").loadLoadRelationItemValue();
				}
			} catch (UifException e1) {
				e1.printStackTrace();
			}
		}


		// 2018-01-29 金额转换成大写
		int rowc=panel.getRowCount();
		if(rowc>0){
			UFDouble mny=UFDouble.ZERO_DBL;
			for(int i=0;i<rowc;i++){
				if(!ValueUtils.getUFBoolean(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(i, "rowcloseflag")).booleanValue()){
					mny = mny.add(HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(i, "jstotal")));
				}
			}

			CommQs comm=new CommQs();
			String dx=comm.mnyToMax(mny);
			panel.setHeadItem("def1", dx); // 大写金额

		}
	}

	public  Object getWeight() {
		Object  ton= "" ;
		IUAPQueryBS queryBS=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try {
			String sql="select standardweight from hgts_weightandbill where billtype = (select pk_billtypeid from bd_billtype where pk_billtypecode='YX04') and nvl(dr,0)=0 ";
			ton=queryBS.executeQuery(sql, new ColumnProcessor());

		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		return ton;

	}

	public void calNum(int row,BillCardPanel panel){
		UFDouble ton =HgtsPubTool.getUFDoubleNullAsZero(this.getWeight());
		panel.setBodyValueAt(ton, row,"carstrong" ); 		//	标准车重
		Object shul = panel.getBodyValueAt(row, "shul");
		if(ton.doubleValue() != 0){			
			UFDouble carnum=HgtsPubTool.getUFDoubleNullAsZero(shul).div(ton);
			String[]str=carnum.toString().split("[.]");
			panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[0], row, "carnum");
		}
	}

}
