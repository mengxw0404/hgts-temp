package nc.ui.hgts.hjsettle.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hgts.ff.pub.AddRefAction;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.ffsaleinvoice.AggFfSaleinvoiceHVO;
import nc.vo.hgts.ffsaleinvoice.FfSaleinvoiceBVO;
import nc.vo.hgts.ffsaleinvoice.FfSaleinvoiceHVO;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.hjsettle_ly.AggLyHjsettleHVO;
import nc.vo.hgts.hjsettle_ly.LyHjsettleBVO;
import nc.vo.hgts.hjsettle_ly.LyHjsettleHVO;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.util.VOSortUtils;
import nc.vo.pub.lang.UFDate;
import nc.vo.hgts.pub.FormulaParseTool;

public class AddHjsettleAction extends AddRefAction {

	private static final long serialVersionUID = 3497128738664147311L;

	@Override
	protected String getCurrBilltype() {
		return "YX19";
	}


	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		PfUtilClient.childButtonClickedNew(createPfButtonClickContext());
		if (PfUtilClient.isCloseOK()) {
			if(PfUtilClient.getRetOldVos() == null || PfUtilClient.getRetOldVos().length == 0){
				throw new BusinessException("ת��ʧ�ܣ���ȡ��Դ����Ϊ��");
			}

			AggregatedValueObject[] obj=PfUtilClient.getRetOldVos();
			String pk_billtype=(String) obj[0].getParentVO().getAttributeValue("pk_billtype");
			String pk_cust=(String) obj[0].getParentVO().getAttributeValue("pk_cust");
			if(obj.length>1){
				for(int i=0;i<obj.length;i++){
					AggregatedValueObject bill=obj[i];
					String i_pk_cust=(String) bill.getParentVO().getAttributeValue("pk_cust");
					if(!pk_cust.equals(i_pk_cust)){
						throw new BusinessException("ת��ʧ�ܣ��ͻ���һ��");
					}
				}

				for(int i=0;i<obj.length-1; i++){	
					AggregatedValueObject bill=obj[i];
					CircularlyAccessibleValueObject[] bvos=bill.getChildrenVO();
					String kb="";
					String mz="";
					if(null !=bvos && bvos.length>0){
						//for(int j=0;j<bvos.length;j++){
						kb=(String) bvos[0].getAttributeValue("kb");
						mz=(String) bvos[0].getAttributeValue("mz");
						//}
					}

					for(int j=i+1;j<obj.length;j++){
						AggregatedValueObject j_bill=obj[j];
						CircularlyAccessibleValueObject[] j_bvos=j_bill.getChildrenVO();
						if(null !=j_bvos && j_bvos.length>0){
							//for(int k=0;k<j_bvos.length;k++){
							String j_kb=(String) j_bvos[0].getAttributeValue("kb");
							String j_mz=(String) j_bvos[0].getAttributeValue("mz");
							if(!j_kb.equals(kb)){
								throw new BusinessException("ת��ʧ�ܣ��󳡲�һ��");
							}

							if(!j_mz.equals(mz)){
								//throw new BusinessException("ת��ʧ�ܣ�ú�ֲ�һ��");
							}
							//}
						}
					}
				}
			}
			//
			AggregatedValueObject[] refVO=getTransSaleVO(obj,pk_billtype);
			this.getTransferViewProcessor().processBillTransfer(refVO);
			fieldsControll();
		}
	}

	/**
	 * ͬһ�ͻ������ú�֡���˰���۵Ľ��кϲ�
	 * @param bills
	 * @return
	 */
	public AggregatedValueObject[] getRefVO(AggregatedValueObject[] bills,String pk_billtype){

		List<CircularlyAccessibleValueObject> list=new ArrayList<CircularlyAccessibleValueObject>();
		String source_send_bpks=""; // ����֪ͨ���ӱ�pk��������Ϣ
		String vnote="";//��ŵ�ǰ��Ʊ�Ľ��㵥�Ĺ�������ʼ����,�������ںͷ���֪ͨ����
		String hjbillnos="";// ���۽��㵥��
		String sendnos=""; // ����֪ͨ����
		String carloadingnos=""; // װ����ҵ����
		String gbbdate=""; // ������ʼ����
		String gbedate="";// ������������
		String pk_reciver=""; // �ջ���
		UFDouble cheshu=UFDouble.ZERO_DBL;// ����
		UFDouble jz=UFDouble.ZERO_DBL;// ë��

		for(int i=0;i<bills.length;i++){
			CircularlyAccessibleValueObject head=bills[i].getParentVO();
			String s_bpks=HgtsPubTool.getStringNullAsTrim(head.getAttributeValue("def1"));
			if(!"".equals(source_send_bpks)){
				source_send_bpks = source_send_bpks+";"+s_bpks;
			}else{
				source_send_bpks=source_send_bpks+s_bpks;
			}
			String hjno=HgtsPubTool.getStringNullAsTrim(head.getAttributeValue("vbillno"));
			if(i==0){				
				hjbillnos=hjno;
			}else{
				hjbillnos=hjbillnos+","+hjno;
			}

			if(HgtsPubConst.XSHJD_LY.equals(pk_billtype)){
				try {
					LyHjsettleHVO[] lyhvo=(LyHjsettleHVO[]) HYPubBO_Client.queryByCondition(LyHjsettleHVO.class, " vbillno='"+hjno+"'");
					if(null !=lyhvo && lyhvo.length>0){
						LyHjsettleHVO hvo=lyhvo[0];
						pk_reciver=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_recieve"));
						LyHjsettleBVO[] lybvos=(LyHjsettleBVO[]) HYPubBO_Client.queryByCondition(LyHjsettleBVO.class, " nvl(dr,0)=0 and pk_hjsettle='"+hvo.getPrimaryKey()+"'");
						if(null !=lybvos && lybvos.length>0){
							for(int j=0;j<lybvos.length;j++){
								String v_carloadingnos=HgtsPubTool.getStringNullAsTrim(lybvos[j].getAttributeValue("def1"));
								if(null !=carloadingnos && !"".equals(carloadingnos)){
									if( !carloadingnos.contains(v_carloadingnos)){							
										carloadingnos = carloadingnos+"��"+v_carloadingnos;
									}
								}else{
									carloadingnos = carloadingnos+v_carloadingnos;
								}

								cheshu = cheshu.add(HgtsPubTool.getUFDoubleNullAsZero(lybvos[j].getAttributeValue("carnum")));
								jz= jz.add(HgtsPubTool.getUFDoubleNullAsZero(lybvos[j].getAttributeValue("settleton")));
							}
						}
					}
				} catch (UifException e) {
					e.printStackTrace();
				}
			}else{
				try {
					HjsettleHVO[] hvos=(HjsettleHVO[]) HYPubBO_Client.queryByCondition(HjsettleHVO.class, " nvl(dr,0)=0 and vbillno='"+hjno+"'");
					if(null !=hvos && hvos.length>0){						
						HjsettleHVO hvo=hvos[0];
						HjsettleBVO[] bvos=(HjsettleBVO[]) HYPubBO_Client.queryByCondition(HjsettleBVO.class, " nvl(dr,0)=0 and pk_hjsettle='"+hvo.getPrimaryKey()+"'");
						if(null !=bvos &&bvos.length>0){
							for(int j=0;j<bvos.length;j++){
								cheshu = cheshu.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[j].getAttributeValue("carnum")));
								jz= jz.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[j].getAttributeValue("settleton")));
							}
						}
					}
				} catch (UifException e) {
					e.printStackTrace();
				}
			}

			CircularlyAccessibleValueObject[] items=bills[i].getChildrenVO();
			VOSortUtils.ascSort(items, new String[]{"gbdate"});			
			if(null !=items && items.length>0){			

				for(int j=0;j<items.length;j++){	

					list.add(items[j]);

				}
			}
		}

		Map<String,List<CircularlyAccessibleValueObject>> map=new HashMap<String,List<CircularlyAccessibleValueObject>>();
		if(null !=list && list.size()>0){			
			for(int i=0;i<list.size();i++){
				CircularlyAccessibleValueObject ca=list.get(i);
				String mz=HgtsPubTool.getStringNullAsTrim(ca.getAttributeValue("mz"));
				UFDouble jsprice=HgtsPubTool.getUFDoubleNullAsZero(ca.getAttributeValue("jsprice"));
				String key=mz+jsprice;
				// ��ú��+��˰���� һ���ģ��Ž�map����
				if(map.containsKey(key)){
					map.get(key).add(ca);
				}else{
					List<CircularlyAccessibleValueObject> lst=new ArrayList<CircularlyAccessibleValueObject>();
					lst.add(ca);
					map.put(key, lst);
				}
			}

			UFDate min_gbdate=new UFDate(list.get(0).getAttributeValue("gbdate").toString().substring(0, 10));
			UFDate max_gbdate=new UFDate(list.get(0).getAttributeValue("gbdate").toString().substring(0, 10));
			String fytzdhs="";
			for(int k=0;k<list.size();k++){
				UFDate gbdate=new UFDate(list.get(k).getAttributeValue("gbdate").toString().substring(0, 10));
				if(gbdate.before(min_gbdate)){
					min_gbdate=gbdate;
				}

				if(gbdate.after(max_gbdate)){
					max_gbdate=gbdate;
				}

				String fytzdh=HgtsPubTool.getStringNullAsTrim(list.get(k).getAttributeValue("fytzdh"));
				if(null !=fytzdhs && !"".equals(fytzdhs)){
					if( !fytzdhs.contains(fytzdh)){							
						fytzdhs = fytzdhs+"��"+fytzdh;
					}
				}else{
					fytzdhs = fytzdhs+fytzdh;
				}
			}

			vnote=vnote+"������ʼ����"+min_gbdate.toString().substring(0, 10)+"��������������"+max_gbdate.toString().substring(0, 10)+"������֪ͨ���ţ�"+fytzdhs;
			gbbdate=min_gbdate.toString().substring(0, 10);
			gbedate=max_gbdate.toString().substring(0, 10);
			sendnos=fytzdhs;
		}

		AggHjsettleHVO[] vos=null;
		AggLyHjsettleHVO[] vos2=null;

		// �����ӱ�vo�ĳ���=map.size();
		if(null !=map && map.size()>0){
			//CircularlyAccessibleValueObject[] bitems=new CircularlyAccessibleValueObject[map.size()];			
			HjsettleBVO[] bitems=null;
			LyHjsettleBVO[] bitems2=null;
			if(HgtsPubConst.XSHJD_QY.equals(pk_billtype)){
				bitems=new HjsettleBVO[map.size()];
			}else{
				bitems2=new LyHjsettleBVO[map.size()];
			}

			int index=0;	
			String hj_bpks="";
			for(String key:map.keySet()){
				List<CircularlyAccessibleValueObject> lst=map.get(key);
				if(null !=lst && lst.size()>0){
					UFDouble sum_zhegl=UFDouble.ZERO_DBL;
					UFDouble sum_jsmny=UFDouble.ZERO_DBL;
					UFDouble sum_dnmny=UFDouble.ZERO_DBL;
					if(HgtsPubConst.XSHJD_QY.equals(pk_billtype)){
						HjsettleBVO ca=null;
						//UFDouble jsprice=UFDouble.ZERO_DBL;
						for(int i=0;i<lst.size();i++){
							ca=(HjsettleBVO) lst.get(i);
							//jsprice=HgtsPubTool.getUFDoubleNullAsZero(ca.getAttributeValue("jsprice"));
							UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(ca.getAttributeValue("settlezhegl"));
							UFDouble jsmny=HgtsPubTool.getUFDoubleNullAsZero(ca.getAttributeValue("jsmny"));
							sum_zhegl=sum_zhegl.add(zhegl);
							sum_jsmny=sum_jsmny.add(jsmny);
							String bpk=HgtsPubTool.getStringNullAsTrim(ca.getPrimaryKey());
							if(!"".equals(hj_bpks)){
								hj_bpks = hj_bpks+","+bpk;
							}else{
								hj_bpks = hj_bpks+bpk;
							}
						}
						ca.setAttributeValue("settlezhegl", sum_zhegl);
						ca.setAttributeValue("jsmny", sum_jsmny);
						bitems[index]=ca;
					}else{
						LyHjsettleBVO ca=new LyHjsettleBVO();
						UFDouble jsprice=UFDouble.ZERO_DBL;
						for(int i=0;i<lst.size();i++){
							HjsettleBVO bvo=(HjsettleBVO) lst.get(i);
							jsprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jsprice"));
							UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("settlezhegl"));
							// 2018-09-23 jsmny ��Ϊ  jgzcmny
							UFDouble jsmny=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jgzcmny"));
							UFDouble def12=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("def12")); // �����ܽ��
							sum_zhegl=sum_zhegl.add(zhegl);
							sum_jsmny=sum_jsmny.add(jsmny);
							sum_dnmny=sum_dnmny.add(def12);
							String bpk=HgtsPubTool.getStringNullAsTrim(bvo.getPrimaryKey());
							if(!"".equals(hj_bpks)){
								hj_bpks = hj_bpks+","+bpk;
							}else{
								hj_bpks = hj_bpks+bpk;
							}
							ca.setAttributeValue("kb", bvo.getAttributeValue("kb"));
							ca.setAttributeValue("mz", bvo.getAttributeValue("mz"));
							ca.setAttributeValue("pk_balatype", bvo.getAttributeValue("pk_balatype"));
						}
						ca.setAttributeValue("jsprice", jsprice);
						ca.setAttributeValue("settlezhegl", sum_zhegl);
						ca.setAttributeValue("jsmny", sum_jsmny);
						ca.setAttributeValue("def12", sum_dnmny);

						bitems2[index]=ca;
					}

				}
				index=index+1;
			}


			if(HgtsPubConst.XSHJD_QY.equals(pk_billtype)){
				vos=new AggHjsettleHVO[1];
				vos[0]=(AggHjsettleHVO) bills[0];
				CircularlyAccessibleValueObject head=bills[0].getParentVO();
				head.setAttributeValue("def1",source_send_bpks );
				head.setAttributeValue("def2",hj_bpks); // ֻ����ʱ����def2�ֶ��У�����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def3", vnote);  // ֻ����ʱ����def3�ֶ��У�����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def5", hjbillnos);  // ֻ����ʱ����def5�ֶ��У�����ȡ�����뷢Ʊ��ĳ���ֶ��У�

				// 2018��11��5��
				head.setAttributeValue("def21", sendnos);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def22", carloadingnos);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def23", gbbdate);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def24", gbedate);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				head.setAttributeValue("def26", cheshu);
				head.setAttributeValue("def27", jz);

				bills[0].setParentVO(head);

				vos[0].setParentVO(head);
			}else{
				vos2=new AggLyHjsettleHVO[1];
				AggLyHjsettleHVO agghvo=new AggLyHjsettleHVO();
				LyHjsettleHVO hvo=new LyHjsettleHVO();

				CircularlyAccessibleValueObject head=bills[0].getParentVO();				

				hvo.setAttributeValue("pk_group", head.getAttributeValue("pk_group"));
				hvo.setAttributeValue("pk_org", head.getAttributeValue("pk_org"));
				hvo.setAttributeValue("pk_cust", head.getAttributeValue("pk_cust"));
				hvo.setAttributeValue("pk_dept", head.getAttributeValue("pk_dept"));
				hvo.setAttributeValue("pk_billtype", HgtsPubConst.XSHJD_LY);
				hvo.setAttributeValue("def1",source_send_bpks);
				hvo.setAttributeValue("def2",hj_bpks);
				hvo.setAttributeValue("def3", vnote);  // ֻ����ʱ����def3�ֶ��У�����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				hvo.setAttributeValue("def5", hjbillnos);  // ֻ����ʱ����def5�ֶ��У�����ȡ�����뷢Ʊ��ĳ���ֶ��У�

				// 2018��11��5��
				hvo.setAttributeValue("def21", sendnos);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				hvo.setAttributeValue("def22", carloadingnos);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				hvo.setAttributeValue("def23", gbbdate);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				hvo.setAttributeValue("def24", gbedate);// ֻ����ʱ���룬����ȡ�����뷢Ʊ��ĳ���ֶ��У�
				hvo.setAttributeValue("def25", pk_reciver);
				hvo.setAttributeValue("def26", cheshu);
				hvo.setAttributeValue("def27", jz);

				agghvo.setParentVO(hvo);
				vos2[0]=agghvo;
				vos2[0].setParentVO(hvo);

			}

			if(HgtsPubConst.XSHJD_QY.equals(pk_billtype)){				
				vos[0].setChildrenVO(bitems);
			}else{
				vos2[0].setChildrenVO(bitems2);
			}
		}

		if(HgtsPubConst.XSHJD_QY.equals(pk_billtype)){				
			return vos;
		}else{
			return vos2;
		}
	}

	public AggregatedValueObject[] getTransSaleVO(AggregatedValueObject[] bills,String pk_billtype){	
		AggFfSaleinvoiceHVO[] ffsalevos=new AggFfSaleinvoiceHVO[1] ; // ���۷�Ʊ�ۺ�vo
		AggFfSaleinvoiceHVO aggvo=new AggFfSaleinvoiceHVO();
		AggregatedValueObject[] hj_bills=this.getRefVO(bills, pk_billtype); // length=1
		if(null !=hj_bills && hj_bills.length>0){
			AggregatedValueObject obj=hj_bills[0];
			FfSaleinvoiceBVO[] saleitems=null;
			FfSaleinvoiceHVO salehvo=new FfSaleinvoiceHVO();
			salehvo.setAttributeValue("pk_group",  obj.getParentVO().getAttributeValue("pk_group"));
			salehvo.setAttributeValue("pk_org",  obj.getParentVO().getAttributeValue("pk_org"));
			salehvo.setAttributeValue("pk_org_v",  obj.getParentVO().getAttributeValue("pk_org_v"));
			salehvo.setAttributeValue("cinvoicecustid",  obj.getParentVO().getAttributeValue("pk_cust"));
			salehvo.setAttributeValue("pk_billtypeid",  HgtsPubConst.XSFP);
			salehvo.setAttributeValue("vbillstatus",  -1);
			salehvo.setAttributeValue("dbilldate",  AppContext.getInstance().getBusiDate());
			salehvo.setAttributeValue("pk_dept", obj.getParentVO().getAttributeValue("pk_dept"));
			//salehvo.setAttributeValue("def1", obj.getParentVO().getAttributeValue("def1"));// ��ŷ���֪ͨ���ӱ�pk������
			salehvo.setAttributeValue("def2", obj.getParentVO().getAttributeValue("def2"));// ��Ż��۽��㵥�ӱ�pk
			salehvo.setAttributeValue("vnote", obj.getParentVO().getAttributeValue("def3"));// ��ŵ�ǰ��Ʊ�Ľ��㵥�Ĺ�������ʼ����,�������ںͷ���֪ͨ����
			salehvo.setAttributeValue("def3", obj.getParentVO().getAttributeValue("def5"));// ��ŵ�ǰ��Ʊ�Ľ��㵥�ĵ���

			// 2018��11��5��
			salehvo.setAttributeValue("sendnos", obj.getParentVO().getAttributeValue("def21"));// ��ŷ���֪ͨ������
			salehvo.setAttributeValue("carloadingnos", obj.getParentVO().getAttributeValue("def22"));// ���װ����ҵ����
			salehvo.setAttributeValue("gbsdate", obj.getParentVO().getAttributeValue("def23")==null?"":new UFDate(obj.getParentVO().getAttributeValue("def23").toString()));// ��ŷ���/��ҵ����ʼ���ڣ��������ڣ�
			salehvo.setAttributeValue("gbedate", obj.getParentVO().getAttributeValue("def24")==null?"":new UFDate(obj.getParentVO().getAttributeValue("def24").toString()));// ��ŷ���/��ҵ����������
			salehvo.setAttributeValue("pk_reciver", obj.getParentVO().getAttributeValue("def25"));
			salehvo.setAttributeValue("def6", HgtsPubTool.getUFDoubleNullAsZero(obj.getParentVO().getAttributeValue("def26")));// ����
			salehvo.setAttributeValue("def7", HgtsPubTool.getUFDoubleNullAsZero(obj.getParentVO().getAttributeValue("def27")));// ë��

			CircularlyAccessibleValueObject[] bitems=obj.getChildrenVO();

			Boolean zcfee=false; // ����װ���ѵ���
			UFDouble fee=UFDouble.ZERO_DBL;
			//UFDouble s_zcfee=UFDouble.ZERO_DBL;

			// ����
			UFDouble shul_yfee=UFDouble.ZERO_DBL;			
			UFDouble shul_zyxfee=UFDouble.ZERO_DBL;
			UFDouble shul_qscfee=UFDouble.ZERO_DBL;
			UFDouble shul_ydfee=UFDouble.ZERO_DBL;

			// ��˰�ϼ�
			UFDouble s_yfee=UFDouble.ZERO_DBL;
			UFDouble s_zyxfee=UFDouble.ZERO_DBL;
			UFDouble s_qscfee=UFDouble.ZERO_DBL;
			UFDouble s_ydfee=UFDouble.ZERO_DBL;

			Boolean yf=false; // ���˷ѵ���
			Boolean zyxf=false; // ��ר���߷�
			Boolean qscf=false; // ��ȡ�ͳ���
			Boolean ydf=false; // �˵���

			if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_QY)){
				salehvo.setAttributeValue("pk_transporttype", HgtsPubConst.TRANSPORT_QY);
				// 2018-8-30 �ж��Ƿ���װ���ѵ���
				for(int i=0;i<bitems.length;i++){
					fee=HgtsPubTool.getUFDoubleNullAsZero(bitems[i].getAttributeValue("def13"));
					if(null !=fee && fee.doubleValue()>0){
						zcfee=true;
						break;
					}
				}
			}else{
				salehvo.setAttributeValue("pk_transporttype", HgtsPubConst.TRANSPORT_LY);
				String hjbpks=HgtsPubTool.getStringNullAsTrim(obj.getParentVO().getAttributeValue("def2"));
				if(null !=hjbpks && !"".equals(hjbpks)){
					String[] bpks=null;
					if(hjbpks.contains(",")){
						bpks=hjbpks.split(",");						
					}else{
						bpks=new String[1];
						bpks[0]=hjbpks;
					}
					// 2018-8-30 �ж��Ƿ����˷�
					FormulaParseTool tool=new FormulaParseTool();
					for(int i=0;i<bpks.length;i++){
						UFDouble yfee=HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "def7", "pk_hjsettle_b", bpks[i]));
						UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "zyxf", "pk_hjsettle_b", bpks[i]));
						UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "qscfee", "pk_hjsettle_b", bpks[i]));
						UFDouble ydfee=HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "ydfee", "pk_hjsettle_b", bpks[i]));
						UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "zhegl", "pk_hjsettle_b", bpks[i])/*bitems[i].getAttributeValue("zhegl")*/);

						if(null !=yfee && yfee.doubleValue()>0){
							yf=true;
							//break;
							shul_yfee=shul_yfee.add(zhegl);
							s_yfee= s_yfee.add(yfee);
						}
						if(null !=zyxfee && zyxfee.doubleValue()>0){
							zyxf=true;
							shul_zyxfee=shul_zyxfee.add(zhegl);
							s_zyxfee = s_zyxfee.add(zyxfee);
						}
						if(null !=qscfee && qscfee.doubleValue()>0){
							qscf=true;
							shul_qscfee = shul_qscfee.add(HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "carnum", "pk_hjsettle_b", bpks[i])));
							s_qscfee = s_qscfee.add(qscfee);
						}
						if(null !=ydfee && ydfee.doubleValue()>0){
							ydf=true;
							shul_ydfee = shul_ydfee.add(HgtsPubTool.getUFDoubleNullAsZero(tool.getNameByID("hgts_hjsettle_b", "carnum", "pk_hjsettle_b", bpks[i])));
							s_ydfee = s_ydfee.add(ydfee);
						}

					}

				}
			}
			aggvo.setParentVO(salehvo);			

			List<FfSaleinvoiceBVO> list=new ArrayList<FfSaleinvoiceBVO>();
			UFDouble s_zhegl=UFDouble.ZERO_DBL;
			for(int i=0;i<bitems.length;i++){
				FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();
				salebvo.setAttributeValue("pk_material", bitems[i].getAttributeValue("mz")); 			// ú��
				salebvo.setAttributeValue("pk_mine", bitems[i].getAttributeValue("kb")); 				// ��
				salebvo.setAttributeValue("nqtorigtaxprice", bitems[i].getAttributeValue("jsprice"));	// ��˰����
				salebvo.setAttributeValue("norigtaxmny", bitems[i].getAttributeValue("jsmny"));			// ��˰�ϼ�
				salebvo.setAttributeValue("pk_balatype", bitems[i].getAttributeValue("pk_balatype"));	// �տʽ
				salebvo.setAttributeValue("nastnum", bitems[i].getAttributeValue("settlezhegl"));		// ��Ʊ����

				// 2018-8-30 add
				s_zhegl =s_zhegl.add(HgtsPubTool.getUFDoubleNullAsZero(bitems[i].getAttributeValue("settlezhegl")));

				if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_QY)){					
				}else{
					// 2018-9-20 add
					UFDouble s_dnmny= HgtsPubTool.getUFDoubleNullAsZero(bitems[i].getAttributeValue("def12")).sub(s_zyxfee).sub(s_qscfee).sub(s_yfee);
					salebvo.setAttributeValue("def8", s_dnmny); // ���ڽ��
				}

				list.add(salebvo);
			}

			// 2018-8-30

			if(zcfee){
				FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();

				if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_QY)){
					// ���� 1001OZ1000000000NY12
					salebvo.setAttributeValue("pk_material", "1001061000000000QUQL"); // ú�� - ����װ����   ����ϵͳ 1001061000000000QUQL

					salebvo.setAttributeValue("nqtorigtaxprice", fee);//��˰����

					// 2019��1��4��
					Object pks_hj_b=obj.getParentVO().getAttributeValue("def2");// ���㵥�ӱ�pk
					UFDouble qy_sumzcfee=UFDouble.ZERO_DBL;
					if(null !=pks_hj_b){
						String[] s_pks_hj_b=pks_hj_b.toString().split(",");
						for(int i=0;i<s_pks_hj_b.length;i++){
							try {
								HjsettleBVO hjbvo=(HjsettleBVO) HYPubBO_Client.queryByPrimaryKey(HjsettleBVO.class, s_pks_hj_b[i]);
								UFDouble qy_zcfee=HgtsPubTool.getUFDoubleNullAsZero(hjbvo.getAttributeValue("def13"));//2��Ǯ��װ����
								UFDouble qy_zhegl=HgtsPubTool.getUFDoubleNullAsZero(hjbvo.getAttributeValue("settlezhegl"));// �۸���
								qy_sumzcfee =qy_sumzcfee.add(qy_zcfee.multiply(qy_zhegl));
							} catch (UifException e) {
								e.printStackTrace();
							}
						}						
					}

					// 2019��1��4��
					salebvo.setAttributeValue("norigtaxmny",qy_sumzcfee/* fee.multiply(s_zhegl)*/);//��˰�ϼ�
					salebvo.setAttributeValue("nastnum", s_zhegl);//��Ʊ����
				}
				salebvo.setAttributeValue("pk_mine", bitems[0].getAttributeValue("kb")); //��				
				salebvo.setAttributeValue("pk_balatype", bitems[0].getAttributeValue("pk_balatype"));//�տʽ

				//saleitems[length]=salebvo;
				list.add(salebvo);
			}else{				
				if(zyxf){
					FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();
					if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_LY)){
						// ���� 1001OZ1000000000O3DF
						salebvo.setAttributeValue("pk_material", "1001061000000000V6QA"); //TODO ú�� - ר���߷� 1001061000000000V6QA
						salebvo.setAttributeValue("pk_mine", bitems[0].getAttributeValue("kb")); //��				
						salebvo.setAttributeValue("pk_balatype", bitems[0].getAttributeValue("pk_balatype"));//�տʽ

						salebvo.setAttributeValue("nastnum", shul_zyxfee);//��Ʊ����
						salebvo.setAttributeValue("norigtaxmny", s_zyxfee);//��˰�ϼ�
						salebvo.setAttributeValue("def8", s_zyxfee);			//���ڽ��
					}
					list.add(salebvo);
				}
				if(qscf){
					FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();
					if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_LY)){
						// ���� 1001OZ1000000000O3DB
						salebvo.setAttributeValue("pk_material", "1001061000000000V6QF"); //TODO ú�� - ȡ�ͳ��� 1001061000000000V6QF
						salebvo.setAttributeValue("pk_mine", bitems[0].getAttributeValue("kb")); //��				
						salebvo.setAttributeValue("pk_balatype", bitems[0].getAttributeValue("pk_balatype"));//�տʽ

						salebvo.setAttributeValue("nastnum", shul_qscfee);//��Ʊ����
						salebvo.setAttributeValue("norigtaxmny", s_qscfee);//��˰�ϼ�
						salebvo.setAttributeValue("def8", s_qscfee);			//���ڽ��
					}
					list.add(salebvo);
				}				
				if(ydf){
					FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();
					if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_LY)){
						// ���� 1001OZ1000000000O76Y
						salebvo.setAttributeValue("pk_material", "1001061000000000Y57O"); //TODO ú�� - �˵��� 
						salebvo.setAttributeValue("pk_mine", bitems[0].getAttributeValue("kb")); //��				
						salebvo.setAttributeValue("pk_balatype", bitems[0].getAttributeValue("pk_balatype"));//�տʽ

						salebvo.setAttributeValue("nastnum", shul_ydfee); 	//��Ʊ����
						salebvo.setAttributeValue("norigtaxmny", s_ydfee);	//��˰�ϼ�
						salebvo.setAttributeValue("def8", s_ydfee);			//���ڽ��
					}
					list.add(salebvo);
				}				
				if(yf){
					FfSaleinvoiceBVO salebvo=new FfSaleinvoiceBVO();
					if(obj.getParentVO().getAttributeValue("pk_billtype").equals(HgtsPubConst.XSHJD_LY)){	
						// ���� 1001OZ1000000000NY15
						salebvo.setAttributeValue("pk_material", "1001061000000000QUQU"); // ú�� - �˷�  ����ϵͳ 1001061000000000QUQU		
					}
					salebvo.setAttributeValue("pk_mine", bitems[0].getAttributeValue("kb")); //��				
					salebvo.setAttributeValue("pk_balatype", bitems[0].getAttributeValue("pk_balatype"));//�տʽ

					salebvo.setAttributeValue("nastnum", shul_yfee);	//��Ʊ����
					salebvo.setAttributeValue("norigtaxmny", s_yfee);	//��˰�ϼ�
					salebvo.setAttributeValue("def8", s_yfee);			//���ڽ��

					list.add(salebvo);
				}
			}

			saleitems = (FfSaleinvoiceBVO[]) list.toArray(new FfSaleinvoiceBVO[0]);

			aggvo.setChildrenVO(saleitems);
			ffsalevos[0]=aggvo;
		}

		return ffsalevos;
	}

	private PfButtonClickContext createPfButtonClickContext() {
		PfButtonClickContext context = new PfButtonClickContext();
		context.setParent(this.getModel().getContext().getEntranceUI());
		context.setSrcBillType(this.getSourceBillType());
		context.setPk_group(this.getModel().getContext().getPk_group());
		context.setUserId(this.getModel().getContext().getPk_loginUser());
		// ����ýڵ����ɽ������ͷ����ģ���ô�������Ӧ�ô��������ͣ����򴫵�������
		context.setCurrBilltype(getCurrBilltype());
		context.setUserObj(null);
		context.setSrcBillId(null);
		context.setBusiTypes(this.getBusitypes());
		// ����Ĳ�����ԭ�����õķ����ж����漰��ֻ���������һ�����ṹ�����������������¼ӵĲ���
		// ���εĽ������ͼ���
		context.setTransTypes(this.getTranstypes());
		// ��־�ڽ�������Ŀ�Ľ������ͷ���ʱ������Ŀ�Ľ������͵����ݣ�������������ֵ��1�����ݽӿڶ��壩��
		// 2�������������ã���-1�������ݽ������ͷ��飩
		context.setClassifyMode(-1);
		return context;
	}



	@Override
	public void fieldsControll() {
		getEditor().getBillCardPanel().getHeadItem("pk_org").setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem("cinvoicecustid").setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem("pk_group").setEnabled(false);
		// ------ 2017-12-8 new begin -----
		// ˰�� �� ���ϵ��� ���룻��˰����=��˰�ϼ�/��������˰����=��˰����/��1+˰�ʣ�����˰���=��˰����*������˰��=��˰�ϼ�-��˰��
		BillCardPanel panel = getEditor().getBillCardPanel();
		int len = getEditor().getBillCardPanel().getRowCount();
		for (int i = 0;i<len ;i++){
			String pk_material=HgtsPubTool.getStringNullAsTrim(panel.getBodyValueAt(i, "pk_material"));
			if(null !=pk_material && !"".equals(pk_material)){
				if("1001061000000000QUQU".equals(pk_material)){ // ���˷�
					continue;
				}
			}
			FormulaParseTool tool=new FormulaParseTool();
			UFDouble taxrate=tool.getTaxrate(pk_material); // ˰��
			// ����
			UFDouble nastnum=HgtsPubTool.getUFDoubleNullAsZero(panel.getBodyValueAt(i, "nastnum"));				
			// ��˰���� 
			//UFDouble nqtorigtaxprice=HgtsPubTool.getUFDoubleNullAsZero(panel.getBodyValueAt(i, "nqtorigtaxprice"));
			// ��˰�ϼ�
			UFDouble norigtaxmny=HgtsPubTool.getUFDoubleNullAsZero(panel.getBodyValueAt(i, "norigtaxmny"));

			//��˰����=��˰�ϼ�/����
			UFDouble nqtorigtaxprice=norigtaxmny.div(nastnum);

			// ��˰����=��˰����/��1+˰�ʣ�
			UFDouble nqtorigprice=nqtorigtaxprice.div(taxrate.div(100).add(1));

			// ��˰���=��˰����*����
			UFDouble ntotalmny=nqtorigprice.multiply(nastnum);

			// ˰��=��˰�ϼ�-��˰���
			UFDouble ntaxratemny=norigtaxmny.sub(ntotalmny);

			panel.setBodyValueAt(nqtorigtaxprice, i, "nqtorigtaxprice");
			panel.setBodyValueAt(taxrate, i, "ntaxrate");
			panel.setBodyValueAt(nqtorigprice, i, "nqtorigprice");
			panel.setBodyValueAt(ntotalmny, i, "ntotalmny");
			panel.setBodyValueAt(ntaxratemny, i, "ntaxratemny");

		}
		// ------ new end -----
	}

	public UFDouble getTaxrate(String pk_material){
		String sql="select taxrate from bd_taxrate "
				+ " where nvl(dr, 0) = 0 "
				+ " and pk_taxcode = (select pk_taxcode from bd_taxcode where nvl(dr, 0) = 0"
				+ " and mattaxes =(select pk_mattaxes from bd_mattaxes where nvl(dr, 0) = 0 "
				+ " and pk_mattaxes =(select pk_mattaxes from bd_material_v where nvl(dr, 0) = 0 "
				+ " and pk_material = '"+pk_material+"')))";
		IUAPQueryBS bs=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		UFDouble taxrate=UFDouble.ZERO_DBL;
		try {
			taxrate=HgtsPubTool.getUFDoubleNullAsZero(bs.executeQuery(sql, new ColumnProcessor()));
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return taxrate;
	}

}
