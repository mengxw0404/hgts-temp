package nc.bs.hgts.pc;

import java.util.List;

import nc.itf.hgts.pc.IPriceBizData;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class PayPriceCol extends AbstactPriceCol {

	@Override
	protected Object getBizValue(IPriceBizData data) throws BusinessException {
		return getRealPay(data);
	}

	@Override
	protected UFDouble getAdjustValue(IPricePolicyVO policy)
			throws BusinessException {
		return HgtsPubTool.getUFDoubleNullAsZero(policy.getAdjustValue());
	}
	
	/**
	 * ѡ���������� �ļ۸�����
	 * @param ov
	 * @param lPolicy
	 * @return
	 * @throws BusinessException
	 */
	protected IPricePolicyVO selPolicy(Object ov,List<IPricePolicyVO> lPolicy) throws BusinessException{
		IPricePolicyVO selPolicy = null;
		for(IPricePolicyVO policy:lPolicy){
			if(HgtsPubTool.getStringNullAsTrim(policy.getValue()).equalsIgnoreCase(HgtsPubTool.getStringNullAsTrim(ov))){
				selPolicy = policy;
				break;
			}
		}
		
		if(selPolicy == null){
			// TODO 2017-12-19
			//throw new BusinessException("δƥ�䵽��Ӧ�ļ۸�����");
		}
		
		return selPolicy;
	}

	/**
	 * 1��	�۸�����-���ʽ�����ҵ�������� ���� ���ʽ����  �ж� ���㡣�����Ԥ��
		����ֽ��� ���̼۽��  ����  �ֽ� �ۿۣ��������尴�ճжҸ��ʽ���㡣
		���ȫ������  �����ϵͳ����  ���п��ơ�   ˵����ͨ���ͻ������ͻ��տ  �ֽ����ȿ��ǡ�

	2017-9-15 modify ���жϿ��ö�ȹ����������㷽ʽ��ʲô���Ͱ�ʲô��
	 */

//	
	private String getRealPay(IPriceBizData data) throws BusinessException{
		//2017-9-15 modify 
		/*//		���������   ֱ�� ���سж�
		if(data.getBizType().equalsIgnoreCase(HgtsPubConst.biztype_sx))
			return HgtsPubConst.pay_cd;

		UFDouble nmny = data.getNum().multiply(data.getGpPrice());
		IQueryCustMny cq = new QueryCustMnyImpl();
		UFDouble[] cms = cq.getCustMny(data.getCust(), data.getMineid(), data.getInvid());
		
		//		���ö�� ����  Ĭ�ϳж�
		if(HgtsPubTool.getUFDoubleNullAsZero(cms==null?UFDouble.ZERO_DBL:cms[3]).compareTo(nmny)>=0)
			return HgtsPubConst.pay_xh;


		return HgtsPubConst.pay_cd;*/
		
		if(data.getPay().equals(HgtsPubConst.paytype_xh)){
			return HgtsPubConst.pay_xh;
		}else if(data.getPay().equals(HgtsPubConst.paytype_cd)){
			
			return HgtsPubConst.pay_cd;
		}
		return HgtsPubConst.pay_cd_my;
	}
	
	/**
	 * ��ȡϵͳ���ò���������ʱ��������ͬ����ʾ
	 * 2017-8-7
	 * chengli
	 * @param pk_org
	 * @param corpcode
	 * @return
	 */
	private String getParSys(String pk_org){
		String value = "";
		try {
			value = SysInitQuery.getParaString(pk_org, "");
			
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return value;
	}
}
