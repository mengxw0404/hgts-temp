package nc.impl.hgts;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import nc.bs.hgts.pc.PriceColHelper;
import nc.impl.hgts.pc.LoadPricePolicyImpl;
import nc.itf.hgts.pc.ILoadPricePolicy;
import nc.itf.hgts.pc.IPriceBizData;
import nc.itf.hgts.pc.IPriceCol;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.vo.hgts.pc.PcTransTool;
import nc.vo.hgts.pc.PricePllicyResultVO;
import nc.vo.hgts.pc.PricePolicyPKVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

public class PriceColImpl implements IPriceCol {

	@Override
	public IBill colPrice(IBill bill) throws BusinessException, SQLException {
		// TODO 自动生成的方法存根
		if(bill == null)
			return null;

		ISuperVO head = bill.getParent();
		ISuperVO[] items =null;
		if(bill instanceof AggSendnoticebillHVO){
			//发货通知单
			items = bill.getChildren(SendnoticebillBVO.class);
			bill=sendnoticeQj(bill,head,items);
		
		}else {
			// 合同
			items = bill.getChildren(PactBVO.class);

			bill=contractQj(bill,head,items);

		}
		return bill;
	}
	/**
	 * 发运通知单-取价格政策最新价格
	 * @param bill
	 * @param head
	 * @param items
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public IBill sendnoticeQj(IBill bill,ISuperVO head,ISuperVO[] items) throws BusinessException, SQLException{

		//发货通知单 行  逐行  计算价格
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			SendnoticebillBVO item=(SendnoticebillBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);
			//加载价格政策
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			if(pInfor == null || pInfor.size() == 0)//未查到政策定义
				continue;

			//根据价格政策  进行  价格计算
			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add 测试过程中， 找到了价格政策，但是只定义了量价优惠，其它价格要素未定义
				// 新增行，取价格政策的  一些条件值  变化前有取到数据，变化后，无对应的价格政策，将变化前取的值清空
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){					
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("fkfsyh", null);
					item.setAttributeValue("ljyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("def13", null);				
					item.setAttributeValue("zxprice", null);
					item.setAttributeValue("jstotal", null);
					item.setAttributeValue("srcjgz", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("vnote", null);
					items[j]=item;
				}

				continue;


			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());//优惠价格
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// 付款方式
						// 付款方式优惠
						pay_tzprice=tzprice;
						vnote=vnote+"付款方式("+policy_rowno+")"+pay_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//量价优惠 
						ljyh_tzprice=tzprice;
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// 运输距离
						qyyh_tzprice=tzprice;
						vnote=vnote+"运输距离("+policy_rowno+")"+qyyh_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // 运输方式
						ysfs_tzprice=tzprice;
						vnote=vnote+"运输方式("+policy_rowno+")"+ysfs_tzprice+";";
					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();//价格
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			item.setAttributeValue("fkfsyh", pay_tzprice.abs());
			item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			item.setAttributeValue("def13", ysfs_tzprice.abs());
			// 执行价格
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg).
					sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")));			 		
			//判断当前单据校验方式  
			UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul"));
			if(head.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx) && shul.compareTo(new UFDouble(60)) > 0 ){
				//数量校验 （总量保持不变） 金额 = 数量 * 新单价
				UFDouble jstotal = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")).multiply(zx_price);
				item.setAttributeValue("shul", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")));
				item.setAttributeValue("jstotal", jstotal);
			}else{
				//金额校验 : 数量 =  金额 / 新单价
				UFDouble shul2 = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jstotal")).div(zx_price);
				item.setAttributeValue("shul", shul2);
				item.setAttributeValue("jstotal", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jstotal")));
			}
			item.setAttributeValue("zxprice", zx_price);

			// 2017-9-15 add
			item.setAttributeValue("srcjgz", jgz);

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			vnote="价格来源"+vsourcecode+"价格组"+jgz+";挂牌价"+gpjg+";"+vnote;
			item.setAttributeValue("vnote", vnote);
			items[j]=item;
		}
		bill.setChildren(SendnoticebillBVO.class, items);
		return bill;
	}
	/**
	 * 
	 * @param bill
	 * @param head
	 * @param items
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public IBill contractQj(IBill bill,ISuperVO head,ISuperVO[] items) throws BusinessException, SQLException{
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			PactBVO item=(PactBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);

			//			加载价格政策
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			//未查到政策定义
			if(pInfor == null || pInfor.size() == 0){
				item.setAttributeValue("gpprice", null);
				item.setAttributeValue("price", null);
				item.setAttributeValue("yhprice", null);
				item.setAttributeValue("ysfsyh", null);
				item.setAttributeValue("qyyh", null);
				item.setAttributeValue("norateprice", null);				
				item.setAttributeValue("bratemny", null);
				item.setAttributeValue("bmny", null);
				item.setAttributeValue("bnoratemny", null);				
				item.setAttributeValue("csourceid", null);
				item.setAttributeValue("vsourcecode", null);
				item.setAttributeValue("csourcebid", null);
				item.setAttributeValue("yl1", null);
				items[j]=item;
				continue;
			}

			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
//			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
//			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
//			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
//			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add 测试过程中， 找到了价格政策，但是只定义了量价优惠，其它价格要素未定义
				// 新增行，取价格政策的  一些条件值  变化前有取到数据，变化后，无对应的价格政策，将变化前取的值清空
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){	
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("price", null);
					item.setAttributeValue("yhprice", null);
					item.setAttributeValue("ysfsyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("norateprice", null);				
					item.setAttributeValue("bratemny", null);
					item.setAttributeValue("bmny", null);
					item.setAttributeValue("bnoratemny", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("csourcebid", null);
					item.setAttributeValue("yl1", null);
					items[j]=item;
				}

				continue;

			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
//					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// 付款方式
//						// 付款方式优惠
//						pay_tzprice=tzprice;
//						vnote=vnote+"付款方式("+policy_rowno+")"+pay_tzprice+";";
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//量价优惠 
//						ljyh_tzprice=tzprice;
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// 运输距离
//						qyyh_tzprice=tzprice;
//						vnote=vnote+"运输距离("+policy_rowno+")"+qyyh_tzprice+";";
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // 运输方式
//						ysfs_tzprice=tzprice;
//						vnote=vnote+"运输方式("+policy_rowno+")"+ysfs_tzprice+";";
//					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			//item.setAttributeValue("yhprice", pay_tzprice.abs());
			//item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			//item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			//item.setAttributeValue("ysfsyh", ysfs_tzprice.abs());
			// 执行价格
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg)
					//.sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					//sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					//.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")))
					;
			// 价税合计
			UFDouble ton=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("ton"));
			UFDouble jshj=zx_price.multiply(ton);
			UFDouble rate=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("rate"));
			UFDouble norateprice=gpjg.div(rate.div(100).add(1)).setScale(2, UFDouble.ROUND_HALF_UP);
			UFDouble bnoratemny=(gpjg.div(rate.div(100).add(1)).multiply(ton)).setScale(2, UFDouble.ROUND_HALF_UP);
			UFDouble bratemny=jshj.sub(bnoratemny);

			item.setAttributeValue("price", zx_price);
			item.setAttributeValue("bmny", jshj);
			item.setAttributeValue("norateprice",norateprice );
			item.setAttributeValue("bratemny",bratemny );

			// 2017-9-15 add

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			item.setAttributeValue("csourcebid", bpk);
			vnote="价格来源"+vsourcecode+"价格组"+jgz+";挂牌价"+gpjg+";"+vnote;
			item.setAttributeValue("yl1", vnote);
			items[j]=item;

			bill.setChildren(PactBVO.class, items);

		}
		return bill;
	}


	@Override
	public IBill colNewPrice(IBill bill) throws BusinessException, SQLException {
		// TODO 自动生成的方法存根
		if(bill == null)
			return null;

		ISuperVO head = bill.getParent();
		//发货通知单 行  逐行  计算价格
		ISuperVO[] items = bill.getChildren(SendnoticebillBVO.class);
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			SendnoticebillBVO item=(SendnoticebillBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);
			//加载价格政策
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			if(pInfor == null || pInfor.size() == 0)//未查到政策定义
				continue;

			//根据价格政策  进行  价格计算
			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add 测试过程中， 找到了价格政策，但是只定义了量价优惠，其它价格要素未定义
				// 新增行，取价格政策的  一些条件值  变化前有取到数据，变化后，无对应的价格政策，将变化前取的值清空
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){					
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("fkfsyh", null);
					item.setAttributeValue("ljyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("def13", null);				
					item.setAttributeValue("zxprice", null);
					item.setAttributeValue("jstotal", null);
					item.setAttributeValue("srcjgz", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("vnote", null);
					items[j]=item;
				}

				continue;


			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());//优惠价格
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// 付款方式
						// 付款方式优惠
						pay_tzprice=tzprice;
						vnote=vnote+"付款方式("+policy_rowno+")"+pay_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//量价优惠 
						ljyh_tzprice=tzprice;
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// 运输距离
						qyyh_tzprice=tzprice;
						vnote=vnote+"运输距离("+policy_rowno+")"+qyyh_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // 运输方式
						ysfs_tzprice=tzprice;
						vnote=vnote+"运输方式("+policy_rowno+")"+ysfs_tzprice+";";
					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();//价格
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			item.setAttributeValue("fkfsyh", pay_tzprice.abs());
			item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			item.setAttributeValue("def13", ysfs_tzprice.abs());
			// 执行价格
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg).
					sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")));			 		
			// 金额 = 数量 * 新单价
			UFDouble jstotal = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")).multiply(zx_price);
			item.setAttributeValue("shul", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")));
			item.setAttributeValue("jstotal", jstotal);
			item.setAttributeValue("zxprice", zx_price);

			// 2017-9-15 add
			item.setAttributeValue("srcjgz", jgz);

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			vnote="价格来源"+vsourcecode+"价格组"+jgz+";挂牌价"+gpjg+";"+vnote;
			item.setAttributeValue("vnote", vnote);
			items[j]=item;
		}
		bill.setChildren(SendnoticebillBVO.class, items);

		return bill;
	}

}
