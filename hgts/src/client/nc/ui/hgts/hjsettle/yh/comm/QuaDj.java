package nc.ui.hgts.hjsettle.yh.comm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.busibean.ISysInitQry;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.pc.BdInfoVOForSettle;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.qualityreport.QualityreportBVO;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.util.VOSortUtils;

/**
 * 质量定价-扣款、扣吨
 * @author cl
 *
 */
public class QuaDj {

	public QuaDj() {
		// TODO 自动生成的构造函数存根
	}


	public List<QualityreportBVO> quality(BdInfoVOForSettle infor){
		try {
			String sql="select b.* "
					+" from hgts_qualityreport h inner join hgts_qualityreport_b b"
					+" on h.pk_qualityreport=b.pk_qualityreport"
					+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 and h.vbillno='"+infor.getQcBillID()+"'";
			List<QualityreportBVO> list=(List) getService().executeQuery(sql, new BeanListProcessor(QualityreportBVO.class));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public IUAPQueryBS getService(){
		IUAPQueryBS bs=NCLocator.getInstance().lookup(IUAPQueryBS.class);
		return bs;
	}

	/**
	 * 售价 ：相当于挂牌价 
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 *例  4500		  	售价	发热量*0.102	相当于挂牌价
		4300	4500	售价	发热量*0.101	相当于挂牌价
		4100	4300	售价	发热量*0.1	相当于挂牌价
		4000	4100	售价	发热量*0.099	相当于挂牌价
		0		4000	售价	0			相当于挂牌价
	 */
	public UFDouble getTz_quaindex_sj(BdInfoVOForSettle infor,String pk_send_h,String settlezt,
			String gbdate,String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		
		int month=Integer.parseInt(gbdate.substring(5, 7));
		SendYzyjBVO[] items=null;
		for(int j=0;j<list.size();j++){		

			QualityreportBVO reportBVO=list.get(j);

			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // 质检项目pk

			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
							+ "and pk_project='"+pk_prj+"'"
							+ " and bkdrule='0' "); // 默认查询为“售价”的

			if(null !=items && items.length>0){
				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // 卖方 质检结果
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// 买方、 单月买双月卖		
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2： 卖方	
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3：单月买双月卖
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // 单月
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:单月卖双月买
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // 价格计算公式

				for(int i=0;i<items.length;i++){
					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// 不输入默认为最大值
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}
				}
				if(null !=jgfd && !"".equals(jgfd)){
					// 指标化验结果满足条件在某个公式中，   如果 “价格变动”字段 直接写的0，说明 本次结算的 综合到厂价 价格为 0 了
					if(jgfd.equals("0")){						
						fdzvalue= null;
						break;
					}
					if(jgfd.indexOf("化验值")>=0){						
						jgfd=jgfd.replace("化验值", "hyz").replaceAll("（", "(").replaceAll("）", ")");
						//TODO 灰分 窑街项目是否还是按以下算法来？
						//｛（化验值-9.52）/0.5｝*（-8） ｛｝内计算取整，不是整数进一
						// 如：（化验值-9.52）/0.5 = 1.1，则取值为2，若=1.0，则取值为1
						FormulaParse fromula = new FormulaParse();		
					
						fromula.addVariable("hyz", zjrst);
						fromula.setExpress(jgfd);
						String value=fromula.getValue();						
						fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value).abs());
						//}
					}else{
						if (jgfd.matches("^[0-9]*$")){							
							fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
						}
					}
				}		
			}
		}

		return fdzvalue;
	}
	
	/**
	 * 扣款
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 * 
	 */
	public UFDouble getTz_quaindex_kk(BdInfoVOForSettle infor,String pk_send_h,String settlezt,String gbdate,
			String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		int month=Integer.parseInt(gbdate.substring(5, 7));
		SendYzyjBVO[] items=null;
		for(int j=0;j<list.size();j++){		
			
			QualityreportBVO reportBVO=list.get(j);
		
			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // 质检项目pk
			
			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
					+ "and pk_project='"+pk_prj+"'"
					+ " and bkdrule='1' "); // 默认查询为扣款的
			
			if(null !=items && items.length>0){
				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // 质检结果
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// 买方			
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2： 卖方	
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3：单月买双月卖
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // 单月
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:单月卖双月买
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // 价格计算公式

				for(int i=0;i<items.length;i++){
					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// 不输入默认为最大值
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}
				}
				if(null !=jgfd && !"".equals(jgfd)){
					// 如果 “价格变动”字段 直接写的0，说明 本次结算的 综合到厂价 价格为 0 了
					if(jgfd.equals("0")){						
						fdzvalue= null;
						break;
					}
					if( jgfd.indexOf("化验值")>=0 ){						
						jgfd=jgfd.replace("化验值", "hyz").replaceAll("（", "(").replaceAll("）", ")");

						//｛（化验值-9.52）/0.5｝*（-8） ｛｝内计算取整，不是整数进一
						// 如：（化验值-9.52）/0.5 = 1.1，则取值为2，若=1.0，则取值为1
						FormulaParse fromula = new FormulaParse();			
						fromula.addVariable("hyz", zjrst);
						fromula.setExpress(jgfd);
						String value=fromula.getValue();
						//fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value).abs());
						fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value));
					
					}else{
						if (jgfd.matches("^[-\\+]?[0-9]*$")){							
							//fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
							fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd));
						}
					}
				}		
			}
		}
		return fdzvalue;
	}
	
	/**
	 * 扣吨
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 * 
	 * 质量标准：热值≧5200Kcal/Kg、固定碳≧48%、灰分≦23%、水分≦7%、分析基水分≦1.5%、挥发份25%-28%、硫≦1.0%供应货物。
		扣吨规则：灰分＞23%或发热量低于5200时，扣罚该批次重量1%；
			        灰分＞24%或发热量低于5100时，扣罚该批次重量2%；
			        灰分＞25%或发热量低于5000时，扣罚该批次重量4%；
			        灰分＞26%或发热量低于4900时，扣罚该批次重量8%；
			        灰分＞27%或发热量低于4800时，该批次全部扣除；
			        当月出现两次小于4800大卡煤质，停止供货。
		外水,每上升1%，扣罚该批次重量2%；
		内水, 每上升0.5%，扣罚该批次重量2%；
		全硫大于1%，每上升0.2%，扣罚该批次重量5%；
		挥发分超过范围每上升1%，扣罚该批次重量2%。
		
									扣罚
		01	发热量	5100	5200	1
		01	灰分%		23		24	
		02	发热量	5000	5100	2
		02	灰分%		24		25	
		03	发热量	4900	5000	4
		03	灰分%		25		26	
		04	发热量	4800	4900	8
		04	灰分%		26		27	
		05	发热量	0		4800	100
		05	灰分%	27	
	 */
	public UFDouble getTz_quaindex_kd(BdInfoVOForSettle infor,String pk_send_h,String settlezt,String gbdate,
			String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		
		SendYzyjBVO[] items=null;
		Map<String,UFDouble> map=new HashMap<String,UFDouble>();
		int month=Integer.parseInt(gbdate.substring(5, 7));
		for(int j=0;j<list.size();j++){		
			
			QualityreportBVO reportBVO=list.get(j);
		
			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // 质检项目pk
			
			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
					+ "and pk_project='"+pk_prj+"'"
					+ " and bkdrule='2' "); // 默认查询为扣吨的
			
			if(null !=items && items.length>0){	

				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // 卖方质检结果
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// 买方			
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2： 卖方	
						if(zbqztype.equals("2")){ // 加权平均
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3：单月买双月卖
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // 单月
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:单月卖双月买
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // 买方质检结果
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // 加权平均
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // 价格计算公式

				VOSortUtils.ascSort(items,new String[]{"batchcode"});

				for(int i=0;i<items.length;i++){
					String batchcode=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("batchcode"));

					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// 不输入默认为最大值
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						}
					}

					if(null !=jgfd && !"".equals(jgfd)){
						if(jgfd.indexOf("化验值")>=0){						
							jgfd=jgfd.replace("化验值", "hyz").replaceAll("（", "(").replaceAll("）", ")");

							//TODO 灰分 窑街项目是否还是按以下算法来？
							//｛（化验值-9.52）/0.5｝*（-8） ｛｝内计算取整，不是整数进一
							// 如：（化验值-9.52）/0.5 = 1.1，则取值为2，若=1.0，则取值为1
							FormulaParse fromula = new FormulaParse();		
							/*if(HgtsPubConst.HF.equals(pk_prj)){					
								String[] str=jgfd.split("[*]");
								fromula.addVariable("hyz", zjrst);
								fromula.setExpress(str[0]);
								String value=fromula.getValue();
								int i_value=0;
								if(null==value || "".equals(value)){
									i_value=0;					
								}else{
									UFDouble d_value=new UFDouble(value);
									if(d_value.doubleValue()<0){					
										i_value=Integer.valueOf(d_value.abs().toString().split("[.]")[0]).intValue()*(-1);
									}else{
										i_value=Integer.valueOf(d_value.toString().split("[.]")[0]).intValue();
									}

									String s=str[1].replace("(", "").replace(")", "");
									if(i_value==d_value.doubleValue()){
										jgfd=(d_value.multiply(new UFDouble(s))).toString();
									}else{
										if(i_value<=0){	
											if(d_value.doubleValue()>0){
												jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
											}else{								
												jgfd=((new UFDouble(i_value).abs().add(1)).multiply(-1).multiply(new UFDouble(s))).toString();
											}
										}else{
											jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
										}
									}
									if(!map.containsKey(batchcode)){										
										map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(jgfd));
										jgfd="";
									}
								}
							}else{*/
								fromula.addVariable("hyz", zjrst);
								fromula.setExpress(jgfd);
								String value=fromula.getValue();
								if(!map.containsKey(batchcode)){									
									map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(value));
								}
							//}						
						}else{
							if(!map.containsKey(batchcode)){								
								map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
								jgfd="";
							}
						}
					}	
				}
			}
		}
		
		if(null !=map && map.size()>0){
			for(String key:map.keySet()){
				fdzvalue=fdzvalue.add(map.get(key));
			}
		}
		return fdzvalue;
	}
	
	/**
	 * 质量指标是适用的项目
	 * 1：福山；2：窑街
	 * @return
	 */
	public String isCurPrj(){
		ISysInitQry init=(ISysInitQry) NCLocator.getInstance().lookup(ISysInitQry.class.getName());
		String value="";
		try {
			SysInitVO sysInitVO = init.queryByParaCode(AppContext.getInstance().getPkGroup(),"FF18");
			value=sysInitVO.getValue();
			if(null ==value || "".equals(value)){
				return "1";
			}
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		return value;
	}
	
	public UFBoolean isD(int month){
		
		if(month==1|| month==3 || month==5|| month==7 || month==9 || month==11){
			return UFBoolean.TRUE;
		}else{
			return UFBoolean.FALSE;
		}
	}
	
	/**
	 * 加权平均权重
	 * @param infor
	 * @param pk_transporttype
	 * @param pk_prj
	 * @param mfOrMf : 1：买方  2：卖方
	 * @return
	 */
	public UFDouble getAvgValue(BdInfoVOForSettle infor,String pk_transporttype,String pk_prj,String mfOrMf,Map<String,UFDouble> map_dates){
		QuqAvg avg=new QuqAvg();
		//UFDouble sum_mthton=avg.getCurMthNum(infor,pk_transporttype);
		//List list_sumdayton=avg.getCurDayNum(infor,pk_transporttype);
	
		UFDouble avgValue=UFDouble.ZERO_DBL;

		String strDates="";
		UFDouble sum_ton=UFDouble.ZERO_DBL;
		
		for(String key:map_dates.keySet()){
			if(!"".equals(strDates)){
				strDates=strDates+",'"+key+"'";
			}else{
				strDates="'"+key+"'";
			}
			sum_ton=sum_ton.add(HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)));	
			
		}
		
		for(String key:map_dates.keySet()){	
			List list_val=avg.getCurDayValue(infor,strDates);
			for(int i=0;i<list_val.size();i++){
				Object[] o_i =  (Object[]) list_val.get(i);										
				// 日期一样、质检项目一样
				if(key.equals(o_i[0]) && pk_prj.equals(o_i[1])){	
					if("1".equals(mfOrMf)){ // 买方
						avgValue=avgValue.add(HgtsPubTool.getAvgRz(sum_ton, HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)), HgtsPubTool.getUFDoubleNullAsZero(o_i[3])));
					}else{
						// 卖方
						avgValue=avgValue.add(HgtsPubTool.getAvgRz(sum_ton, HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)), HgtsPubTool.getUFDoubleNullAsZero(o_i[2])));
					}
				}
			}									
		}
		
		return avgValue;
	}
}
