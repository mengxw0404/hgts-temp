package nc.bs.hgts.ff_sknoticebill.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.ffsknoticebill.AggFfSknoticebillHVO;
import nc.vo.hgts.ffsknoticebill.FfSknoticebillBVO;
import nc.vo.hgts.ffsknoticebill.FfSknoticebillHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * ��д��ͬ���տ���
 * @author cl
 *
 */
public class AfterInsertRule implements IRule<AggFfSknoticebillHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}
	public AfterInsertRule(){
		super();
	}
	@Override
	public void process(AggFfSknoticebillHVO[] vos) {

		// TODO �Զ����ɵķ������
		if(null !=vos && vos.length>0){
			FfSknoticebillHVO hvo=vos[0].getParentVO();
			String hpk=hvo.getPrimaryKey();
			if(null==hpk || "".equals(hpk)){
				hpk=" ";
			}
			String contcode=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("contcode"));
			if(null !=contcode && !"".equals(contcode)){			
				// 1����ѯ��ʷ�տ�֪ͨ�������ú�ͬ�����
				UFDouble mny=UFDouble.ZERO_DBL;
				String sql_sk="select sum(nvl(b.local_money_cr,0)) mny"
						+" from hgts_sknoticebill h "
						+" inner join hgts_sknoticebill_b b "
						+" on h.pk_sknotice = b.pk_sknotice "
						+" where nvl(h.dr, 0) = 0 "
						+" and nvl(b.dr, 0) = 0 "
						+" and h.contcode ='"+contcode+"' "
						+" and h.pk_sknotice !='"+hpk+"'";				

				// 2����ǰ�����ĵ��� ռ�� �ĺ�ͬ�����
				FfSknoticebillBVO[] bvos=(FfSknoticebillBVO[]) vos[0].getChildren(FfSknoticebillBVO.class);			
				UFDouble cur_mny=UFDouble.ZERO_DBL;
				if(null !=bvos && bvos.length>0){
					for(FfSknoticebillBVO bvo:bvos){
						String pk_pact_b=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("csourcebid"));
						cur_mny=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("local_money_cr"));		

						sql_sk= sql_sk+" and b.csourcebid='"+pk_pact_b+"' ";						
						try {
							mny=HgtsPubTool.getUFDoubleNullAsZero(this.getDao().executeQuery(sql_sk,new ColumnProcessor()));
						} catch (DAOException e) {
							e.printStackTrace();
							ExceptionUtils.wrappBusinessException("��ѯ�տ�֪ͨ�������ú�ͬ���:"+e.getMessage());
						}
						
						////2019-10-30 ����ޱ ע��
						// 3����д ���ú�ͬ�����=
//						UFDouble t_sk_mny=mny.add(cur_mny);
//						String sql="update hgts_pact_b set ysmny="+t_sk_mny+" where nvl(dr,0)=0 and pk_pact_b='"+pk_pact_b+"'";
//						try {
//							this.getDao().executeUpdate(sql);
//						} catch (DAOException e) {
//							e.printStackTrace();
//							ExceptionUtils.wrappBusinessException("��д��ͬ�տ�������쳣:"+e.getMessage());
//						}
					}
				}
			}
		}
	}
}