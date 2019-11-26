package nc.bs.hgts.pricepolicy.task;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pricepolicy.PricepolicyHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 * 定时任务：获取价格政策 开始执行时间 的发运通知单
 * @author TR
 *
 */
public class PricePolicyCloseSendBillPlusgin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext context)
			throws BusinessException {
		  String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		  String wheresql = " vbillstatus=1 and closeflag!='Y' and substr(zxtime,0,10) = '"+curDate+"'";
		  PricepolicyHVO[] Pricevos=(PricepolicyHVO[]) HYPubBO_Client.queryByCondition(PricepolicyHVO.class,wheresql);
		  
		  for( PricepolicyHVO pricevo: Pricevos){
			// 客户
				String pk_cust=HgtsPubTool.getStringNullAsTrim(pricevo.getAttributeValue("pk_cust"));
				// 客户分类
				String pk_custclass=HgtsPubTool.getStringNullAsTrim(pricevo.getAttributeValue("def1"));
				// 执行时间
				String zxtime=HgtsPubTool.getStringNullAsTrim(pricevo.getAttributeValue("zxtime")).substring(0, 10);
			
//				if(zxtime.compareTo(curDate)>=1){
//					throw new BusinessException("当前日期未到此价格政策的执行日期，不允许此操作！");
//				}
				PricepolicyBVO[] pbvos=(PricepolicyBVO[]) HYPubBO_Client.queryByCondition(PricepolicyBVO.class, "nvl(dr,0)=0 and PK_PRICEPOLICY ='"+pricevo.getPrimaryKey()+"'");
				List<String[]> list=this.getQryInfo(pbvos);

				String vnote="";
				if(null !=list && list.size()>0){
					for(int i=0;i<list.size();i++){
						String[] str=list.get(i);
						String pk_mine=str[0];
						String pk_pz=str[1];
						String pk_busitype=str[2];
						UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(str[4]);

						String wherepart=" nvl(dr,0)=0 and vbillstatus=1  and  isbidding != 'Y' "
								+ " and nvl(closeflag,'N')='N' "
								+ " and	pk_fhkc='"+pk_mine+"' and pk_busitype='"+pk_busitype+"'"
								// 在有效范围内
								+ " and (substr(startdate,0,10)<='"+zxtime+"' and substr(enddate,0,10)>='"+zxtime+"')";
						if(null !=pk_cust && !"".equals(pk_cust)){
							// 1、客户
							wherepart = wherepart +" and pk_cust='"+pk_cust+"' ";
						}else{
							if(null !=pk_custclass && !"".equals(pk_custclass)){
								// 2、客户分类
								wherepart = wherepart +" and pk_cust in (select pk_customer from bd_customer where nvl(dr,0)=0 and ecotypesincevfive='"+pk_custclass+"') ";
							}else{
								// 3、公共：条件即 wherepart	
								return null;
							}
						}

						SendnoticebillHVO[] hvos=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class,wherepart);
						if(null !=hvos && hvos.length>0){
							String wherepart_b=" nvl(dr,0)=0 and nvl(rowcloseflag,'N')='N' ";

							for(int j=0;j<hvos.length;j++){
								SendnoticebillHVO hvo=hvos[j];
								String vbillno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillno"));
								String hpk=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_sendnoticebill"));

								wherepart_b=wherepart_b+" and pk_sendnoticebill='"+hpk+"' and pz='"+pk_pz+"'";

								SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, wherepart_b);
								if(null !=bvos && bvos.length>0){
									Boolean isClose=false;
									for(int k=0;k<bvos.length;k++){
										SendnoticebillBVO item=bvos[k];
										UFDouble i_gpprice=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("gpprice"));

										// TODO 价格不相等
										if(gpprice.doubleValue() !=i_gpprice.doubleValue()){
											isClose=true;
										}
										item.setAttributeValue("rowcloseflag", "Y");
										item.setAttributeValue("closer", InvocationInfoProxy.getInstance().getUserId());
										item.setAttributeValue("closetime", AppContext.getInstance().getServerTime());
										item.setAttributeValue("dr", 0);
										item.setAttributeValue("pk_sendnoticebill", hpk);
										bvos[k]=item;
									}
									// 通知单表体不为空，执行关闭操作
									hvo.setAttributeValue("closeflag", "Y");
									hvo.setAttributeValue("dr", 0);
									hvo.setAttributeValue("closer", InvocationInfoProxy.getInstance().getUserId());
									hvo.setAttributeValue("closetime", AppContext.getInstance().getServerTime());

									if(isClose){

										HYPubBO_Client.updateAry(bvos);
										HYPubBO_Client.update(hvo);

										if(null !=vnote && !"".equals(vnote)){								
											if(!vnote.contains(vbillno)){													
												vnote = vnote+","+vbillno;
											}
										}else{
											vnote = "关闭的发运通知单有："+vbillno;
										}
									}
								}							
							}
						}
					}
					// 记录价格政策有变动的发运通知单
					if(null !=vnote && !"".equals(vnote)){
						if(vnote.length()>=1000){							
							vnote = vnote.substring(0, 1000);
						}
						pricevo.setAttributeValue("vnote", vnote);
						pricevo.setAttributeValue("dr", 0);
						pricevo.setAttributeValue("def6", 1); // 记录已经执行该按钮操作
						HYPubBO_Client.update(pricevo);
					}
				}
		  }
		return null;
	}

	/**
	 * 价格政策表体： 矿别+煤种+业务类型 分组
	 * @param pbvos
	 * @return
	 */
	public List<String[]> getQryInfo(PricepolicyBVO[] pbvos){
		List<String[]> list=new ArrayList<String[]>();
		if(null != pbvos && pbvos.length>0){
			for(int i=0;i<pbvos.length;i++){
				String pk_mine=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("kbie"));
				String pk_pz=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("typegrp"));
				String pk_busitype=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("sktj"));
				String pk_pricepolicy=pbvos[i].getPk_pricepolicy();
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(pbvos[i].getAttributeValue("gpprice"));
				String[] strs=new String[5];
				strs[0]=pk_mine;
				strs[1]=pk_pz;
				strs[2]=pk_busitype;
				strs[3]=pk_pricepolicy;
				strs[4]=gpprice.toString();
				if(!list.contains(strs)){
					list.add(strs);
				}
			}
		}
		return list;
	}
	
}
