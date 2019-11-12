package nc.impl.hgts.qry.cust.mny;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.itf.hgts.qry.cust.mny.ICustBalanceInfo;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 发运通知单保存校验使用
 * @author Administrator
 *
 */

public class CustBalanceInfoImpl implements ICustBalanceInfo {

	private BaseDAO dao =  null;

	private BaseDAO getQuery(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	private ColumnProcessor cp = new ColumnProcessor();

	/**
	 * 2019年4月19日 去掉部门条件
	 */
	@Override
	public UFDouble[] getBalanceInfo(String hpk, String pk_billtype,
			String pk_org, String pk_cust, String pk_balatype,
			String pk_deptdoc, String obj) throws BusinessException {

		UFDouble[] info=null;

		// 收款金额 -- 按结算方式
		UFDouble skmny=getSKYE(pk_org, pk_cust, pk_balatype, pk_deptdoc,obj);
		// 发票金额 -- 按结算方式
		UFDouble fpmny=getFPYE(pk_org, pk_cust, pk_balatype, pk_deptdoc,obj);

		// 收款金额 -- 不按结算方式
		UFDouble skmny_all=getSKYE(pk_org, pk_cust, null,pk_deptdoc,obj);
		// 发票金额 -- 不按结算方式
		UFDouble fpmny_all=getFPYE(pk_org, pk_cust, null,pk_deptdoc,obj);

		// 2018年12月19日 增加按结算方式
		// 已占用金额 - 未关闭
		UFDouble sendmny=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"N",HgtsPubConst.TRANSPORT_QY);	
		UFDouble sendmny_ly=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"N",HgtsPubConst.TRANSPORT_LY);	
		// 已占用金额 - 已关闭
		UFDouble sendmny_close=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype,pk_deptdoc,obj,"Y",HgtsPubConst.TRANSPORT_QY);
		UFDouble sendmny_close_ly=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"Y",HgtsPubConst.TRANSPORT_LY);

		//信用额度
		UFDouble credit=this.getCredit(pk_cust,pk_deptdoc,pk_balatype);

		//客商档案上  应收余额(现汇余额、承兑余额)以负数的形式显示，  可用额度=（信用额度-应收余额(现汇余额、承兑余额)-业务占用）
		// skmny：是直接查询的收款单，故代码计算的时候，是add
		//UFDouble balance=credit.add(skmny).sub(sendmny);

		// 2018.8.31 modify 
		// 客户余额=累计收款—累计发票
		// 可用余额=信用额度 +客户余额 —业务占用
		UFDouble balance=credit.add(/*skmny_all.sub(fpmny_all)*/skmny.sub(fpmny)).sub(sendmny.add(sendmny_ly).add(sendmny_close).add(sendmny_close_ly));

		info=new UFDouble[5];
		info[0]=credit;
		info[1]=skmny.sub(fpmny);
		info[2]=sendmny.add(sendmny_ly).add(sendmny_close).add(sendmny_close_ly);
		info[3]=balance;
		info[4]=skmny_all.sub(fpmny_all); // 总余额

		return info;
	}

	/**
	 * 发票金额
	 * @param pk_org：组织
	 * @param pk_cust:客户
	 * @param pk_balatype：结算方式：现汇、承兑
	 * @param pk_deptdoc：部门
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getFPYE(String pk_org,String pk_cust,String pk_balatype,
			String pk_deptdoc,String dbilldate) throws BusinessException{		
		String sql ="select sum(round(nvl(norigtaxmny,0),2)) from hgts_saleinvoice_b where nvl(dr,0) = 0 "
				+ (null==pk_balatype||"".equals(pk_balatype)?"":" and pk_balatype = '"+pk_balatype+"' ")
				+ " and csaleinvoiceid in ("
				+ " select csaleinvoiceid from hgts_saleinvoice where nvl(dr,0) = 0 "
				+ " and pk_org = '"+pk_org+"' "
				+ " and cinvoicecustid = '"+pk_cust+"' "
				+ " and pk_dept='"+pk_deptdoc+"'"
				+ " and substr(dbilldate,0,10) >='"+dbilldate+"'"
				+ ")";

		UFDouble fp = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return fp;
	}

	/**
	 * 收款金额
	 * 2019年4月19日 去掉部门条件
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_balatype
	 * @param pk_deptdoc
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSKYE(String pk_org,String pk_cust,String pk_balatype,
			String pk_deptdoc,String dbilldate) throws BusinessException{
		String sql="select sum(skmny) from hgts_sknoticebill where nvl(dr,0) = 0 "
				+ " and vbillstatus=1 "
				+ " and pk_org='"+pk_org+"'"
				+ " and customer = '"+pk_cust+"' "
			//	+ " and checktype = 1 "//金额校验
				+( null==pk_balatype || "".equals(pk_balatype)?"": " and pk_balatype = '"+pk_balatype+"' ")
				//+ " and pk_dept ='"+pk_deptdoc+"'"
				+ " and substr(dbilldate,0,10) >='"+dbilldate+"'";

		UFDouble sk = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return sk;
	}

	/**
	 * 信用额度
	 * 2018-9-28  客户+部门
	 * 
	 * 2019年4月19日 去掉部门条件
	 * @return
	 */
	public UFDouble getCredit(String pk_cust,String pk_dept,String pk_balatype) throws BusinessException{
		String sql="select credit from hgts_merchant "
				+ " where nvl(dr,0)=0 "
				+ " and merchats='"+pk_cust+"'"
			//	+ (pk_dept==null|| "".equals(pk_dept)?"":" and def20='"+pk_dept+"'")
				+(pk_balatype==null||"".equals(pk_balatype)?"":" and def19='"+pk_balatype+"'")
				;
		UFDouble credit = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		
		return credit;
	}

	/**
	 * 业务占用
	 * @param pk_org
	 * @param pk_cust
	 * @param hpk
	 * @param pk_billtype
	 * @param pk_balatype
	 * @param pk_deptdoc
	 * @param dbilldate
	 * @param closeflag 关闭/未关闭
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getAllSendMny(String hpk,String pk_billtype,String pk_org,
			String pk_cust,String pk_balatype,String pk_deptdoc,
			String dbilldate,String closeflag,String pk_transporttype)
					throws BusinessException {

		if(null==hpk || "".equals(hpk)){
			hpk=" ";
		}
		String sql="";
		String qrysql="";
		if(closeflag.equals("Y")){
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){				
				// 已关闭: (已过磅数量-已开票数量)* 执行单价
				qrysql =" sum(round((nvl(b.yzxnum, 0) - nvl(b.ykpnum, 0)) * zxprice,2)) mny ";
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				// （已装车数-开票车数）* 标重*执行单价
				qrysql=" sum(round((nvl(b.def6, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0) * zxprice,2)) mny";
			}

		}else if(closeflag.equals("N")){
			// 未关闭: (数量-已开票数量)* 执行单价
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){	
				qrysql =" sum(round((nvl(b.shul, 0) - nvl(b.ykpnum, 0)) * zxprice,2)) mny ";	

			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				qrysql=" sum(round((nvl(b.carnum, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0) * zxprice,2)) mny";
			}
		}
		if(HgtsPubConst.FHTZD.equals(pk_billtype)){		
			sql = "select "
					+ qrysql
					+ " from hgts_sendnoticebill_b b "
					+ " inner join hgts_sendnoticebill h "
					+ " on b.pk_sendnoticebill=h.pk_sendnoticebill "
					+ " where nvl(b.dr,0) = 0 and nvl(h.dr,0) = 0 "
					+ " and COALESCE(b.rowcloseflag,'N')='"+closeflag+"' "
					+ " and h.pk_cust='"+pk_cust+"' "
				//	+ " and h.pk_dept='"+pk_deptdoc+"'"
					+ (null==pk_balatype||"".equals(pk_balatype)?"": " and h.pk_balatype='"+pk_balatype+"'")
					+ " and h.pk_transporttype='"+pk_transporttype+"'"
					+ " and substr(h.dbilldate,0,10) >='"+dbilldate+"'"
					+ " and h.pk_sendnoticebill<>'"+hpk+"'"
					;
			return  HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		}

		return UFDouble.ZERO_DBL;
	}

	/**
	 * 发运通知单-保存时校验数量
	 */
	@Override
	public UFDouble[] getResidueInfo(String hpk, String pk_transporttype,String pk_billtype,
			String pk_org, String pk_cust, String pk_fhkc, String pk_deptdoc,
			String pk_pz, String dbilldate) throws BusinessException {
		
		// 收款-按数量控制
		UFDouble shnum=getSHNum( pk_org,pk_transporttype, pk_cust, pk_fhkc, pk_deptdoc, pk_pz);
		// 发运-(未关闭的数量)
		UFDouble sdnum=getSDNum(hpk, pk_org,pk_transporttype, pk_cust, pk_fhkc, pk_deptdoc, pk_pz,"N");
		// 发运-(已经关闭的剩余)
		UFDouble close_num=getSDNum(hpk, pk_org, pk_transporttype,pk_cust, pk_fhkc, pk_deptdoc, pk_pz,"Y");
		
		//最终可用数量 = 收款数量 - 未关闭发运发数量 + 已关闭发运单剩余量
		UFDouble toltal_sum = shnum.sub(sdnum).add(close_num);
		
		UFDouble[] info=new UFDouble[5];
		info[0] = toltal_sum;
		return info;
	}
	
	/**
	 * 
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_fhkc
	 * @param pk_deptdoc
	 * @param pk_pz
	 * @return
	 * @throws DAOException 
	 */
	private UFDouble getSDNum(String hpk, String pk_org,String pk_transporttype, String pk_cust, String pk_fhkc,
			String pk_deptdoc, String pk_pz,String closeflag) throws DAOException {

		if(null==hpk || "".equals(hpk)){
			hpk=" ";
		}
		String sql="";
		String qrysql="";
		if(closeflag.equals("Y")){
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){				
				// 已关闭: (已过磅数量-已开票数量)
				qrysql =" sum(round((nvl(b.yzxnum, 0) - nvl(b.ykpnum, 0)) ,2)) mny ";
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				// （已装车数-开票车数）* 标重
				qrysql=" sum(round((nvl(b.def6, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0),2)) mny";
			}

		}else if(closeflag.equals("N")){
			// 未关闭  数量		
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){	
				qrysql =" sum(nvl(b.shul, 0)) mny ";	
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				//  车数 * 标重
				qrysql=" sum(round(nvl(b.carnum, 0)  * nvl(b.carstrong, 0),2)) mny";
			}
		}
	
		sql = "select "
					+ qrysql
					+ " from hgts_sendnoticebill_b b "
					+ " inner join hgts_sendnoticebill h "
					+ " on b.pk_sendnoticebill=h.pk_sendnoticebill "
					+ " where nvl(b.dr,0) = 0 and nvl(h.dr,0) = 0 "
				//	+ " and h.checktype = 2 "
					+ " and COALESCE(b.rowcloseflag,'N')='"+closeflag+"' "
					+ " and h.pk_org='"+pk_org+"' "
					+ " and h.pk_cust='"+pk_cust+"' "
					+ " and h.pk_dept='"+pk_deptdoc+"'"
					+ " and h.pk_transporttype='"+pk_transporttype+"'"
					+ " and h.pk_fhkc='"+pk_fhkc+"'"
					+ " and b.pz='"+pk_pz+"'"
					+ " and h.pk_sendnoticebill<>'"+hpk+"'"
					;
			return  HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		
	}

	/**
	 * 查询收款中 符合条件 ”收货数量“
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_fhkc 矿场
	 * @param pk_deptdoc
	 * @param pk_pz 煤种
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSHNum(String pk_org,String pk_transporttype,String pk_cust,String pk_fhkc,
			String pk_deptdoc,String pk_pz) throws BusinessException{
		String sql="select sum(hgts_sknoticebill_b.ton) "
				+ " from hgts_sknoticebill "
				+ "  inner join hgts_sknoticebill_b "
				+ "     on hgts_sknoticebill.pk_sknotice = hgts_sknoticebill_b.pk_sknotice "
				+ "   where nvl(hgts_sknoticebill.dr , 0) = 0  "
				+ "         AND nvl(hgts_sknoticebill.dr , 0) = 0  "
				+ "         and  hgts_sknoticebill.vbillstatus=1    "
				//+ "        and  hgts_sknoticebill.checktype = 2  "
				+ "          and hgts_sknoticebill.pk_org= '"+pk_org+ "'"
			    + "          and hgts_sknoticebill.pk_transporttype= '"+pk_transporttype+ "'"
				+ "          and hgts_sknoticebill.customer = '"+pk_cust+ "'"
				+ "          and hgts_sknoticebill.pk_dept = '"+pk_deptdoc+ "'"
				+ "          and hgts_sknoticebill.pk_kc =  '"+pk_fhkc+ "'"
				+ "          and hgts_sknoticebill_b.pk_inv = '"+pk_pz+ "'";
		UFDouble sk = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return sk;
	}

}
