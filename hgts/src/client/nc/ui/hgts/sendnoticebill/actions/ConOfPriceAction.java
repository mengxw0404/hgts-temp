package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.pubapp.util.VOSortUtils;

public class ConOfPriceAction  extends NCAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5296203813087965366L;

	public ConOfPriceAction(){
		super();
		super.setCode("ConOfPriceAction");
		super.setBtnName("取价折吨");
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
	
	/**
	 * 结转行：1、 价税合计 = 最开始的价税合计（新增保存时的）-sum(每一行的已过磅数量 * 执行价格)；
	 * 2、查询新的价格政策：计算新的执行价格
	 * 3、数量 = 价税合计除以执行价格
	 */
	@Override
	public void doAction(ActionEvent event) throws Exception {
		// TODO 此次补丁注释,补丁后放开此注释 
		AggSendnoticebillHVO data=(AggSendnoticebillHVO) this.getModel().getSelectedData();
		SendnoticebillHVO hvo=data.getParentVO();
		//发送单来源判断
		if("Y".equals(HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("isbidding")))){
			MessageDialog.showWarningDlg(null, "错误", "来源合同为竞价合同，不可进行取价变更。");
			return;
		}
		//获取时间信息，判断单据可操作性
		String startdate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("startdate")).substring(0, 10);
		String enddate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("enddate")).substring(0, 10);		
		String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		if(curDate.compareTo(enddate)>=1 || curDate.compareTo(startdate)<=-1){
			throw new BusinessException("此通知单不在有效期范围内，不允许折吨操作！");
		}
		
		//获取运输方式
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_transporttype"));	
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) data.getTableVO("hgts_sendnoticebill_b");
	
		//创建新子数据对象
		List<SendnoticebillBVO> list=new ArrayList<SendnoticebillBVO>();
		//最新子数据
		SendnoticebillBVO[] blavo= (SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, "nvl(dr,0) = 0 and blatest = 'Y' and  pk_sendnoticebill ='"+hvo.getPrimaryKey()+"'");
		
		if(null !=bvos && bvos.length>0){
			//关闭现有子表数据
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				bvo.setAttributeValue("dr", 0);
				bvo.setAttributeValue("blatest", "N");
				list.add(bvo);
			}
			// 查询新的挂牌价，优惠的浮动价格，算出最终的执行价格，再算重量		
			AggSendnoticebillHVO clientVO=(AggSendnoticebillHVO) data.clone();
			SendnoticebillHVO chvo=clientVO.getParentVO();
			clientVO.setParentVO(chvo);
			clientVO.setChildrenVO(blavo);
			IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
			//获取一个返回结果，其中子数据为信息的挂牌价格
			AggSendnoticebillHVO aggvo=(AggSendnoticebillHVO) col.colPrice(clientVO);
			SendnoticebillBVO[] newbvos=(SendnoticebillBVO[]) aggvo.getTableVO("hgts_sendnoticebill_b");
			if(null !=newbvos && newbvos.length>0){
				UFDouble newprice=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("zxprice"));
				UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("jstotal"));
				
				UFDouble num=UFDouble.ZERO_DBL;			// 数量
				UFDouble carstrong=UFDouble.ZERO_DBL;	// 标准车重
				if(null !=pk_transporttype && HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){//铁路运
					UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("cyfee"));
					UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def11"));
					UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def12"));

					newprice = newprice.add(cyfee).add(zyxfee).add(qscfee);
					
					carstrong=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("carstrong"));
					
				}
				UFDouble ches=UFDouble.ZERO_DBL;// 车数
				
				UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("shul"));
				if(aggvo.getParentVO().getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx) && shul.compareTo(new UFDouble(60)) > 0 ){
					//数量校验 （总量保持不变） 金额 = 数量 * 新单价
					num = HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("shul"));
					jstotal=num.multiply(newprice);
				}else{
					//金额校验 : 数量 =  金额 / 新单价
					if(newprice.doubleValue() !=0)			
						num=jstotal.div(newprice);
					if(carstrong.doubleValue() !=0){
						// 取模
						UFDouble carnum=num.div(carstrong);
						String[]str=carnum.toString().split("[.]");
						ches=HgtsPubTool.getUFDoubleNullAsZero(str[0]);
					}
				}
//	
				UFDouble rowno =HgtsPubTool.getUFDoubleNullAsZero(blavo[0].getAttributeValue("rowno"));
				newbvos[0].setAttributeValue("rowno", (rowno.add(10).intValue()));
				newbvos[0].setAttributeValue("shul", num);
				newbvos[0].setAttributeValue("jstotal", jstotal);
				newbvos[0].setAttributeValue("zxprice", newprice);
				newbvos[0].setAttributeValue("carnum", ches);
				newbvos[0].setAttributeValue("dr", 0);
				newbvos[0].setAttributeValue("rowcloseflag", "N");
				newbvos[0].setAttributeValue("blatest", "Y");	// 最新版本
				newbvos[0].setAttributeValue("closer", null);
				newbvos[0].setAttributeValue("closetime", null);
				newbvos[0].setAttributeValue("opener", null);
				newbvos[0].setAttributeValue("opentime", null);
				newbvos[0].setAttributeValue("pk_sendnoticebill_b", null);
				newbvos[0].setAttributeValue("dr", 0);
