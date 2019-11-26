package nc.ui.hgts.hjsettle.yh.comm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.busibean.ISysInitQry;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hgts.pc.BdInfoVOForSettle;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.qualityreport.QualityreportBVO;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.util.VOSortUtils;

/**
 * ��������-�ۿ�۶�
 * @author cl
 *
 */
public class QuaDj {

	public QuaDj() {
		// TODO �Զ����ɵĹ��캯�����
	}


	public List<QualityreportBVO> quality(BdInfoVOForSettle infor){
		try {
			String sql="select b.* "
					+" from hgts_qualityreport h inner join hgts_qualityreport_b b"
					+" on h.pk_qualityreport=b.pk_qualityreport"
					+" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 and h.vbillno='"+infor.getQcBillID()+"'";
			List<QualityreportBVO> list=(List) getService().executeQuery(sql, new BeanListProcessor(QualityreportBVO.class));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public IUAPQueryBS getService(){
		IUAPQueryBS bs=NCLocator.getInstance().lookup(IUAPQueryBS.class);
		return bs;
	}

	/**
	 * �ۼ� ���൱�ڹ��Ƽ� 
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 *��  4500		  	�ۼ�	������*0.102	�൱�ڹ��Ƽ�
		4300	4500	�ۼ�	������*0.101	�൱�ڹ��Ƽ�
		4100	4300	�ۼ�	������*0.1	�൱�ڹ��Ƽ�
		4000	4100	�ۼ�	������*0.099	�൱�ڹ��Ƽ�
		0		4000	�ۼ�	0			�൱�ڹ��Ƽ�
	 */
	public UFDouble getTz_quaindex_sj(BdInfoVOForSettle infor,String pk_send_h,String settlezt,
			String gbdate,String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		
		int month=Integer.parseInt(gbdate.substring(5, 7));
		SendYzyjBVO[] items=null;
		for(int j=0;j<list.size();j++){		

			QualityreportBVO reportBVO=list.get(j);

			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // �ʼ���Ŀpk

			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
							+ "and pk_project='"+pk_prj+"'"
							+ " and bkdrule='0' "); // Ĭ�ϲ�ѯΪ���ۼۡ���

			if(null !=items && items.length>0){
				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // ���� �ʼ���
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// �򷽡� ������˫����		
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2�� ����	
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3��������˫����
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // ����
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:������˫����
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // �۸���㹫ʽ

				for(int i=0;i<items.length;i++){
					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// ������Ĭ��Ϊ���ֵ
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}
				}
				if(null !=jgfd && !"".equals(jgfd)){
					// ָ�껯��������������ĳ����ʽ�У�   ��� ���۸�䶯���ֶ� ֱ��д��0��˵�� ���ν���� �ۺϵ����� �۸�Ϊ 0 ��
					if(jgfd.equals("0")){						
						fdzvalue= null;
						break;
					}
					if(jgfd.indexOf("����ֵ")>=0){						
						jgfd=jgfd.replace("����ֵ", "hyz").replaceAll("��", "(").replaceAll("��", ")");
						//TODO �ҷ� Ҥ����Ŀ�Ƿ��ǰ������㷨����
						//��������ֵ-9.52��/0.5��*��-8�� �����ڼ���ȡ��������������һ
						// �磺������ֵ-9.52��/0.5 = 1.1����ȡֵΪ2����=1.0����ȡֵΪ1
						FormulaParse fromula = new FormulaParse();		
					
						fromula.addVariable("hyz", zjrst);
						fromula.setExpress(jgfd);
						String value=fromula.getValue();						
						fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value).abs());
						//}
					}else{
						if (jgfd.matches("^[0-9]*$")){							
							fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
						}
					}
				}		
			}
		}

		return fdzvalue;
	}
	
	/**
	 * �ۿ�
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 * 
	 */
	public UFDouble getTz_quaindex_kk(BdInfoVOForSettle infor,String pk_send_h,String settlezt,String gbdate,
			String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		int month=Integer.parseInt(gbdate.substring(5, 7));
		SendYzyjBVO[] items=null;
		for(int j=0;j<list.size();j++){		
			
			QualityreportBVO reportBVO=list.get(j);
		
			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // �ʼ���Ŀpk
			
			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
					+ "and pk_project='"+pk_prj+"'"
					+ " and bkdrule='1' "); // Ĭ�ϲ�ѯΪ�ۿ��
			
			if(null !=items && items.length>0){
				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // �ʼ���
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// ��			
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2�� ����	
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3��������˫����
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // ����
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:������˫����
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // �۸���㹫ʽ

				for(int i=0;i<items.length;i++){
					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// ������Ĭ��Ϊ���ֵ
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
							break;
						}
					}
				}
				if(null !=jgfd && !"".equals(jgfd)){
					// ��� ���۸�䶯���ֶ� ֱ��д��0��˵�� ���ν���� �ۺϵ����� �۸�Ϊ 0 ��
					if(jgfd.equals("0")){						
						fdzvalue= null;
						break;
					}
					if( jgfd.indexOf("����ֵ")>=0 ){						
						jgfd=jgfd.replace("����ֵ", "hyz").replaceAll("��", "(").replaceAll("��", ")");

						//��������ֵ-9.52��/0.5��*��-8�� �����ڼ���ȡ��������������һ
						// �磺������ֵ-9.52��/0.5 = 1.1����ȡֵΪ2����=1.0����ȡֵΪ1
						FormulaParse fromula = new FormulaParse();			
						fromula.addVariable("hyz", zjrst);
						fromula.setExpress(jgfd);
						String value=fromula.getValue();
						//fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value).abs());
						fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(value));
					
					}else{
						if (jgfd.matches("^[-\\+]?[0-9]*$")){							
							//fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
							fdzvalue=fdzvalue.add(HgtsPubTool.getUFDoubleNullAsZero(jgfd));
						}
					}
				}		
			}
		}
		return fdzvalue;
	}
	
	/**
	 * �۶�
	 * @param infor
	 * @param pk_send_h
	 * @return
	 * @throws UifException
	 * 
	 * ������׼����ֵ�R5200Kcal/Kg���̶�̼�R48%���ҷ֨Q23%��ˮ�֨Q7%��������ˮ�֨Q1.5%���ӷ���25%-28%����Q1.0%��Ӧ���
		�۶ֹ��򣺻ҷ֣�23%����������5200ʱ���۷�����������1%��
			        �ҷ֣�24%����������5100ʱ���۷�����������2%��
			        �ҷ֣�25%����������5000ʱ���۷�����������4%��
			        �ҷ֣�26%����������4900ʱ���۷�����������8%��
			        �ҷ֣�27%����������4800ʱ��������ȫ���۳���
			        ���³�������С��4800��ú�ʣ�ֹͣ������
		��ˮ,ÿ����1%���۷�����������2%��
		��ˮ, ÿ����0.5%���۷�����������2%��
		ȫ�����1%��ÿ����0.2%���۷�����������5%��
		�ӷ��ֳ�����Χÿ����1%���۷�����������2%��
		
									�۷�
		01	������	5100	5200	1
		01	�ҷ�%		23		24	
		02	������	5000	5100	2
		02	�ҷ�%		24		25	
		03	������	4900	5000	4
		03	�ҷ�%		25		26	
		04	������	4800	4900	8
		04	�ҷ�%		26		27	
		05	������	0		4800	100
		05	�ҷ�%	27	
	 */
	public UFDouble getTz_quaindex_kd(BdInfoVOForSettle infor,String pk_send_h,String settlezt,String gbdate,
			String zbqztype,String pk_transporttype,Map<String,UFDouble> map_dates) throws UifException{
		UFDouble fdzvalue=UFDouble.ZERO_DBL;

		List<QualityreportBVO> list=this.quality(infor);
		if(null==list || list.size()==0){
			return UFDouble.ZERO_DBL;
		}
		
		SendYzyjBVO[] items=null;
		Map<String,UFDouble> map=new HashMap<String,UFDouble>();
		int month=Integer.parseInt(gbdate.substring(5, 7));
		for(int j=0;j<list.size();j++){		
			
			QualityreportBVO reportBVO=list.get(j);
		
			String pk_prj=HgtsPubTool.getStringNullAsTrim(reportBVO.getAttributeValue("prjcode")); // �ʼ���Ŀpk
			
			items=(SendYzyjBVO[]) HYPubBO_Client.queryByCondition(SendYzyjBVO.class, 
					" nvl(dr,0)=0 and pk_sendnoticebill='"+pk_send_h+"' "
					+ "and pk_project='"+pk_prj+"'"
					+ " and bkdrule='2' "); // Ĭ�ϲ�ѯΪ�۶ֵ�
			
			if(null !=items && items.length>0){	

				UFDouble zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("zjrst")); // �����ʼ���
				if(null !=settlezt && !"".equals(settlezt)){
					if("1".equals(settlezt)){ 			// ��			
						zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1", map_dates);
							zjrst=avgValue;
						}
					}else if("2".equals(settlezt)){ //2�� ����	
						if(zbqztype.equals("2")){ // ��Ȩƽ��
							UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
							zjrst=avgValue;
						}
					}else if("3".equals(settlezt)){ //3��������˫����
						UFBoolean isD=this.isD(month);
						if(isD.booleanValue()){ // ����
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}else { //4:������˫����
						UFBoolean isD=this.isD(month);
						if(!isD.booleanValue()){
							zjrst=HgtsPubTool.getUFDoubleNullAsZero(reportBVO.getAttributeValue("custzjrst")); // ���ʼ���
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"1",map_dates);
								zjrst=avgValue;
							}
						}else{
							if(zbqztype.equals("2")){ // ��Ȩƽ��
								UFDouble avgValue=this.getAvgValue(infor, pk_transporttype, pk_prj,"2",map_dates);
								zjrst=avgValue;
							}
						}
					}
				}
				
				UFDouble min = null;
				UFDouble max = null;
				String jgfd=""; // �۸���㹫ʽ

				VOSortUtils.ascSort(items,new String[]{"batchcode"});

				for(int i=0;i<items.length;i++){
					String batchcode=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("batchcode"));

					min=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("minv"));
					max=HgtsPubTool.getUFDoubleNullAsZero(items[i].getAttributeValue("maxv"));
					if(max.doubleValue()==0){
						// ������Ĭ��Ϊ���ֵ
						if(zjrst.compareTo(min)>=0){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						}
					}else{
						if(zjrst.doubleValue()>=min.doubleValue() && zjrst.doubleValue()<max.doubleValue()){
							jgfd=HgtsPubTool.getStringNullAsTrim(items[i].getAttributeValue("pricechange"));
						}
					}

					if(null !=jgfd && !"".equals(jgfd)){
						if(jgfd.indexOf("����ֵ")>=0){						
							jgfd=jgfd.replace("����ֵ", "hyz").replaceAll("��", "(").replaceAll("��", ")");

							//TODO �ҷ� Ҥ����Ŀ�Ƿ��ǰ������㷨����
							//��������ֵ-9.52��/0.5��*��-8�� �����ڼ���ȡ��������������һ
							// �磺������ֵ-9.52��/0.5 = 1.1����ȡֵΪ2����=1.0����ȡֵΪ1
							FormulaParse fromula = new FormulaParse();		
							/*if(HgtsPubConst.HF.equals(pk_prj)){					
								String[] str=jgfd.split("[*]");
								fromula.addVariable("hyz", zjrst);
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
									if(!map.containsKey(batchcode)){										
										map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(jgfd));
										jgfd="";
									}
								}
							}else{*/
								fromula.addVariable("hyz", zjrst);
								fromula.setExpress(jgfd);
								String value=fromula.getValue();
								if(!map.containsKey(batchcode)){									
									map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(value));
								}
							//}						
						}else{
							if(!map.containsKey(batchcode)){								
								map.put(batchcode, HgtsPubTool.getUFDoubleNullAsZero(jgfd).abs());
								jgfd="";
							}
						}
					}	
				}
			}
		}
		
		if(null !=map && map.size()>0){
			for(String key:map.keySet()){
				fdzvalue=fdzvalue.add(map.get(key));
			}
		}
		return fdzvalue;
	}
	
	/**
	 * ����ָ�������õ���Ŀ
	 * 1����ɽ��2��Ҥ��
	 * @return
	 */
	public String isCurPrj(){
		ISysInitQry init=(ISysInitQry) NCLocator.getInstance().lookup(ISysInitQry.class.getName());
		String value="";
		try {
			SysInitVO sysInitVO = init.queryByParaCode(AppContext.getInstance().getPkGroup(),"FF18");
			value=sysInitVO.getValue();
			if(null ==value || "".equals(value)){
				return "1";
			}
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		return value;
	}
	
	public UFBoolean isD(int month){
		
		if(month==1|| month==3 || month==5|| month==7 || month==9 || month==11){
			return UFBoolean.TRUE;
		}else{
			return UFBoolean.FALSE;
		}
	}
	
	/**
	 * ��Ȩƽ��Ȩ��
	 * @param infor
	 * @param pk_transporttype
	 * @param pk_prj
	 * @param mfOrMf : 1����  2������
	 * @return
	 */
	public UFDouble getAvgValue(BdInfoVOForSettle infor,String pk_transporttype,String pk_prj,String mfOrMf,Map<String,UFDouble> map_dates){
		QuqAvg avg=new QuqAvg();
		//UFDouble sum_mthton=avg.getCurMthNum(infor,pk_transporttype);
		//List list_sumdayton=avg.getCurDayNum(infor,pk_transporttype);
	
		UFDouble avgValue=UFDouble.ZERO_DBL;

		String strDates="";
		UFDouble sum_ton=UFDouble.ZERO_DBL;
		
		for(String key:map_dates.keySet()){
			if(!"".equals(strDates)){
				strDates=strDates+",'"+key+"'";
			}else{
				strDates="'"+key+"'";
			}
			sum_ton=sum_ton.add(HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)));	
			
		}
		
		for(String key:map_dates.keySet()){	
			List list_val=avg.getCurDayValue(infor,strDates);
			for(int i=0;i<list_val.size();i++){
				Object[] o_i =  (Object[]) list_val.get(i);										
				// ����һ�����ʼ���Ŀһ��
				if(key.equals(o_i[0]) && pk_prj.equals(o_i[1])){	
					if("1".equals(mfOrMf)){ // ��
						avgValue=avgValue.add(HgtsPubTool.getAvgRz(sum_ton, HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)), HgtsPubTool.getUFDoubleNullAsZero(o_i[3])));
					}else{
						// ����
						avgValue=avgValue.add(HgtsPubTool.getAvgRz(sum_ton, HgtsPubTool.getUFDoubleNullAsZero(map_dates.get(key)), HgtsPubTool.getUFDoubleNullAsZero(o_i[2])));
					}
				}
			}									
		}
		
		return avgValue;
	}
}
