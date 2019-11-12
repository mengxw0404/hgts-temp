package nc.ui.hgts.sendcarlist.ref;

import java.awt.Container;

import nc.bs.logging.Logger;
import nc.ui.hgts.ff.pub.FfPubSourceRefDlg;
import nc.ui.pub.pf.BillSourceVar;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.hgts.dayplansend.DayplanSendBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.pubapp.AppContext;
import nc.vo.trade.pub.IBillStatus;

public class SourceRefDlgForSendCarList extends FfPubSourceRefDlg {

	private static final long serialVersionUID = 888125485880014429L;

	public SourceRefDlgForSendCarList(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);
		m_whereStr = getBillSourceVar().getQueryScheme().getWhereSQLOnly();
	}



	@Override
	public String getHeadCondition() {
		String curdate=AppContext.getInstance().getBusiDate().toString().substring(0, 10);
		String sql = " nvl(dr,0)=0 and vbillstatus = "+IBillStatus.CHECKPASS;
				//+ " and pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'";
				//+" and ('"+curdate+"' between substr(startdate,0,10) and substr(enddate,0,10))";

		StringBuffer str = new StringBuffer();
		str.append(sql);

		str.append(" and "+getHeadIDField()+" in ( select distinct "+getHeadIDField()+" from "+getBodyTableName()+"  where "+getBodyCondition()+")");

		return str.toString();

	}

	@Override
	public String getBodyCondition() {
		String sql="";
		if(this.getBodyTableName().equals("hgts_dayplansend_b")){

			sql = " hgts_dayplansend_b.plancars > nvl(hgts_dayplansend_b.def6,0) and nvl(hgts_dayplansend_b.dr,0)=0  ";
		}

		StringBuffer str = new StringBuffer();
		str.append(sql);
		return str.toString();

	}

	@Override
	protected String getHeadIDField() {
		// TODO 自动生成的方法存根
		return "pk_dayplansend";
	}



	@Override
	protected String getBodyIDField() {
		// TODO 自动生成的方法存根
		return "pk_dayplansend_b";
	}



	@Override
	protected String getBodyTableName() {
		// TODO 自动生成的方法存根
		return "hgts_dayplansend_b";
	}

	@Override
	public void loadBodyData(int arg0) {
		// TODO 自动生成的方法存根

		try {
			//获得主表ID
			String id = getbillListPanel().getHeadBillModel().getValueAt(arg0,
					getBillSourceVar().getPkField()).toString();
			//查询子表VO数组
			DayplanSendBVO[] bvos=(DayplanSendBVO[]) HYPubBO_Client.queryByCondition(DayplanSendBVO.class, getBodyCondition()+ "and nvl(dr,0)=0 and pk_dayplansend='"+id+"'");
			getbillListPanel().setBodyValueVO(bvos);
			getbillListPanel().getBodyBillModel().execLoadFormula();
			getbillListPanel().getBodyBillModel().loadLoadRelationItemValue();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}


	}


}
