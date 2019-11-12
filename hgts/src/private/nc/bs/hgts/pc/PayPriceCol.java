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
	 * 选出符合条件 的价格政策
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
			//throw new BusinessException("未匹配到相应的价格政策");
		}
		
		return selPolicy;
	}

	/**
	 * 1、	价格因素-付款方式：如果业务类型是 赊销 付款方式按照  承兑 计算。如果是预收
		如果现金余额够 挂盘价金额  则按照  现金 折扣，不够整体按照承兑付款方式计算。
		如果全部余额不够  则根据系统参数  进行控制。   说明：通过客户关联客户收款单  现金优先考虑。

	2017-9-15 modify 不判断可用额度够不够，结算方式是什么，就按什么来
	 */

//	
	private String getRealPay(IPriceBizData data) throws BusinessException{
		//2017-9-15 modify 
		/*//		如果是赊销   直接 返回承兑
		if(data.getBizType().equalsIgnoreCase(HgtsPubConst.biztype_sx))
			return HgtsPubConst.pay_cd;

		UFDouble nmny = data.getNum().multiply(data.getGpPrice());
		IQueryCustMny cq = new QueryCustMnyImpl();
		UFDouble[] cms = cq.getCustMny(data.getCust(), data.getMineid(), data.getInvid());
		
		//		可用额度 不够  默认承兑
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
	 * 读取系统配置参数，余额不够时，给出不同的提示
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
