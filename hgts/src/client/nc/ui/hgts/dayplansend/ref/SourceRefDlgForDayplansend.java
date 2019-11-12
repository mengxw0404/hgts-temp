package nc.ui.hgts.dayplansend.ref;

import java.awt.Container;

import nc.bs.logging.Logger;
import nc.ui.hgts.ff.pub.FfPubSourceRefDlg;
import nc.ui.pub.pf.BillSourceVar;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.sendnoticebill.QualityIndicatorsBVO;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.XieYiBVO;
import nc.vo.pubapp.AppContext;
import nc.vo.trade.pub.IBillStatus;

public class SourceRefDlgForDayplansend extends FfPubSourceRefDlg {

	private static final long serialVersionUID = 888125485880014429L;

	public SourceRefDlgForDayplansend(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);


		m_whereStr = getBillSourceVar().getQueryScheme().getWhereSQLOnly();
	}



	@Override
	public String getHeadCondition() {
		String curdate=AppContext.getInstance().getBusiDate().toString().substring(0, 10);
		String sql = " isnull(dr,0)=0 and vbillstatus = "+IBillStatus.CHECKPASS
				//+" and closeflag='N'  "
				+ " and pk_transporttype='"+HgtsPubConst.TRANSPORT_QY+"'"
				+" and ('"+curdate+"' between substr(startdate,0,10) and substr(enddate,0,10))";

		StringBuffer str = new StringBuffer();
		str.append(sql);

		str.append(" and "+getHeadIDField()+" in ( select distinct "+getBodyIDField()+" from "+getBodyTableName()+"  where"+getBodyCondition()+")");

		return str.toString();

	}

	@Override
	public String getBodyCondition() {
		String sql="";
		if(this.getBodyTableName().equals("hgts_sendnoticebill_b")){

			sql = " hgts_sendnoticebill_b.shul > nvl(hgts_sendnoticebill_b.yzxnum,0) and nvl(hgts_sendnoticebill_b.rowcloseflag,'N')='N'";
		}

		StringBuffer str = new StringBuffer();
		str.append(sql);



		return str.toString();

	}

	@Override
	protected String getHeadIDField() {
		// TODO 自动生成的方法存根
		return "pk_sendnoticebill";
	}



	@Override
	protected String getBodyIDField() {
		// TODO 自动生成的方法存根
		return "pk_sendnoticebill";
	}



	@Override
	protected String getBodyTableName() {
		// TODO 自动生成的方法存根
		return "hgts_sendnoticebill_b";
	}




	@Override
	public void loadBodyData(int arg0) {
		// TODO 自动生成的方法存根

		try {
			//获得主表ID
			String id = getbillListPanel().getHeadBillModel().getValueAt(arg0,
					getBillSourceVar().getPkField()).toString();
			//查询子表VO数组
			SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+id+"'");
			SendYzyjBVO[] yzyjVOS=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+id+"'");
			XieYiBVO[] xyVOS=(XieYiBVO[]) HYPubBO_Client.queryByCondition(XieYiBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+id+"'");
			QualityIndicatorsBVO[] qbvos=(QualityIndicatorsBVO[]) HYPubBO_Client.queryByCondition(QualityIndicatorsBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+id+"'");


			getbillListPanel().setBodyValueVO("hgts_sendnoticebill_b", bvos);
			getbillListPanel().setBodyValueVO("pk_xy_b", xyVOS);
			getbillListPanel().setBodyValueVO("pk_yzyj_b", yzyjVOS);
			getbillListPanel().setBodyValueVO("pk_quality_b", qbvos);
			getbillListPanel().getBodyBillModel().execLoadFormula();
			getbillListPanel().getBodyBillModel().loadLoadRelationItemValue();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}


	}


}
