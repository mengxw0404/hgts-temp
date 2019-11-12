package nc.impl.hgts.pc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYSuperDMO;
import nc.itf.hgts.pc.ILoadPricePolicy;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.vo.hgts.pc.PricePolicyPKVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.pub.BusinessException;

public class LoadPricePolicyImpl implements ILoadPricePolicy {

	/**
	 * ǰ�᣺����ͨ����δ�رյģ����ѿ�ʼִ�е�
	 * ȡ�۸����ߣ����˹̶������������ȸ��ݱ�ͷ�Ŀͻ�(����ͻ����أ�����Ҫ)�Ƿ���ֵ��
	 * �����ֵ��ȡ�ÿͻ��ļ۸�����,���û�У�ȡ�۸����Ϊ�����ģ���������˶�����ߣ�ȡ�������������ļ۸�����
	 */
	@Override
	public Map<Integer, List<IPricePolicyVO>> loadPricePolicy(PricePolicyPKVO pricePk) throws BusinessException {
		Map<Integer, List<IPricePolicyVO>> map=new HashMap<Integer, List<IPricePolicyVO>>();
		String base=" nvl(dr,0)=0 "
				+ " and kbie='"+pricePk.getPk_mine()+"'  "//���
				+ " and typegrp='"+pricePk.getPk_invtype()+"'" //Ʒ����ϣ�ú�� 
				+ " and sktj='"+pricePk.getPk_busitype()+"' ";//�ܿ�������ҵ������(�Զ��嵵��)

		String coditions=base
				+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
				+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
				+ " and zxtime is not null "
				//+ " and pk_org='"+pricePk.getPk_org()+"' "
				+ " and substr(zxtime,1,10) <='"+pricePk.getBizdate()+"'"
				+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b where "
				+ base+" )";

		String codition=" and pk_cust='"+pricePk.getPk_cust()+"' ";
		String orderfiled=" order by zxtime desc ) where rownum=1 )";
		String sql=coditions+codition+orderfiled;

		HYSuperDMO dmo=new HYSuperDMO();
		PricepolicyBVO[] bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, sql);
		if(null !=bvos && bvos.length>0){			
			for(int i=0;i< bvos.length;i++){
				PricepolicyBVO bvo= bvos[i];	
				//�۸�Ҫ��
				Integer jgys=bvo.getAttributeValue("jgys")==null?-1:Integer.parseInt(bvo.getAttributeValue("jgys").toString());
				if(map.containsKey(jgys)){
					map.get(jgys).add(bvo);
				}else{
					List<IPricePolicyVO> list=new ArrayList<IPricePolicyVO>();
					list.add(bvo);
					map.put(jgys, list);
				}

			}
		}else{
			// 2���ͻ�����۸�
			String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pricePk.getPk_cust()+"')";
			String ssql=coditions+strWhere+orderfiled;
			bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, ssql);
			if(null !=bvos && bvos.length>0){			
				for(int i=0;i<bvos.length;i++){
					PricepolicyBVO bvo=bvos[i];	
					Integer jgys=bvo.getAttributeValue("jgys")==null?-1:Integer.parseInt(bvo.getAttributeValue("jgys").toString());
					if(map.containsKey(jgys)){
						map.get(jgys).add(bvo);
					}else{
						List<IPricePolicyVO> list=new ArrayList<IPricePolicyVO>();
						list.add(bvo);
						map.put(jgys, list);
					}		
				}
			}else{
				// 3�������۸�
				String s_sql=coditions+" and pk_cust='~' and (def1 is null or def1='~')"+orderfiled;
				bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, s_sql);
				if(null !=bvos && bvos.length>0){			
					for(int i=0;i<bvos.length;i++){
						PricepolicyBVO bvo=bvos[i];	
						Integer jgys=bvo.getAttributeValue("jgys")==null?-1:Integer.parseInt(bvo.getAttributeValue("jgys").toString());
						if(map.containsKey(jgys)){
							map.get(jgys).add(bvo);
						}else{
							List<IPricePolicyVO> list=new ArrayList<IPricePolicyVO>();
							list.add(bvo);
							map.put(jgys, list);
						}		
					}
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * @param pricePk
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public IPricePolicyVO[] loadLjyhPricePolicy(PricePolicyPKVO pricePk) throws BusinessException {
		String base=" nvl(dr,0)=0 "
				+ " and kbie='"+pricePk.getPk_mine()+"'  "
				+ " and typegrp='"+pricePk.getPk_inv()+"'"  
				+ " and sktj='"+pricePk.getPk_busitype()+"' ";

		String codi=base				    
				+ " and jgys = '"+HgtsPubConst.pricepolicy_ljyh_jgysid+"'"
				+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
				+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
				+ " and zxtime is not null "
				//+ " and pk_org='"+pricePk.getPk_org()+"' "
				+ " and substr(zxtime,1,10) <='"+pricePk.getBizdate()+"'"
				+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b where "
				+ base+" )";

		String codition=" and pk_cust='"+pricePk.getPk_cust()+"' ";
		String orderfiled=" order by zxtime desc ) where rownum=1 )";
		String sql=codi+codition+orderfiled;
		HYSuperDMO dmo=new HYSuperDMO();
		PricepolicyBVO[] bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, sql);
		if(null==bvos || bvos.length==0){
			// 2���ͻ�����۸�
			String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pricePk.getPk_cust()+"')";
			String ssql=codi+strWhere+orderfiled;
			bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, ssql);
			if(null==bvos || bvos.length==0){
				// 3�������۸�
				String nsql=codi+" and pk_cust='~' and (def1 is null or def1='~') "+orderfiled;
				bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, nsql);
			}
		}
		return bvos;
	}

	/**
	 * ���۽��㵥�в���ʱ����ѯ����ʱ  ���ʽ Ϊ �жҵ� ���Ƽ۸�
	 * @param pricePk
	 * @return
	 * @throws BusinessException
	 */
	public IPricePolicyVO[] loadChPricePolicy(PricePolicyPKVO pricePk) throws BusinessException {
		String base=" nvl(dr,0)=0 "
				+ " and kbie='"+pricePk.getPk_mine()+"'  "
				+ " and typegrp='"+pricePk.getPk_invtype()+"'"  
				+ " and sktj='"+pricePk.getPk_busitype()+"' ";

		String codi=base
				+ " and zqj='"+HgtsPubConst.pay_cd+"'"
				+ " and jgys = '1' "
				+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
				+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
				+ " and zxtime is not null "
				//+ " and pk_org='"+pricePk.getPk_org()+"' "
				+ " and substr(zxtime,1,10) <='"+pricePk.getBizdate()+"'"
				+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b where "
				+ base+" )";
		String codition=" and pk_cust='"+pricePk.getPk_cust()+"' ";
		String orderfiled=" order by zxtime desc ) where rownum=1 )";
		String sql=codi+codition+orderfiled;
		HYSuperDMO dmo=new HYSuperDMO();
		PricepolicyBVO[] bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, sql);
		if(null==bvos || bvos.length==0){
			// 2���ͻ�����۸�
			String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pricePk.getPk_cust()+"')";
			String ssql=codi+strWhere+orderfiled;
			bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, ssql);
			if(null==bvos || bvos.length==0){							
				String nsql=codi+" and pk_cust='~' and (def1 is null or def1='~') "+orderfiled;
				bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, nsql);
			}
		}
		return bvos;
	}

	/**
	 * ���۽��㵥�в���ʱ����ѯ����ʱ  ���ʽ Ϊ �ֻ�� ���Ƽ۸񡢸����۸�
	 * @param pricePk
	 * @return
	 * @throws BusinessException
	 */
	public IPricePolicyVO[] loadChOneRowPricePolicy(PricePolicyPKVO pricePk) throws BusinessException {
		String base=" nvl(dr,0)=0 "
				+ " and kbie='"+pricePk.getPk_mine()+"'  "
				+ " and typegrp='"+pricePk.getPk_invtype()+"'"  
				+ " and sktj='"+pricePk.getPk_busitype()+"' ";

		String codi=base
				+ " and zqj='"+HgtsPubConst.pay_xh+"'"
				+ " and jgys = '1' "
				+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
				+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
				+ " and zxtime is not null "
				//+ " and pk_org='"+pricePk.getPk_org()+"' "
				+ " and substr(zxtime,1,10) <='"+pricePk.getBizdate()+"'"
				+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b where "
				+ base+" )";
		String codition=" and pk_cust='"+pricePk.getPk_cust()+"' ";
		String orderfiled=" order by zxtime desc ) where rownum=1 )";
		String sql=codi+codition+orderfiled;
		HYSuperDMO dmo=new HYSuperDMO();
		PricepolicyBVO[] bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, sql);
		if(null==bvos || bvos.length==0){
			// 2���ͻ�����۸�
			String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pricePk.getPk_cust()+"')";
			String ssql=codi+strWhere+orderfiled;
			bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, ssql);
			if(null==bvos || bvos.length==0){

				// 3�������۸�
				String nsql=codi+" and pk_cust='~' and (def1 is null or def1='~') "+orderfiled;
				bvos=(PricepolicyBVO[]) dmo.queryByWhereClause(PricepolicyBVO.class, nsql);
			}
		}
		return bvos;
	}
}
