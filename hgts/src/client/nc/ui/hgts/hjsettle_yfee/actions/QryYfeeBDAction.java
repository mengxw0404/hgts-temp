package nc.ui.hgts.hjsettle_yfee.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.settle.IPriceSettle;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.sendnoticebill.*;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;

/**
 *  �˷ѽ���
 */
public class QryYfeeBDAction extends nc.ui.pubapp.uif2app.actions.AddAction{

	private static final long serialVersionUID = 7741678759106506923L;

	public QryYfeeBDAction(){
		super();
		super.setCode("qryYfeeBDAction");
		super.setBtnName("���չ�����");
	}
	private AbstractAppModel model;
	private ShowUpableBillForm editor;

	public AbstractAppModel getModel() {
		return model;
	}
	public void setModel(AbstractAppModel model) {
		this.model = model;
	}
	public ShowUpableBillForm getEditor() {
		return editor;
	}
	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}
	public HashMap bufferCondition = new HashMap();//�����һ�β�ѯ�����ݣ���Ϊ��һ�β�ѯʱ�Ļ���

	public HashMap getBufferCondition() {
		return bufferCondition;
	}
	protected QueryConditonDlg getConditionDlg() {
		QueryConditonDlg rtnValue = new QueryConditonDlg(this.getBufferCondition());
		return rtnValue;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO �Զ����ɵķ������

		QueryConditonDlg dlg=this.getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String[] swhere=dlg.getQuerycondition();
			
			// * �˷ѽ��㣺��ȡ��������
			// * @param Object[]:���ȹ̶�Ϊ2��[0]:Э���˷ѵ��ۣ�[1]:����vo����
			IPriceSettle settle=(IPriceSettle) NCLocator.getInstance().lookup(IPriceSettle.class.getName());
			Object[] rst=settle.getAggvos_yfee(swhere);
			Object price=rst[0];
			AggInvoicesheetHVO[] skdAggVOS =rst[1]==null?null:(AggInvoicesheetHVO[])rst[1];
			if(null==skdAggVOS || skdAggVOS.length==0){
				MessageDialog.showWarningDlg(null, "��ʾ", "û�з�������������");
				return;
			}

			ShowBdDataDialog buffDataDlalog=new ShowBdDataDialog(null, getModel().getContext(),skdAggVOS, null, dlg.isImp);
			buffDataDlalog.setVisible(true);
			//��ȡѡ�д�����Ĺ�����
			AggInvoicesheetHVO[] aggvos=buffDataDlalog.getResults();
			if(null !=aggvos && aggvos.length>0){
				AggHjsettleHVO aggvo=data(aggvos,price);
				if(null!=aggvo){					// ��������
					this.model.setUiState(UIState.ADD);
					this.model.setAppUiState(AppUiState.ADD);

					this.getEditor().getBillCardPanel().setBillValueVO(aggvo);
					this.getEditor().getBillCardPanel().getBillTable().setSortEnabled(false);
					this.getEditor().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
				}
			}
		}
	}

	/**
	 * ��������+���� �ϲ�
	 * @param aggvos
	 * @return
	 * @throws Exception
	 */
	public AggHjsettleHVO data(AggInvoicesheetHVO[] aggvos,Object price) throws Exception{
		AggHjsettleHVO aggHjsettleHVO=new AggHjsettleHVO();
		Map<String,List<AggInvoicesheetHVO>> map=new TreeMap<String,List<AggInvoicesheetHVO>>();// TreeMap Ĭ�ϸ���key����
		for(int i=0;i<aggvos.length;i++){
			InvoicesheetHVO hvo=aggvos[i].getParentVO();
			
			String gbdate=hvo.getAttributeValue("dbilldate").toString().substring(0, 10);
			String carno=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("def5")); // ����
			String key=gbdate+carno;
			if(map.containsKey(key)){
				map.get(key).add(aggvos[i]);
			}else{
				List<AggInvoicesheetHVO> list=new ArrayList<AggInvoicesheetHVO>();
				list.add(aggvos[i]);
				map.put(key, list);
			}
		}

		if(map !=null && map.size()>0){
			HjsettleHVO hjsettleHVO=new HjsettleHVO();
			List<HjsettleBVO> list_bvos=new ArrayList<HjsettleBVO>();
			FormulaParseTool tool =new FormulaParseTool();
			int index=1;
			String mineid="";
			String settlezt=""; 
			// ��������
			for(String key:map.keySet()){
				List<AggInvoicesheetHVO> list=map.get(key);
				if(null !=list && list.size()>0){
					// TODO ���� ��ֵ��Ӧȡһ�μ��ɣ������ظ�ѭ��
					AggInvoicesheetHVO aggvo=list.get(0);					
					InvoicesheetHVO hvo=aggvo.getParentVO();
					mineid =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_kc"));					

					hjsettleHVO.setAttributeValue("pk_group", hvo.getAttributeValue("pk_group"));
					hjsettleHVO.setAttributeValue("pk_org", hvo.getAttributeValue("pk_org"));
					hjsettleHVO.setAttributeValue("pk_org_v", hvo.getAttributeValue("pk_org_v"));

					hjsettleHVO.setAttributeValue("pk_busitype", hvo.getAttributeValue("pk_busitype"));		
					hjsettleHVO.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
					hjsettleHVO.setAttributeValue("pk_balatype", hvo.getAttributeValue("pk_balatype"));
					hjsettleHVO.setAttributeValue("pk_billtype", HgtsPubConst.XSHJD_YZF);
					hjsettleHVO.setAttributeValue("vbillstatus", BillStatusEnum.FREE.value());
					hjsettleHVO.setAttributeValue("isks", hvo.getAttributeValue("isks"));

					settlezt=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("settlezt"));
					hjsettleHVO.setAttributeValue("settlezt", settlezt);
					break;
				}
			}

			//������ϸ����
			UFDouble t_jingz=UFDouble.ZERO_DBL;
			UFDouble t_custton=UFDouble.ZERO_DBL;
			UFDouble kdbl=UFDouble.ZERO_DBL;//���ֱ���
			UFDouble kdprice=UFDouble.ZERO_DBL;	//���ֵ���
			UFDouble t_jsmny=UFDouble.ZERO_DBL;//�����˷ѽ��ϼ�
			UFDouble t_dckmny=UFDouble.ZERO_DBL;//������ֽ��ϼ�
			String yfxyno="";
			String pk_supplier="";
			int qsrule=-1;// ȡ������
			for(String key:map.keySet()){
				List<AggInvoicesheetHVO> list=map.get(key);
				if(null !=list && list.size()>0){
					UFDouble jingz =UFDouble.ZERO_DBL;
					UFDouble custton=UFDouble.ZERO_DBL;// �� ʵ�ն���
					String mz="";
					String carno="";
					String fz="";
					String dz="";
					String pk_customer="";
					UFDouble kdprice2=UFDouble.ZERO_DBL; // �������ֵ���
					UFDouble dckd=UFDouble.ZERO_DBL;//��������ֵ
					for(int i=0;i<list.size();i++){						
						AggInvoicesheetHVO aggvo=list.get(i);
						InvoicesheetHVO hvo=aggvo.getParentVO();					
						String sendnoticebillno =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("sendnoticebillno"));
						pk_customer=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_cust"));
						SendnoticebillHVO[] sendHVOS=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+sendnoticebillno+"' ");
						if(null !=sendHVOS && sendHVOS.length>0){							
							String pk_sendnoticebill=sendHVOS[0].getPrimaryKey();
							String pk_yfxy=HgtsPubTool.getStringNullAsTrim(sendHVOS[0].getAttributeValue("yfxycode"));
							PactVO pactHVO=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_yfxy);
							yfxyno="".equals(yfxyno)?
									pactHVO==null?"":pactHVO.getAttributeValue("vbillno")+"":
										yfxyno.contains(HgtsPubTool.getStringNullAsTrim(pactHVO.getAttributeValue("vbillno")))?yfxyno+"":yfxyno+","+pactHVO.getAttributeValue("vbillno");
							pk_supplier=HgtsPubTool.getStringNullAsTrim(pactHVO==null?"":pactHVO.getAttributeValue("pk_supplier"));
							qsrule="".equals(HgtsPubTool.getStringNullAsTrim(pactHVO==null?"":pactHVO.getAttributeValue("qsrule")))?-1:Integer.parseInt(HgtsPubTool.getStringNullAsTrim(pactHVO.getAttributeValue("qsrule")));


							PactBVO[] pactBVOs=	(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_yfxy+"'");
							if(null !=pactBVOs && pactBVOs.length>0){
								price=HgtsPubTool.getUFDoubleNullAsZero(pactBVOs[0].getAttributeValue("price")); // �˷Ѽ۸�
								kdprice=HgtsPubTool.getUFDoubleNullAsZero(pactBVOs[0].getAttributeValue("kdprice")); // ���ּ۸�
								kdprice2=HgtsPubTool.getUFDoubleNullAsZero(pactBVOs[0].getAttributeValue("kdprice2")); // �������ּ۸�
								kdbl=HgtsPubTool.getUFDoubleNullAsZero(pactBVOs[0].getAttributeValue("kdrate")); // 
								dckd=HgtsPubTool.getUFDoubleNullAsZero(pactBVOs[0].getAttributeValue("dckd")); // 
							}

							SendnoticebillBVO[] sendBVOS=(SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+pk_sendnoticebill+"' ");
							if(null !=sendBVOS && sendBVOS.length>0){
								fz=HgtsPubTool.getStringNullAsTrim(sendBVOS[0].getAttributeValue("startstadion"));
								dz=HgtsPubTool.getStringNullAsTrim(sendBVOS[0].getAttributeValue("arrviestadion"));
							}
						}
						InvoicesheetBVO[] ibvos=(InvoicesheetBVO[]) aggvo.getChildrenVO();
						if(null ==ibvos || ibvos.length==0){			

							String pk_invoice=aggvo.getParentVO().getPrimaryKey();
							ibvos=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");
						}
						for(int j=0;j<ibvos.length;j++){
							UFDouble jsweight=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("jingz"));
							jingz=jingz.add(jsweight);
							t_jingz=t_jingz.add(jsweight);

							UFDouble custjingz=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("custjingz"));
							custton=custton.add(custjingz);
							t_custton=t_custton.add(custton);

							mz =HgtsPubTool.getStringNullAsTrim(ibvos[j].getAttributeValue("pz"));
							carno =HgtsPubTool.getStringNullAsTrim(ibvos[j].getAttributeValue("carno"));
						}
					}

					HjsettleBVO hjsettleBVO=new HjsettleBVO();
					hjsettleBVO.setAttributeValue("rowno", index*10);
					hjsettleBVO.setAttributeValue("gbdate", new UFDate(key.substring(0, 10)));
					hjsettleBVO.setAttributeValue("kb", mineid);//���
					hjsettleBVO.setAttributeValue("mz", mz);//ú��
					hjsettleBVO.setAttributeValue("pk_customer", pk_customer);
					hjsettleBVO.setAttributeValue("fstation", fz); 				//��վ
					hjsettleBVO.setAttributeValue("barrivestation", dz);		//��վ
					hjsettleBVO.setAttributeValue("jz", jingz); 				// ԭ������
					hjsettleBVO.setAttributeValue("custton",custton); 			// ʵ�ն���

					UFDouble settleton=UFDouble.ZERO_DBL;
					if(qsrule==1){ 			// ����						
						settleton=jingz;						
					}else if(qsrule==2){ 	// ��						
						settleton=custton;						
					}else if(qsrule==3){ 	// �͸�						
						settleton=jingz.doubleValue()>custton.doubleValue()?jingz:custton;					
					}else if(qsrule==4){ 	// �͵�						
						settleton=jingz.doubleValue()>custton.doubleValue()?custton:jingz; 
					}else{
						// ���ݽ�������
						if(!"".equals(settlezt)){
							if(settlezt.equals("1")){ // ��
								settleton=custton;
							}else{ // ����
								settleton=jingz;
							}
						}

					}
					UFDouble jsmny=settleton.multiply(HgtsPubTool.getUFDoubleNullAsZero(price));					
					t_jsmny=t_jsmny.add(jsmny);

					// TODO ��˰���ۡ���˰��˰�ʡ�˰��
					FormulaParseTool ft=new FormulaParseTool();
					String pk_material=HgtsPubTool.getStringNullAsTrim(mz);
					UFDouble rate=ft.getTaxrate(pk_material);//"16%"; 
					UFDouble norateprice=HgtsPubTool.getUFDoubleNullAsZero(price).div((rate.div(100).add(1)));
					UFDouble noratemny=norateprice.multiply(settleton);					
					UFDouble ntaxratemny=jsmny.sub(noratemny);

					// �������֡��������ֿۿ�
					UFDouble kton=jingz.sub(custton);
					UFDouble cton=UFDouble.ZERO_DBL; // ���ֲ�ֵ
					if(kton.doubleValue()>dckd.doubleValue()){
						cton=kton.sub(dckd);
					}

					UFDouble ktonmny=cton.multiply(kdprice2);					
					t_dckmny=t_dckmny.add(ktonmny);

					hjsettleBVO.setAttributeValue("settleton",settleton); 	// �������
					hjsettleBVO.setAttributeValue("carno", carno);
					hjsettleBVO.setAttributeValue("jsprice", price); 		// �˷Ѽ۸�Ԫ/�֣�
					hjsettleBVO.setAttributeValue("jsmny", jsmny); 	 		// �����Ԫ��

					hjsettleBVO.setAttributeValue("norateprice", norateprice);
					hjsettleBVO.setAttributeValue("noratemny", noratemny);
					hjsettleBVO.setAttributeValue("rate", rate);
					hjsettleBVO.setAttributeValue("def14", ntaxratemny);

					hjsettleBVO.setAttributeValue("dckd", kton);
					hjsettleBVO.setAttributeValue("kdprice", kdprice2);
					hjsettleBVO.setAttributeValue("kdmny", ktonmny);


					list_bvos.add(hjsettleBVO);

					index=index+1;
				}
			}
			//���ӽ��㵥����������������� װж��
			//TODO
			
			// �����������������������������֡���������������ֵ��ۡ��������ֿۿ���������
			UFDouble billkton=t_jingz.sub(t_custton);
			UFDouble yxkton=t_jingz.multiply(kdbl.div(100));

			UFDouble kkmny=UFDouble.ZERO_DBL;
			UFDouble billmny=UFDouble.ZERO_DBL;
			if(billkton.doubleValue()>yxkton.doubleValue()){				
				kkmny=(billkton.sub(yxkton)).multiply(kdprice);
				billmny=t_jsmny.sub(kkmny);
			}else{
				billmny=t_jsmny.sub(t_dckmny);
			}


			hjsettleHVO.setAttributeValue("billton", t_jingz);
			hjsettleHVO.setAttributeValue("billcustton", t_custton);
			hjsettleHVO.setAttributeValue("billkton", billkton);			
			hjsettleHVO.setAttributeValue("yxkton", yxkton);
			hjsettleHVO.setAttributeValue("kdprice", kdprice);
			hjsettleHVO.setAttributeValue("kkmny", kkmny);
			hjsettleHVO.setAttributeValue("lastmny",billmny);
			hjsettleHVO.setAttributeValue("yfxyno", yfxyno);
			hjsettleHVO.setAttributeValue("pk_supplier", pk_supplier);
			hjsettleHVO.setAttributeValue("qsrule", qsrule);

			aggHjsettleHVO.setParent(hjsettleHVO);
			aggHjsettleHVO.setChildrenVO(list_bvos.toArray(new HjsettleBVO[0]));

		}
		return aggHjsettleHVO;
	}

}
