package nc.ui.hgts.ff_sknoticebill.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import nc.ui.hgts.sendnoticebill.actions.PactDataShowDialog;
import nc.ui.hgts.sendnoticebill.actions.QueryPactDlg;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.ffsknoticebill.AggFfSknoticebillHVO;
import nc.vo.hgts.ffsknoticebill.FfSknoticebillBVO;
import nc.vo.hgts.ffsknoticebill.FfSknoticebillHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.NumberToCN;
import nc.vo.hgts.sendnoticebill.SendRefPactVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;

public class RefContractAction extends nc.ui.pubapp.uif2app.actions.AddAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4586104270391951370L;

	public RefContractAction() {
		// TODO �Զ����ɵĹ��캯�����
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
		QueryPactDlg rtnValue = new QueryPactDlg(this.getBufferCondition(),"40H10905");
		return rtnValue;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO �Զ����ɵķ������
		QueryPactDlg dlg = this.getConditionDlg();
		if (dlg.showModal()==UIDialog.ID_OK) {
			String sql=dlg.getRstSql();
			sql=sql+" and nvl(b.bmny,0)-nvl(b.ysmny,0)>0 ";
			PactDataShowDialog dig=new PactDataShowDialog(null, sql,getModel().getContext());
			dig.setVisible(true);
			SendRefPactVO vo=dig.getPactVO();
			if(null !=vo){
				String pk_org=vo.getPk_org();
				String vbillno=vo.getVbillno(); // ��ͬ���ݺ�
				String contcode=vo.getContcode();
				String pk_cust=vo.getPk_cust();
				String pk_balatype=vo.getPk_balatype();
				String pk_busitype=vo.getPk_busitype();
				String transport=vo.getTransport();
				String pk_kc=vo.getPk_kb();
				String pk_pact=vo.getPk_pact();
				String pk_pact_b=vo.getPk_pact_b();// ��ͬ������
				String pk_dept=vo.getPk_dept();//���۲���
				UFDouble yhprice=HgtsPubTool.getUFDoubleNullAsZero(vo.getYhprice());

				UFDouble price=vo.getPrice();
				UFDouble shul=vo.getShul();
				UFDouble zxprice=price.sub(yhprice);
				UFDouble jstotal=zxprice.multiply(shul);
				FfSknoticebillHVO hvo=new FfSknoticebillHVO();
				hvo.setAttributeValue("pk_group", AppContext.getInstance().getPkGroup());
				hvo.setAttributeValue("pk_org", pk_org);				
				hvo.setAttributeValue("pk_org_v", new FormulaParseTool().getNameByID("org_orgs", "pk_vid", "pk_org", pk_org));
				hvo.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
				hvo.setAttributeValue("vbillstatus", -1);
				hvo.setAttributeValue("pk_billtypeid", HgtsPubConst.SKTZD);
				hvo.setAttributeValue("customer", pk_cust);
				hvo.setAttributeValue("pk_balatype", pk_balatype);
				hvo.setAttributeValue("pk_transporttype", transport);
				hvo.setAttributeValue("pk_kc", pk_kc);
				hvo.setAttributeValue("contcode", contcode);
				hvo.setAttributeValue("dr", 0);
				hvo.setAttributeValue("pk_dept", pk_dept);
				
				// ��ͬ �ӱ�
				PactBVO pactBVO=(PactBVO) HYPubBO_Client.queryByPrimaryKey(PactBVO.class, pk_pact_b);
				String	csourcetypecode = HgtsPubTool.getStringNullAsTrim(pactBVO.getAttributeValue("csourcetypecode")) ;
				//�����ս��
				UFDouble bmny=HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("bmny"));
				UFDouble ysmny=HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("ysmny"));//����
				
				UFDouble t_skmny=bmny.sub(ysmny);// ʣ���տ���
				UFDouble t_shnum = t_skmny.div(HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("price")));//������==���/����
				FfSknoticebillBVO skbvo=new FfSknoticebillBVO();
				skbvo.setAttributeValue("local_money_cr", t_skmny);
				skbvo.setAttributeValue("ton",t_shnum );
				skbvo.setAttributeValue("pk_inv", pactBVO.getAttributeValue("inv"));
				skbvo.setAttributeValue("price", pactBVO.getAttributeValue("price"));
				skbvo.setAttributeValue("csourcebid", pactBVO.getPrimaryKey());
				skbvo.setAttributeValue("csourceid", pk_pact);
				skbvo.setAttributeValue("csourcetypecode", "YX40");
				
//				PactBVO[] pactBVOs=(PactBVO[]) HYPubBO_Client.queryByCondition(PactBVO.class, " nvl(dr,0)=0 and pk_pact='"+pk_pact+"'");
//				FfSknoticebillBVO[] skbvos=null;
//				UFDouble t_skmny=UFDouble.ZERO_DBL;//�տ���
//				UFDouble t_shnum=UFDouble.ZERO_DBL;//�տ�����
//				String  csourcetypecode = "";
//				if(null !=pactBVOs && pactBVOs.length>0){
//					skbvos=new FfSknoticebillBVO[pactBVOs.length];
//					
//					for(int i=0;i<pactBVOs.length;i++){
//						PactBVO pactBVO=pactBVOs[i];
//						csourcetypecode = HgtsPubTool.getStringNullAsTrim(pactBVO.getAttributeValue("csourcetypecode")) ;
//						FfSknoticebillBVO skbvo=new FfSknoticebillBVO();
//						//�����ս��
//						UFDouble bmny=HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("bmny"));
//						UFDouble ysmny=HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("ysmny"));
//						
//						UFDouble symny=bmny.sub(ysmny);// ʣ���տ���
//						t_skmny =t_skmny.add(symny);
//						//����==���/����
//						t_shnum = symny.div(HgtsPubTool.getUFDoubleNullAsZero(pactBVO.getAttributeValue("price")));
//						
//						skbvo.setAttributeValue("local_money_cr", symny);
//						skbvo.setAttributeValue("ton",t_shnum );
//						skbvo.setAttributeValue("pk_inv", pactBVO.getAttributeValue("inv"));
//						skbvo.setAttributeValue("price", pactBVO.getAttributeValue("price"));
//						skbvo.setAttributeValue("csourcebid", pactBVO.getPrimaryKey());
//						skbvo.setAttributeValue("csourceid", pk_pact);
//						skbvo.setAttributeValue("csourcetypecode", "YX40");
//						skbvos[i]=skbvo;
//					}
//				}
				hvo.setAttributeValue("skmny", t_skmny);
				hvo.setAttributeValue("shnum", t_shnum);
				hvo.setAttributeValue("checktype", 1);
				AggFfSknoticebillHVO aggvo=new AggFfSknoticebillHVO();
				NumberToCN cn=new NumberToCN();
				String dx=cn.numberCNMontrayUnit(t_skmny.toBigDecimal());
				hvo.setAttributeValue("def0", dx);
				aggvo.setParentVO(hvo);
//				if(null !=skbvos && skbvos.length>0){					
//					aggvo.setChildrenVO(skbvos);
//				}
				aggvo.setChildrenVO(new FfSknoticebillBVO[]{skbvo});
				this.model.setUiState(UIState.ADD);
				this.model.setAppUiState(AppUiState.ADD);
				
				//FieldEventUIUtil.fireUILoadComplete((BillManageModel) getModel());
				this.getEditor().setValue(aggvo);
				this.getEditor().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
			}
		}
	}

	@Override
	protected boolean isActionEnable() {
		// TODO �Զ����ɵķ������
		return this.model.getUiState() == UIState.NOT_EDIT;
	}

	
}
