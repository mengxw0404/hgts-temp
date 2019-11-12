package nc.itf.hgts.qry.cust.mny;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 
 * 发运通知单保存时，校验可用余额是否足够
 * @author Administrator
 * 2017-12-6
 *
 */
public interface ICustBalanceInfo {

	public UFDouble[] getBalanceInfo(String hpk, String pk_billtype,String pk_org,String pk_cust,String pk_balatype,String pk_deptdoc,String obj) throws BusinessException;
	
	/**
	 * 
	 * @param hpk
	 * @param pk_transporttype 运输方式
	 * @param pk_billtype
	 * @param pk_org
	 * @param pk_cust 客户
	 * @param pk_fhkc 矿场
	 * @param pk_deptdoc 部门
	 * @param pk_pz  煤种
	 * @param obj
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble[] getResidueInfo(String hpk,String pk_transporttype, String pk_billtype,String pk_org,String pk_cust,String pk_fhkc,String pk_deptdoc,String pk_pz,String obj) throws BusinessException;
	
}
