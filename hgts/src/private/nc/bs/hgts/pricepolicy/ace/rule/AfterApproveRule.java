package nc.bs.hgts.pricepolicy.ace.rule;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.trade.business.HYSuperDMO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pricepolicy.PricepolicyHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

public class AfterApproveRule implements IRule<AggPricepolicyHVO>{

	private BaseDAO dao = null;

	private BaseDAO getDao(){
		if(dao == null)
			dao = new BaseDAO();
		return dao;
	}
	public AfterApproveRule(){
		super();
	}

	@Override
	public void process(AggPricepolicyHVO[] vos) {
		try {
			//价格变动，自动关闭对应发运通知单
			this.qrySendbillToClose(vos);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public AggSendnoticebillHVO[] getAggSendnoticebillHVO() throws DAOException{
		AggSendnoticebillHVO[] aggvos=null;
		String sql_b="";
		HYSuperDMO dmo=new HYSuperDMO();
		SendnoticebillHVO[] hvos=(SendnoticebillHVO[]) dmo.queryByWhereClause(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillstatus=1 nvl(closeflag,'N')='N'");
		if(null !=hvos && hvos.length>0){
			aggvos=new AggSendnoticebillHVO[hvos.length];
			String hpk="";
			for(int i=0;i<hvos.length;i++){
				SendnoticebillHVO hvo=hvos[i];
				aggvos[i]=new AggSendnoticebillHVO();
				aggvos[i].setParent(hvo);

				hpk=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_sendnoticebill"));
				sql_b=" nvl(dr,0)=0 and nvl(rowcloseflag,'N')='N' and pk_sendnoticebill='"+hpk+"'";
				SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) dmo.queryByWhereClause(SendnoticebillBVO.class, sql_b);
				if(null !=bvos && bvos.length>0){
					aggvos[i].setChildrenVO(bvos);
				}
			}
		}

		return aggvos;
	}

	/**
	 * 2018-4-11
	 * @param vos
	 * @throws DAOException
	 * 关闭对应的发运通知单
	 */
	public void qrySendbillToClose(AggPricepolicyHVO[] vos) throws DAOException{
		if(null !=vos && vos.length>0){
			PricepolicyHVO phvo=vos[0].getParentVO();
			// 客户
			String pk_cust=HgtsPubTool.getStringNullAsTrim(phvo.getAttributeValue("pk_cust"));
			// 客户分类
			String pk_custclass=HgtsPubTool.getStringNullAsTrim(phvo.getAttributeValue("def1"));
			
			PricepolicyBVO[] pbvos=(PricepolicyBVO[]) vos[0].getChildrenVO();
			List<String[]> list=this.getQryInfo(pbvos);
			
			HYSuperDMO dmo=new HYSuperDMO();
			String vnote="";
			String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
			if(null !=list && list.size()>0){
				for(int i=0;i<list.size();i++){
					String[] str=list.get(i);
					String pk_mine=str[0];
					String pk_pz=str[1];
					String pk_busitype=str[2];
					String pk_pricepolicy=str[3];
					UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(str[4]);
					
					String wherepart=" nvl(dr,0)=0 and vbillstatus=1 "
							+ " and nvl(closeflag,'N')='N' "
							+ " and nvl(isbidding,'N')='N' "//过滤竞价来源的发运通知单
							+ " and	pk_fhkc='"+pk_mine+"' and pk_busitype='"+pk_busitype+"'"
							+ " and substr(startdate,0,10)<='"+curDate+"' and substr(enddate,0,10)>='"+curDate+"'";
					if(null !=pk_cust && !"".equals(pk_cust)){
						// 1、客户
						wherepart = wherepart +" and pk_cust='"+pk_cust+"' ";
					}else{
						if(null !=pk_custclass && !"".equals(pk_custclass)){
							// 2、客户分类
							wherepart = wherepart +" and pk_cust in (select pk_customer from bd_customer where nvl(dr,0)=0 and ecotypesincevfive='"+pk_custclass+"') ";
						}else{
							// 3、公共：条件即 wherepart		
							return;
						}
					}
				
					SendnoticebillHVO[] hvos=(SendnoticebillHVO[]) dmo.queryByWhereClause(SendnoticebillHVO.class,wherepart);
					if(null !=hvos && hvos.length>0){

						for(int j=0;j<hvos.length;j++){
							SendnoticebillHVO hvo=hvos[j];
							String vbillno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillno"));
							String hpk=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_sendnoticebill"));
							String wherepart_b=" nvl(dr,0)=0 and nvl(rowcloseflag,'N')='N' ";
							wherepart_b +=" and pk_sendnoticebill='"+hpk+"' and pz='"+pk_pz+"'";
							
							SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) dmo.queryByWhereClause(SendnoticebillBVO.class, wherepart_b);
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
									
									this.getDao().updateVOArray(bvos);
									this.getDao().updateVO(hvo);
									
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
					phvo.setAttributeValue("vnote", vnote);
					phvo.setAttributeValue("dr", 0);
					getDao().updateVO(phvo);
				}
			}
		}
	}
	
	/**
	 * 价格政策表体： 矿别+煤种+业务类型 分组
	 * @param pbvos
	 * @return
	 */
	public List<String[]> getQryInfo(PricepolicyBVO[] pbvos){
		//Map<String,String> maps=new HashMap<String,String>();
		List<String[]> list=new ArrayList<String[]>();
		if(null != pbvos && pbvos.length>0){
			for(int i=0;i<pbvos.length;i++){
				String pk_mine=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("kbie"));
				String pk_pz=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("typegrp"));
				String pk_busitype=HgtsPubTool.getStringNullAsTrim(pbvos[i].getAttributeValue("sktj"));
				String pk_pricepolicy=pbvos[i].getPk_pricepolicy();
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(pbvos[i].getAttributeValue("gpprice"));
				//String key=pk_mine+pk_pz;
				String[] strs=new String[5];
				strs[0]=pk_mine;
				strs[1]=pk_pz;
				strs[2]=pk_busitype;
				strs[3]=pk_pricepolicy;
				strs[3]=gpprice.toString();
				if(!list.contains(strs)){
					list.add(strs);
				}
			}
		}
		return list;
	}
	
	
}