//				newbvos[0].setAttributeValue("yzxnum", null);
				newbvos[0].setAttributeValue("def19", null);    // 折吨人+折吨时间；取消折吨人+时间
//				newbvos[0].setAttributeValue("def6", null); 	// 装车计划已装车数
//				newbvos[0].setAttributeValue("yjsnum", null); 	// 已结算数量
//				newbvos[0].setAttributeValue("ykpnum", null); 	// 已开票数量
//				newbvos[0].setAttributeValue("mny", null); 		// 已开票金额

				newbvos[0].setAttributeValue("def1", null); 	// 记录结转到目的单据的单据号
				newbvos[0].setAttributeValue("def2", null);		// 记录  当前结转的 子表主键
				newbvos[0].setAttributeValue("def3", null);		// 记录  当前结转到目的单据的 子表主键
				newbvos[0].setAttributeValue("def4", null);     // 记录结转的单据号，结转数量
				newbvos[0].setAttributeValue("def14", null);	// 记录结转过来的数量
				newbvos[0].setAttributeValue("def18", null);	// 取价人+取价时间
				newbvos[0].setAttributeValue("def20", null);	// 结转人+结转时间；取消结转人+取消结转时间

				String cuserid=AppContext.getInstance().getPkUser();
				String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
				newbvos[0].setAttributeValue("def18", "取价人："+username+"，取价时间："+AppContext.getInstance().getServerTime());
				// 2018-10-11 begin检查  通知单数量，是否超 月请车计划数
				CommActionCheck check=new CommActionCheck();			
				list.add(newbvos[0]);
				clientVO.setChildrenVO(list.toArray(new SendnoticebillBVO[0]));
				String rst=check.isOver(clientVO);
				if(null !=rst && !"".equals(rst)){
					throw new BusinessException(rst);
				}
				// 2018-10-11 end
				HYPubBO_Client.getService().insert(newbvos[0]);
				
			}
			// 2018-2-26 begin 变更前一版最近数据
			//VOSortUtils.ascSort(blavo, new String[]{"rowno"});
			String cuserid=AppContext.getInstance().getPkUser();
			String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
			blavo[0].setAttributeValue("def19", "折吨人："+username+"，折吨时间："+AppContext.getInstance().getServerTime()+"；");
			blavo[0].setAttributeValue("dr", 0);
			blavo[0].setAttributeValue("blatest", "N");
			blavo[0].setAttributeValue("rowcloseflag", "Y");
			HYPubBO_Client.getService().updateAry(blavo);
			// 2018-2-26 end 
			if(ValueUtils.getUFBoolean(hvo.getAttributeValue("closeflag")).booleanValue()){					
				hvo.setAttributeValue("closeflag", "N");
				hvo.setAttributeValue("dr", 0);
				hvo.setAttributeValue("closer",null);
				hvo.setAttributeValue("closetime", null);
				hvo.setAttributeValue("opener", AppContext.getInstance().getPkUser());
				hvo.setAttributeValue("opentime", AppContext.getInstance().getServerTime());
				HYPubBO_Client.update(hvo);
			}
		}
	
		//刷新
		this.getRefreshAction().doAction(event);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggSendnoticebillHVO billVO=(AggSendnoticebillHVO) this.model.getSelectedData();
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) billVO.getTableVO("hgts_sendnoticebill_b"); 
		VOSortUtils.ascSort(bvos, new String[]{"rowno"});
		UFBoolean blatest=UFBoolean.FALSE;
		UFBoolean isJz=UFBoolean.FALSE;
		if(null !=bvos && bvos.length>0){
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				//行关闭 && 最新版本
				if(ValueUtils.getUFBoolean(bvo.getAttributeValue("rowcloseflag")).booleanValue() 
						&& ValueUtils.getUFBoolean(bvo.getAttributeValue("blatest")).booleanValue()){
					blatest = UFBoolean.TRUE;
				}
			}
			String def1=HgtsPubTool.getStringNullAsTrim(bvos[bvos.length-1].getAttributeValue("def1")); // 记录结转到目的单据的单据号
			if(null ==def1 || "".equals(def1)){
				isJz=UFBoolean.TRUE;
			}
		}
		// 行关闭 && 最新版本，并且未进行结转操作，该按钮 才可用
		if(blatest.booleanValue()  && isJz.booleanValue()){
			return true;
		}
		return false;
	}
	
}
