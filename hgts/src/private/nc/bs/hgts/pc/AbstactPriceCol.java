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
	 * ѡ���������� �ļ۸�����
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
					// ������Ĭ��Ϊ���ֵ
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
		}//else ���ิд
		return selPolicy;
		
	}
	/**
	 * ����۸�����vo��ҵ������  �����  �۸����� ������Ľ��  ����ļ۸����� ��չ����  ʵ�־���� �۸����
	 * @param data
	 * @param policy
	 * @return
	 */
	public   PricePllicyResultVO col(IPriceBizData data,List<IPricePolicyVO> lPolicy) throws BusinessException{
		//		��ȡ�ʼ���Ŀ �ҷ�  ��ֵ  Ŀǰ ϵͳ��֧��  �ʼ���Ŀ�ҷ�   �����ʼ���Ŀ �ݲ�֧��
		Object ov = getBizValue(data);


		IPricePolicyVO selPolicy = selPolicy(ov,lPolicy);

		if(selPolicy == null)
			return null;

		PricePllicyResultVO resultVO = new PricePllicyResultVO();
		resultVO.setPolicy(selPolicy);
		resultVO.setPk_pricepolicy(selPolicy.getPk_policy_b());
		
		String fdz=selPolicy.getAdjustValue();
		String fomustr = ValueUtils.getString(fdz);		
		
		if(null !=fomustr && !"".equals(fomustr) &&fomustr.indexOf("����ֵ")>0){
			fomustr=fomustr.replace("����ֵ", "hyz").replaceAll("��", "(").replaceAll("��", ")");
			//��������ֵ-9.52��/0.5��*��8�� �����ڼ���ȡ��������������һ
			// �磺������ֵ-9.52��/0.5 = 1.1����ȡֵΪ2����=1.0����ȡֵΪ1
			String[] str=fomustr.split("[*]");
			FormulaParse fromula = new FormulaParse();			
			fromula.addVariable("hyz", ov);
			fromula.setExpress(str[0]);
			String value=fromula.getValue();
			int i_value=0;
			if(null==value || "".equals(value)){
				i_value=0;
				resultVO.setNadprice(new UFDouble(0)); // �۸񸡶�ֵ
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
				resultVO.setNadprice(fdzvalue); // �۸񸡶�ֵ
			}
			
		}else{			
			resultVO.setNadprice(HgtsPubTool.getUFDoubleNullAsZero(fdz)); // �۸񸡶�ֵ
		}
		resultVO.setVpvalue(ov.toString()); //�۸����� ֵ


		return resultVO;
	}

	/**
	 * ��ȡ�۸�����      ֵ
	 * @param data
	 * @return
	 */
	protected abstract Object getBizValue(IPriceBizData data) throws BusinessException;

	/**
	 * ��ȡ����ü۸����ߺ� �۸���� ֵ 
	 * @param policy
	 * @return
	 */
	protected abstract UFDouble getAdjustValue(IPricePolicyVO policy)  throws BusinessException;

}
