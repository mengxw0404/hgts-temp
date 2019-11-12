package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.pubapp.util.VOSortUtils;

public class ConOfPriceAction  extends NCAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5296203813087965366L;

	public ConOfPriceAction(){
		super();
		super.setCode("ConOfPriceAction");
		super.setBtnName("ȡ���۶�");
	}
	private AbstractAppModel model;
	private RefreshSingleAction refreshAction;
	
	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public RefreshSingleAction getRefreshAction() {
		return refreshAction;
	}

	public void setRefreshAction(RefreshSingleAction refreshAction) {
		this.refreshAction = refreshAction;
	}
	
	/**
	 * ��ת�У�1�� ��˰�ϼ� = �ʼ�ļ�˰�ϼƣ���������ʱ�ģ�-sum(ÿһ�е��ѹ������� * ִ�м۸�)��
	 * 2����ѯ�µļ۸����ߣ������µ�ִ�м۸�
	 * 3������ = ��˰�ϼƳ���ִ�м۸�
	 */
	@Override
	public void doAction(ActionEvent event) throws Exception {
		// TODO �˴β���ע��,������ſ���ע�� 
		AggSendnoticebillHVO data=(AggSendnoticebillHVO) this.getModel().getSelectedData();
		SendnoticebillHVO hvo=data.getParentVO();
		//���͵���Դ�ж�
		if("Y".equals(HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("isbidding")))){
			MessageDialog.showWarningDlg(null, "����", "��Դ��ͬΪ���ۺ�ͬ�����ɽ���ȡ�۱����");
			return;
		}
		//��ȡʱ����Ϣ���жϵ��ݿɲ�����
		String startdate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("startdate")).substring(0, 10);
		String enddate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("enddate")).substring(0, 10);		
		String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		if(curDate.compareTo(enddate)>=1 || curDate.compareTo(startdate)<=-1){
			throw new BusinessException("��֪ͨ��������Ч�ڷ�Χ�ڣ��������۶ֲ�����");
		}
		
		//��ȡ���䷽ʽ
		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_transporttype"));	
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) data.getTableVO("hgts_sendnoticebill_b");
	
		//�����������ݶ���
		List<SendnoticebillBVO> list=new ArrayList<SendnoticebillBVO>();
		//����������
		SendnoticebillBVO[] blavo= (SendnoticebillBVO[]) HYPubBO_Client.queryByCondition(SendnoticebillBVO.class, "nvl(dr,0) = 0 and blatest = 'Y' and  pk_sendnoticebill ='"+hvo.getPrimaryKey()+"'");
		
		if(null !=bvos && bvos.length>0){
			//�ر������ӱ�����
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				bvo.setAttributeValue("dr", 0);
				bvo.setAttributeValue("blatest", "N");
				list.add(bvo);
			}
			// ��ѯ�µĹ��Ƽۣ��Żݵĸ����۸�������յ�ִ�м۸���������		
			AggSendnoticebillHVO clientVO=(AggSendnoticebillHVO) data.clone();
			SendnoticebillHVO chvo=clientVO.getParentVO();
			clientVO.setParentVO(chvo);
			clientVO.setChildrenVO(blavo);
			IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
			//��ȡһ�����ؽ��������������Ϊ��Ϣ�Ĺ��Ƽ۸�
			AggSendnoticebillHVO aggvo=(AggSendnoticebillHVO) col.colPrice(clientVO);
			SendnoticebillBVO[] newbvos=(SendnoticebillBVO[]) aggvo.getTableVO("hgts_sendnoticebill_b");
			if(null !=newbvos && newbvos.length>0){
				UFDouble newprice=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("zxprice"));
				UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("jstotal"));
				
				UFDouble num=UFDouble.ZERO_DBL;			// ����
				UFDouble carstrong=UFDouble.ZERO_DBL;	// ��׼����
				if(null !=pk_transporttype && HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){//��·��
					UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("cyfee"));
					UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def11"));
					UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def12"));

					newprice = newprice.add(cyfee).add(zyxfee).add(qscfee);
					
					carstrong=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("carstrong"));
					
				}
				UFDouble ches=UFDouble.ZERO_DBL;// ����
				
				UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("shul"));
				if(aggvo.getParentVO().getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx) && shul.compareTo(new UFDouble(60)) > 0 ){
					//����У�� ���������ֲ��䣩 ��� = ���� * �µ���
					num = HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("shul"));
					jstotal=num.multiply(newprice);
				}else{
					//���У�� : ���� =  ��� / �µ���
					if(newprice.doubleValue() !=0)			
						num=jstotal.div(newprice);
					if(carstrong.doubleValue() !=0){
						// ȡģ
						UFDouble carnum=num.div(carstrong);
						String[]str=carnum.toString().split("[.]");
						ches=HgtsPubTool.getUFDoubleNullAsZero(str[0]);
					}
				}
