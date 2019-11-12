package nc.vo.hgts.vochange;

import nc.vo.hgts.dayplansend.DayplanSendBVO;
import nc.vo.hgts.dayplansend.DayplanSendHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class YDPSToYPCD implements IChangeVOAdjust {

	@Override
	public AggregatedValueObject adjustBeforeChange(  AggregatedValueObject aggregatedvalueobject,
			ChangeVOAdjustContext changevoadjustcontext) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggregatedValueObject adjustAfterChange( AggregatedValueObject aggregatedvalueobject,
			AggregatedValueObject aggregatedvalueobject1, ChangeVOAdjustContext changevoadjustcontext)
			throws BusinessException {
		// TODO 自动生成的方法存根
		aggregatedvalueobject.getChildrenVO();
		aggregatedvalueobject1.getChildrenVO();
		return null;
	}

	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange( AggregatedValueObject[] aaggregatedvalueobject,
			ChangeVOAdjustContext changevoadjustcontext) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggregatedValueObject[] batchAdjustAfterChange( AggregatedValueObject[] aaggregatedvalueobject,
			AggregatedValueObject[] AggYPCDVO, ChangeVOAdjustContext changevoadjustcontext)
			throws BusinessException {
		// TODO 自动生成的方法存根
		DayplanSendHVO head = (DayplanSendHVO) aaggregatedvalueobject[0].getParentVO();
		DayplanSendBVO[] bbills = (DayplanSendBVO[]) aaggregatedvalueobject[0].getChildrenVO();
		
		for(int i=0;i<bbills.length;i++){
			DayplanSendBVO item=bbills[i];
			UFDouble plancars=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("plancars"));//计划车数
			UFDouble cars=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("def6"));//已派车数
			int carsnum = plancars.sub(cars).intValue();
			AggYPCDVO[0].getParentVO().setAttributeValue("plancars", HgtsPubTool.getStringNullAsTrim(carsnum));
		    
			// 3、目标单据子表vo 长度= 计划车数
			SendCarListBVO[] bvos=new SendCarListBVO[carsnum];
			for(int j=0;j<carsnum;j++){
				SendCarListBVO bvo=new SendCarListBVO();
				bvo.setAttributeValue("crowno", (j+1)+"0");
				bvo.setAttributeValue("vsourcecode", head.getAttributeValue("vbillno"));
				bvo.setAttributeValue("csourceid", head.getPrimaryKey());
				bvo.setAttributeValue("csourcebid", item.getPrimaryKey());
				bvos[j]=bvo;	
			}
			AggYPCDVO[0].setChildrenVO(bvos);
		}
		return new AggregatedValueObject[]{AggYPCDVO[0]};
	}

}
