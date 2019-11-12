package nc.ui.hgts.pricepolicy.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pricepolicy.AggPricepolicyHVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pricepolicy.PricepolicyHVO;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class CloseSendbillAction extends NCAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5182981624840748040L;

	public CloseSendbillAction(){
		super();
		super.setCode("closeSendbillAction");
		super.setBtnName("关闭对应通知单");
	}

	private AbstractAppModel model;
	private BillForm editor;
	private RefreshSingleAction refreshAction;
	
	public RefreshSingleAction getRefreshAction() {
		return refreshAction;
	}

	public void setRefreshAction(RefreshSingleAction refreshAction) {
		this.refreshAction = refreshAction;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		AggPricepolicyHVO aggvo=(AggPricepolicyHVO) this.model.getSelectedData();
		
		qrySendbillToClose(aggvo);
		
		this.refreshAction.doAction(arg0);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggPricepolicyHVO aggvo=(AggPricepolicyHVO) this.model.getSelectedData();
		PricepolicyHVO hvo=aggvo.getParentVO();
		int vbillstatus=Integer.parseInt(hvo.getAttributeValue("vbillstatus").toString());
		if(vbillstatus !=1){
			return false;
		}
		return !ValueUtils.getUFBoolean(hvo.getAttributeValue("closeflag")).booleanValue();
	}

	
	/**
	 * 2018-4-11
	 * @param vos
	 * @throws DAOException
	 * 关闭对应的发运通知单
	 */
	public void qrySendbillToClose(AggPricepolicyHVO vos) throws Exception{
		PricepolicyHVO phvo=vos.getParentVO();
		// 客户
		String pk_cust=HgtsPubTool.getStringNullAsTrim(phvo.getAttributeValue("pk_cust"));
		// 客户分类
		String pk_custclass=HgtsPubTool.getStringNullAsTrim(phvo.getAttributeValue("def1"));

		// 执行时间
		String zxtime=HgtsPubTool.getStringNullAsTrim(phvo.getAttributeValue("zxtime")).substring(0, 10);
		String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		if(zxtime.compareTo(curDate)>=1){
			throw new BusinessException("当前日期未到此价格政策的执行日期，不允许此操作！");
		}
		
		PricepolicyBVO[] pbvos=(PricepolicyBVO[]) vos.getChildrenVO();
		List<String[]> list=this.getQryInfo(pbvos);

		String vnote="";
		if(null !=list && list.size()>0){
			for(int i=0;i<list.size();i++){
				String[] str=list.get(i);
				String pk_mine=str[0];
				String pk_pz=str[1];
				String pk_busitype=str[2];
				//String pk_pricepolicy=str[3];
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(str[4]);

				String wherepart=" nvl(dr,0)=0 and vbillstatus=1  and  isbidding != 'Y' "
						+ " and nvl(closeflag,'N')='N' "
						+ " and	pk_fhkc='"+pk_mine+"' and pk_busitype='"+pk_busitype+"'"
						// 在有效范围内
						+ " and (substr(startdate,0,10)<='"+curDate+"' and substr(enddate,0,10)>='"+curDate+"')";
				if(null !=pk_cust && !"".equals(pk_cust)){
					// 1、客户
					wherepart = wherepart +" and pk_cust='"+pk_cust+"' ";
				}else{
					if(null !=pk_custclass && !"".equals(pk_custclass)){
						// 2、客户分类
						wherepart = wherepart +" and pk_cust in (select pk_customer from bd_customer where nvl(dr,0)=0 and ecotypesincevfive='"+pk_custclass+"') ";
					}else{
						// 3、公共：条件即 wherepart							
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
				phvo.setAttributeValue("vnote", vnote);
				phvo.setAttributeValue("dr", 0);
				phvo.setAttributeValue("def6", 1); // 记录已经执行该按钮操作
				HYPubBO_Client.update(phvo);
			}
		}

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
