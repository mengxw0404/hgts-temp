package nc.impl.hgts;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import nc.bs.hgts.pc.PriceColHelper;
import nc.impl.hgts.pc.LoadPricePolicyImpl;
import nc.itf.hgts.pc.ILoadPricePolicy;
import nc.itf.hgts.pc.IPriceBizData;
import nc.itf.hgts.pc.IPriceCol;
import nc.itf.hgts.pc.IPricePolicyVO;
import nc.vo.hgts.pc.PcTransTool;
import nc.vo.hgts.pc.PricePllicyResultVO;
import nc.vo.hgts.pc.PricePolicyPKVO;
import nc.vo.hgts.pricepolicy.PricepolicyBVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

public class PriceColImpl implements IPriceCol {

	@Override
	public IBill colPrice(IBill bill) throws BusinessException, SQLException {
		// TODO �Զ����ɵķ������
		if(bill == null)
			return null;

		ISuperVO head = bill.getParent();
		ISuperVO[] items =null;
		if(bill instanceof AggSendnoticebillHVO){
			//����֪ͨ��
			items = bill.getChildren(SendnoticebillBVO.class);
			bill=sendnoticeQj(bill,head,items);
		
		}else {
			// ��ͬ
			items = bill.getChildren(PactBVO.class);

			bill=contractQj(bill,head,items);

		}
		return bill;
	}
	/**
	 * ����֪ͨ��-ȡ�۸��������¼۸�
	 * @param bill
	 * @param head
	 * @param items
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public IBill sendnoticeQj(IBill bill,ISuperVO head,ISuperVO[] items) throws BusinessException, SQLException{

		//����֪ͨ�� ��  ����  ����۸�
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			SendnoticebillBVO item=(SendnoticebillBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);
			//���ؼ۸�����
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			if(pInfor == null || pInfor.size() == 0)//δ�鵽���߶���
				continue;

			//���ݼ۸�����  ����  �۸����
			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add ���Թ����У� �ҵ��˼۸����ߣ�����ֻ�����������Żݣ������۸�Ҫ��δ����
				// �����У�ȡ�۸����ߵ�  һЩ����ֵ  �仯ǰ��ȡ�����ݣ��仯���޶�Ӧ�ļ۸����ߣ����仯ǰȡ��ֵ���
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){					
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("fkfsyh", null);
					item.setAttributeValue("ljyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("def13", null);				
					item.setAttributeValue("zxprice", null);
					item.setAttributeValue("jstotal", null);
					item.setAttributeValue("srcjgz", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("vnote", null);
					items[j]=item;
				}

				continue;


			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());//�Żݼ۸�
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// ���ʽ
						// ���ʽ�Ż�
						pay_tzprice=tzprice;
						vnote=vnote+"���ʽ("+policy_rowno+")"+pay_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//�����Ż� 
						ljyh_tzprice=tzprice;
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// �������
						qyyh_tzprice=tzprice;
						vnote=vnote+"�������("+policy_rowno+")"+qyyh_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // ���䷽ʽ
						ysfs_tzprice=tzprice;
						vnote=vnote+"���䷽ʽ("+policy_rowno+")"+ysfs_tzprice+";";
					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();//�۸�
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			item.setAttributeValue("fkfsyh", pay_tzprice.abs());
			item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			item.setAttributeValue("def13", ysfs_tzprice.abs());
			// ִ�м۸�
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg).
					sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")));			 		
			//�жϵ�ǰ����У�鷽ʽ  
			UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul"));
			if(head.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx) && shul.compareTo(new UFDouble(60)) > 0 ){
				//����У�� ���������ֲ��䣩 ��� = ���� * �µ���
				UFDouble jstotal = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")).multiply(zx_price);
				item.setAttributeValue("shul", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")));
				item.setAttributeValue("jstotal", jstotal);
			}else{
				//���У�� : ���� =  ��� / �µ���
				UFDouble shul2 = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jstotal")).div(zx_price);
				item.setAttributeValue("shul", shul2);
				item.setAttributeValue("jstotal", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("jstotal")));
			}
			item.setAttributeValue("zxprice", zx_price);

			// 2017-9-15 add
			item.setAttributeValue("srcjgz", jgz);

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			vnote="�۸���Դ"+vsourcecode+"�۸���"+jgz+";���Ƽ�"+gpjg+";"+vnote;
			item.setAttributeValue("vnote", vnote);
			items[j]=item;
		}
		bill.setChildren(SendnoticebillBVO.class, items);
		return bill;
	}
	/**
	 * 
	 * @param bill
	 * @param head
	 * @param items
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	public IBill contractQj(IBill bill,ISuperVO head,ISuperVO[] items) throws BusinessException, SQLException{
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			PactBVO item=(PactBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);

			//			���ؼ۸�����
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			//δ�鵽���߶���
			if(pInfor == null || pInfor.size() == 0){
				item.setAttributeValue("gpprice", null);
				item.setAttributeValue("price", null);
				item.setAttributeValue("yhprice", null);
				item.setAttributeValue("ysfsyh", null);
				item.setAttributeValue("qyyh", null);
				item.setAttributeValue("norateprice", null);				
				item.setAttributeValue("bratemny", null);
				item.setAttributeValue("bmny", null);
				item.setAttributeValue("bnoratemny", null);				
				item.setAttributeValue("csourceid", null);
				item.setAttributeValue("vsourcecode", null);
				item.setAttributeValue("csourcebid", null);
				item.setAttributeValue("yl1", null);
				items[j]=item;
				continue;
			}

			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
//			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
//			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
//			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
//			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add ���Թ����У� �ҵ��˼۸����ߣ�����ֻ�����������Żݣ������۸�Ҫ��δ����
				// �����У�ȡ�۸����ߵ�  һЩ����ֵ  �仯ǰ��ȡ�����ݣ��仯���޶�Ӧ�ļ۸����ߣ����仯ǰȡ��ֵ���
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){	
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("price", null);
					item.setAttributeValue("yhprice", null);
					item.setAttributeValue("ysfsyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("norateprice", null);				
					item.setAttributeValue("bratemny", null);
					item.setAttributeValue("bmny", null);
					item.setAttributeValue("bnoratemny", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("csourcebid", null);
					item.setAttributeValue("yl1", null);
					items[j]=item;
				}

				continue;

			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
//					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// ���ʽ
//						// ���ʽ�Ż�
//						pay_tzprice=tzprice;
//						vnote=vnote+"���ʽ("+policy_rowno+")"+pay_tzprice+";";
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//�����Ż� 
//						ljyh_tzprice=tzprice;
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// �������
//						qyyh_tzprice=tzprice;
//						vnote=vnote+"�������("+policy_rowno+")"+qyyh_tzprice+";";
//					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // ���䷽ʽ
//						ysfs_tzprice=tzprice;
//						vnote=vnote+"���䷽ʽ("+policy_rowno+")"+ysfs_tzprice+";";
//					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			//item.setAttributeValue("yhprice", pay_tzprice.abs());
			//item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			//item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			//item.setAttributeValue("ysfsyh", ysfs_tzprice.abs());
			// ִ�м۸�
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg)
					//.sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					//sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					//.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")))
					;
			// ��˰�ϼ�
			UFDouble ton=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("ton"));
			UFDouble jshj=zx_price.multiply(ton);
			UFDouble rate=HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("rate"));
			UFDouble norateprice=gpjg.div(rate.div(100).add(1)).setScale(2, UFDouble.ROUND_HALF_UP);
			UFDouble bnoratemny=(gpjg.div(rate.div(100).add(1)).multiply(ton)).setScale(2, UFDouble.ROUND_HALF_UP);
			UFDouble bratemny=jshj.sub(bnoratemny);

			item.setAttributeValue("price", zx_price);
			item.setAttributeValue("bmny", jshj);
			item.setAttributeValue("norateprice",norateprice );
			item.setAttributeValue("bratemny",bratemny );

			// 2017-9-15 add

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			item.setAttributeValue("csourcebid", bpk);
			vnote="�۸���Դ"+vsourcecode+"�۸���"+jgz+";���Ƽ�"+gpjg+";"+vnote;
			item.setAttributeValue("yl1", vnote);
			items[j]=item;

			bill.setChildren(PactBVO.class, items);

		}
		return bill;
	}


	@Override
	public IBill colNewPrice(IBill bill) throws BusinessException, SQLException {
		// TODO �Զ����ɵķ������
		if(bill == null)
			return null;

		ISuperVO head = bill.getParent();
		//����֪ͨ�� ��  ����  ����۸�
		ISuperVO[] items = bill.getChildren(SendnoticebillBVO.class);
		if(items == null || items.length ==0){
			return null;
		}
		for(int j=0;j<items.length;j++){
			SendnoticebillBVO item=(SendnoticebillBVO) items[j];
			PricePolicyPKVO pkey = PcTransTool.tran(head, item);
			//���ؼ۸�����
			ILoadPricePolicy loadBean = new LoadPricePolicyImpl();
			Map<Integer, List<IPricePolicyVO>> pInfor = loadBean.loadPricePolicy(pkey);

			if(pInfor == null || pInfor.size() == 0)//δ�鵽���߶���
				continue;

			//���ݼ۸�����  ����  �۸����
			IPriceBizData data = PcTransTool.getBizData(head, item);
			List<PricePllicyResultVO> price=PriceColHelper.col(pInfor,data);
			UFDouble gpjg=UFDouble.ZERO_DBL;
			UFDouble pay_tzprice=UFDouble.ZERO_DBL;
			UFDouble ljyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble qyyh_tzprice=UFDouble.ZERO_DBL;
			UFDouble ysfs_tzprice=UFDouble.ZERO_DBL;
			String jgz="";
			String bpk="";
			String policy_rowno="";
			FormulaParseTool tool=new FormulaParseTool();
			String vnote="";
			if(null == price || price.size()==0){
				// TODO 2017-11-27 add ���Թ����У� �ҵ��˼۸����ߣ�����ֻ�����������Żݣ������۸�Ҫ��δ����
				// �����У�ȡ�۸����ߵ�  һЩ����ֵ  �仯ǰ��ȡ�����ݣ��仯���޶�Ӧ�ļ۸����ߣ����仯ǰȡ��ֵ���
				if(null==item.getPrimaryKey() || "".equals(item.getPrimaryKey())){					
					item.setAttributeValue("gpprice", null);
					item.setAttributeValue("fkfsyh", null);
					item.setAttributeValue("ljyh", null);
					item.setAttributeValue("qyyh", null);
					item.setAttributeValue("def13", null);				
					item.setAttributeValue("zxprice", null);
					item.setAttributeValue("jstotal", null);
					item.setAttributeValue("srcjgz", null);				
					item.setAttributeValue("csourceid", null);
					item.setAttributeValue("vsourcecode", null);
					item.setAttributeValue("vnote", null);
					items[j]=item;
				}

				continue;


			}else{				
				for(int i=0;i<price.size();i++){
					PricePllicyResultVO rstVO=price.get(i);
					PricepolicyBVO bvo=(PricepolicyBVO) rstVO.getPolicy();
					UFDouble tzprice=HgtsPubTool.getUFDoubleNullAsZero(rstVO.getNadprice());//�Żݼ۸�
					bpk=bvo.getPk_policy_b();
					policy_rowno=tool.getBsNameByID("hgts_pricepolicy_b", "rowno", "pk_pricepolicy_b", bpk);
					String jgys=HgtsPubTool.getStringNullAsTrim(bvo.getAttributeValue("jgys"));
					if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_pay){ 				// ���ʽ
						// ���ʽ�Ż�
						pay_tzprice=tzprice;
						vnote=vnote+"���ʽ("+policy_rowno+")"+pay_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_numprice){		//�����Ż� 
						ljyh_tzprice=tzprice;
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_senddistance){	// �������
						qyyh_tzprice=tzprice;
						vnote=vnote+"�������("+policy_rowno+")"+qyyh_tzprice+";";
					}else if(Integer.parseInt(jgys)==HgtsPubConst.price_policy_transtype){ // ���䷽ʽ
						ysfs_tzprice=tzprice;
						vnote=vnote+"���䷽ʽ("+policy_rowno+")"+ysfs_tzprice+";";
					}
					jgz=bvo.getJgz();
					gpjg=bvo.getGpPrice();//�۸�
				}
			}

			item.setAttributeValue("gpprice", gpjg);
			item.setAttributeValue("fkfsyh", pay_tzprice.abs());
			item.setAttributeValue("ljyh", ljyh_tzprice.abs());
			item.setAttributeValue("qyyh", qyyh_tzprice.abs());
			item.setAttributeValue("def13", ysfs_tzprice.abs());
			// ִ�м۸�
			UFDouble zx_price=HgtsPubTool.getUFDoubleNullAsZero(gpjg).
					sub(pay_tzprice.abs()).sub(ljyh_tzprice.abs()).
					sub(qyyh_tzprice.abs()).sub(ysfs_tzprice.abs())
					.add(HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("zcfee")));			 		
			// ��� = ���� * �µ���
			UFDouble jstotal = HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")).multiply(zx_price);
			item.setAttributeValue("shul", HgtsPubTool.getUFDoubleNullAsZero(item.getAttributeValue("shul")));
			item.setAttributeValue("jstotal", jstotal);
			item.setAttributeValue("zxprice", zx_price);

			// 2017-9-15 add
			item.setAttributeValue("srcjgz", jgz);

			String csourceid=tool.getBsNameByID("hgts_pricepolicy_b", "pk_pricepolicy", "pk_pricepolicy_b", bpk);
			String vsourcecode=tool.getBsNameByID("hgts_pricepolicy", "vbillno", "pk_pricepolicy", csourceid);
			item.setAttributeValue("csourceid", csourceid);
			item.setAttributeValue("vsourcecode", vsourcecode);
			vnote="�۸���Դ"+vsourcecode+"�۸���"+jgz+";���Ƽ�"+gpjg+";"+vnote;
			item.setAttributeValue("vnote", vnote);
			items[j]=item;
		}
		bill.setChildren(SendnoticebillBVO.class, items);

		return bill;
	}

}
