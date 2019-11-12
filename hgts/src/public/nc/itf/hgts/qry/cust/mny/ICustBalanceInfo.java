package nc.itf.hgts.qry.cust.mny;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 
 * ����֪ͨ������ʱ��У���������Ƿ��㹻
 * @author Administrator
 * 2017-12-6
 *
 */
public interface ICustBalanceInfo {

	public UFDouble[] getBalanceInfo(String hpk, String pk_billtype,String pk_org,String pk_cust,String pk_balatype,String pk_deptdoc,String obj) throws BusinessException;
	
	/**
	 * 
	 * @param hpk
	 * @param pk_transporttype ���䷽ʽ
	 * @param pk_billtype
	 * @param pk_org
	 * @param pk_cust �ͻ�
	 * @param pk_fhkc ��
	 * @param pk_deptdoc ����
	 * @param pk_pz  ú��
	 * @param obj
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble[] getResidueInfo(String hpk,String pk_transporttype, String pk_billtype,String pk_org,String pk_cust,String pk_fhkc,String pk_deptdoc,String pk_pz,String obj) throws BusinessException;
	
}
