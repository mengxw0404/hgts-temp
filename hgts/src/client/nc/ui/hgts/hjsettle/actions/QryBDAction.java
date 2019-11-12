package nc.ui.hgts.hjsettle.actions;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.hgts.common.ISysDocValues;
import nc.itf.hgts.settle.INumColResult;
import nc.itf.hgts.settle.IPriceSettle;
import nc.ui.hgts.hjsettle.yh.comm.QuaDj;
import nc.ui.hgts.hjsettle_ly.actions.QryCarloadingplanAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pc.BdInfoVOForSettle;
import nc.vo.hgts.pc.NumColResult;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.util.VOSortUtils;

/**
 * ���� --���㵥 ���� ������ 
 */
public class QryBDAction extends nc.ui.pubapp.uif2app.actions.AddAction {

	private static final long serialVersionUID = 7473834006638663348L;
	FormulaParseTool tool =new FormulaParseTool();
	public QryBDAction(){
		super();
		super.setCode("qryBDAction");
		super.setBtnName("���չ�����");
	}

	private AbstractAppModel model;
	private ShowUpableBillForm editor;

	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public HashMap bufferCondition = new HashMap();//�����һ�β�ѯ�����ݣ���Ϊ��һ�β�ѯʱ�Ļ���

	public HashMap getBufferCondition() {
		return bufferCondition;
	}

	public void setBufferCondition(HashMap bufferCondition) {
		this.bufferCondition = bufferCondition;
	}

