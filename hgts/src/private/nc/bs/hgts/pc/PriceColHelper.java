package nc.bs.hgts.pc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.itf.hgts.pc.IPriceBizData;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.vo.hgts.pc.PricePllicyResultVO;
import nc.vo.pub.BusinessException;

public class PriceColHelper {

	/**
	 * ���ݼ۸�����  ����  �۸����
	 * @param pInfor
	 * @param data
	 */
	public static  List<PricePllicyResultVO> col(Map<Integer, List<IPricePolicyVO>> pInfor,IPriceBizData data) throws BusinessException{
		AbstactPriceCol col = null;
		List<PricePllicyResultVO> lprice = new ArrayList<PricePllicyResultVO>();
		for(Integer key:pInfor.keySet()){
			col = PriceColFactory.getColTool(key);//���ݲ�ͬ�۸����ػ�ȡ��ͬ������
			PricePllicyResultVO price = col.col(data, pInfor.get(key));
			if(price!=null)
				lprice.add(price);
		}
		
		return lprice;
	}
}
