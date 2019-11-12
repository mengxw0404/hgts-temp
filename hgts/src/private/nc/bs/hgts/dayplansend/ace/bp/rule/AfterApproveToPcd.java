package nc.bs.hgts.dayplansend.ace.bp.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.hgts.sendcarlist.ace.bp.AceSendcarlistInsertBP;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.vo.hgts.dayplansend.AggDayplanSendHVO;
import nc.vo.hgts.dayplansend.DayplanSendBVO;
import nc.vo.hgts.dayplansend.DayplanSendHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendcarlist.AggSendCarListHVO;
import nc.vo.hgts.sendcarlist.SendCarListBVO;
import nc.vo.hgts.sendcarlist.SendCarListHVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 * 审核后生成 派车单（ 日计划发运明细单）
 * @author cl
 * 2019年6月19日
 */
public class AfterApproveToPcd implements IRule<AggDayplanSendHVO> {

	private BaseDAO dao = null;
	private BaseDAO getDao(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	public AfterApproveToPcd() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void process(AggDayplanSendHVO[] vos) {
		if(null !=vos && vos.length>0){
			//FormulaParseTool tool=new FormulaParseTool();
			for(AggDayplanSendHVO aggvo:vos){
				DayplanSendHVO head=aggvo.getParentVO();
				// 审核通过
				if(Integer.parseInt(head.getAttributeValue("vbillstatus").toString())==ApproveStatus.APPROVED){
					DayplanSendBVO[] bodys=(DayplanSendBVO[]) aggvo.getChildrenVO();

					// 1、目标单据聚合vo：日计划发运单明细
					AggSendCarListHVO[] bills=new AggSendCarListHVO[bodys.length]; 
					for(int i=0;i<bodys.length;i++){
						DayplanSendBVO item=bodys[i];
						Integer plancars=Integer.parseInt(item.getAttributeValue("plancars").toString());

						// 2、目标单据主表vo
						SendCarListHVO hvo=new SendCarListHVO();
						hvo.setAttributeValue("dbilldate", null == head.getAttributeValue("plansenddate")?head.getAttributeValue("dbilldate"):head.getAttributeValue("plansenddate"));
						hvo.setAttributeValue("pcdate", head.getAttributeValue("plansenddate"));
						hvo.setAttributeValue("pk_dept", head.getAttributeValue("pk_dept"));
						hvo.setAttributeValue("orgman", head.getAttributeValue("orgman"));
						hvo.setAttributeValue("pk_group", head.getAttributeValue("pk_group"));
						hvo.setAttributeValue("pk_org", head.getAttributeValue("pk_org"));
						hvo.setAttributeValue("pk_org_v", head.getAttributeValue("pk_org_v"));
						hvo.setAttributeValue("vbillstatus", ApproveStatus.FREE);
						hvo.setAttributeValue("pk_billtypeid", HgtsPubConst.PCD);
						hvo.setAttributeValue("pk_billtypecode", "YPCD");
						hvo.setAttributeValue("creator", AppContext.getInstance().getPkUser());
						hvo.setAttributeValue("creationtime", AppContext.getInstance().getServerTime());
						
						// 值来源于日计划发运单表体
						hvo.setAttributeValue("sendbillno", item.getAttributeValue("sendnoticebillno"));
						hvo.setAttributeValue("pk_mine", item.getAttributeValue("pk_mine"));
						hvo.setAttributeValue("pk_inv", item.getAttributeValue("pk_material"));
						hvo.setAttributeValue("pk_cust", item.getAttributeValue("pk_cust"));
						hvo.setAttributeValue("plancars", plancars);
						hvo.setAttributeValue("pk_stordoc", item.getAttributeValue("pk_stordoc"));
						hvo.setAttributeValue("syl", item.getAttributeValue("syl"));
						hvo.setAttributeValue("lxr", item.getAttributeValue("lxr"));
						hvo.setAttributeValue("tel", item.getAttributeValue("tel"));
						hvo.setAttributeValue("isoverload", item.getAttributeValue("isoverload"));
						hvo.setAttributeValue("note", item.getAttributeValue("vrownote"));
						//计划发运数
						hvo.setAttributeValue("plansendnum",HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("plansendnum")));
						
						// 3、目标单据子表vo 长度= 计划车数
						SendCarListBVO[] bvos=new SendCarListBVO[plancars];
						for(int j=0;j<plancars;j++){
							SendCarListBVO bvo=new SendCarListBVO();
							bvo.setAttributeValue("crowno", (j+1)*10);
							bvo.setAttributeValue("vsourcecode", head.getAttributeValue("vbillno"));
							bvo.setAttributeValue("csourceid", head.getPrimaryKey());
							bvo.setAttributeValue("csourcebid", item.getPrimaryKey());
							bvos[j]=bvo;
							
						}
						bills[i]=new AggSendCarListHVO();
						bills[i].setParentVO(hvo);
						bills[i].setChildrenVO(bvos);

					}

					try {
						// 4、插入数据库
						AceSendcarlistInsertBP action = new AceSendcarlistInsertBP();
						AggSendCarListHVO[] retvos = action.insert(bills);
						// 5、回写日计划发运单
						if(null !=retvos && retvos.length>0){
							for(AggSendCarListHVO bill:retvos){
								SendCarListHVO hvo=(SendCarListHVO) bill.getParentVO();
								String vbillno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("vbillno"));
								SendCarListBVO[] bvos=(SendCarListBVO[]) bill.getChildrenVO();

								String sql="update hgts_dayplansend_b set dayplandetailno='"+vbillno+"' where pk_dayplansend_b='"+bvos[0].getAttributeValue("csourcebid")+"'";
								getDao().executeUpdate(sql);

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
