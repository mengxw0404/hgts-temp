package nc.impl.hgts.settle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hgts.pc.PriceColHelper;
import nc.bs.trade.business.HYSuperDMO;
import nc.impl.hgts.pc.LoadPricePolicyImpl;
import nc.impl.hgts.pc.QueryCustMnyImpl;
import nc.itf.hgts.pc.ILoadPricePolicy;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.itf.hgts.settle.IBdInforVOForSettle;
import nc.itf.hgts.settle.INumColResult;
import nc.itf.hgts.settle.IPriceSettle;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hgts.carloadingplan.AggCarloadingplanHVO;
import nc.vo.hgts.carloadingplan.CarloadingplanBVO;
import nc.vo.hgts.carloadingplan.CarloadingplanHVO;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pc.BdInfoVOForSettle;
import nc.vo.hgts.pc.Hj_PriceBizData;
import nc.vo.hgts.pc.NumColResult;
import nc.vo.hgts.pc.PricePllicyResultVO;
import nc.vo.hgts.pc.PricePolicyPKVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class PriceSettleImpl implements IPriceSettle {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null)
			dao = new BaseDAO();
		return dao;
	}

	@Override
	public IBdInforVOForSettle[] loadBDForSettle(String pk_org,String custid, String pk_kb,String invid,
			Object oUserObject) throws BusinessException {
		IBdInforVOForSettle[] settles=null;
		String sql="select pk_cust,pk_kc kb,dbilldate,pk_qualityreport,pz,SUM(jingz),sendnoticebillno pk_sendnoticebill_b "
				+ " from hgts_invoicesheet h inner join hgts_invoicesheet_b b "
				+" on h.pk_invoice=b.pk_invoice "
				+" where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 and vbillstatus=1 "
				+" and h.pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'"
				+ " and (issettle is null or issettle='N') and pk_qualityreport is not null"
				+" and pk_kc='"+pk_kb+"' and pk_cust='"+custid+"'"
				+" and pz='"+invid+"' group by pk_qualityreport,pk_cust,pz,dbilldate,kb,sendnoticebillno  order by pk_qualityreport,dbilldate ,pk_cust";

		List<Map> list=(List<Map>) getDao().executeQuery(sql, new MapListProcessor());
		if(null !=list && list.size()>0){
			settles=new BdInfoVOForSettle [list.size()];

			for(int i=0;i<list.size();i++){
				Map map=list.get(i);
				if(null !=map){
					String pk_cust=HgtsPubTool.getStringNullAsTrim(map.get("pk_cust"));
					String kb=HgtsPubTool.getStringNullAsTrim(map.get("kb"));
					String dbilldate=HgtsPubTool.getStringNullAsTrim(map.get("dbilldate"));
					String pk_qualityreport=HgtsPubTool.getStringNullAsTrim(map.get("pk_qualityreport"));
					String pz=HgtsPubTool.getStringNullAsTrim(map.get("pz"));
					String jingz=HgtsPubTool.getStringNullAsTrim(map.get("jingz"));
					String pk_sendnoticebill_b=HgtsPubTool.getStringNullAsTrim(map.get("pk_sendnoticebill_b"));

					BdInfoVOForSettle settle=new BdInfoVOForSettle();
					settle.setPk_cust(pk_cust);
					settle.setMineid(kb);
					settle.setGbdate(new UFDate(dbilldate));
					settle.setQcbillid(pk_qualityreport);
					settle.setPk_invid(pz);
					settle.setJingz(new UFDouble(jingz));
					settle.setOrderbillbid(pk_sendnoticebill_b);
					settles[i]=settle;
				}
			}
		}
		return settles;
	}

	/**
	 * 查询发过计量单组合量
	 * @return
	 * @throws DAOException 
	 */
	public UFDouble[] getBdnum(IBdInforVOForSettle infor,UFDate bddate,String sDate,UFDate last_date) throws DAOException{
		UFDouble[] num=new UFDouble[3];
		String comm="select SUM(b.jingz) from hgts_invoicesheet h "
				+" inner join hgts_invoicesheet_b b "
				+" on h.pk_invoice=b.pk_invoice" 
				+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 "
				+" and h.vbillstatus=1 "
				+" and h.pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'"
				+" and h.pk_org='"+infor.getOrg()+"'";

		String sumBDSql = comm
				+" and h.pk_cust='"+infor.getCustid()+"' "
				+" and pz='"+infor.getInvid()+"' "
				+" and substr(h.dbilldate,0,10) between '"+sDate.substring(0, 10)+"' "
				+ " and '"+last_date.toString().substring(0, 10)+"'";

		
		UFDouble sumBdJz =HgtsPubTool.getUFDoubleNullAsZero(getDao().executeQuery(sumBDSql, new ColumnProcessor()));
		num[0]=sumBdJz; // 磅单净重
		//num[1]=sumZhlj; // 组合累计
		//num[2]=sumZhgl; // 折干量累计
		return num;
	}

	/**
	 * 查询装车计划组合量
	 * @param infor
	 * @param bddate
	 * @param sDate
	 * @param last_date
	 * @return
	 * @throws DAOException
	 */
	public UFDouble[] getCarloadingnum(IBdInforVOForSettle infor,UFDate bddate,String sDate,UFDate last_date) throws DAOException{
		UFDouble[] num=new UFDouble[3];
		// ------------------	  装车计划组合过磅量     -------------
		String comm="select SUM(b.jsweight) from hgts_carloadingplan h "
				+" inner join hgts_carloadingplan_b b "
				+" on h.pk_carloading=b.pk_carloading" 
				+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 "
				+" and h.vbillstatus=1 "
				+" and h.pk_org='"+infor.getOrg()+"'";

		String sumBDSql = comm
				+" and b.pk_customer='"+infor.getCustid()+"' "
				+" and pk_mz='"+infor.getInvid()+"' "
				+" and substr(h.dbilldate,0,10) between '"+sDate.substring(0, 10)+"' "
				+ " and '"+last_date.toString().substring(0, 10)+"'";

		

		UFDouble sumBdJz =HgtsPubTool.getUFDoubleNullAsZero(getDao().executeQuery(sumBDSql, new ColumnProcessor()));

		num[0]=sumBdJz;
	
		return num;
	}
	/**
	 * @param infor:磅单信息
	 * @param dbusidate:当前结算日期
	 */
	@Override
	public INumColResult numCol(IBdInforVOForSettle infor, UFDate dbusidate,Object isks)
			throws BusinessException {
		UFDate bddate=infor.getGBDate();
		//String pk_cust=infor.getCustid();

		// TODO 2019年3月7日  应该没有组合这种情况
		//checkCustZh(pk_cust,bddate);

		String bd_date=bddate.toString().substring(0, 7);
		String busidate= dbusidate.toString().substring(0, 7);

		UFDate last_date=null;
		if(bd_date.equals(busidate)){
			// 如果磅单日期小于当前结算日期，取结算日期，否则取磅单日期
			if(bddate.before(dbusidate)){				
				last_date=dbusidate;
			}else{
				last_date=bddate;
			}
		}else{
			// 不相等，直接取磅单日期 中的 月份的 最后一天
			String month = bddate.toString().trim().substring(5, 7);
			String year = bddate.toString().trim().substring(0, 4);
			int days=UFDate.getDaysMonth(Integer.parseInt(year), Integer.parseInt(month));
			last_date=UFDate.getDate(bddate.toString().substring(0, 7)+"-"+days);
		}

		String sDate=last_date.toString().substring(0, 7)+"-01";

		String sql="select b.prjcode,b.zjrst,b.custzjrst "
				+" from hgts_qualityreport h  inner join hgts_qualityreport_b b"
				+" on h.pk_qualityreport=b.pk_qualityreport"
				+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 and h.vbillno='"+infor.getQcBillID()+"'";

		UFDouble sumBdJz = UFDouble.ZERO_DBL;
		//UFDouble sumZhlj = UFDouble.ZERO_DBL;
		//UFDouble sumZhgl = UFDouble.ZERO_DBL; 
		UFDouble[] comm_num=getBdnum(infor, bddate, sDate, last_date);
		if(null !=comm_num && comm_num.length>0){
			sumBdJz=HgtsPubTool.getUFDoubleNullAsZero(comm_num[0]);
			// 2019年3月7日
			//sumZhlj=HgtsPubTool.getUFDoubleNullAsZero(comm_num[1]);
			//sumZhgl=HgtsPubTool.getUFDoubleNullAsZero(comm_num[2]);
		}

		UFDouble[] num=getCarloadingnum(infor, bddate, sDate, last_date);
		if(null !=num && num.length>0){
			sumBdJz=sumBdJz.add(HgtsPubTool.getUFDoubleNullAsZero(num[0]));
			// 2019年3月7日
			/*sumZhlj=sumZhlj.add(HgtsPubTool.getUFDoubleNullAsZero(num[1]));
			sumZhgl=sumZhgl.add(HgtsPubTool.getUFDoubleNullAsZero(num[2]));*/
		}
		// 2019年3月7日
		//	UFDouble grpzgllj=new PriceSettleBo().getSumGrpZhegl(infor, bddate, sDate, last_date, HgtsPubConst.XSHJD_QY);

		List list = (List) getDao().executeQuery(sql, new ArrayListProcessor());
		NumColResult numResult = new NumColResult();
		if(null !=list && list.size()>0){	
			for(int i = 0;i<list.size();i++){
				Object[] results =  (Object[]) list.get(i);
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.HF)){

					numResult.setHuif(HgtsPubTool.getUFDoubleNullAsZero(results[1]));	
					numResult.setCusthf(HgtsPubTool.getUFDoubleNullAsZero(results[2]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.SF)){
					numResult.setShuif(HgtsPubTool.getUFDoubleNullAsZero(results[1]));	

					numResult.setCustsf(HgtsPubTool.getUFDoubleNullAsZero(results[2]));	
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.FRL)){
					numResult.setFarl(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.WHJHFF)){
					numResult.setWuhjhff(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.GZJLF)){
					numResult.setGanzjlf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.NSF)){
					numResult.setNeisf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.LF)){
					numResult.setLiuf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.HFF)){
					numResult.setHuiff(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				// 2018-8-13 查询该煤种是否进行折干 : 物料-集团 档案上：助促销品 字段：用来标记是否需要进行折干，打上对勾，不需要进行折干
				UFDouble zhegl= UFDouble.ZERO_DBL;
				UFDouble custzhegl=UFDouble.ZERO_DBL;
				//String sql_s="select ishproitems from bd_material_v where nvl(dr,0)=0 and pk_material='"+infor.getInvid()+"' ";
				//String ishproitems=HgtsPubTool.getStringNullAsTrim(this.getDao().executeQuery(sql_s, new ColumnProcessor()));
				//if(null !=ishproitems && !"".equals(ishproitems) && "Y".equals(ishproitems)){
				if(null ==isks || (null !=isks && "N".equals(isks.toString()))){
					// 折干量=净重
					zhegl =infor.getNum();
					custzhegl=infor.getCustNum();
				}else{					
					zhegl = (new UFDouble(100).sub(HgtsPubTool.getUFDoubleNullAsZero(numResult.getShuif()))).div(100-8).multiply(infor.getNum());
					custzhegl= (new UFDouble(100).sub(HgtsPubTool.getUFDoubleNullAsZero(numResult.getCustsf()))).div(100-8).multiply(infor.getCustNum());
				}				

				numResult.setZhegl(zhegl);
				numResult.setCustzhegl(custzhegl);
				// 2019年3月7日
				//numResult.setZhegllj(sumZhgl.add(zhegl));				
				//numResult.setGrpzgllj(grpzgllj.add(zhegl));
			}

			// 2017-9-21
			// 2019年3月7日
			//numResult.setDyjslj(sumBdJz);
			//numResult.setZdyjslj(sumZhlj); // 客户组合 或 煤种组合


		}else{
			// 没有质检报告
			UFDouble zhegl =infor.getNum();
			numResult.setZhegl(zhegl);

			UFDouble custzhegl=infor.getCustNum();
			numResult.setCustzhegl(custzhegl);

			// 2019年3月7日
			/*numResult.setZhegllj(sumZhgl.add(zhegl));
			numResult.setDyjslj(sumBdJz);
			numResult.setZdyjslj(sumZhlj); // 客户组合 或 煤种组合
			numResult.setGrpzgllj(grpzgllj.add(zhegl));*/
		}


		return numResult;
	}

	@Override
	public HjsettleBVO[] priceCol(HjsettleHVO hvo,HjsettleBVO item,boolean ishaveflag) throws BusinessException, SQLException {
		String pk_org=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_org"));
		String pk_cust=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_cust"));
		String pk_busitype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_busitype"));
		String pk_dept=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_dept"));
		String pk_kb=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("kb"));
		String pk_mz=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("mz"));
		String pk_balatype=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("pk_balatype"));
		PricePolicyPKVO pkey = new PricePolicyPKVO();
		pkey.setPk_org(pk_org);
		pkey.setPk_cust(pk_cust);
		pkey.setPk_busitype(pk_busitype);
		pkey.setPk_mine(pk_kb);
		pkey.setPk_invtype(pk_mz);
		pkey.setBizdate(new UFDate(HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("gbdate"))));
		//			加载价格政策
		ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
		Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);
		if(null==pInfor || pInfor.size() == 0){
			throw new BusinessException("未定义对应的价格政策");
		}
		UFDouble gpjg=UFDouble.ZERO_DBL;		
		for(Integer key:pInfor.keySet()){
			List<IPricePolicyVO> list=pInfor.get(key);
			gpjg=list.get(0).getGpPrice();
			break;
		}

		Hj_PriceBizData data=new Hj_PriceBizData();
		data.setPk_cust(pk_cust);
		data.setPk_kc(pk_kb);
		data.setPk_invid(pk_mz);
		data.setPk_billtype(HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_billtype")));
		data.setPk_busitype(pk_busitype);
		data.setCurnum(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jz")));
		data.setGpjg(gpjg);
		// 2017-9-5 取离过磅日期最近的
		data.setDbilldate(new UFDate(HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("gbdate"))));
		//data.setDbilldate(new UFDate(HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("dbilldate"))));
		data.setZlzb(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("huif")));

		// 根据价格政策 计算价格
		List<PricePllicyResultVO> lprice=PriceColHelper.col(pInfor, data);
		UFDouble pay_tzprice=UFDouble.ZERO_DBL;
		//UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
		UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
		UFDouble zlzb_tzprice=UFDouble.ZERO_DBL;
		String pk_policy_b="";
		FormulaParseTool tool=new FormulaParseTool();
		String policy_rowno="";
		String note="付款方式(";
		String note2="运输距离(";
		String note3="质量指标(";
		for(int i=0;i<lprice.size();i++){
			PricePllicyResultVO rstVO=lprice.get(i);
			PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
			UFDouble tzprice=rstVO.getNadprice();
			String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
			pk_policy_b=rstVO.getPk_pricepolicy();
			policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", pk_policy_b);
			if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// 付款方式
				pay_tzprice=tzprice;
				note+=policy_rowno+")";
			}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//量价优惠 
				//ljyh_tzprice=tzprice; // 计算价格的时候不考虑量价优惠
			}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// 运输距离
				qyyh_tzprice=tzprice;
				note2+=policy_rowno+")";
			}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_qulityindex){   //质量指标
				zlzb_tzprice=tzprice;
				note3+=policy_rowno+")";
			}
		}

		/**
		 * 客户档案：客户现汇应收余额为负数时（如 -10000），说明客户有余额，去掉“负号”（即其绝对值）后，与 结算金额进行比较，如果小于结算金额，进行拆行
		 * 		  客户现汇应收余额不为负数（即>=0，如10000），说明客户没有余额，无需拆行，按 承兑计算
		 */
		//1、客户 现汇应收余额>=0，按承兑;2、客户余额>0且<价格政策金额 ，进行拆行：
		QueryCustMnyImpl impl=new QueryCustMnyImpl();
		UFDouble[] mnys=impl.getCustMny(pk_cust, null, null,pk_dept);

		// 价格政策价格
		UFDouble jgzc_price=UFDouble.ZERO_DBL;		

		if(HgtsPubConst.biztype_ys.equals(pk_busitype)){
			if(mnys[0].doubleValue()>=0){//现汇余额大于等于0，按承兑
				jgzc_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg.sub(pay_tzprice.abs()).sub(qyyh_tzprice.abs()).add(zlzb_tzprice));//质量指标为正值（质量比较好）加上浮动值，否则做减法
				item.setAttributeValue("pk_balatype", HgtsPubConst.paytype_cd);		
				note+=pay_tzprice;
			}else{				
				LoadPricePolicyImpl lppimpl=new LoadPricePolicyImpl();
				PricepolicyBVO[] pbvos=(PricepolicyBVO[]) lppimpl.loadChOneRowPricePolicy(pkey);
				if(null==pbvos || pbvos.length==0){
					throw new BusinessException("未匹配到相应的价格政策");
				}else{
					UFDouble tz=HgtsPubTool.getUFDoubleNullAsZero(pbvos[0].getAttributeValue("jgfd")); // 付款方式： 现汇  价格浮动值
					jgzc_price=gpjg.sub(tz.abs()).sub(qyyh_tzprice.abs()).add(zlzb_tzprice);

					// 2017-9-6 
					pk_policy_b=HgtsPubTool.getStringNullAsTrim(pbvos[0].getAttributeValue("pk_pricepolicy_b"));
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", pk_policy_b);
					note="付款方式(";
					note+=policy_rowno+")"+tz;
				}
			}
		}else{
			jgzc_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg.sub(pay_tzprice.abs()).sub(qyyh_tzprice.abs()).add(zlzb_tzprice));			
			note+=pay_tzprice;
		}

		// 价格政策金额 = 价格政策价格 * 折干量
		UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zhegl")).setScale(3, UFDouble.ROUND_HALF_UP);
		UFDouble jshj=jgzc_price.multiply(zhegl);
		UFDouble jsprice=UFDouble.ZERO_DBL;
		if(zhegl.doubleValue()!=0){			
			jsprice=jshj.div(zhegl);
		}
		item.setAttributeValue("jgzcprice", jgzc_price);
		item.setAttributeValue("jgzcmny", jshj);
		item.setAttributeValue("jsmny", jshj);
		item.setAttributeValue("jsprice", jsprice);


		String jgz=tool.getBsNameByID("hgts_pricepolicy_b", "pricegrp", "pk_pricepolicy_b", pk_policy_b);
		String pk_policy_h=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", pk_policy_b);
		String policy_vbillno=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", pk_policy_h);

		String vnote="价格来源"+policy_vbillno+"价格组"+jgz+";"
				+ "挂牌价"+gpjg+";"
				+ note+";"
				+ note2+qyyh_tzprice+";"
				+ note3+zlzb_tzprice;
		String par_note=note2+qyyh_tzprice+";"+note3+zlzb_tzprice;
		item.setAttributeValue("vnote",vnote );
		HjsettleBVO[] bvos=null;
		if(HgtsPubConst.biztype_ys.equals(pk_busitype)){
			if(null !=mnys && mnys.length>0){			
				UFDouble balance=mnys[0];		
				if(balance.doubleValue()<0){
					if(balance.abs().doubleValue()<jshj.doubleValue()){
						if(ishaveflag){//余额小于0，去掉负号后值小于结算金额，且已经进行拆行操作，不再进行拆行，直接按承兑
							bvos=new HjsettleBVO[1];
							item.setAttributeValue("pk_balatype", HgtsPubConst.paytype_cd);						
							bvos[0]=item;
						}else{		
							// 拆行操作
							bvos= chaih(pk_busitype,pk_balatype,item,balance,pkey,qyyh_tzprice,zlzb_tzprice,par_note);
						}
					}else{
						bvos=new HjsettleBVO[1];
						bvos[0]=item;
					}
				}else{
					// 现汇应收余额 >0 按承兑
					bvos=new HjsettleBVO[1];
					bvos[0]=item;
				}

			}else{
				throw new BusinessException("未定义客户余额信息");
			}

		}else{
			bvos=new HjsettleBVO[1];
			item.setAttributeValue("pk_balatype", HgtsPubConst.paytype_cd);	
			bvos[0]=item;
		}
		return bvos;
	}

	/**
	 * 生成表体行数据之前，后台判断客户.现汇余额是否>=当前行计算所得的‘结算金额’（此时后台计算的结算金额不考虑量价优惠，人工调整！）
	 * 判断价格政策金额 是否大于 客户余额，进行拆行：
	 * 1、余额<结算金额，业务类型为赊销，付款方式为承兑，不拆行
	 * 2、余额<结算金额，
	 * 3、余额<结算金额，
	 * 4、余额<结算金额，业务类型为预收，付款方式为现金，进行拆行
	 * @throws BusinessException 
	 */

	public HjsettleBVO[] chaih(String pk_busitype,String pk_balatype,HjsettleBVO item,UFDouble balance,PricePolicyPKVO pkey,UFDouble qyyh_tzprice,UFDouble zlzb_tzprice,String par_note) throws BusinessException{
		HjsettleBVO[] bvos=null;

		//if(HgtsPubConst.biztype_ys.equals(pk_busitype)){
		if(HgtsPubConst.paytype_xh.equals(pk_balatype)){
			UFDouble jgzc_mny=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jgzcmny"));//价格政策金额
			//if(balance.doubleValue()<jgzc_mny.doubleValue()){
			bvos=new HjsettleBVO[2];
			UFDouble jz=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jz"));

			UFDouble oldyjslj=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("dyjslj"));//已有的当月累计结算量
			UFDouble oldyzhjslj=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("dyzhjslj"));//已有的当月组合累计结算量
			UFDouble nweight=jz;// 净重

			UFDouble shuif=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shuif"));//水分
			UFDouble jgzc_price=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jgzcprice"));//价格政策价格
			UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zhegl")); // 折干量
			//正常算法：折干量 =(100-水分)/(100-8)*净重
			//		 价格政策金额=折干量*价格政策价格
			//		 结算金额=价格政策金额-人工调整-使用上次优惠-本次量价优惠
			//		 结算单价=结算金额/折干量

			// 进行拆行
			//第一行是客户所有的现汇余额，即结算金额=客户.现汇余额，
			//反算（价格政策金额=结算金额=现汇余额，再反算出折干量，根据折干量反算出净重）；第二行即剩余数量按承兑金额结算）
			//第二行即剩余数量按承兑金额结算

			UFDouble js_mny=balance.abs();//结算金额：取余额的绝对值，否则界面上显示负数，导致后续算的结果有误
			jgzc_mny=js_mny;//价格政策金额
			zhegl=jgzc_mny.div(jgzc_price).setScale(3, UFDouble.ROUND_HALF_UP);
			jz=zhegl.div(((new UFDouble(100).sub(shuif)).div(100-8)));
			UFDouble price=UFDouble.ZERO_DBL;
			if(zhegl.doubleValue()!=0){				
				price=js_mny.div(zhegl);//结算单价
			}

			item.setAttributeValue("jz", jz);
			item.setAttributeValue("zhegl", zhegl);
			item.setAttributeValue("jgzcmny", jgzc_mny);
			item.setAttributeValue("jsmny", js_mny);
			item.setAttributeValue("jsprice", price);

			// 进行拆行的第1行的 当月累计结算量=反算后当前行的净重 + （已有的当月累计结算量-反算前的净重）
			item.setAttributeValue("dyjslj", jz.add(oldyjslj.sub(nweight)));
			item.setAttributeValue("dyzhjslj", jz.add(oldyzhjslj).sub(nweight));
			bvos[0]=item;

			// 拆行后的 第二行
			UFDouble nextrow_jz=nweight.sub(jz);

			LoadPricePolicyImpl impl=new LoadPricePolicyImpl();
			PricepolicyBVO[] pbvos=(PricepolicyBVO[]) impl.loadChPricePolicy(pkey);
			if(null==pbvos || pbvos.length==0){
				throw new BusinessException("未匹配到相应的价格政策");
			}else{
				UFDouble gpj=HgtsPubTool.getUFDoubleNullAsZero(pbvos[0].getAttributeValue("gpprice"));
				UFDouble tz=HgtsPubTool.getUFDoubleNullAsZero(pbvos[0].getAttributeValue("jgfd"));//价格浮动值
				jgzc_price=gpj.sub(tz.abs()).sub(qyyh_tzprice.abs()).add(zlzb_tzprice);
				zhegl=((new UFDouble(100).sub(shuif))).div((new UFDouble((100-8)))).multiply(nextrow_jz).setScale(3, UFDouble.ROUND_HALF_UP);				
				jgzc_mny=jgzc_price.multiply(zhegl);
				price=jgzc_mny.div(zhegl);				

				String fytzdh=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("fytzdh"));
				String zjpc=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("zjpc"));
				String gbdate=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("gbdate"));
				UFDouble huif=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("huif"));

				String mzgrp=HgtsPubTool.getStringNullAsTrim(item.getAttributeValue("mzgrp"));
				item=new HjsettleBVO();
				item.setAttributeValue("jz", nextrow_jz);
				item.setAttributeValue("zhegl", zhegl);
				item.setAttributeValue("jgzcprice", jgzc_price);
				item.setAttributeValue("jgzcmny", jgzc_mny);
				item.setAttributeValue("jsmny", jgzc_mny);
				item.setAttributeValue("jsprice", price);

				// 拆行后的第i行 当月累计结算量 = 第i行的净重 + 第 i-1 行的  当月累计结算量
				item.setAttributeValue("dyjslj", nextrow_jz.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("dyjslj"))));
				item.setAttributeValue("dyjslj", nextrow_jz.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("dyzhjslj"))));

				item.setAttributeValue("fytzdh", fytzdh);
				item.setAttributeValue("zjpc", zjpc);
				item.setAttributeValue("gbdate", gbdate);
				item.setAttributeValue("kb", pkey.getPk_mine());
				item.setAttributeValue("mz", pkey.getPk_invtype());
				item.setAttributeValue("mzgrp", mzgrp);
				item.setAttributeValue("huif", huif);
				item.setAttributeValue("shuif", shuif);
				item.setAttributeValue("pk_balatype", HgtsPubConst.paytype_cd);

				// 2017-9-6 
				String pk_policy_b=HgtsPubTool.getStringNullAsTrim(pbvos[0].getAttributeValue("pk_pricepolicy_b"));
				FormulaParseTool tool=new FormulaParseTool();
				String pk_policy_h=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", pk_policy_b);
				String policy_vbillno=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", pk_policy_h);
				String jgz=tool.getBsNameByID("hgts_pricepolicy_b", "pricegrp", "pk_pricepolicy_b", pk_policy_b);
				String policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", pk_policy_b);
				String vnote="价格来源"+policy_vbillno+"价格组"+jgz+"挂牌价"+gpj+";付款方式("+policy_rowno+")"+tz+";"+par_note;
				item.setAttributeValue("vnote", vnote);
				//s_pk_balatype=HgtsPubConst.paytype_cd;

				bvos[1]=item;
			}					
			/*}else{
				bvos=new HjsettleBVO[1];
				bvos[0]=item;
			}*/
		}else{
			bvos=new HjsettleBVO[1];
			bvos[0]=item;
		}
		/*}else{
			bvos=new HjsettleBVO[1];
			bvos[0]=item;
		}*/
		return bvos;
	}

	/**
	 * 参照过磅单查询
	 * @param hstr
	 * @param bstr
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public AggInvoicesheetHVO[] getAggvos(String[] str) throws DAOException, ClassNotFoundException{
		AggInvoicesheetHVO[] aggvos=null;
	
		UFBoolean isNeedZj=this.isNeddZj(str[2]);
		
		HYSuperDMO dmo=new HYSuperDMO();
		
		String sql=" nvl(dr,0)=0 and vbillstatus=1 and (issettle is null or issettle='N')"
				+" and pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'"
				+" and (def3 is null or def3 ='')" // 未作废
				+" and (isyy is null or isyy='N') "				
				+str[0].toString()
				+" and pk_invoice in (select pk_invoice from hgts_invoicesheet_b where nvl(dr,0)=0 "
				+str[1].toString()+") "	;
		if(!isNeedZj.booleanValue()){ // 无需质检
			sql=sql+" order by dbilldate";
		}else{
			sql=sql+" and pk_qualityreport is not null "
					+ " and pk_qualityreport in (select vbillno from hgts_qualityreport where nvl(dr,0)=0 "
					+ " and vbillstatus=1 ) "
					+ " order by dbilldate";
		}

		InvoicesheetHVO[] hvos=(InvoicesheetHVO[])dmo.queryByWhereClause(InvoicesheetHVO.class, sql);
		if(null !=hvos && hvos.length>0){
			String b_sql="";
			aggvos=new AggInvoicesheetHVO[hvos.length];
			for(int i=0;i<hvos.length;i++){
				InvoicesheetHVO hvo=hvos[i];
				aggvos[i]=new AggInvoicesheetHVO();
				aggvos[i].setParent(hvo);
				b_sql = " nvl(dr,0)=0 and pk_invoice = '" +hvo.getAttributeValue("pk_invoice") + "' ";
				InvoicesheetBVO[]bvos=(InvoicesheetBVO[]) dmo.queryByWhereClause(InvoicesheetBVO.class, b_sql);
				if(null !=bvos && bvos.length>0){					
					aggvos[i].setChildrenVO(bvos);
				}
			}
		}
		return aggvos;
	}

	@Override
	public void wrtBackBd(String hpks, String bpk,String[] zjno,String actiontype) throws Exception {
		if(null==hpks || "".equals(hpks)){
			return;
		}
		String sql="";
		if(actiontype.equals("SAVEBASE")){
			sql="update hgts_invoicesheet set issettle='Y',isyy='Y',hjsettlebillno='"+zjno[0]+"' "
					+ " where nvl(dr,0)=0 "
					//+ " and pk_invoice in("+hpks+")"
					+ " and vbillno in ("+hpks+")";
		}else if(actiontype.equals("DELETE")){
			sql="update hgts_invoicesheet set issettle='N',isyy='N',hjsettlebillno='' "
					+ " where nvl(dr,0)=0 "
					// + " and pk_invoice in("+hpks+")"
					+ " and hjsettlebillno='"+zjno[0]+"'";
		}
		getDao().executeUpdate(sql);

	}

	/**
	 * 校验该客户在有效日期范围内是否存在多个组合里，如果有多个，不继续后续操作
	 * @param pk_cust
	 * @param bddate
	 * @throws DAOException 
	 */
	public void checkCustZh(String pk_cust,UFDate bddate) throws BusinessException{

		String sql="select count(*) from hgts_clientgrouph "
				+" where nvl(dr, 0) = 0 "
				+" and vbillstatus=1   " 
				+" and state = 'Y' "
				+" and id in(select distinct id from hgts_clientgroupb where nvl(dr,0)=0 "
				+" and code='"+pk_cust+"')"
				+" and ('"+bddate.toString().substring(0, 10)+"'>=begindate and '"+bddate.toString().substring(0, 10)+"' <=enddate)";
		int num=getDao().executeQuery(sql, new ColumnProcessor())==null?0:(int)getDao().executeQuery(sql, new ColumnProcessor());
		if(num>=2){
			throw new BusinessException("当前客户在有效执行日期范围内，存在多个组合，请处理");
		}
	}

	@Override
	public AggCarloadingplanHVO[] getAggCarloadingplanVOs(String[] str)
			throws DAOException, ClassNotFoundException {

		AggCarloadingplanHVO[] aggvos=null;
		//String strwhere=str[1].substring(str[1].length()-28, str[1].length());

		// 2018-8-29  物料档案上 “是否赠品”打上对勾，无需进行折干，无需质检，可以被查询出来
		//String strwhere2=strwhere.replaceAll("pk_mz", "pk_material");
		//String sql_s="select ishproitems from bd_material_v where nvl(dr,0)=0 and "+strwhere2;
		//String ishproitems=HgtsPubTool.getStringNullAsTrim(this.getDao().executeQuery(sql_s, new ColumnProcessor()));

		//2017-10-11  判断该煤种是否定义了质检方案：1、没有质检方案，可以被查询出来；2、有质检方案，查询有质检报告且质检报告为审批通过的
		//strwhere=strwhere.replaceAll("pk_mz", "variety");
		//String s_sql=" nvl(dr,0)=0 and "+strwhere;
		
		UFBoolean isNeedZj=this.isNeddZj(str[2]);
		
		
		HYSuperDMO dmo=new HYSuperDMO();
		//QualityprojectVO[] prjVO=(QualityprojectVO[]) dmo.queryByWhereClause(QualityprojectVO.class,s_sql);
		String sql=" nvl(dr,0)=0 and vbillstatus=1 "
				+str[0].toString()
				+" and pk_carloading in (select pk_carloading from hgts_carloadingplan_b where nvl(dr,0)=0 "
				+str[1].toString()	;

		if(/*(null !=ishproitems && !"".equals(ishproitems) && "Y".equals(ishproitems))
				||*/ //(null==prjVO || prjVO.length==0)){
				!isNeedZj.booleanValue()){
			sql=sql+" and nvl(def11,'N')='N' ) order by dbilldate";
			CarloadingplanHVO[] hvos=(CarloadingplanHVO[])dmo.queryByWhereClause(CarloadingplanHVO.class, sql);
			if(null !=hvos && hvos.length>0){
				String b_sql="";
				aggvos=new AggCarloadingplanHVO[hvos.length];
				for(int i=0;i<hvos.length;i++){
					CarloadingplanHVO hvo=hvos[i];
					aggvos[i]=new AggCarloadingplanHVO();
					aggvos[i].setParent(hvo);
					b_sql = " nvl(dr,0)=0 and nvl(def11,'N')='N' and pk_carloading = '" +hvo.getAttributeValue("pk_carloading") + "' order by rowno ";
					CarloadingplanBVO[]bvos=(CarloadingplanBVO[]) dmo.queryByWhereClause(CarloadingplanBVO.class, b_sql);
					if(null !=bvos && bvos.length>0){					
						aggvos[i].setChildrenVO(bvos);
					}
				}
			}
		}else{
			// 有质检方案
			sql=sql+ " and nvl(def11,'N')='N' "
					+" and def1 is not null "  // 质检报告单号
					+ " and def1 in (select vbillno from hgts_qualityreport where nvl(dr,0)=0 "
					+ " and vbillstatus=1 ) "
					+ " ) order by dbilldate";
			CarloadingplanHVO[] hvos=(CarloadingplanHVO[])dmo.queryByWhereClause(CarloadingplanHVO.class, sql);
			if(null !=hvos && hvos.length>0){
				String b_sql="";
				aggvos=new AggCarloadingplanHVO[hvos.length];
				for(int i=0;i<hvos.length;i++){
					CarloadingplanHVO hvo=hvos[i];
					aggvos[i]=new AggCarloadingplanHVO();
					aggvos[i].setParent(hvo);
					b_sql = " nvl(dr,0)=0 and pk_carloading = '" +hvo.getAttributeValue("pk_carloading") + "' "
							+ " and nvl(def11,'N')='N' "
							+ " and def1 is not null "
							+ " and def1 in (select vbillno from hgts_qualityreport where nvl(dr,0)=0 "
							+ " and vbillstatus=1 ) "
							+ " order by rowno ";
					CarloadingplanBVO[]bvos=(CarloadingplanBVO[]) dmo.queryByWhereClause(CarloadingplanBVO.class, b_sql);
					if(null !=bvos && bvos.length>0){					
						aggvos[i].setChildrenVO(bvos);
					}
				}
			}
		}

		return aggvos;

	}

	@Override
	public INumColResult numColByLy(IBdInforVOForSettle infor, UFDate dbusidate,Object isks)
			throws BusinessException {

		UFDate bddate=infor.getGBDate();
		//String pk_cust=infor.getCustid();

		// TODO 福山应该没有组合情况 2019年3月7日 
		//checkCustZh(pk_cust,bddate);

		String bd_date=bddate.toString().substring(0, 7);
		String busidate= dbusidate.toString().substring(0, 7);

		UFDate last_date=null;
		if(bd_date.equals(busidate)){
			// 如果磅单日期小于当前结算日期，取结算日期，否则取磅单日期
			if(bddate.before(dbusidate)){				
				last_date=dbusidate;
			}else{
				last_date=bddate;
			}
		}else{
			// 不相等，直接取磅单日期 中的 月份的 最后一天
			String month = bddate.toString().trim().substring(5, 7);
			String year = bddate.toString().trim().substring(0, 4);
			int days=UFDate.getDaysMonth(Integer.parseInt(year), Integer.parseInt(month));
			last_date=UFDate.getDate(bddate.toString().substring(0, 7)+"-"+days);
		}

		String sDate=last_date.toString().substring(0, 7)+"-01";

		String sql="select b.prjcode,b.zjrst,b.custzjrst"
				+" from hgts_qualityreport h  inner join hgts_qualityreport_b b"
				+" on h.pk_qualityreport=b.pk_qualityreport"
				+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 and  h.vbillno='"+infor.getQcBillID()+"'";

		UFDouble sumBdJz = UFDouble.ZERO_DBL;
		//UFDouble sumZhlj = UFDouble.ZERO_DBL;
		//UFDouble sumZhgl = UFDouble.ZERO_DBL; 
		UFDouble[] comm_num=getCarloadingnum(infor, bddate, sDate, last_date);
		if(null !=comm_num && comm_num.length>0){
			sumBdJz=HgtsPubTool.getUFDoubleNullAsZero(comm_num[0]);
			// TODO 福山应该没有组合情况 2019年3月7日 
			/*sumZhlj=HgtsPubTool.getUFDoubleNullAsZero(comm_num[1]);
			sumZhgl=HgtsPubTool.getUFDoubleNullAsZero(comm_num[2]);*/
		}

		UFDouble[] num=getBdnum(infor, bddate, sDate, last_date);
		if(null !=num && num.length>0){
			sumBdJz=sumBdJz.add(HgtsPubTool.getUFDoubleNullAsZero(num[0]));
			// TODO 福山应该没有组合情况 2019年3月7日 
			/*sumZhlj=sumZhlj.add(HgtsPubTool.getUFDoubleNullAsZero(num[1]));
			sumZhgl=sumZhgl.add(HgtsPubTool.getUFDoubleNullAsZero(num[2]));*/
		}


		//	UFDouble grpzgllj=new PriceSettleBo().getSumGrpZhegl(infor, bddate, sDate, last_date, HgtsPubConst.XSHJD_LY);

		List list = (List) getDao().executeQuery(sql, new ArrayListProcessor());
		NumColResult numResult = new NumColResult();
		if(null !=list && list.size()>0){	
			for(int i = 0;i<list.size();i++){
				Object[] results =  (Object[]) list.get(i);
				if(results[0].equals(HgtsPubConst.HF)){
					numResult.setHuif(HgtsPubTool.getUFDoubleNullAsZero(results[1]));	
				}
				if(results[0].equals(HgtsPubConst.SF)){
					numResult.setShuif(HgtsPubTool.getUFDoubleNullAsZero(results[1]));	
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.FRL)){
					numResult.setFarl(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.WHJHFF)){
					numResult.setWuhjhff(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.GZJLF)){
					numResult.setGanzjlf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.NSF)){
					numResult.setNeisf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.LF)){
					numResult.setLiuf(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				if(HgtsPubTool.getStringNullAsTrim(results[0]).equals(HgtsPubConst.HFF)){
					numResult.setHuiff(HgtsPubTool.getUFDoubleNullAsZero(results[1]));
				}
				// 2018-8-13 查询该煤种是否进行折干 : 物料-集团 档案上：助促销品 字段：用来标记是否需要进行折干，打上对勾，不需要进行折干
				UFDouble zhegl= UFDouble.ZERO_DBL;
				UFDouble custzhegl=UFDouble.ZERO_DBL;
				if(null ==isks || (null !=isks && "N".equals(isks.toString()))){
					// 折干量=净重
					zhegl =infor.getNum();
					custzhegl =infor.getCustNum();
				}else{					
					zhegl = (new UFDouble(100).sub(HgtsPubTool.getUFDoubleNullAsZero(numResult.getShuif()))).div(100-8).multiply(infor.getNum());
					custzhegl= (new UFDouble(100).sub(HgtsPubTool.getUFDoubleNullAsZero(results[2]))).div(100-8).multiply(infor.getCustNum());
				}

				numResult.setZhegl(zhegl);
				numResult.setCustzhegl(custzhegl);

			}

		}else{
			// 没有质检报告：折干量=净重
			UFDouble zhegl =infor.getNum();
			UFDouble custzhegl=infor.getCustNum();			
			numResult.setZhegl(zhegl);			
			numResult.setCustzhegl(custzhegl);
		}

		return numResult;

	}
	/**
	 * 2019年3月11日
	 * 运费结算：获取磅单数据
	 * @param Object[]:长度固定为2，[0]:协议运费单价；[1]:磅单vo数组
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	@Override
	public Object[] getAggvos_yfee(String[] str)
			throws DAOException, ClassNotFoundException {
		Object[] obj=new Object[2];
		AggInvoicesheetHVO[] aggvos=null;
		String condition=str[0];
		String swhere=str[1];

		HYSuperDMO dmo=new HYSuperDMO();
		//PactVO pactVO=(PactVO) dmo.queryByPrimaryKey(PactVO.class, condition);
		PactVO[] pactVOs=(PactVO[]) dmo.queryByWhereClause(PactVO.class, condition);
		if(null !=pactVOs && pactVOs.length>0){
			List<AggInvoicesheetHVO> list=new ArrayList<AggInvoicesheetHVO>();

			UFDouble price=UFDouble.ZERO_DBL;
			for(int i=0;i<pactVOs.length;i++){				
				String	pk_org=HgtsPubTool.getStringNullAsTrim(pactVOs[i].getAttributeValue("pk_org"));
				// 合同协议子表
				PactBVO[] pactBVOS=(PactBVO[])dmo.queryByWhereClause(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pactVOs[i].getPrimaryKey()+"'");
				String pk_material="";
				String pk_mine="";
				//String pk_cust="";
				if(null !=pactBVOS && pactBVOS.length>0){
					PactBVO bvo=pactBVOS[0];
					price=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("price"));
					pk_material=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("inv"));
					pk_mine=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("kuang"));
					//pk_cust=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("cust"));
				}

				InvoicesheetHVO[] hvos=(InvoicesheetHVO[])dmo.queryByWhereClause(InvoicesheetHVO.class, 
						swhere+" and pk_org='"+pk_org+"' "
								+" and pk_kc='"+pk_mine+"'"
								//  +" and pk_cust='"+pk_cust+"'"
								+" and nvl(def11,'N')='N'"); // 未被引用

				if(null !=hvos && hvos.length>0){
					String b_sql="";
					for(int k=0;k<hvos.length;k++){
						InvoicesheetHVO hvo=hvos[k];
						b_sql = " nvl(dr,0)=0 and pz='"+pk_material+"' and pk_invoice = '" +hvo.getAttributeValue("pk_invoice") + "' ";
						InvoicesheetBVO[]bvos=(InvoicesheetBVO[]) dmo.queryByWhereClause(InvoicesheetBVO.class, b_sql);
						if(null !=bvos && bvos.length>0){	
							AggInvoicesheetHVO aggvo=new AggInvoicesheetHVO();
							aggvo.setParent(hvo);
							aggvo.setChildrenVO(bvos);
							list.add(aggvo);
						}
					}
				}
			}
			aggvos=null==list || list.size()==0?null:list.toArray(new AggInvoicesheetHVO[0]);
			obj[0]=price; // TODO 这里的价格不是唯一的，后面不能直接用此值，得重新查
			obj[1]=aggvos;
		}
		return obj;
	}

	@Override
	public AggHjsettleHVO[] getYfSettAggvos(String[] str) throws DAOException,
	ClassNotFoundException {
		AggHjsettleHVO[] aggvos=null;
		String h_sql=str[0];
		String b_sql=str[1];
		HYSuperDMO dmo=new HYSuperDMO();
		HjsettleHVO[] hvos=(HjsettleHVO[]) dmo.queryByWhereClause(HjsettleHVO.class, h_sql);
		if(null !=hvos && hvos.length>0){
			aggvos=new AggHjsettleHVO[hvos.length];
			String sql_b="";
			for(int i=0;i<hvos.length;i++){
				HjsettleHVO hvo=hvos[i];
				aggvos[i]=new AggHjsettleHVO();
				aggvos[i].setParent(hvo);
				sql_b=b_sql+" and pk_hjsettle='"+hvo.getPrimaryKey()+"'";
				HjsettleBVO[] bvos=(HjsettleBVO[]) dmo.queryByWhereClause(HjsettleBVO.class,sql_b);
				if(null !=bvos && bvos.length>0){					
					aggvos[i].setChildrenVO(bvos);
				}
			}
		}
		return aggvos;
	}

	@Override
	public AggHjsettleHVO[] getSettAggvos(String[] str)
			throws BusinessException {
		String h_sql=str[0];
		String b_sql=str[1];
		//String jshzkj=str[2];
		HYSuperDMO dmo=new HYSuperDMO();
		HjsettleHVO[] hvos=(HjsettleHVO[]) dmo.queryByWhereClause(HjsettleHVO.class, h_sql);
		List<AggHjsettleHVO> list=new ArrayList<AggHjsettleHVO>();
		if(null !=hvos && hvos.length>0){
			String sql_b="";
			for(int i=0;i<hvos.length;i++){
				HjsettleHVO hvo=hvos[i];
				sql_b=b_sql+" and pk_hjsettle='"+hvo.getPrimaryKey()+"'";
				HjsettleBVO[] bvos=(HjsettleBVO[]) dmo.queryByWhereClause(HjsettleBVO.class,sql_b);
				if(null !=bvos && bvos.length>0){	
					AggHjsettleHVO aggvo=new AggHjsettleHVO();
					aggvo.setParentVO(hvo);
					aggvo.setChildrenVO(bvos);
					list.add(aggvo);
				}
			}
		}
		return null==list|| list.size()==0?null:list.toArray(new AggHjsettleHVO[0] );
	}
	
	/**
	 * 查询该户是否需要做质检
	 *  零售门店（  isretailstore ） 
	 * @param pk_cust
	 * @return
	 */
	public UFBoolean isNeddZj(String pk_cust){
		String sql="select isretailstore from bd_customer where nvl(dr,0)=0 and pk_customer='"+pk_cust+"' ";
		try {
			String isretailstore=HgtsPubTool.getStringNullAsTrim(this.getDao().executeQuery(sql, new ColumnProcessor()));
			return "".equals(isretailstore)|| "N".equals(isretailstore)?UFBoolean.FALSE:UFBoolean.TRUE;
		} catch (DAOException e) {
			
			e.printStackTrace();
		}
		return UFBoolean.FALSE;
	}

	@Override
	public PactBVO getZXPactB(Object pk_org, Object kuangc, Object transport,String pk_zxxy)
			throws BusinessException {
		try {
			StringBuffer sql=new StringBuffer();
			sql.append(" pk_pact_b in (select hgts_pact_b.pk_pact_b  from hgts_pact_b ");
			sql.append("  inner join hgts_sopact   ");
			sql.append(" on hgts_pact_b.pk_pact = hgts_sopact.pk_pact ");
			sql.append(" where hgts_sopact.approvestatus = '1' ");
			sql.append(" and hgts_sopact.pk_billtypeid='"+HgtsPubConst.CONTRACT_ZXXY+"' ");// 合同-装卸协议
			//先查询装卸费主键的信息.
			HYSuperDMO dmo=new HYSuperDMO();
			String ssql = sql.toString()+"and hgts_pact_b.pk_pact_b='"+pk_zxxy+"')";
			PactBVO[] bvos=(PactBVO[]) dmo.queryByWhereClause(PactBVO.class, ssql);
			if(null!=bvos && bvos.length>0){
				return bvos[0];
			}
			//不存在信息，则按照运输方式+矿场+组织的条件查询
			else{
				sql.append(" and hgts_sopact.transport='"+transport.toString()+"' ");//运输方式
				//sql.append("  and hgts_pact_b.kuang ='"+null!=kuangc ? kuangc.toString() : ""+"' ");
				sql.append(" and hgts_sopact.pk_org ='"+pk_org.toString()+"') ");
				sql.append(" and rownum = 1 order by ts desc ");
				bvos=(PactBVO[]) dmo.queryByWhereClause(PactBVO.class, sql.toString());
				if(null!=bvos && bvos.length>0)
					return bvos[0];
			}

		} catch (DAOException e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 运输费用查询
	 */
	@Override
	public PactBVO getYSPactB(Object pk_org, Object kuangc, Object transport, String pk_ysxy) throws BusinessException {
		try {
			StringBuffer sql=new StringBuffer();
			sql.append(" pk_pact_b in (select hgts_pact_b.pk_pact_b  from hgts_pact_b ");
			sql.append("  inner join hgts_sopact   ");
			sql.append(" on hgts_pact_b.pk_pact = hgts_sopact.pk_pact ");
			sql.append(" where hgts_sopact.approvestatus = '1' ");
			sql.append(" and hgts_sopact.pk_billtypeid='"+HgtsPubConst.CONTRACT_YFXY+"' ");// 合同-运费协议
			//先查询运输费主键的信息.
			HYSuperDMO dmo=new HYSuperDMO();
			String nsql = sql.toString()+" and hgts_pact_b.pk_pact_b='"+pk_ysxy+"' )  and rownum = 1  order by ts desc ";
			PactBVO[] bvos=(PactBVO[]) dmo.queryByWhereClause(PactBVO.class, nsql);
			if(null!=bvos && bvos.length>0){
				return bvos[0];
			}
		 	

		} catch (DAOException e) {

			e.printStackTrace();
		}
		return null;
	}
}
