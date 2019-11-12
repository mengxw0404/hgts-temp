package nc.bs.hgts.pc;

import java.util.List;

import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.hgts.pc.IPriceBizData;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.vo.hgts.pc.PricePllicyResultVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.ValueUtils;

public abstract class AbstactPriceCol {

	/**
	 * 选出符合条件 的价格政策
	 * @param ov
	 * @param lPolicy
	 * @return
	 * @throws BusinessException
	 */
	protected IPricePolicyVO selPolicy(Object ov,List<IPricePolicyVO> lPolicy) throws BusinessException{
		UFDouble min = null;
		UFDouble max = null;
		IPricePolicyVO selPolicy = null;
		
		if(ov instanceof UFDouble){//
			for(IPricePolicyVO policy:lPolicy){
				min = HgtsPubTool.getUFDoubleNullAsZero(policy.getMinValue());
				max = HgtsPubTool.getUFDoubleNullAsZero(policy.getMaxValue());
				UFDouble sd = HgtsPubTool.getUFDoubleNullAsZero(ov);
				if(max.doubleValue()==0){
					// 不输入默认为最大值
					if(sd.compareTo(min)>=0){
						selPolicy=policy;
						break;
					}
				}else{

					//if(sd.compareTo(min)>=0&&sd.compareTo(max)<=0){
					if(sd.doubleValue()>=min.doubleValue() && sd.doubleValue()<max.doubleValue()){
						selPolicy =  policy;
						break;
					}
				}
			}	
		}//else 自类复写
		return selPolicy;
		
	}
	/**
	 * 输入价格政策vo和业务数据  计算出  价格政策 计算出的结果  具体的价格政策 扩展该类  实现具体的 价格计算
	 * @param data
	 * @param policy
	 * @return
	 */
	public   PricePllicyResultVO col(IPriceBizData data,List<IPricePolicyVO> lPolicy) throws BusinessException{
		//		获取质检项目 灰分  的值  目前 系统仅支持  质检项目灰分   其他质检项目 暂不支持
		Object ov = getBizValue(data);


		IPricePolicyVO selPolicy = selPolicy(ov,lPolicy);

		if(selPolicy == null)
			return null;

		PricePllicyResultVO resultVO = new PricePllicyResultVO();
		resultVO.setPolicy(selPolicy);
		resultVO.setPk_pricepolicy(selPolicy.getPk_policy_b());
		
		String fdz=selPolicy.getAdjustValue();
		String fomustr = ValueUtils.getString(fdz);		
		
		if(null !=fomustr && !"".equals(fomustr) &&fomustr.indexOf("化验值")>0){
			fomustr=fomustr.replace("化验值", "hyz").replaceAll("（", "(").replaceAll("）", ")");
			//｛（化验值-9.52）/0.5｝*（8） ｛｝内计算取整，不是整数进一
			// 如：（化验值-9.52）/0.5 = 1.1，则取值为2，若=1.0，则取值为1
			String[] str=fomustr.split("[*]");
			FormulaParse fromula = new FormulaParse();			
			fromula.addVariable("hyz", ov);
			fromula.setExpress(str[0]);
			String value=fromula.getValue();
			int i_value=0;
			if(null==value || "".equals(value)){
				i_value=0;
				resultVO.setNadprice(new UFDouble(0)); // 价格浮动值
			}else{
				UFDouble d_value=new UFDouble(value);
				if(d_value.doubleValue()<0){					
					i_value=Integer.valueOf(d_value.abs().toString().split("[.]")[0]).intValue()*(-1);
				}else{
					i_value=Integer.valueOf(d_value.toString().split("[.]")[0]).intValue();
				}
				
				String s=str[1].replace("(", "").replace(")", "");
				if(i_value==d_value.doubleValue()){
					fomustr=(d_value.multiply(new UFDouble(s))).toString();
				}else{
					if(i_value<0){	
						fomustr=((new UFDouble(i_value).abs().add(1)).multiply(-1).multiply(new UFDouble(s))).toString();
					}else{
						fomustr=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
					}
				}
				/*fromula.setExpress(fomustr);			
				UFDouble fdzvalue=HgtsPubTool.getUFDoubleNullAsZero(fromula.getValue());*/
				UFDouble fdzvalue=HgtsPubTool.getUFDoubleNullAsZero(fomustr);
				resultVO.setNadprice(fdzvalue); // 价格浮动值
			}
			
		}else{			
			resultVO.setNadprice(HgtsPubTool.getUFDoubleNullAsZero(fdz)); // 价格浮动值
		}
		resultVO.setVpvalue(ov.toString()); //价格因素 值


		return resultVO;
	}

	/**
	 * 获取价格因素      值
	 * @param data
	 * @return
	 */
	protected abstract Object getBizValue(IPriceBizData data) throws BusinessException;

	/**
	 * 获取满足该价格政策后 价格调整 值 
	 * @param policy
	 * @return
	 */
	protected abstract UFDouble getAdjustValue(IPricePolicyVO policy)  throws BusinessException;

}