	protected ImpQueryDlg getConditionDlg() {
		ImpQueryDlg rtnValue = new ImpQueryDlg(this.getBufferCondition(),this.model);
		return rtnValue;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ImpQueryDlg dlg = this.getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String pk_org=null;
			//�������������ݽ���
			String[] whereBuffer = dlg.getQuerycondition();
			IPriceSettle settle=(IPriceSettle) NCLocator.getInstance().lookup(IPriceSettle.class.getName());
			AggInvoicesheetHVO[] skdAggVOS =settle.getAggvos(whereBuffer);
			if(null==skdAggVOS || skdAggVOS.length==0){
				MessageDialog.showWarningDlg(null, "��ʾ", "û�з�������������");
				return;
			}
			ShowDataDialog buffDataDlalog=new ShowDataDialog(null, getModel().getContext(),skdAggVOS, pk_org, dlg.isImp);
			buffDataDlalog.setVisible(true);
			AggInvoicesheetHVO[] aggvos=buffDataDlalog.getResults();
			if(null !=aggvos && aggvos.length>0){
				//���������ɽ�����Ϣ
				AggHjsettleHVO aggvo=data(aggvos);
				if(null!=aggvo){					// ��������
					this.model.setUiState(UIState.ADD);
					this.model.setAppUiState(AppUiState.ADD);
				//	this.getEditor().getBillCardPanel().setBillValueVO(aggvo);
					this.getEditor().setValue(aggvo);
					this.getEditor().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
					//this.getEditor().getBillCardPanel().getBillTable().setSortEnabled(false);
					this.ishaveflag=false;
				}
			}
		}
	}

	/**
	 * ͬһ ��֯+�ͻ�+���+ú��+����֪ͨ����+�ʼ�����+�������ڵĹ������ϲ���һ������
	 * ��������+�ʼ����Σ��ʼ챨�浥�ţ�+����֪ͨ����+����֪ͨ����id  ���кϲ�:��ͬ�ĺϲ��γɱ����һ��
	 * @param aggvos
	 * @throws SQLException 
	 * @throws BusinessException 
	 */
	boolean ishaveflag=false;  // �Ƿ��в��б�ʶ
	public AggHjsettleHVO data(AggInvoicesheetHVO[] aggvos) throws Exception{
		AggHjsettleHVO aggHjsettleHVO=new AggHjsettleHVO();
		Map<String,List<AggInvoicesheetHVO>> map=new TreeMap<String,List<AggInvoicesheetHVO>>();
		String pk_zxxy ="";//װжЭ������
		String pk_ysxy ="";//����Э������
		UFBoolean type =UFBoolean.FALSE;//�Ƿ�һƱ��
		
		// TreeMap Ĭ�ϸ���key����
		for(int i=0;i<aggvos.length;i++){
			InvoicesheetHVO hvo=aggvos[i].getParentVO();
			String pk_invoice=hvo.getPrimaryKey();
			hvo=(InvoicesheetHVO) HYPubBO_Client.queryByPrimaryKey(InvoicesheetHVO.class, pk_invoice);
			pk_zxxy = HgtsPubTool.getStringNullAsTrim( hvo.getAttributeValue("zxxycode"));
			String ida  = HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_fytzd"));
			type=HgtsPubTool.getUFBooleanNullAsFalse(tool.getNameByID("hgts_sendnoticebill", "type", "pk_sendnoticebill", ida));
			
			pk_ysxy = HgtsPubTool.getStringNullAsTrim( hvo.getAttributeValue("yfxycode"));
			InvoicesheetBVO[] items=(InvoicesheetBVO[]) aggvos[i].getChildrenVO();
			if(null == items || items.length==0 ){
				
				items=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");		
			}
			
			aggvos[i].setChildrenVO(items);
			String gbdate=hvo.getAttributeValue("dbilldate").toString().substring(0, 10);
			String zjpc=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_qualityreport"));
			String sendnoticebillno=hvo.getAttributeValue("sendnoticebillno").toString();
			String pk_send_b=HgtsPubTool.getStringNullAsTrim(items[0].getAttributeValue("csourcebid"));

			String key=pk_send_b+sendnoticebillno+zjpc+gbdate;
			//��������+�ʼ����Σ��ʼ챨�浥�ţ�+����֪ͨ����+����֪ͨ����id  VO�ϲ�
			if(map.containsKey(key)){
				map.get(key).add(aggvos[i]);
			}else{
				List<AggInvoicesheetHVO> list=new ArrayList<AggInvoicesheetHVO>();
				list.add(aggvos[i]);
				map.put(key, list);
			}
		}
		
		// ����ָ��ʹ�� begin
		Map<String,UFDouble> map_dates=new TreeMap<String,UFDouble>();
		if(map !=null && map.size()>0){
			for(String key:map.keySet()){
				UFDouble sum_jz=UFDouble.ZERO_DBL;
				List<AggInvoicesheetHVO> list=map.get(key);
				String gbdate=key.substring(key.length()-10, key.length());
				if(null !=list && list.size()>0){
					for(int i=0;i<list.size();i++){						
						AggInvoicesheetHVO aggvo=list.get(i);
						InvoicesheetBVO[] ibvos=(InvoicesheetBVO[]) aggvo.getChildrenVO();
						if(null ==ibvos || ibvos.length==0){
							String pk_invoice=aggvo.getParentVO().getPrimaryKey();
							ibvos=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");
						}
						for(int j=0;j<ibvos.length;j++){
							UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("jingz"));
							sum_jz=sum_jz.add(jingz);
						}						
					}					
				}
	
				if(map_dates.containsKey(gbdate)){
					map_dates.get(gbdate).add(sum_jz);
				}else{					
					map_dates.put(gbdate, sum_jz);
				}				
			}
		}
		
		// end
		
		if(map !=null && map.size()>0){
			HjsettleHVO hjsettleHVO=new HjsettleHVO();
			List<HjsettleBVO[]> list_bvos=new ArrayList<HjsettleBVO[]>();
			String srchpk="";	//2017-12-22 ��֮ǰ��ŷ�������������pk����Ϊ���ݺ�
			Map<String,UFDouble> s_map=new HashMap<String,UFDouble>();
			String lastsrcbpks="";		// ��� ����֪ͨ���ӱ�����������������������д
			
			for(String key:map.keySet()){
				List<AggInvoicesheetHVO> list=map.get(key);
				if(null !=list && list.size()>0){
					BdInfoVOForSettle infor =new  BdInfoVOForSettle() ;
					int carnum=list.size();	// ����
					UFDouble jingz =UFDouble.ZERO_DBL;
					UFDouble custton=UFDouble.ZERO_DBL;
					String mz="";
					String vsourcecode="";
					String vsourcerowno="";
					String csourcebid="";
					String pk_balatype="";
					for(int i=0;i<list.size();i++){
						AggInvoicesheetHVO aggvo=list.get(i);
						// 1���ʼ����Ρ��������ڡ����ú�� 
						InvoicesheetHVO hvo=aggvo.getParentVO();
						String pk_group =hvo.getAttributeValue("pk_group").toString();
						String pk_org =hvo.getAttributeValue("pk_org").toString();
						String pk_org_v =hvo.getAttributeValue("pk_org_v").toString();
						String pk_busitype =hvo.getAttributeValue("pk_busitype").toString();
						String pk_cust =hvo.getAttributeValue("pk_cust").toString();
						String qcbillid =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_qualityreport"));
						String mineid =hvo.getAttributeValue("pk_kc").toString();
						UFDate gbdate =AppContext.getInstance().getBusiDate();
						pk_balatype =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_balatype"));
						hjsettleHVO.setAttributeValue("pk_group", pk_group);
						hjsettleHVO.setAttributeValue("pk_org", pk_org);
						hjsettleHVO.setAttributeValue("pk_org_v", pk_org_v);
						hjsettleHVO.setAttributeValue("pk_cust", pk_cust);
						hjsettleHVO.setAttributeValue("pk_busitype", pk_busitype);		
						hjsettleHVO.setAttributeValue("dbilldate", gbdate);
						hjsettleHVO.setAttributeValue("pk_billtype", HgtsPubConst.XSHJD_QY);
						hjsettleHVO.setAttributeValue("vbillstatus", BillStatusEnum.FREE.value());
						hjsettleHVO.setAttributeValue("pk_balatype", pk_balatype);

						// 2017-12-13 ȡ����֪ͨ������ begin
						String sendnoticebillno =HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("sendnoticebillno"));
						String pk_dept=tool.getNameByID("hgts_sendnoticebill", "pk_dept", "vbillno", sendnoticebillno);

						hjsettleHVO.setAttributeValue("isks", hvo.getAttributeValue("isks"));
						hjsettleHVO.setAttributeValue("pk_dept", pk_dept);
						hjsettleHVO.setAttributeValue("settlezt", hvo.getAttributeValue("settlezt"));
						// 2017-12-13 ȡ����֪ͨ������ end

						// ��Դhpk��������д
						if(!"".equals(srchpk)){
							srchpk=srchpk+",'"+hvo.getAttributeValue("vbillno")+"'";
						}else{
							srchpk="'"+hvo.getAttributeValue("vbillno")+"'";
						}
						hjsettleHVO.setAttributeValue("srchpks", srchpk);

						InvoicesheetBVO[] ibvos=(InvoicesheetBVO[]) aggvo.getChildrenVO();
						if(null ==ibvos || ibvos.length==0){
							String pk_invoice=aggvo.getParentVO().getPrimaryKey();
							ibvos=(InvoicesheetBVO[]) HYPubBO_Client.queryByCondition(InvoicesheetBVO.class, " nvl(dr,0)=0 and pk_invoice='"+pk_invoice+"'");
						}

						for(int j=0;j<ibvos.length;j++){
							UFDouble jsweight=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("jingz"));
							jingz=jingz.add(jsweight);

							UFDouble custjingz=HgtsPubTool.getUFDoubleNullAsZero(ibvos[j].getAttributeValue("custjingz"));
							custton=custton.add(custjingz);

							mz =ibvos[j].getAttributeValue("pz").toString();

							String pk_sendnoticebill_b=HgtsPubTool.getStringNullAsTrim(ibvos[j].getAttributeValue("csourcebid"));
							if(s_map.containsKey(pk_sendnoticebill_b)){
								jsweight=s_map.get(pk_sendnoticebill_b).add(jsweight);
								s_map.put(pk_sendnoticebill_b, jsweight);
							}else{
								s_map.put(pk_sendnoticebill_b, jsweight);
							}	
						}

						vsourcecode=HgtsPubTool.getStringNullAsTrim(ibvos[0].getAttributeValue("vsourcecode")); // ����֪ͨ����
						csourcebid=HgtsPubTool.getStringNullAsTrim(ibvos[0].getAttributeValue("csourcebid"));
					
						infor.setPk_org(pk_org);
						infor.setPk_cust(pk_cust);
						infor.setQcbillid(qcbillid);
						infor.setMineid(mineid);
						infor.setGbdate(new UFDate(hvo.getAttributeValue("dbilldate").toString()));//��������
						infor.setPk_invid(mz);						
						infor.setJingz(jingz);
						infor.setCustjingz(custton);
						
					}

					// �ʼ����Ρ��������ڡ����ú�֡�ú����ϡ�����
					HjsettleBVO hjsettleBVO=new HjsettleBVO();
					hjsettleBVO.setAttributeValue("rowno", 10);
					hjsettleBVO.setAttributeValue("zjpc", infor.getQcbillid());
					hjsettleBVO.setAttributeValue("gbdate", infor.getGbdate());
					hjsettleBVO.setAttributeValue("kb", infor.getMineid());
					hjsettleBVO.setAttributeValue("mz", mz);
				
					hjsettleBVO.setAttributeValue("jz", jingz);
					hjsettleBVO.setAttributeValue("custton", custton);

					hjsettleBVO.setAttributeValue("fytzdh", vsourcecode);//����֪ͨ����

					vsourcerowno=tool.getNameByID("hgts_sendnoticebill_b", "rowno", "pk_sendnoticebill_b", csourcebid);
					hjsettleBVO.setAttributeValue("fytzdrowno",vsourcerowno);//����֪ͨ���к�
					hjsettleBVO.setAttributeValue("csourcebid",csourcebid);//����֪ͨ����id
					hjsettleBVO.setAttributeValue("pk_balatype",pk_balatype);
					hjsettleBVO.setAttributeValue("carnum", carnum);
					
					// 3�����ء��ҷ֡�ˮ�֡������ۼƽ���  �۸����
					HjsettleBVO[] bvos=datacol(hjsettleHVO,hjsettleBVO,infor,ishaveflag,map_dates);

					list_bvos.add(bvos);
				}
			}

			if(null !=s_map && s_map.size()>0){
				UFDouble s_jsweight=UFDouble.ZERO_DBL;
				for(String s_key:s_map.keySet()){
					s_jsweight=s_map.get(s_key);
					if(!"".equals(lastsrcbpks)){
						lastsrcbpks = lastsrcbpks+";"+s_key+","+s_jsweight;
					}else{
						lastsrcbpks=s_key+","+s_jsweight;
					}
				}
			}

			hjsettleHVO.setAttributeValue("def1", lastsrcbpks);

			aggHjsettleHVO.setParent(hjsettleHVO);
			List<HjsettleBVO> list_hjbvos=new ArrayList<HjsettleBVO>();
			if(null !=list_bvos && list_bvos.size()>0){
				for(int i=0;i<list_bvos.size();i++){
					HjsettleBVO[] items=list_bvos.get(i);
					if(null !=items && items.length>0){						
						for(int j=0;j<items.length;j++){
							HjsettleBVO item=items[j];
							list_hjbvos.add(item);
						}
					}
				}
			}
			//
			HjsettleBVO[] bvos=list_hjbvos.toArray(new HjsettleBVO[0]);
			VOSortUtils.ascSort(bvos, new String[]{"fytzdh","zjpc","gbdate"});
			UFDouble zxton = UFDouble.ZERO_DBL;//װж����
			UFDouble M_mny = UFDouble.ZERO_DBL;//������ú��
			for(int i=0;i<bvos.length;i++){
				zxton = zxton.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("jz")));
				M_mny = M_mny.add(HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("jsmny")));
				if(i!=0){
					// ȡ��i�еľ��ء��۸���
					UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(bvos[i].getAttributeValue("zhegl"));
					// ȡ�ڣ�i-1���еĵ��½����ۼ�
					UFDouble curzhegllj=HgtsPubTool.getUFDoubleNullAsZero(bvos[i-1].getAttributeValue("zhegllj"));//�۸����ۼ�
					UFDouble grpzgllj=HgtsPubTool.getUFDoubleNullAsZero(bvos[i-1].getAttributeValue("grpzgllj"));//�����۸����ۼ�
					UFDouble i_curzhegllj=curzhegllj.add(zhegl);
					UFDouble i_grpzgllj=grpzgllj.add(zhegl);
					bvos[i].setAttributeValue("zhegllj", i_curzhegllj);
					bvos[i].setAttributeValue("grpzgllj", i_grpzgllj);
					bvos[i].setAttributeValue("rowno", (i+1)*10);
				}					
			}
			
			//���������Ľ�������Ϣ���������ϼƲ�����װж��
			HjsettleBVO zxbvo = new  HjsettleBVO();
			String pk_material=tool.getNameByID("bd_material", "pk_material", "code", "0901");//��ȡװ������Ϣ
			IPriceSettle Ipactser= NCLocator.getInstance().lookup(IPriceSettle.class);
			PactBVO  zxpvo =Ipactser.getZXPactB(hjsettleHVO.getAttributeValue("pk_org"), bvos[0].getAttributeValue("kb"),HgtsPubConst.TRANSPORT_QY ,pk_zxxy);
			zxbvo.setAttributeValue("rowno", (bvos.length+1)*10);
			zxbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
			zxbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//��
			zxbvo.setAttributeValue("mz", pk_material);//ú��-װ����
			zxbvo.setAttributeValue("jz", zxton);
			zxbvo.setAttributeValue("settleton",zxton); 	// �������	
			zxbvo.setAttributeValue("settlezhegl",zxton); 	// �����۸���(װж�Ѱ�ʵ����������)
			UFDouble price = HgtsPubTool.getUFDoubleNullAsZero(zxpvo.getAttributeValue("price"));
			zxbvo.setAttributeValue("jsprice",price);//;//���㵥�۸��ݷ���֪ͨ���е�װжЭ��װж����
			zxbvo.setAttributeValue("jsmny", price.multiply(zxton));//������
			zxbvo.setAttributeValue("rate",HgtsPubTool.getUFDoubleNullAsZero(zxpvo.getAttributeValue("rate")).setScale(0, 4));//˰�ʣ����ݷ���֪ͨ���е�װжЭ��װж˰��
			zxbvo.setAttributeValue("def16", "Y");//�Ƿ񴫷�Ʊ
			List<HjsettleBVO> nList = new ArrayList<HjsettleBVO>();
			for(HjsettleBVO bo:bvos){
				bo.setAttributeValue("def16", "Y");
				nList.add(bo);
			}
			nList.add(zxbvo);
			//�ж��Ƿ�ΪһƱ�Ƶ���
			if(!pk_ysxy.equals("") && type.booleanValue() ){//&& type.booleanValue()
				//���������Ľ�������Ϣ���������ϼƲ����������
				HjsettleBVO ysbvo = new  HjsettleBVO();
				String  ys=tool.getNameByID("bd_material", "pk_material", "code", "0902");//��ȡװ������Ϣ
				PactBVO yspvo =Ipactser.getYSPactB(hjsettleHVO.getAttributeValue("pk_org"), bvos[0].getAttributeValue("kb"),HgtsPubConst.TRANSPORT_QY ,pk_ysxy);
				ysbvo.setAttributeValue("rowno", (bvos.length+2)*10);
				ysbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
				ysbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//��
				ysbvo.setAttributeValue("mz", ys);//ú��-�����
				ysbvo.setAttributeValue("jz", zxton);
				ysbvo.setAttributeValue("settleton",zxton); 	// �������	
				ysbvo.setAttributeValue("settlezhegl",zxton); 	// �����۸���(װж�Ѱ�ʵ����������)
				UFDouble price1 = HgtsPubTool.getUFDoubleNullAsZero(yspvo.getAttributeValue("price"));
				ysbvo.setAttributeValue("jsprice",price1);//;//���㵥�۸��ݷ���֪ͨ���е�����Э��װж����
				ysbvo.setAttributeValue("jsmny", price1.multiply(zxton));//������
				ysbvo.setAttributeValue("rate",HgtsPubTool.getUFDoubleNullAsZero(yspvo.getAttributeValue("rate")).setScale(0, 4));//˰�ʣ����ݷ���֪ͨ���е�װжЭ��װж˰��
				//
				nList.add(ysbvo);
				
				//һƱ���ܣ�ʹ�� �������ܿ� �� ����� ��/������ ==��ú����
				HjsettleBVO mbvo = new  HjsettleBVO();
				mbvo.setAttributeValue("rowno", (bvos.length+3)*10);
				mbvo.setAttributeValue("gbdate", AppContext.getInstance().getBusiDate());
				mbvo.setAttributeValue("kb",  bvos[0].getAttributeValue("kb"));//��
				mbvo.setAttributeValue("mz", bvos[0].getAttributeValue("mz"));//ú�� 			
				UFDouble rate = HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("rate"));
				mbvo.setAttributeValue("rate", HgtsPubTool.getUFDoubleNullAsZero(rate).setScale(0, 4));//˰��
				mbvo.setAttributeValue("jz", zxton);
				mbvo.setAttributeValue("settleton",zxton); 	// �������	
				mbvo.setAttributeValue("settlezhegl",zxton); 	// �����۸���(װж�Ѱ�ʵ����������)
				UFDouble mmny = M_mny.sub(price1.multiply(zxton));
				mbvo.setAttributeValue("jsprice",mmny.div(zxton).setScale(2, 4));//;//���㵥�۸��ݷ���֪ͨ���е�����Э��װж����
				mbvo.setAttributeValue("jsmny", mmny );//������
				UFDouble norateprice=mmny.div(zxton).div(rate.div(100).add(1));
				mbvo.setAttributeValue("norateprice",norateprice);//��˰����
				mbvo.setAttributeValue("noratemny", norateprice.multiply(zxton) );//��˰���
				mbvo.setAttributeValue("def14", mmny.sub(norateprice.multiply(zxton)) );//˰��
				//
				nList.add(mbvo);
			}
			 
		
			
			aggHjsettleHVO.setChildrenVO(nList.toArray(new HjsettleBVO[0]));
		}
		return aggHjsettleHVO;
	}



	/**
	 * �������ݵ���ؼ���
	 * @param aggvo
	 * @param hjsettleHVO
	 * @param infor
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public HjsettleBVO[] datacol(HjsettleHVO hjsettleHVO,HjsettleBVO hjsettleBVO,
			BdInfoVOForSettle infor,boolean ishaveflag,Map<String,UFDouble> map_dates) throws BusinessException, SQLException{
		INumColResult result = new NumColResult();
		IPriceSettle settle=(IPriceSettle) NCLocator.getInstance().lookup(IPriceSettle.class.getName());
		// ��ȡ�۸�������С��λ��
		ISysDocValues isdv=NCLocator.getInstance().lookup(ISysDocValues.class);		
		int digits=isdv.getDecDigits(infor.getPk_cust());
		// 2�����ء��ҷ֡�ˮ�֡������ۼƽ��� ����
		UFDate jsdate=(UFDate) hjsettleHVO.getAttributeValue("dbilldate"); // ��������
		Object isks=hjsettleHVO.getAttributeValue("isks"); // �Ƿ��ˮ
		result=settle.numCol(infor,jsdate,isks);
		if(null !=result){	
			hjsettleBVO.setAttributeValue("huif", result.getHf());
			hjsettleBVO.setAttributeValue("shuif", result.getSf());
			hjsettleBVO.setAttributeValue("custhuif", result.getCustHf());
			hjsettleBVO.setAttributeValue("custshuif", result.getCustSf());

			hjsettleBVO.setAttributeValue("zhegl", result.getZgNum().setScale(digits, UFDouble.ROUND_HALF_UP));
			hjsettleBVO.setAttributeValue("custzhegl", result.getCustZgNum().setScale(digits, UFDouble.ROUND_HALF_UP));
	
			// 2018-01-17  ����
			QryCarloadingplanAction qca=new QryCarloadingplanAction();
			String jibie=qca.getJiBie(result.getHf());
			if(null !=jibie && !"".equals(jibie)){						
				hjsettleBVO.setAttributeValue("jib", Integer.parseInt(jibie));
			}
		}

		// 3���۸����		
		String pk_busitype=HgtsPubTool.getStringNullAsTrim(hjsettleHVO.getAttributeValue("pk_busitype"));
		String settlezt=HgtsPubTool.getStringNullAsTrim(hjsettleHVO.getAttributeValue("settlezt"));
		HjsettleBVO[] hjbvos=this.getLastVO(hjsettleBVO,pk_busitype,settlezt,result,isks,infor,map_dates);
		return hjbvos;
	}

	@Override
	protected boolean isActionEnable() {
		return this.model.getUiState() == UIState.NOT_EDIT;
	}

	/**
	 * 
	 * @param hjsettleBVO
	 * @throws UifException
	 */
	public HjsettleBVO[] getLastVO(HjsettleBVO hjsettleBVO,String pk_busitype,String settlezt,
			INumColResult result,Object isks,BdInfoVOForSettle infor,Map<String,UFDouble> map_dates) throws UifException{
		HjsettleBVO[] hjsettleBVOs=new HjsettleBVO[1];
		if(null !=hjsettleBVO){			
			String sendnoticebillno=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("fytzdh"));
			String pk_send_b=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("csourcebid"));
			UFDouble huif=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("huif"));
			UFDouble custhuif=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custhuif"));
			
			FormulaParseTool tool =new FormulaParseTool();
			SendnoticebillHVO[] sbillHVO=(SendnoticebillHVO[]) HYPubBO_Client.queryByCondition(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+sendnoticebillno+"'");
			String pk_send_h=sbillHVO[0].getPrimaryKey();
			// �����ͬ��
			String pk_pact=HgtsPubTool.getStringNullAsTrim(sbillHVO[0].getAttributeValue("contcode"));
			String contcode="";	
			if(!"".equals(pk_pact) && null !=pk_pact){	
				contcode=tool.getNameByID("hgts_sopact", "contcode", "pk_pact", pk_pact);		
			}
			// ָ��ȡֵ��ʽ��1�������Σ�2����Ȩƽ��
			String zbqztype=HgtsPubTool.getStringNullAsTrim(sbillHVO[0].getAttributeValue("zbqztype"));
			SendnoticebillBVO bvo=(SendnoticebillBVO) HYPubBO_Client.queryByPrimaryKey(SendnoticebillBVO.class, pk_send_b);
			if(null !=bvo){
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("gpprice"));
				UFDouble tz_paytype=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("fkfsyh"));
				UFDouble tz_qyyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("qyyh"));
				UFDouble tz_ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("def13"));
				String vnote=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("vnote"));
				String gbdate=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("gbdate"));
				UFDouble zhegl=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("zhegl"));
				UFDouble jingz=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("jz"));				
				UFDouble custton=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custton"));
				UFDouble custzhegl=HgtsPubTool.getUFDoubleNullAsZero(hjsettleBVO.getAttributeValue("custzhegl"));
				//��۸����߼۸񡢼۸����߽����������۸�
				// ���ʽ�Żݡ������Ż����µ����ӷ���֪ͨ��ȡ�����ĵ����۸�������������������
				UFDouble price=gpprice.sub(tz_paytype.abs()).sub(tz_qyyh.abs())
						.sub(tz_ysfsyh.abs())/*.sub(def15.abs())*/;
				
				QuaDj dj=new QuaDj();
				if(null !=result){	
					UFDouble tz_zlzb=UFDouble.ZERO_DBL;
					
					String isCurPrj=dj.isCurPrj();					
					if("1".equals(isCurPrj)){
						// 2019��3��7�� modify ȡ������ָ��  �����۸�   ��֮ǰ�Ӽ۸�������ȡ������ͳһ��֪ͨ����ȡ
						//	tz_zlzb=getTz_quaindex(pk_pricepolicy, jgz, huif,pk_busitype);
						if(null !=settlezt && !"".equals(settlezt)){
							if("1".equals(settlezt)){ // ��						
								tz_zlzb=this.getTz_quaindex(pk_send_h, tool, custhuif);
							}else{ // ����
								tz_zlzb=this.getTz_quaindex(pk_send_h, tool, huif);
							}
						}else{ // ����					
							tz_zlzb=this.getTz_quaindex(pk_send_h, tool, huif);
						}

						if(null !=map && map.size()>0){
							String rowno=map.get("rowno");
							vnote=vnote+"����ָ��("+rowno+")"+tz_zlzb.setScale(2, UFDouble.ROUND_HALF_UP);
						}
						map=new HashMap<String,String>();
					
						// ����ָ�꣺���ݻҷ������ֵ�������������ʾ�������٣����ϵ��������µ����ʼӷ�	
						price=price.add(tz_zlzb);
					}else{						
						// 1���ۼ�
						UFDouble u_price=dj.getTz_quaindex_sj(infor, pk_send_h, settlezt,
								gbdate,zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);
						System.out.println("�ۼۣ�"+u_price);
						// 2���ۿ�
						UFDouble u_kk=dj.getTz_quaindex_kk(infor, pk_send_h,settlezt,gbdate,
								zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);
						System.out.println("�ۿ"+u_kk);
						// 3���۶�
						UFDouble u_kd=dj.getTz_quaindex_kd(infor, pk_send_h,settlezt,gbdate,
								zbqztype,HgtsPubConst.TRANSPORT_QY, map_dates);					
						System.out.println("�۶֣�"+u_kd);
						// ˵���ۺϵ�����Ϊ0
						if(null==u_price || null==u_kk){
							price=UFDouble.ZERO_DBL;
						}else{							
							if(u_price.doubleValue()>0){
								price=u_price.sub(u_kk);
							}else{
								price=price.sub(u_kk);
							}
						}

						if(null !=settlezt && !"".equals(settlezt)){
							if("1".equals(settlezt)){ 			// ��
								custzhegl=custton.sub(custton.multiply(u_kd).div(100));
							}else if("2".equals(settlezt)){ //2�� ����								
								zhegl=jingz.sub(jingz.multiply(u_kd).div(100));								
							}else if("3".equals(settlezt)){ //3��������˫����
								UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
								if(isD.booleanValue()){ // ����
									custzhegl=custton.sub(custton.multiply(u_kd).div(100));
								}else{
									zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
								}
							}else { //4:������˫����
								UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
								if(isD.booleanValue()){
									zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
								}else{
									custzhegl=custton.sub(custton.multiply(u_kd).div(100));
								}
							}
						}else{
							zhegl=jingz.sub(jingz.multiply(u_kd).div(100));
						}
						
						// ������ȫ���۳���
						if(zhegl.doubleValue()<0){
							zhegl=UFDouble.ZERO_DBL;
						}
						if(custzhegl.doubleValue()<0){
							custzhegl=UFDouble.ZERO_DBL;
						}
					}
				}
				
				zhegl=zhegl.setScale(3, UFDouble.ROUND_HALF_UP);
				UFDouble totalmny=price.multiply(zhegl);

				// TODO ˰�ʡ���˰���ۡ���˰��˰��				
				String pk_material=HgtsPubTool.getStringNullAsTrim(hjsettleBVO.getAttributeValue("mz"));
				UFDouble rate=tool.getTaxrate(pk_material);//"16%"; 
				UFDouble norateprice=price.div((rate.div(100).add(1)));						

				UFDouble settleton=UFDouble.ZERO_DBL;
				UFDouble settlezhegl=UFDouble.ZERO_DBL;
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ // ��						
						settleton=custton;
						settlezhegl=custzhegl;
					}else if("2".equals(settlezt)){ // ����
						settleton=jingz;
						settlezhegl=zhegl;
					}else if("3".equals(settlezt)){ //3��������˫����
						UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
						if(isD.booleanValue()){ // ����
							settleton=custton;
							settlezhegl=custzhegl;
						}else{
							settleton=jingz;
							settlezhegl=zhegl;
						}
					}else { //4:������˫����
						UFBoolean isD=dj.isD(Integer.parseInt(gbdate.toString().substring(5, 7)));
						if(isD.booleanValue()){
							settleton=jingz;
							settlezhegl=zhegl;
						}else{
							settleton=custton;
							settlezhegl=custzhegl;
						}
					}
				}else{ // ����
					settleton=jingz;
					settlezhegl=zhegl;
				}

				UFDouble jsmny=price.multiply(settlezhegl);				
				UFDouble noratemny=norateprice.multiply(settlezhegl);				
				UFDouble ntaxratemny=price.multiply(settlezhegl).sub(noratemny);

				hjsettleBVO.setAttributeValue("zhegl", zhegl);				
				hjsettleBVO.setAttributeValue("custzhegl", custzhegl);
				
				hjsettleBVO.setAttributeValue("rate", rate);
				hjsettleBVO.setAttributeValue("jgzcprice", price);
				hjsettleBVO.setAttributeValue("jgzcmny", totalmny);
				hjsettleBVO.setAttributeValue("jsmny", jsmny);//������
				hjsettleBVO.setAttributeValue("jsprice", price);//���㵥��
				hjsettleBVO.setAttributeValue("norateprice", norateprice);
				hjsettleBVO.setAttributeValue("noratemny", noratemny);	// ��˰���
				hjsettleBVO.setAttributeValue("def14", ntaxratemny); 	// ˰��
				hjsettleBVO.setAttributeValue("settleton",settleton); 	// �������	
				hjsettleBVO.setAttributeValue("settlezhegl",settlezhegl); 	// �����۸���	
				hjsettleBVO.setAttributeValue("vnote", vnote);
				//;��=��������-�Է�������
				//��ˮ=�Է�������-�Է��۸���
				hjsettleBVO.setAttributeValue("tuh", jingz.sub(custton));
				hjsettleBVO.setAttributeValue("kous", custton.sub(custzhegl));
				hjsettleBVO.setAttributeValue("contcode", contcode);
				hjsettleBVOs[0]=hjsettleBVO;
			}
		}
		return hjsettleBVOs;
	}

	/**
	 * ȡ����ָ��ĵ���ֵ
	 * @throws UifException 
	 */
	Map<String,String> map=new HashMap<String,String>();
	public UFDouble getTz_quaindex(String pk_send_h,FormulaParseTool tool,UFDouble huif) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		SendYzyjBVO[] items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, " nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"'");
		if(null !=items && items.length>0){
			UFDouble min = null;
			UFDouble max = null;
			String rowno="";
			String jgfd=""; // �۸���㹫ʽ
			for(int i=0;i<items.length;i++){
				min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
				max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
				if(max.doubleValue()==0){
					// ������Ĭ��Ϊ���ֵ
					if(huif.compareTo(min)>=0){
						jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						rowno=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("rowno"));
						map.put("rowno", rowno);
						break;
					}
				}else{
					if(huif.doubleValue()>=min.doubleValue() && huif.doubleValue()<max.doubleValue()){
						jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						rowno=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("rowno"));
						map.put("rowno", rowno);
						break;
					}
				}
			}

			if(null !=jgfd && !"".equals(jgfd) && jgfd.indexOf("����ֵ")>0){
				jgfd=jgfd.replace("����ֵ", "hyz").replaceAll("��", "(").replaceAll("��", ")");
				//��������ֵ-9.52��/0.5��*��-8�� �����ڼ���ȡ��������������һ
				// �磺������ֵ-9.52��/0.5 = 1.1����ȡֵΪ2����=1.0����ȡֵΪ1
				String[] str=jgfd.split("[*]");
				FormulaParse fromula = new FormulaParse();		
				fromula.addVariable("hyz", huif);
				fromula.setExpress(str[0]);
				String value=fromula.getValue();
				int i_value=0;
				if(null==value || "".equals(value)){
					i_value=0;					
				}else{
					UFDouble d_value=new UFDouble(value);
					if(d_value.doubleValue()<0){					
						i_value=Integer.valueOf(d_value.abs().toString().split("[.]")[0]).intValue()*(-1);
					}else{
						i_value=Integer.valueOf(d_value.toString().split("[.]")[0]).intValue();
					}

					String s=str[1].replace("(", "").replace(")", "");
					if(i_value==d_value.doubleValue()){
						jgfd=(d_value.multiply(new UFDouble(s))).toString();
					}else{
						if(i_value<=0){	
							if(d_value.doubleValue()>0){
								jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
							}else{								
								jgfd=((new UFDouble(i_value).abs().add(1)).multiply(-1).multiply(new UFDouble(s))).toString();
							}
						}else{
							jgfd=((new UFDouble(i_value).abs().add(1)).multiply(new UFDouble(s))).toString();
						}
					}
					fdzvalue=HgtsPubTool.getUFDoubleNullAsZero(jgfd);
				}
			}		
		}
		return fdzvalue;
	}

}