//	
				UFDouble rowno =HgtsPubTool.getUFDoubleNullAsZero(blavo[0].getAttributeValue("rowno"));
				newbvos[0].setAttributeValue("rowno", (rowno.add(10).intValue()));
				newbvos[0].setAttributeValue("shul", num);
				newbvos[0].setAttributeValue("jstotal", jstotal);
				newbvos[0].setAttributeValue("zxprice", newprice);
				newbvos[0].setAttributeValue("carnum", ches);
				newbvos[0].setAttributeValue("dr", 0);
				newbvos[0].setAttributeValue("rowcloseflag", "N");
				newbvos[0].setAttributeValue("blatest", "Y");	// ���°汾
				newbvos[0].setAttributeValue("closer", null);
				newbvos[0].setAttributeValue("closetime", null);
				newbvos[0].setAttributeValue("opener", null);
				newbvos[0].setAttributeValue("opentime", null);
				newbvos[0].setAttributeValue("pk_sendnoticebill_b", null);
				newbvos[0].setAttributeValue("dr", 0);
//				newbvos[0].setAttributeValue("yzxnum", null);
				newbvos[0].setAttributeValue("def19", null);    // �۶���+�۶�ʱ�䣻ȡ���۶���+ʱ��
//				newbvos[0].setAttributeValue("def6", null); 	// װ���ƻ���װ����
//				newbvos[0].setAttributeValue("yjsnum", null); 	// �ѽ�������
//				newbvos[0].setAttributeValue("ykpnum", null); 	// �ѿ�Ʊ����
//				newbvos[0].setAttributeValue("mny", null); 		// �ѿ�Ʊ���

				newbvos[0].setAttributeValue("def1", null); 	// ��¼��ת��Ŀ�ĵ��ݵĵ��ݺ�
				newbvos[0].setAttributeValue("def2", null);		// ��¼  ��ǰ��ת�� �ӱ�����
				newbvos[0].setAttributeValue("def3", null);		// ��¼  ��ǰ��ת��Ŀ�ĵ��ݵ� �ӱ�����
				newbvos[0].setAttributeValue("def4", null);     // ��¼��ת�ĵ��ݺţ���ת����
				newbvos[0].setAttributeValue("def14", null);	// ��¼��ת����������
				newbvos[0].setAttributeValue("def18", null);	// ȡ����+ȡ��ʱ��
				newbvos[0].setAttributeValue("def20", null);	// ��ת��+��תʱ�䣻ȡ����ת��+ȡ����תʱ��

				String cuserid=AppContext.getInstance().getPkUser();
				String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
				newbvos[0].setAttributeValue("def18", "ȡ���ˣ�"+username+"��ȡ��ʱ�䣺"+AppContext.getInstance().getServerTime());
				// 2018-10-11 begin���  ֪ͨ���������Ƿ� ���복�ƻ���
				CommActionCheck check=new CommActionCheck();			
				list.add(newbvos[0]);
				clientVO.setChildrenVO(list.toArray(new SendnoticebillBVO[0]));
				String rst=check.isOver(clientVO);
				if(null !=rst && !"".equals(rst)){
					throw new BusinessException(rst);
				}
				// 2018-10-11 end
				HYPubBO_Client.getService().insert(newbvos[0]);
				
			}
			// 2018-2-26 begin ���ǰһ���������
			//VOSortUtils.ascSort(blavo, new String[]{"rowno"});
			String cuserid=AppContext.getInstance().getPkUser();
			String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
			blavo[0].setAttributeValue("def19", "�۶��ˣ�"+username+"���۶�ʱ�䣺"+AppContext.getInstance().getServerTime()+"��");
			blavo[0].setAttributeValue("dr", 0);
			blavo[0].setAttributeValue("blatest", "N");
			blavo[0].setAttributeValue("rowcloseflag", "Y");
			HYPubBO_Client.getService().updateAry(blavo);
			// 2018-2-26 end 
			if(ValueUtils.getUFBoolean(hvo.getAttributeValue("closeflag")).booleanValue()){					
				hvo.setAttributeValue("closeflag", "N");
				hvo.setAttributeValue("dr", 0);
				hvo.setAttributeValue("closer",null);
				hvo.setAttributeValue("closetime", null);
				hvo.setAttributeValue("opener", AppContext.getInstance().getPkUser());
				hvo.setAttributeValue("opentime", AppContext.getInstance().getServerTime());
				HYPubBO_Client.update(hvo);
			}
		}
	
		//ˢ��
		this.getRefreshAction().doAction(event);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggSendnoticebillHVO billVO=(AggSendnoticebillHVO) this.model.getSelectedData();
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) billVO.getTableVO("hgts_sendnoticebill_b"); 
		VOSortUtils.ascSort(bvos, new String[]{"rowno"});
		UFBoolean blatest=UFBoolean.FALSE;
		UFBoolean isJz=UFBoolean.FALSE;
		if(null !=bvos && bvos.length>0){
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				//�йر� && ���°汾
				if(ValueUtils.getUFBoolean(bvo.getAttributeValue("rowcloseflag")).booleanValue() 
						&& ValueUtils.getUFBoolean(bvo.getAttributeValue("blatest")).booleanValue()){
					blatest = UFBoolean.TRUE;
				}
			}
			String def1=HgtsPubTool.getStringNullAsTrim(bvos[bvos.length-1].getAttributeValue("def1")); // ��¼��ת��Ŀ�ĵ��ݵĵ��ݺ�
			if(null ==def1 || "".equals(def1)){
				isJz=UFBoolean.TRUE;
			}
		}
		// �йر� && ���°汾������δ���н�ת�������ð�ť �ſ���
		if(blatest.booleanValue()  && isJz.booleanValue()){
			return true;
		}
		return false;
	}
	
}
