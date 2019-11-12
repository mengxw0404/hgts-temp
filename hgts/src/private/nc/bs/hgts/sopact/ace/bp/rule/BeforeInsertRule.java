package nc.bs.hgts.sopact.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.NumberToCN;
import nc.vo.hgts.sopact.AggPactVO;
import nc.vo.hgts.sopact.ContChangeBVO;
import nc.vo.hgts.sopact.ContQualityBVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class BeforeInsertRule implements IRule<AggPactVO>{

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}
	
	public BeforeInsertRule() {
		// TODO 自动生成的构造函数存根
		super();
	}

	@Override
	public void process(AggPactVO[] vos) {
		// TODO 自动生成的方法存根
		if(null !=vos && vos.length>0){
			PactVO hvo=vos[0].getParentVO();
			UFDouble total_mny=UFDouble.ZERO_DBL;
			UFDouble total_noratemny=UFDouble.ZERO_DBL;
			UFDouble total_ratemny=UFDouble.ZERO_DBL;
			ContQualityBVO[] quaBVOS=(ContQualityBVO[]) vos[0].getTableVO("pk_cont_zlzb_b");
			if(null !=quaBVOS && quaBVOS.length>0){
				FormulaParseTool tool=new FormulaParseTool();
				String prjnote="";
				for(int i=0;i<quaBVOS.length;i++){
					String pk_qua=HgtsPubTool.getStringNullAsTrim(quaBVOS[i].getAttributeValue("pk_quaprj"));
					String prjvalue=HgtsPubTool.getStringNullAsTrim(quaBVOS[i].getAttributeValue("prjvalue"));
					
					String name=tool.getBsNameByID("bd_defdoc", "name", "pk_defdoc", pk_qua);
					String note=name+prjvalue;
					if("".equals(prjnote)){
						prjnote=note;
					}else{
						prjnote=prjnote+";"+note;
					}
					hvo.setAttributeValue("yl3", prjnote); // 质量指标放在一个字段上，打印时使用
				}
			}
			
			PactBVO[] pactBVOS=(PactBVO[]) vos[0].getTableVO("pk_pact_b");
			if(null !=pactBVOS && pactBVOS.length>0){
				for(int i=0;i<pactBVOS.length;i++){
					UFDouble mny=HgtsPubTool.getUFDoubleNullAsZero(pactBVOS[i].getAttributeValue("bmny"));
					UFDouble noratemny=HgtsPubTool.getUFDoubleNullAsZero(pactBVOS[i].getAttributeValue("bnoratemny"));
					UFDouble ratemny=HgtsPubTool.getUFDoubleNullAsZero(pactBVOS[i].getAttributeValue("bratemny"));
					total_mny=total_mny.add(mny);
					total_noratemny=total_noratemny.add(noratemny);
					total_ratemny=total_ratemny.add(ratemny);
				}
				hvo.setAttributeValue("mny", total_mny);
				hvo.setAttributeValue("noratemny", total_noratemny);
				hvo.setAttributeValue("ratemny", total_ratemny);
				
				NumberToCN cn=new NumberToCN();
				String dx_mny=cn.numberCNMontrayUnit(total_mny.toBigDecimal());
				hvo.setAttributeValue("mnytoupp", dx_mny);
			}
			
			ContChangeBVO[] bodys=(ContChangeBVO[]) vos[0].getTableVO("hgts_contchange_b");
			if(null !=bodys && bodys.length>0){
				String i_key="YY";
				for(int i=0;i<bodys.length;i++){
					String blatest=HgtsPubTool.getStringNullAsTrim(bodys[i].getAttributeValue("blatest"));
					String enablestate=HgtsPubTool.getStringNullAsTrim(bodys[i].getAttributeValue("enablestate"));
					String key=blatest+enablestate;
					if(!"".equals(key) && i_key.equals(key)){
						ExceptionUtils.wrappBusinessException("[价格变更]页签：只允许存在一条最新价且启用的数据。");
					}
				}
			}			
		}
	}

}
