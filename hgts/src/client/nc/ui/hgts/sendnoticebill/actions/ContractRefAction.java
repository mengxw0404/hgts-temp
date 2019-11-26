package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.model.BillManageModel;
//import nc.ui.tmpub.field.affect.util.FieldEventUIUtil;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.QualityIndicatorsBVO;
import nc.vo.hgts.sendnoticebill.SendRefPactVO;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.XieYiBVO;
import nc.vo.hgts.sopact.ContQualityBVO;
import nc.vo.hgts.sopact.ContYzyjBVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.sopact.PactVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

/**
 *  ���� �����ͬaction
 */
public class ContractRefAction extends nc.ui.pubapp.uif2app.actions.AddAction {

	private static final long serialVersionUID = -128078137530247303L;

	public ContractRefAction(){
		super();
		super.setCode("contractrefAction");
		super.setBtnName("���պ�ͬ");
	}
	private AbstractAppModel model;
	private BillForm editor;

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}


	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public HashMap bufferCondition = new HashMap();//�����һ�β�ѯ�����ݣ���Ϊ��һ�β�ѯʱ�Ļ���

	public HashMap getBufferCondition() {
		return bufferCondition;
	}

	public void setBufferCondition(HashMap bufferCondition) {
		this.bufferCondition = bufferCondition;
	}

	protected QueryPactDlg getConditionDlg() {
		QueryPactDlg rtnValue = new QueryPactDlg(this.getBufferCondition(),"40H10403");
		return rtnValue;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO �Զ����ɵķ������
		QueryPactDlg dlg = this.getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String sql=dlg.getRstSql();
			String busidate=AppContext.getInstance().getBusiDate().toString().substring(0, 10);
			sql=sql+" and nvl(b.ton,0)-nvl(b.yzxnum,0)>0 "
					+ "and (substr(h.sdate,0,10)<='"+busidate+"' and '"+busidate+"' <=substr(h.edate,0,10))";
			PactDataShowDialog dig=new PactDataShowDialog(null, sql,getModel().getContext());
			dig.setVisible(true);
			SendRefPactVO vo=dig.getPactVO();
			if(null !=vo){
				String pk_org=vo.getPk_org();
				String pk_pact=vo.getPk_pact();
				String pk_pact_b=vo.getPk_pact_b();// ��ͬ������
				UFBoolean iskztzd=vo.getIskztzd(); //
				String pk_cust=vo.getPk_cust();
				String pk_balatype=vo.getPk_balatype();
				String pk_busitype=vo.getPk_busitype();
				String pk_dept=vo.getPk_dept();
				UFBoolean isbidding= vo.getIsbidding() == null ? UFBoolean.FALSE: vo.getIsbidding() ; //�Ƿ񾺼�
				UFDouble yhprice=HgtsPubTool.getUFDoubleNullAsZero(vo.getYhprice());
				UFDouble qyyh=HgtsPubTool.getUFDoubleNullAsZero(vo.getQyyh());
				UFDouble ysfsyh=HgtsPubTool.getUFDoubleNullAsZero(vo.getYsfsyh());
				
				UFDouble gpprice=HgtsPubTool.getUFDoubleNullAsZero(vo.getGpprice());
				UFDouble shul=HgtsPubTool.getUFDoubleNullAsZero(vo.getShul());
				UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(vo.getPrice());
				UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(zxprice.multiply(shul));
			
				// ��ͬ ��vo
				PactVO pactVO=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_pact);
				// ��ͬ ����ָ��
				ContQualityBVO[] contQualityBVOs=(ContQualityBVO[]) HYPubBO_Client.queryByCondition(ContQualityBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact+"'");
				// ��ͬ �����ż�
				ContYzyjBVO[] contYzyjBVOs=(ContYzyjBVO[]) HYPubBO_Client.queryByCondition(ContYzyjBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact+"'");

				// 1��֪ͨ������
				SendnoticebillHVO hvo=new SendnoticebillHVO();
				hvo.setAttributeValue("pk_group", AppContext.getInstance().getPkGroup());
				hvo.setAttributeValue("pk_org", pk_org);				
				hvo.setAttributeValue("pk_org_v", new FormulaParseTool().getNameByID("org_orgs", "pk_vid", "pk_org", pk_org));
				hvo.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
				hvo.setAttributeValue("startdate", AppContext.getInstance().getBusiDate());
				hvo.setAttributeValue("vbillstatus", -1);
				hvo.setAttributeValue("contcode", pk_pact);
				hvo.setAttributeValue("pk_cust", pk_cust);
				hvo.setAttributeValue("receiver", pk_cust);
				hvo.setAttributeValue("pk_balatype", pk_balatype);
				hvo.setAttributeValue("pk_busitype", pk_busitype);
				hvo.setAttributeValue("pk_fhkc", vo.getPk_kb());
				hvo.setAttributeValue("pk_transporttype", pactVO.getAttributeValue("transport"));
				hvo.setAttributeValue("settlezt", pactVO.getAttributeValue("settlezt"));
				hvo.setAttributeValue("isks", vo.getIsks());
				hvo.setAttributeValue("pk_billtypecode","YX04");
				hvo.setAttributeValue("pk_billtypeid", HgtsPubConst.FHTZD);
				hvo.setAttributeValue("jytype", 1);
				hvo.setAttributeValue("isks", pactVO.getAttributeValue("isks"));
				hvo.setAttributeValue("dr", 0);
				hvo.setAttributeValue("isbidding", isbidding.toString());
				hvo.setAttributeValue("pk_supplier", pactVO.getAttributeValue("pk_supplier"));
				hvo.setAttributeValue("pk_dept", pk_dept);
				hvo.setAttributeValue("zxxycode", pactVO.getAttributeValue("zxxycode"));//װж
				hvo.setAttributeValue("yfxycode", pactVO.getAttributeValue("yfxycode"));//����
				hvo.setAttributeValue("type", pactVO.getAttributeValue("type"));//�Ƿ�һƱ��
				hvo.setAttributeValue("checktype", isbidding.booleanValue()?2:1);//У���߼�

				// 2��֪ͨ���ӱ�
				SendnoticebillBVO[] bvos=new SendnoticebillBVO[1];				
					SendnoticebillBVO bvo=new SendnoticebillBVO();
					bvo.setAttributeValue("rowno", 10);
					bvo.setAttributeValue("pz", vo.getPk_min());
					bvo.setAttributeValue("startstadion", vo.getFz());
					bvo.setAttributeValue("arrviestadion", vo.getDz());
					bvo.setAttributeValue("shul", shul);
					bvo.setAttributeValue("gpprice", gpprice);//���Ƽ۸�
					bvo.setAttributeValue("fkfsyh", yhprice);// �Żݼ۸�
					bvo.setAttributeValue("qyyh", qyyh);
					bvo.setAttributeValue("def13", ysfsyh);
					bvo.setAttributeValue("zxprice", zxprice);//ִ�м۸�
					bvo.setAttributeValue("jstotal", jstotal);
					bvo.setAttributeValue("dr", 0);					
					bvo.setAttributeValue("csourcebid", pk_pact_b);
					bvo.setAttributeValue("csourceid", pk_pact);
					bvo.setAttributeValue("csourcetypecode", "YX40");
					bvo.setAttributeValue("blatest", "Y");
					bvos[0]=bvo;
	
				// ֪ͨ�� ����ָ��
				QualityIndicatorsBVO[] qualityBVOs=null;				
				// ֪ͨ�� �����ż�
				SendYzyjBVO[] sendYzyjBVOs=null;
				// ֪ͨ�� �˷�/װжЭ��
				XieYiBVO[] xieYiBVO=null;

				// 2.1������ָ��
				if(null !=contQualityBVOs && contQualityBVOs.length>0){

					qualityBVOs=new QualityIndicatorsBVO[contQualityBVOs.length];

					for(int i=0;i<contQualityBVOs.length;i++){
						ContQualityBVO item=contQualityBVOs[i];

						QualityIndicatorsBVO qbvo=new QualityIndicatorsBVO();
						qbvo.setAttributeValue("rowno", item.getAttributeValue("crowno")/*(i+1)*10*/);
						qbvo.setAttributeValue("pk_quaprj", item.getAttributeValue("pk_quaprj"));
						qbvo.setAttributeValue("prjvalue", item.getAttributeValue("prjvalue"));
						qbvo.setAttributeValue("dr", 0);
						qualityBVOs[i]=qbvo;
					}
				}

				// 2.2�������ż�
				if(null !=contYzyjBVOs && contYzyjBVOs.length>0){
					sendYzyjBVOs=new SendYzyjBVO[contYzyjBVOs.length];
					for(int i=0;i<contYzyjBVOs.length;i++){
						SendYzyjBVO sybvo=new SendYzyjBVO();
						sybvo.setAttributeValue("rowno", contYzyjBVOs[i].getAttributeValue("crowno")/*(i+1)*10*/);
						sybvo.setAttributeValue("pk_project", contYzyjBVOs[i].getAttributeValue("pk_project"));
						sybvo.setAttributeValue("zbvalue", contYzyjBVOs[i].getAttributeValue("zbvalue"));
						sybvo.setAttributeValue("minv", contYzyjBVOs[i].getAttributeValue("minv"));
						sybvo.setAttributeValue("maxv", contYzyjBVOs[i].getAttributeValue("maxv"));
						sybvo.setAttributeValue("pricechange", contYzyjBVOs[i].getAttributeValue("pricechange"));
						sybvo.setAttributeValue("batchcode", contYzyjBVOs[i].getAttributeValue("batchcode"));
						sybvo.setAttributeValue("bkdrule", contYzyjBVOs[i].getAttributeValue("bkdrule"));
						sybvo.setAttributeValue("dr", 0);
						sendYzyjBVOs[i]=sybvo;
					}
				}

				// 2.3���˷�/װжЭ��
				String pk_pactb_yf=HgtsPubTool.getStringNullAsTrim(pactVO.getAttributeValue("yfxycode"));
				String pk_pactb_zx=HgtsPubTool.getStringNullAsTrim(pactVO.getAttributeValue("zxxycode"));
				PactBVO pactBVO_yf=(PactBVO) HYPubBO_Client.queryByPrimaryKey(PactBVO.class,pk_pactb_yf );
				PactBVO pactBVO_zx=(PactBVO) HYPubBO_Client.queryByPrimaryKey(PactBVO.class,pk_pactb_zx );
//				PactVO pactVO_yf=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_pact_yf);
//				PactVO pactVO_zx=(PactVO) HYPubBO_Client.queryByPrimaryKey(PactVO.class, pk_pact_zx);
//				PactBVO[] pactBVO_yf=(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact_yf+"'");
//				PactBVO[] pactBVO_zx=(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact_zx+"'");

				List<XieYiBVO> lists=new ArrayList<XieYiBVO>();
				int v_rowno=0;
				if(null !=pactBVO_yf){

					String yfvbillno = new FormulaParseTool().getBsNameByID("hgts_sopact", "vbillno", "pk_pact", HgtsPubTool.getStringNullAsTrim(pactBVO_yf.getPrimaryKey())); 

					XieYiBVO xybvo=new XieYiBVO();
					xybvo.setAttributeValue("rowno", (1)*10);
					// �ֱ��Ӧ Э�鵥�ݺš��˷�/װж���͡��˷ѵ��ۡ�˰�ʡ����ֱ��������ֵ��ۡ��������֡��������ֵ���
					xybvo.setAttributeValue("xyvbillno",yfvbillno);
					xybvo.setAttributeValue("xytype", "1");//// �˷�
					xybvo.setAttributeValue("xypz", pactBVO_yf.getAttributeValue("pz"));
					xybvo.setAttributeValue("price", pactBVO_yf.getAttributeValue("price"));
					xybvo.setAttributeValue("rate", pactBVO_yf.getAttributeValue("rate"));
					xybvo.setAttributeValue("kdrate", pactBVO_yf.getAttributeValue("kdrate"));
					xybvo.setAttributeValue("kdprice", pactBVO_yf.getAttributeValue("kdprice"));
					xybvo.setAttributeValue("dckd", pactBVO_yf.getAttributeValue("dckd"));
					xybvo.setAttributeValue("kdprice2", pactBVO_yf.getAttributeValue("kdprice2"));
					xybvo.setAttributeValue("dr", 0);
					lists.add(xybvo);

				}
				if(null !=pactBVO_zx){
					String xyvbillno = new FormulaParseTool().getBsNameByID("hgts_sopact", "vbillno", "pk_pact", HgtsPubTool.getStringNullAsTrim(pactBVO_zx.getPrimaryKey())); 

					XieYiBVO xybvo=new XieYiBVO();
					String zxpz=HgtsPubTool.getStringNullAsTrim(pactBVO_zx.getAttributeValue("pz"));
					if(!"".equals(zxpz)){
						xybvo.setAttributeValue("xytype", "2");// װ����
//						if(HgtsPubConst.PZ_ZCF.equals(zxpz)){ // TODO �ǵøĳ���ʽ������pk
//							xybvo.setAttributeValue("xytype", "2");
//						}else if(HgtsPubConst.PZ_DMF.equals(zxpz)){
//							xybvo.setAttributeValue("xytype", "3");
//						}
					}
					xybvo.setAttributeValue("rowno", (v_rowno+1)*10);
					xybvo.setAttributeValue("xyvbillno",xyvbillno);
					xybvo.setAttributeValue("xypz", pactBVO_zx.getAttributeValue("pz"));
					xybvo.setAttributeValue("price", pactBVO_zx.getAttributeValue("price"));
					xybvo.setAttributeValue("rate", pactBVO_zx.getAttributeValue("rate"));
					xybvo.setAttributeValue("kdrate", pactBVO_zx.getAttributeValue("kdrate"));
					xybvo.setAttributeValue("kdprice", pactBVO_zx.getAttributeValue("kdprice"));
					xybvo.setAttributeValue("dckd", pactBVO_zx.getAttributeValue("dckd"));
					xybvo.setAttributeValue("kdprice2", pactBVO_zx.getAttributeValue("kdprice2"));
					xybvo.setAttributeValue("dr", 0);
					lists.add(xybvo);

				}

				AggSendnoticebillHVO aggvo=new AggSendnoticebillHVO();
				aggvo.setParentVO(hvo);
				aggvo.setTableVO("hgts_sendnoticebill_b", bvos);
				if(null !=qualityBVOs && qualityBVOs.length>0){
					aggvo.setTableVO("pk_quality_b", qualityBVOs);
				}

				if(null !=sendYzyjBVOs && sendYzyjBVOs.length>0){
					aggvo.setTableVO("pk_yzyj_b", sendYzyjBVOs);
				}

				if(null !=lists && lists.size()>0){
					xieYiBVO=lists.toArray(new XieYiBVO[0]);
					aggvo.setTableVO("pk_xy_b", xieYiBVO);
				}

				this.model.setUiState(UIState.ADD);
				this.model.setAppUiState(AppUiState.ADD);
				//���¼۸�,�Ǿ��ۺ�ͬ���¼۸�
				if(!isbidding.booleanValue()){
					IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
					AggSendnoticebillHVO temp_aggvo=(AggSendnoticebillHVO) col.colNewPrice(aggvo);
					aggvo.setChildrenVO(temp_aggvo.getChildrenVO());
				}
				this.getEditor().setValue(aggvo);

				String[] tablecode=new String[]{"hgts_sendnoticebill_b","pk_quality_b","pk_yzyj_b","pk_xy_b"};
				for(String k:tablecode){					
					this.getEditor().getBillCardPanel().getBillModel(k).loadLoadRelationItemValue();
				}
				
				// �����ֶβ��ɱ༭
				this.setItemEdit();
			}
		}
	}

	@Override
	protected boolean isActionEnable() {
		// TODO �Զ����ɵķ������
		return this.model.getUiState() == UIState.NOT_EDIT;
	}


	public void setItemEdit(){
		String[] key=new String[]{"pk_balatype","pk_transporttype","contcode","pk_busitype","pk_cust"};
		for(int i=0;i<key.length;i++){
			this.getEditor().getBillCardPanel().getHeadItem(key[i]).setEnabled(false);
		}
		this.getEditor().getBillCardPanel().getBillModel("hgts_sendnoticebill_b").getItemByKey("pz").setEnabled(false);
	}
}
