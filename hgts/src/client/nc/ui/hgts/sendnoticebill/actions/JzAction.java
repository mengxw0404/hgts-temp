package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class JzAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2850671689154537767L;

	public JzAction(){
		super();
		super.setCode("jzAction");
		super.setBtnName("结转");
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
	 *  将剩余数量结转到目标 单据上；在本张单据上记录 目标单据号、行号。
	 *	剩余数量计算：本张 发运通知单 第一行数量减去表体累计过磅数数量。
	 *	追加到 目标单据上，最后一行 数量上（目前数量=原数量+结转数量）；同时启用一个自定义项目，记录调整数量。
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		AggSendnoticebillHVO data=(AggSendnoticebillHVO) this.getModel().getSelectedData();
		SendnoticebillHVO hvo=data.getParentVO();
		String startdate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("startdate")).substring(0, 10);
		String enddate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("enddate")).substring(0, 10);		
		String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		if(curDate.compareTo(enddate)>=1 || curDate.compareTo(startdate)<=-1){
			throw new BusinessException("此通知单不在有效期范围内，不允许结转操作！");
		}

		Object pk_org=hvo.getAttributeValue("pk_org");
		Object vbillno=hvo.getAttributeValue("vbillno");
		Object pk_cust=hvo.getAttributeValue("pk_cust");
		Object pk_kb=hvo.getAttributeValue("pk_fhkc");
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_transporttype"));
		String pk_dept=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_dept"));
		String pk_balatype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_balatype"));		
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) data.getTableVO("hgts_sendnoticebill_b");
		if(null !=bvos && bvos.length>0){
			UFDouble total_yzxnum=UFDouble.ZERO_DBL; // 累计已过磅数量
			String policy_billno="";	// 价格政策单据号
			String bpk="";				// 子表pk
			String pk_mz="";
			UFDouble gpprice=UFDouble.ZERO_DBL;//挂牌价格

			// 发运数量
			UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("shul"));
			UFDouble synum=UFDouble.ZERO_DBL;
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];

				String rowcloseflag=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("rowcloseflag"));
				if(null == rowcloseflag || "".equals(rowcloseflag) || "N".equals(rowcloseflag)){

					// 2018-9-5 校验计量单是否有空车
					String haveKC=isKongChe(bvo.getPrimaryKey());
					if(null !=haveKC && !"".equals(haveKC)){						
						throw new BusinessException("此通知单有未完成的过磅单 "+haveKC+"，不允许执行结转操作！");
					}

					policy_billno = HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("vsourcecode"));
					bpk=bvo.getPrimaryKey();
					pk_mz=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("pz"));
					UFDouble def6=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("def6")); // 已装车数
					gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice")); 

					// 路运
					if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){	
						UFDouble carnum=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("carnum")); // 车数
						UFDouble carstrong=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("carstrong")); // 标准车重
						UFDouble sy_carnum=carnum.sub(def6); // 剩余车数
						synum=sy_carnum.multiply(carstrong); // 剩余数量 

					}else{		
						// 2018-8-29 数据库里查询实时的已过磅量
						SendnoticebillBVO item=(SendnoticebillBVO) HYPubBO_Client.queryByPrimaryKey(SendnoticebillBVO.class, bvo.getPrimaryKey());
						total_yzxnum =HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("yzxnum")); // 已过磅数量
						shul=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("shul"));
						synum=shul.sub(total_yzxnum);	// 剩余数量 
					}
				}
			}


			//TODO  获取选择的目标单据
			String sql=this.getSql(pk_org, pk_cust, pk_kb, pk_mz, vbillno, policy_billno,pk_transporttype,pk_dept,pk_balatype,gpprice);

			ShowDataDialog rst=new ShowDataDialog(null, sql,getModel().getContext());
			rst.setVisible(true);

			String target_vbillno=rst.getVbillno();
			if(null == target_vbillno || "".equals(target_vbillno)){
				return;
			}
			SendnoticebillHVO[] tar_hvos=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+target_vbillno+"'");
			if(null == tar_hvos || tar_hvos.length==0){
				return ;
			}

			SendnoticebillHVO tar_hvo=tar_hvos[0];
			String t_hpk=tar_hvo.getPrimaryKey();
			Object t_vbillno=tar_hvo.getAttributeValue("vbillno");

			SendnoticebillBVO[] tar_bvos=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+t_hpk+"' and nvl(rowcloseflag,'N')='N'");
			if(null == tar_bvos || tar_bvos.length==0){
				return;
			}

			String t_bpk="";			
			for(int i=0;i<tar_bvos.length;i++){				
				SendnoticebillBVO tar_bvo=tar_bvos[i];
				t_bpk=tar_bvo.getPrimaryKey();
				UFDouble tar_shul=HgtsPubTool.getUFDoubleNullAsZero(tar_bvo.getAttributeValue("shul"));
				UFDouble tar_price=HgtsPubTool.getUFDoubleNullAsZero(tar_bvo.getAttributeValue("zxprice"));
				tar_shul = tar_shul.add(synum);				
				UFDouble tar_jstotal=tar_shul.multiply(tar_price);

				UFDouble def14=HgtsPubTool.getUFDoubleNullAsZero(tar_bvo.getAttributeValue("def14"));
				def14=def14.add(synum);

				// 路运
				if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){					
					UFDouble t_carstrong=HgtsPubTool.getUFDoubleNullAsZero(tar_bvo.getAttributeValue("carstrong"));
					if(t_carstrong.doubleValue() !=0){						
						UFDouble tar_carnum=tar_shul.div(t_carstrong);
						String[]str=tar_carnum.toString().split("[.]");
						UFDouble ches=HgtsPubTool.getUFDoubleNullAsZero(str[0]);
						tar_bvo.setAttributeValue("carnum", ches);		// 结转后的车数
					}
				}

				String def2=HgtsPubTool.getStringNullAsTrim(tar_bvo.getAttributeValue("def2"));// 当前结转的 子表主键
				if(null==def2 || "".equals(def2)){
					def2=bpk+",";
				}else{
					def2=def2+bpk+",";
				}

				String def4=HgtsPubTool.getStringNullAsTrim(tar_bvo.getAttributeValue("def4"));// 结转来源信息
				if(null==def4 || "".equals(def4)){
					def4=vbillno+":"+synum.setScale(3, UFDouble.ROUND_HALF_UP)+";";
				}else{
					def4=def4+vbillno+":"+synum.setScale(3, UFDouble.ROUND_HALF_UP)+";";
				}
				tar_bvo.setAttributeValue("shul", tar_shul);		// 结转后的数量
				tar_bvo.setAttributeValue("jstotal", tar_jstotal);	// 结转后的金额
				tar_bvo.setAttributeValue("def14", def14);			// TODO 记录  结转 过来的 数量
				tar_bvo.setAttributeValue("def2", def2);			// TODO 记录  当前结转的 子表主键
				tar_bvo.setAttributeValue("def4", def4);			// TODO 结转来源信息
				tar_bvo.setAttributeValue("dr", 0);
				tar_bvo.setAttributeValue("pk_sendnoticebill", t_hpk);
			}

			HYPubBO_Client.getService().updateAry(tar_bvos);     // 更新目标单据数据

			// 关闭当前单据: 整单关闭
			hvo.setAttributeValue("closeflag", "Y");
			hvo.setAttributeValue("closer", AppContext.getInstance().getPkUser());
			hvo.setAttributeValue("closetime", AppContext.getInstance().getServerTime());
			hvo.setAttributeValue("dr", 0);

			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				bvo.setAttributeValue("dr", 0);
				String rowcloseflag=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("rowcloseflag"));
				if(null == rowcloseflag || "".equals(rowcloseflag) || "N".equals(rowcloseflag)){					
					bvo.setAttributeValue("rowcloseflag", "Y");
					bvo.setAttributeValue("closer", AppContext.getInstance().getPkUser());
					bvo.setAttributeValue("closetime", AppContext.getInstance().getServerTime());
					bvo.setAttributeValue("def1", t_vbillno);	// TODO 记录结转到哪张发运通知单上了
					bvo.setAttributeValue("def3", t_bpk);		// TODO 记录目的单据的子表主键

					String cuserid=AppContext.getInstance().getPkUser();
					String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
					bvo.setAttributeValue("def20", "结转人："+username+"，结转时间："+AppContext.getInstance().getServerTime()+"；");
				}
			}

			HYPubBO_Client.getService().update(hvo);
			HYPubBO_Client.getService().updateAry(bvos);

			this.getRefreshAction().doAction(e);
		}
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggSendnoticebillHVO data=(AggSendnoticebillHVO) this.getModel().getSelectedData();
		SendnoticebillHVO hvo=data.getParentVO();

		String vbillstatus=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillstatus"));
		int status="".equals(vbillstatus)?-1:Integer.parseInt(vbillstatus);
		if(status !=1){
			return false;
		}

		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) data.getTableVO("hgts_sendnoticebill_b");
		int num=0;
		if(null !=bvos && bvos.length>0){
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				if(ValueUtils.getUFBoolean(bvo.getAttributeValue("rowcloseflag")).booleanValue()){
					num=num+1;
				}
			}
		}

		// 表体全部关闭了，该按钮不可用
		if(num==bvos.length){
			return false;
		}

		return true;
	}

	/** 
	 * 2018.8.30 
	 *  由 组织+客户+矿别+运输方式+部门+结算方式+煤种+价格政策单据号
	 *  改为  组织+客户+矿别+运输方式+部门+结算方式+煤种+挂牌价
	 **/
	public String getSql(Object pk_org,Object pk_cust,Object pk_kb,String pk_mz,
			Object vbillno,String policy_billno,String pk_transporttype,String pk_dept,String pk_balatype,Object obj){
		String sql="select h.pk_org,h.vbillno,h.dbilldate,h.pk_cust,h.pk_fhkc,b.pz,b.shul,b.gpprice "
				+" from hgts_sendnoticebill h inner join hgts_sendnoticebill_b b "
				+" on h.pk_sendnoticebill=b.pk_sendnoticebill "
				+" where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 " 
				+" and nvl(h.closeflag,'N')='N' and nvl(b.rowcloseflag,'N')='N' "
				+" and h.pk_org='"+pk_org+"' "
				+" and h.pk_cust='"+pk_cust+"'"
				+" and h.pk_fhkc='"+pk_kb+"'"
				+" and h.pk_transporttype='"+pk_transporttype+"'"
				+" and h.pk_dept='"+pk_dept+"'"
				+" and h.pk_balatype='"+pk_balatype+"'"
				+" and b.pz='"+pk_mz+"'"
				+" and h.vbillno <> '"+vbillno+"'"
				//	+" and b.vsourcecode='"+policy_billno+"'"
				+" and b.gpprice="+obj
				;

		return sql;
	}

	/**
	 * 判断是否有空车，如果有，不允许进行此动作，提示“此通知单有未完成过磅单，不允许执行！”
	 * 非审批通过态
	 */
	public String isKongChe(String pk_send_b){
		IUAPQueryBS bs=NCLocator.getInstance().lookup(IUAPQueryBS.class);		
		String sql="select vbillno from hgts_invoicesheet where nvl(dr,0)=0 and vbillstatus !=1 "
				+ " and pk_invoice in (select pk_invoice from hgts_invoicesheet_b "
				+ " where nvl(dr,0)=0 and csourcebid='"+pk_send_b+"')  ";
		String rst="";
		try {
			List<InvoicesheetHVO> list=(List<InvoicesheetHVO>) bs.executeQuery(sql, new BeanListProcessor(InvoicesheetHVO.class));
			if(null !=list && list.size()>0){
				for(int i=0;i<list.size();i++){
					InvoicesheetHVO vo=list.get(i);
					String vbillno=HgtsPubTool.getStringNullAsTrim(vo.getAttributeValue("vbillno"));
					rst = rst+vbillno+",";
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return rst;
	}

	/**
	 * 特殊处理
	 * 结转后,当前单据 才 执行关闭操作,所以在 算 发运单总数量时, 当前单据的 已执行数量 作为 占用数量
	 * @param bvos
	 * @param t_hpk : 目标单据的主表主键
	 * @param synum : 当前单据的剩余量
	 */
	public String isOver(SendnoticebillBVO[] bvos,String hpk,String t_hpk,UFDouble synum,
			String startdate,String month,String pk_cust,String pk_mine,String pk_transporttype){
		UFDouble yzxnum=UFDouble.ZERO_DBL;
		String pk_material=null;
		if(null !=bvos && bvos.length>0){
			pk_material =HgtsPubTool.getStringNullAsTrim(bvos[0].getAttributeValue("pz"));
			for(SendnoticebillBVO bvo:bvos){
				yzxnum= yzxnum.add(HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("yzxnum")));
			}
		}
		try {
			SendnoticebillBVO[] tar_bvos=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+t_hpk+"' ");
			UFDouble shul=UFDouble.ZERO_DBL;
			if(null !=tar_bvos && tar_bvos.length>0){
				for(SendnoticebillBVO bvo:tar_bvos){
					if(!ValueUtils.getUFBoolean(bvo.getAttributeValue("rowcloseflag")).booleanValue()){
						shul = shul.add(HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("shul")));
					}else{
						shul = shul.add(HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("yzxnum")));
					}
				}
			}

			CommActionCheck check=new CommActionCheck();
			boolean isSanH=check.isSanH(pk_cust);
			UFDouble sendTon=check.getBillTon(month, pk_cust, pk_mine, pk_material, pk_transporttype, hpk,t_hpk, "N",isSanH);
			UFDouble sendTon_close=check.getBillTon(month, pk_cust, pk_mine, pk_material, pk_transporttype, hpk,t_hpk, "Y",isSanH);
			// 2018年12月26日
			UFDouble hisMothNum=check.getBillHisMothTon(startdate, pk_cust, pk_mine, pk_material, pk_transporttype, isSanH);

			UFDouble sumTon=yzxnum.add(shul).add(synum).add(sendTon).add(sendTon_close).add(hisMothNum);
			UFDouble monthTon=check.getMthplanTon(month, pk_cust, pk_mine, pk_material, pk_transporttype, isSanH);
			if(sumTon.doubleValue()>monthTon.doubleValue()){
				boolean isControl=check.isContrMonthplan();
				if(isControl){
					return "通知单发运数量"+sumTon.setScale(3, UFDouble.ROUND_HALF_UP)+"超月计划数"+monthTon;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
