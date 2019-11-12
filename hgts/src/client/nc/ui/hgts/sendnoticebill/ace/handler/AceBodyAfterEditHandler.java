package nc.ui.hgts.sendnoticebill.ace.handler;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sopact.ContQualityBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.ValueUtils;

/**
 *���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();
		String pk_org=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_org").getValueObject());
		String pk_cust=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_cust").getValueObject());
		String pk_busitypeid=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_busitype").getValueObject());
		String kb=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_fhkc").getValueObject());
		// 2017-10-17 ����֪ͨ�� ������ʼ���ڡ�Ϊ׼ȡ�۸����� 
		String dbilldate=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("startdate").getValueObject());
		String pk_balatype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_balatype").getValueObject());
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("pk_transporttype").getValueObject()); // ���䷽ʽ�����ˡ�·��
		String pz=HgtsPubTool.getStringNullAsTrim(panel.getBodyValueAt(e.getRow(), "pz"));		
		String contcode=HgtsPubTool.getStringNullAsTrim(panel.getHeadItem("contcode").getValueObject());
	//--- ���պ�ͬ,�۸�ֱ�ӴӺ�ͬ�ϴ���������ȡ�۸�����
		if(null==contcode || "".equals(contcode)){	
			if(e.getKey().equals("pz")){
				if(/*!"".equals(pk_org) &&*/  !"".equals(pk_cust) 
						&& !"".equals(pk_busitypeid) && !"".equals(kb) && !"".equals(pz)
						&& !"".equals(pk_balatype)){

					String base=" where nvl(dr,0)=0 and kbie='"+kb+"' "
							+" and typegrp='"+pz+"'" 
							+" and sktj='"+pk_busitypeid+"' ";
					String coditions=" select gpprice from hgts_pricepolicy_b "
							+ base
							+ " and pk_pricepolicy in (select pk_pricepolicy from (select pk_pricepolicy from hgts_pricepolicy "
							+ " where nvl(dr,0)=0 and vbillstatus=1 and (closeflag='N' or closeflag is null ) "
							+ " and zxtime is not null "
							//+ " and pk_org='"+pk_org+"' "
							+ " and substr(zxtime,1,10) <='"+dbilldate.substring(0, 10)+"'"
							+ " and pk_pricepolicy in (select distinct pk_pricepolicy from hgts_pricepolicy_b "
							+ base+" )";
					String codition=" and pk_cust='"+pk_cust+"' ";
					String orderfiled=" order by zxtime desc ) where rownum=1 ) ";
					String sql=coditions+codition+orderfiled;
					IUAPQueryBS bs=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
					try {
						List<PricepolicyBVO> list=(List<PricepolicyBVO>) bs.executeQuery(sql, new BeanListProcessor(PricepolicyBVO.class));
						UFDouble gpprice=new UFDouble();
						PricepolicyBVO bvo=null;
						if(null !=list && list.size()>0){
							bvo=list.get(0);
							gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));

						}else{
							// 2���ͻ�����۸�
							String strWhere=" and def1=(select ecotypesincevfive from bd_customer where nvl(dr,0)=0 and pk_customer='"+pk_cust+"')";
							String ssql=coditions+strWhere+orderfiled;
							list=(List<PricepolicyBVO>) bs.executeQuery(ssql, new BeanListProcessor(PricepolicyBVO.class));
							if(null !=list && list.size()>0){
								bvo=list.get(0);
								gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
							}else{
								// 3�������۸�
								// 2017-10-12 modify ���� and pk_cust=''������������Ҽ۸����ߵ�ʱ�򣬲��ҵ��������ͻ��ļ۸�����
								String s_sql=coditions+" and pk_cust='~' and (def1 is null or def1='~')"+orderfiled;
								list=(List<PricepolicyBVO>) bs.executeQuery(s_sql, new BeanListProcessor(PricepolicyBVO.class));
								if(null !=list && list.size()>0){
									bvo=list.get(0);
									gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
								}else{
									MessageDialog.showHintDlg(null, "��ʾ", "δ�����Ӧ�ļ۸�����");
								}
							}
						}
						if(gpprice.doubleValue()!=0){										
							AggSendnoticebillHVO aggvo=(AggSendnoticebillHVO) panel.getBillValueVO(AggSendnoticebillHVO.class.getName(), SendnoticebillHVO.class.getName(), SendnoticebillBVO.class.getName());
							IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
							aggvo=(AggSendnoticebillHVO) col.colPrice(aggvo);
							panel.getBillModel("hgts_sendnoticebill_b").setBodyDataVO(aggvo.getTableVO("hgts_sendnoticebill_b"));

							// 2017-10-30
							UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "cyfee"));
							this.mnyCol(e.getRow(), panel, pk_transporttype,pk_cust, kb, pz,cyfee);

						}else{
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "gpprice");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "fkfsyh");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "ljyh");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "qyyh");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "def13");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "zxprice");
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(null, e.getRow(), "jstotal");
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				this.setZlzb(e, panel, pz, pk_org);
			}
		}
		if(e.getKey().equals("shul")){ 	//·��:��������, �����˰�ϼơ�����
			UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "zxprice"));
				UFDouble jstotal=UFDouble.ZERO_DBL;
				if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){
					if(zxprice.doubleValue()!=0){						
						jstotal=shul.multiply(zxprice);
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal, e.getRow(), "jstotal");
					}else{
						if(shul.doubleValue()!=0){							
							panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.div(shul), e.getRow(), "zxprice");
							
							UFDouble fkyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "fkfsyh"));
							UFDouble qyyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "qyyh"));
							UFDouble ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "def13"));
							UFDouble gpprice=jstotal.div(shul).add(fkyh.abs()).add(qyyh.abs()).add(ysfsyh.abs());
							panel.getBillModel().setValueAt(gpprice, e.getRow(), "gpprice");
						}
					}
				}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
					//UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "cyfee"));
					//UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "def11"));
					//UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "def12"));
					CommQs comm=new CommQs();
					zxprice=comm.getZxprice(e.getRow(), panel)/*.add(cyfee).add(zyxfee).add(qscfee)*/;
					jstotal=shul.multiply(zxprice);
					panel.getBillModel().setValueAt(jstotal, e.getRow(), "jstotal");

					UFDouble carstrong=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "carstrong"));

					// ȡģ
					UFDouble carnum=UFDouble.ZERO_DBL;
					if(carstrong.doubleValue()!=0){						
						carnum=shul.div(carstrong);
					}
					String[]str=carnum.toString().split("[.]");
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[0], e.getRow(), "carnum");
				}
			}
		}else if(e.getKey().equals("zxprice")){ // ִ�е���
			UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "shul"));
				UFDouble jstotal=UFDouble.ZERO_DBL;
				if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){
					jstotal=shul.multiply(zxprice);
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");
				}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
					//UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "cyfee"));
					jstotal=shul.multiply(zxprice/*.add(cyfee)*/);
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");
				}
			}
		}else if(e.getKey().equals("jstotal")){ //·��:���� ��˰�ϼ�, ��������������
			UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "zxprice"));
				UFDouble num = UFDouble.ZERO_DBL; // ����
				if(zxprice.doubleValue()!=0){						
					num=HgtsPubTool.getTon(jstotal.div(zxprice));
					panel.getBillModel().setValueAt(num, e.getRow(), "shul");
				}else{
					// 2019��8��10�� ���ݼ�˰�ϼơ����������㵥��
					UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "shul"));
					if(shul.doubleValue()!=0){
						panel.getBillModel().setValueAt(jstotal.div(shul), e.getRow(), "zxprice");
						UFDouble fkyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "fkfsyh"));
						UFDouble qyyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "qyyh"));
						UFDouble ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "def13"));
						UFDouble gpprice=jstotal.div(shul).add(fkyh.abs()).add(qyyh.abs()).add(ysfsyh.abs());
						panel.getBillModel().setValueAt(gpprice, e.getRow(), "gpprice");
					}
					
				}
				if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
					// ���� = ����/���� ,ȡ�����磺2.6 ȡ2, 2.2ȡ2
					UFDouble bweight=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "carstrong"));
					if(bweight.doubleValue() !=0){						
						UFDouble carnum=num.div(bweight);
						String[]str=carnum.toString().split("[.]");
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(str[0], e.getRow(), "carnum");

						// 2018.01.29  �ٸ��ݳ���,����,����������,��� begin
						num = HgtsPubTool.getUFDoubleNullAsZero(str[0]).multiply(bweight);
						jstotal = num.multiply(zxprice);
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(num.setScale(3, UFDouble.ROUND_HALF_UP), e.getRow(), "shul");
						panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");
						// 2018.01.29 �� ���ݳ���,����,����������,��� end
					}

				}
			}
		}else if(e.getKey().equals("carnum")){//2018-01-12 ·��:���� ����, ������������˰�ϼ�
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
					UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "zxprice"));
					UFDouble bweight=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "carstrong"));

					UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(e.getValue()).multiply(bweight); // ����=���� *����
					UFDouble jstotal=shul.multiply(zxprice);//��˰�ϼ�

					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(shul.setScale(3, UFDouble.ROUND_HALF_UP), e.getRow(), "shul");
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");

				}
			}
		}else if(e.getKey().equals("gpprice")){  // 2018-8-29 ���Ƽ�
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(e.getRow(), "shul"));
				CommQs comm=new CommQs();
				UFDouble zxprice=comm.getZxprice(e.getRow(), panel);
				/*if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){	
					UFDouble zcfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "zcfee"));
					zxprice=zxprice.add(zcfee);
				}else{
					// ���˷ѵ���
					UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "cyfee"));
					zxprice=zxprice.add(cyfee);
				}*/
				UFDouble jstotal=shul.multiply(zxprice);//��˰�ϼ�
				panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zxprice, e.getRow(), "zxprice");
				panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");
			}
		}
		/*else if(e.getKey().equals("zcfee")){ // 2018-8-29 ���� װ���� ����
			if(null !=pk_transporttype && !"".equals(pk_transporttype)){
				if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){					
					UFDouble zcfee=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
					UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel().getValueAt(e.getRow(), "shul"));
					CommQs comm=new CommQs();
					UFDouble zxprice=comm.getZxprice(e.getRow(), panel).add(zcfee);
					UFDouble jstotal=shul.multiply(zxprice);//��˰�ϼ�
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zxprice, e.getRow(), "zxprice");
					panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), e.getRow(), "jstotal");
				}
			}
		}*/else if(e.getKey().equals("enddate")){
			String enddate=e.getValue()==null?null:e.getValue().toString().substring(0, 10);
			if((null !=dbilldate && !"".equals(dbilldate))
					&& (null !=enddate && !"".equals(enddate))){
				if(enddate.compareTo(dbilldate)<0){
					MessageDialog.showWarningDlg(null, "��ʾ", "�������ڲ���С�ڿ�ʼ����");
					panel.setHeadItem(e.getKey(), null);
					return;
				}
			}
		}else if(e.getKey().equals("bkdrule")){
			if(!e.getValue().equals("2")){ // �۶�
				panel.getBillModel().setCellEditable(e.getRow(), "batchcode", false);
				panel.getBillModel().setValueAt(null, e.getRow(), "batchcode");
			}else{
				panel.getBillModel().setCellEditable(e.getRow(), "batchcode", true);
			}
		}

		// 2018-01-29 ���ת���ɴ�д
		int rowc=panel.getRowCount();
		if(rowc>0){
			UFDouble mny=UFDouble.ZERO_DBL;
			for(int i=0;i<rowc;i++){
				if(!ValueUtils.getUFBoolean(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(i, "rowcloseflag")).booleanValue()){
					mny = mny.add(HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(i, "jstotal")));
				}
			}

			CommQs comm=new CommQs();
			String dx=comm.mnyToMax(mny);
			panel.setHeadItem("def1", dx); // ��д���

		}
	}



	public void mnyCol(int rowindex,BillCardPanel panel,String pk_transporttype,
			String pk_cust,String kb,String pz,UFDouble cyfee){
		//try {
		if(null !=pk_transporttype && !"".equals(pk_transporttype)){
			if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				CommQs comm=new CommQs();
				//UFDouble zyxfee = comm.getZyxf(pk_cust, kb, pz);	// ר���߷ѵ���
				//UFDouble qscfee = comm.getQscfee(pk_cust, kb, pz);	// ȡ�ͳ��ѵ���

				UFDouble zxprice=comm.getZxprice(rowindex, panel)/*.add(zyxfee).add(qscfee).add(cyfee)*/; //ִ�м۸�
				UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(panel.getBillModel("hgts_sendnoticebill_b").getValueAt(rowindex, "shul"));
				UFDouble jstotal=zxprice.multiply(shul);

				//	panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zyxfee, rowindex, "def11");// ר���߷ѵ���
				//	panel.getBillModel("hgts_sendnoticebill_b").setValueAt(qscfee, rowindex, "def12");// ȡ�ͳ��ѵ���

				panel.getBillModel("hgts_sendnoticebill_b").setValueAt(zxprice, rowindex, "zxprice");
				panel.getBillModel("hgts_sendnoticebill_b").setValueAt(jstotal.setScale(2, UFDouble.ROUND_HALF_UP), rowindex, "jstotal");
			}
		}
		/*} catch (UifException e1) {
			e1.printStackTrace();
		}*/
	}

	
	public void setZlzb(CardBodyAfterEditEvent e,BillCardPanel panel,String pk_inv,String pk_org){
		// 2������ָ��ҳǩ�Ƿ��Զ�ȡ��
		int rowcount=e.getBillCardPanel().getBillModel("pk_quality_b").getRowCount();
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
				panel.getBillModel("pk_quality_b").setBodyDataVO(list_bvo.toArray(new ContQualityBVO[0]));
				panel.getBillModel("pk_quality_b").loadLoadRelationItemValue();
			}
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
	}
}
