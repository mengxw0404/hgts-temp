package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;


public class QryPriceAction extends NCAction {

	private static final long serialVersionUID = 3683757158465633684L;
	
	public QryPriceAction(){
		super();
		super.setCode("qryPriceAction");
		super.setBtnName("取价");
	}

	private AbstractAppModel model;
	private RefreshSingleAction refreshAction;
	
	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public RefreshSingleAction getRefreshAction() {
		return refreshAction;
	}

	public void setRefreshAction(RefreshSingleAction refreshAction) {
		this.refreshAction = refreshAction;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		AggSendnoticebillHVO billVO=(AggSendnoticebillHVO) this.model.getSelectedData();
		SendnoticebillHVO hvo=billVO.getParentVO();
		if("Y".equals(HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("isbidding")))){
			MessageDialog.showWarningDlg(null, "错误", "来源合同为竞价合同，不可进行取价变更。");
			return;
		}
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_transporttype"));
		SendnoticebillBVO[] sbvos=(SendnoticebillBVO[]) billVO.getTableVO("hgts_sendnoticebill_b"); 
		UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(sbvos[0].getAttributeValue("jstotal"));
		
		AggSendnoticebillHVO clientVO=(AggSendnoticebillHVO) billVO.clone();
		SendnoticebillHVO chvo=clientVO.getParentVO();
		chvo.setAttributeValue("startdate", AppContext.getInstance().getBusiDate());
		clientVO.setParentVO(chvo);
		IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
		clientVO=(AggSendnoticebillHVO) col.colPrice(clientVO);
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) clientVO.getTableVO("hgts_sendnoticebill_b"); 
		if(null !=bvos && bvos.length>0){
			UFDouble newprice=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("zxprice"));
			
			UFDouble num=UFDouble.ZERO_DBL;			// 数量
			UFDouble carstrong=UFDouble.ZERO_DBL;	// 标准车重
			if(null !=pk_transporttype && HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){//铁路运
				UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("cyfee"));
				UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("def11"));
				UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("def12"));

				newprice = newprice.add(cyfee).add(zyxfee).add(qscfee);
				
				carstrong=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("carstrong"));
				
			}
			UFDouble ches=UFDouble.ZERO_DBL;// 车数
			//判断当前单据校验方式  1==金额校验
			if(hvo.getAttributeValue("checktype").equals("1")){
				if(newprice.doubleValue() !=0){	
					num=jstotal.div(newprice);
				}
				if(carstrong.doubleValue() !=0){
					// 取模
					UFDouble carnum=num.div(carstrong);
					String[]str=carnum.toString().split("[.]");
					ches=HgtsPubTool.getUFDoubleNullAsZero(str[0]);
				}
			}else{
				num = HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("shul"));
				jstotal=num.multiply(newprice);
			}

			// 
			bvos[0].setAttributeValue("jstotal", jstotal);
			bvos[0].setAttributeValue("zxprice", newprice);
			bvos[0].setAttributeValue("shul", num);
			bvos[0].setAttributeValue("carnum", ches);
			bvos[0].setAttributeValue("dr", 0);
			
			// 2018-2-26 取价人+取价时间
			//String def18=HgtsPubTool.getStringNullAsTrim(bvos[0].getAttributeValue("def18"));
			String cuserid=AppContext.getInstance().getPkUser();
			String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
			bvos[0].setAttributeValue("def18", "取价人："+username+"，取价时间："+AppContext.getInstance().getServerTime());
			
			// 2018-10-11 begin
			CommActionCheck check=new CommActionCheck();
			clientVO.setChildrenVO(bvos);
			String rst=check.isOver(clientVO);
			if(null !=rst && !"".equals(rst)){
				throw new BusinessException(rst);
			}
			// 2018-10-11 end
			
			HYPubBO_Client.getService().update(bvos[0]);
			
			this.getRefreshAction().doAction(e);
		}
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggSendnoticebillHVO billVO=(AggSendnoticebillHVO) this.model.getSelectedData();
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) billVO.getTableVO("hgts_sendnoticebill_b"); 
		if(null==bvos || bvos.length==0){
			return false;
		}
		if(null !=bvos && bvos.length>=2){
			return false;
		}
		
		if(bvos.length==1){
			UFDouble jsnum=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("yjsnum"));
			// 已有结算数量，不可用
			if(jsnum.doubleValue()!=0){
				return false;
			}			
		}
		return true;
	}

	
	
}
