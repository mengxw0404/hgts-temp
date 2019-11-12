package nc.ui.hgts.sopact.ace.handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.hgts.sopact.ace.parent.handler.ParentAceBodyAfterEditHandler;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sopact.AggPactVO;
import nc.vo.hgts.sopact.ContQualityBVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.BusinessException;

public class AceBodyAfterEditHandler extends ParentAceBodyAfterEditHandler {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO 自动生成的方法存根
		super.handleAppEvent(e);
		BillCardPanel panel = e.getBillCardPanel();
		if(e.getKey().equals("inv")){
			//选择煤种后，自动带出价格政策中的价格
			Object pk_inv=e.getValue();
			String pk_org=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_org").getValueObject());
			String pk_cust=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("cust").getValueObject());//需方
			String pk_busitype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_busitype").getValueObject());
			String pk_balatype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_balatype").getValueObject());
			String kb=HgtsPubTool.getStringNullAsTrim(panel.getBillModel("pk_pact_b").getValueAt(e.getRow(), "kuang"));
			if(/*!"".equals(pk_org) &&*/  !"".equals(pk_cust) 
					&& !"".equals(pk_busitype) && !"".equals(kb) && !"".equals(pk_inv)
					&& !"".equals(pk_balatype)){
				AggPactVO aggvo=(AggPactVO) panel.getBillValueVO(AggPactVO.class.getName(), PactVO.class.getName(), PactBVO.class.getName());
				IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
				try {
					//此处将价格政策写入获得的VO
					aggvo=(AggPactVO) col.colPrice(aggvo);
					//TODO 需获取aggvo.getTableVO("pk_pact_b")对象，将含税单价==挂牌价格，并重新计算金额。税额
					
					panel.getBillModel("pk_pact_b").setBodyDataVO(aggvo.getTableVO("pk_pact_b"));
				} catch (BusinessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			// 2、质量指标页签是否自动取数
			int rowcount=e.getBillCardPanel().getBillModel("pk_cont_zlzb_b").getRowCount();
			if(rowcount>0){
				return;
			}
			String sql="select b.prjcode,b.prjvalue from hgts_qualityproject h "
					+" inner join hgts_qualityprojectmx b "
					+" on h.id = b.id "
					+" where nvl(h.dr, 0) = 0 and nvl(b.dr, 0) = 0 "
					+" and h.variety='"+pk_inv+"'"
					+" and h.pk_org='"+pk_org+"'";
			
			IUAPQueryBS bs=NCLocator.getInstance().lookup(IUAPQueryBS.class);
			try {
				List list =(List) bs.executeQuery(sql, new ArrayListProcessor());
				if(null !=list && list.size()>0){	
					List<ContQualityBVO> list_bvo=new ArrayList<ContQualityBVO>();
					for(int i = 0;i<list.size();i++){
						Object[] results =  (Object[]) list.get(i);
						String prjcode=HgtsPubTool.getStringNullAsTrim(results[0]);
						String prjvalue=HgtsPubTool.getStringNullAsTrim(results[1]);
						
						ContQualityBVO bvo=new ContQualityBVO();
						bvo.setAttributeValue("crowno", (i+1)*10);
						bvo.setAttributeValue("pk_quaprj", prjcode);
						bvo.setAttributeValue("prjvalue", prjvalue);
						
						list_bvo.add(bvo);
					}
					
					// pk_cont_zlzb_b
					panel.getBillModel("pk_cont_zlzb_b").setBodyDataVO(list_bvo.toArray(new ContQualityBVO[0]));
					panel.getBillModel("pk_cont_zlzb_b").loadLoadRelationItemValue();
				}
			} catch (BusinessException e1) {
				e1.printStackTrace();
			}
		
		}else if(e.getKey().equals("bkdrule")){
			if(!e.getValue().equals("2")){ // 扣吨
				panel.getBillModel().setCellEditable(e.getRow(), "batchcode", false);
				panel.getBillModel().setValueAt(null, e.getRow(), "batchcode");
			}else{
				panel.getBillModel().setCellEditable(e.getRow(), "batchcode", true);
			}
		}
	}
}
