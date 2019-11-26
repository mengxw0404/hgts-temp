package nc.ui.hgts.sopact.ace.handler;

import java.sql.SQLException;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sopact.AggPactVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.BusinessException;
/**
 * 合同表头编辑后事件
 * @author TR
 *
 */
public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {

	public AceHeadTailAfterEditHandler() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// TODO 自动生成的方法存根
		BillCardPanel panel = e.getBillCardPanel();
		String pk_org=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_org").getValueObject());
		String pk_cust=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("cust").getValueObject());
		String pk_busitype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_busitype").getValueObject());
		String pk_balatype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_balatype").getValueObject());

		if(!"".equals(pk_cust)  && !"".equals(pk_busitype)  && !"".equals(pk_balatype)){
			int rowcount=panel.getBillModel("pk_pact_b").getRowCount();
			if(rowcount>0){
				String jjpriceno=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("jjpriceno").getValueObject());
				if(null !=jjpriceno && !"".equals(jjpriceno)){
					return;
				}
				
				for(int i=0;i<rowcount;i++){
					String pk_inv=HgtsPubTool.getStringNullAsTrim(panel.getBillModel("pk_pact_b").getValueAt(i, "inv"));
					String pk_kb=HgtsPubTool.getStringNullAsTrim(panel.getBillModel("pk_pact_b").getValueAt(i, "kuang"));
					if(!"".equals(pk_inv) && !"".equals(pk_kb)){
						AggPactVO aggvo=(AggPactVO) panel.getBillValueVO(AggPactVO.class.getName(), PactVO.class.getName(), PactBVO.class.getName());
						IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
						try {
							aggvo=(AggPactVO) col.colPrice(aggvo);
							panel.getBillModel("pk_pact_b").setBodyDataVO(aggvo.getTableVO("pk_pact_b"));
						} catch (BusinessException e1) {
							e1.printStackTrace();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		
		//是否自提
		else if(e.getKey().equals("type") && e.getValue().toString().equals("true")){
			e.getBillCardPanel().getHeadItem("yfxycode").setNull(true);
		}
		
	}

}
