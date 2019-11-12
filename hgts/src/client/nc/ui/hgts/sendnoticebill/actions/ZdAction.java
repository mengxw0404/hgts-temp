package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.hgts.pc.IPriceCol;
import nc.ui.pubapp.uif2app.actions.RefreshSingleAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.pubapp.util.VOSortUtils;

import java.util.List;
import java.util.ArrayList;

public class ZdAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2850671689154537767L;

	public ZdAction(){
		super();
		super.setCode("zdAction");
		super.setBtnName("�۶�");
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
	public void doAction(ActionEvent e) throws Exception {
		// TODO �˴β���ע��,������ſ���ע�� 
		AggSendnoticebillHVO data=(AggSendnoticebillHVO) this.getModel().getSelectedData();
		SendnoticebillHVO hvo=data.getParentVO();
		String startdate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("startdate")).substring(0, 10);
		String enddate=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("enddate")).substring(0, 10);		
		String curDate=AppContext.getInstance().getServerTime().toStdString().substring(0, 10);
		if(curDate.compareTo(enddate)>=1 || curDate.compareTo(startdate)<=-1){
			throw new BusinessException("��֪ͨ��������Ч�ڷ�Χ�ڣ��������۶ֲ�����");
		}

		String pk_transporttype=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_transporttype"));
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) data.getTableVO("hgts_sendnoticebill_b");

		AggSendnoticebillHVO clientVO=(AggSendnoticebillHVO) data.clone();

		List<SendnoticebillBVO> list=new ArrayList<SendnoticebillBVO>();

		int maxrowno=0;

		if(null !=bvos && bvos.length>0){
			String no=HgtsPubTool.getStringNullAsTrim(bvos[0].getAttributeValue("rowno"));
			maxrowno="".equals(no)?0:Integer.parseInt(no);
			for(int i=0;i<bvos.length;i++){
				String rowno=HgtsPubTool.getStringNullAsTrim(bvos[i].getAttributeValue("rowno"));
				int ino="".equals(rowno)?0:Integer.parseInt(rowno);
				if(ino>maxrowno){
					maxrowno=ino;
				}
			}

			// �ܷ��˽��
			UFDouble jstotal=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("jstotal"));
			//�ر������ӱ�����
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				bvo.setAttributeValue("dr", 0);
				UFDouble i_jstotal=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jstotal"));
				if(i_jstotal.doubleValue()>jstotal.doubleValue()){
					jstotal=i_jstotal;
				}
				list.add(bvo);
			}

			// �ѷ��˽��
			UFDouble yfymny=UFDouble.ZERO_DBL;
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];				
				UFDouble gbnum=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("yzxnum"));//�ѹ�������
				UFDouble zxprice=HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("zxprice"));//ִ�м۸�
				yfymny = yfymny.add(gbnum.multiply(zxprice));
			}
			UFDouble mny=jstotal.sub(yfymny);			// ���	

			// ��ѯ�µĹ��Ƽۣ��Żݵĸ����۸�������յ�ִ�м۸���������		
			SendnoticebillHVO chvo=clientVO.getParentVO();
			chvo.setAttributeValue("startdate", AppContext.getInstance().getBusiDate());
			clientVO.setParentVO(chvo);
			IPriceCol col=(IPriceCol) NCLocator.getInstance().lookup(IPriceCol.class.getName());
			AggSendnoticebillHVO aggvo=(AggSendnoticebillHVO) col.colPrice(clientVO);
			SendnoticebillBVO[] newbvos=(SendnoticebillBVO[]) aggvo.getTableVO("hgts_sendnoticebill_b");
			if(null !=newbvos && newbvos.length>0){
				UFDouble newprice=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("zxprice"));
				UFDouble carstrong=UFDouble.ZERO_DBL;	// ��׼����
				if(null !=pk_transporttype && HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
					UFDouble cyfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("cyfee"));
					UFDouble zyxfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def11"));
					UFDouble qscfee=HgtsPubTool.getUFDoubleNullAsZero(newbvos[0].getAttributeValue("def12"));

					carstrong=HgtsPubTool.getUFDoubleNullAsZero(bvos[0].getAttributeValue("carstrong"));

					newprice = newprice.add(cyfee).add(zyxfee).add(qscfee);
				}

				UFDouble newnum=mny.div(newprice);

				UFDouble ches=UFDouble.ZERO_DBL;	// ����
				if(carstrong.doubleValue() !=0){
					// ȡģ
					UFDouble carnum=newnum.div(carstrong);
					String[]str=carnum.toString().split("[.]");
					ches=HgtsPubTool.getUFDoubleNullAsZero(str[0]);
				}

				newbvos[0].setAttributeValue("rowno", (maxrowno+10));
				newbvos[0].setAttributeValue("shul", newnum);
				newbvos[0].setAttributeValue("jstotal", mny);
				newbvos[0].setAttributeValue("zxprice", newprice);
				newbvos[0].setAttributeValue("carnum", ches);
				newbvos[0].setAttributeValue("rowcloseflag", "N");
				newbvos[0].setAttributeValue("closer", null);
				newbvos[0].setAttributeValue("closetime", null);
				newbvos[0].setAttributeValue("opener", null);
				newbvos[0].setAttributeValue("opentime", null);
				newbvos[0].setAttributeValue("pk_sendnoticebill_b", null);
				newbvos[0].setAttributeValue("dr", 0);
				newbvos[0].setAttributeValue("yzxnum", null);
				newbvos[0].setAttributeValue("def19", null);    // �۶���+�۶�ʱ�䣻ȡ���۶���+ʱ��
				newbvos[0].setAttributeValue("def6", null); 	// װ���ƻ���װ����
				newbvos[0].setAttributeValue("yjsnum", null); 	// �ѽ�������
				newbvos[0].setAttributeValue("ykpnum", null); 	// �ѿ�Ʊ����
				newbvos[0].setAttributeValue("mny", null); 		// �ѿ�Ʊ���

				newbvos[0].setAttributeValue("def1", null); 	// ��¼��ת��Ŀ�ĵ��ݵĵ��ݺ�
				newbvos[0].setAttributeValue("def2", null);		// ��¼  ��ǰ��ת�� �ӱ�����
				newbvos[0].setAttributeValue("def3", null);		// ��¼  ��ǰ��ת��Ŀ�ĵ��ݵ� �ӱ�����
				newbvos[0].setAttributeValue("def4", null);     // ��¼��ת�ĵ��ݺţ���ת����
				newbvos[0].setAttributeValue("def14", null);	// ��¼��ת����������
				newbvos[0].setAttributeValue("def18", null);	// ȡ����+ȡ��ʱ��
				newbvos[0].setAttributeValue("def20", null);	// ��ת��+��תʱ�䣻ȡ����ת��+ȡ����תʱ��

				// 2018-10-11 begin
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

			// 2018-2-26 begin
			VOSortUtils.ascSort(bvos, new String[]{"rowno"});
			String cuserid=AppContext.getInstance().getPkUser();
			String username=new FormulaParseTool().getNameByID("sm_user","user_name","cuserid",cuserid);
			bvos[bvos.length-1].setAttributeValue("def19", "�۶��ˣ�"+username+"���۶�ʱ�䣺"+AppContext.getInstance().getServerTime()+"��");
			bvos[bvos.length-1].setAttributeValue("dr", 0);
			bvos[bvos.length-1].setAttributeValue("blatest", "N");
			bvos[bvos.length-1].setAttributeValue("rowcloseflag", "Y");
			HYPubBO_Client.getService().updateAry(bvos);
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

			this.getRefreshAction().doAction(e);

		}

	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData()==null)
			return false;
		AggSendnoticebillHVO billVO=(AggSendnoticebillHVO) this.model.getSelectedData();
		SendnoticebillBVO[] bvos=(SendnoticebillBVO[]) billVO.getTableVO("hgts_sendnoticebill_b"); 
		VOSortUtils.ascSort(bvos, new String[]{"rowno"});
		int num=0;
		UFBoolean isJz=UFBoolean.FALSE;
		if(null !=bvos && bvos.length>0){
			for(int i=0;i<bvos.length;i++){
				SendnoticebillBVO bvo=bvos[i];
				if(ValueUtils.getUFBoolean(bvo.getAttributeValue("rowcloseflag")).booleanValue()){
					num=num+1;
				}
			}
			String def1=HgtsPubTool.getStringNullAsTrim(bvos[bvos.length-1].getAttributeValue("def1")); // ��¼��ת��Ŀ�ĵ��ݵĵ��ݺ�
			if(null ==def1 || "".equals(def1)){
				isJz=UFBoolean.TRUE;
			}
		}
		// ����ȫ���رգ�����δ���н�ת�������ð�ť �ſ���
		if(num==bvos.length && isJz.booleanValue()){
			return true;
		}
		return false;
	}


}
