package nc.impl.hgts.qry.cust.mny;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.itf.hgts.qry.cust.mny.ICustBalanceInfo;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * ����֪ͨ������У��ʹ��
 * @author Administrator
 *
 */

public class CustBalanceInfoImpl implements ICustBalanceInfo {

	private BaseDAO dao =  null;

	private BaseDAO getQuery(){
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	private ColumnProcessor cp = new ColumnProcessor();

	/**
	 * 2019��4��19�� ȥ����������
	 */
	@Override
	public UFDouble[] getBalanceInfo(String hpk, String pk_billtype,
			String pk_org, String pk_cust, String pk_balatype,
			String pk_deptdoc, String obj) throws BusinessException {

		UFDouble[] info=null;

		// �տ��� -- �����㷽ʽ
		UFDouble skmny=getSKYE(pk_org, pk_cust, pk_balatype, pk_deptdoc,obj);
		// ��Ʊ��� -- �����㷽ʽ
		UFDouble fpmny=getFPYE(pk_org, pk_cust, pk_balatype, pk_deptdoc,obj);

		// �տ��� -- �������㷽ʽ
		UFDouble skmny_all=getSKYE(pk_org, pk_cust, null,pk_deptdoc,obj);
		// ��Ʊ��� -- �������㷽ʽ
		UFDouble fpmny_all=getFPYE(pk_org, pk_cust, null,pk_deptdoc,obj);

		// 2018��12��19�� ���Ӱ����㷽ʽ
		// ��ռ�ý�� - δ�ر�
		UFDouble sendmny=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"N",HgtsPubConst.TRANSPORT_QY);	
		UFDouble sendmny_ly=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"N",HgtsPubConst.TRANSPORT_LY);	
		// ��ռ�ý�� - �ѹر�
		UFDouble sendmny_close=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype,pk_deptdoc,obj,"Y",HgtsPubConst.TRANSPORT_QY);
		UFDouble sendmny_close_ly=getAllSendMny(hpk, pk_billtype,pk_org, pk_cust,pk_balatype, pk_deptdoc,obj,"Y",HgtsPubConst.TRANSPORT_LY);

		//���ö��
		UFDouble credit=this.getCredit(pk_cust,pk_deptdoc,pk_balatype);

		//���̵�����  Ӧ�����(�ֻ����ж����)�Ը�������ʽ��ʾ��  ���ö��=�����ö��-Ӧ�����(�ֻ����ж����)-ҵ��ռ�ã�
		// skmny����ֱ�Ӳ�ѯ���տ���ʴ�������ʱ����add
		//UFDouble balance=credit.add(skmny).sub(sendmny);

		// 2018.8.31 modify 
		// �ͻ����=�ۼ��տ�ۼƷ�Ʊ
		// �������=���ö�� +�ͻ���� ��ҵ��ռ��
		UFDouble balance=credit.add(/*skmny_all.sub(fpmny_all)*/skmny.sub(fpmny)).sub(sendmny.add(sendmny_ly).add(sendmny_close).add(sendmny_close_ly));

		info=new UFDouble[5];
		info[0]=credit;
		info[1]=skmny.sub(fpmny);
		info[2]=sendmny.add(sendmny_ly).add(sendmny_close).add(sendmny_close_ly);
		info[3]=balance;
		info[4]=skmny_all.sub(fpmny_all); // �����

		return info;
	}

	/**
	 * ��Ʊ���
	 * @param pk_org����֯
	 * @param pk_cust:�ͻ�
	 * @param pk_balatype�����㷽ʽ���ֻ㡢�ж�
	 * @param pk_deptdoc������
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getFPYE(String pk_org,String pk_cust,String pk_balatype,
			String pk_deptdoc,String dbilldate) throws BusinessException{		
		String sql ="select sum(round(nvl(norigtaxmny,0),2)) from hgts_saleinvoice_b where nvl(dr,0) = 0 "
				+ (null==pk_balatype||"".equals(pk_balatype)?"":" and pk_balatype = '"+pk_balatype+"' ")
				+ " and csaleinvoiceid in ("
				+ " select csaleinvoiceid from hgts_saleinvoice where nvl(dr,0) = 0 "
				+ " and pk_org = '"+pk_org+"' "
				+ " and cinvoicecustid = '"+pk_cust+"' "
				+ " and pk_dept='"+pk_deptdoc+"'"
				+ " and substr(dbilldate,0,10) >='"+dbilldate+"'"
				+ ")";

		UFDouble fp = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return fp;
	}

	/**
	 * �տ���
	 * 2019��4��19�� ȥ����������
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_balatype
	 * @param pk_deptdoc
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSKYE(String pk_org,String pk_cust,String pk_balatype,
			String pk_deptdoc,String dbilldate) throws BusinessException{
		String sql="select sum(skmny) from hgts_sknoticebill where nvl(dr,0) = 0 "
				+ " and vbillstatus=1 "
				+ " and pk_org='"+pk_org+"'"
				+ " and customer = '"+pk_cust+"' "
			//	+ " and checktype = 1 "//���У��
				+( null==pk_balatype || "".equals(pk_balatype)?"": " and pk_balatype = '"+pk_balatype+"' ")
				//+ " and pk_dept ='"+pk_deptdoc+"'"
				+ " and substr(dbilldate,0,10) >='"+dbilldate+"'";

		UFDouble sk = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return sk;
	}

	/**
	 * ���ö��
	 * 2018-9-28  �ͻ�+����
	 * 
	 * 2019��4��19�� ȥ����������
	 * @return
	 */
	public UFDouble getCredit(String pk_cust,String pk_dept,String pk_balatype) throws BusinessException{
		String sql="select credit from hgts_merchant "
				+ " where nvl(dr,0)=0 "
				+ " and merchats='"+pk_cust+"'"
			//	+ (pk_dept==null|| "".equals(pk_dept)?"":" and def20='"+pk_dept+"'")
				+(pk_balatype==null||"".equals(pk_balatype)?"":" and def19='"+pk_balatype+"'")
				;
		UFDouble credit = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		
		return credit;
	}

	/**
	 * ҵ��ռ��
	 * @param pk_org
	 * @param pk_cust
	 * @param hpk
	 * @param pk_billtype
	 * @param pk_balatype
	 * @param pk_deptdoc
	 * @param dbilldate
	 * @param closeflag �ر�/δ�ر�
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getAllSendMny(String hpk,String pk_billtype,String pk_org,
			String pk_cust,String pk_balatype,String pk_deptdoc,
			String dbilldate,String closeflag,String pk_transporttype)
					throws BusinessException {

		if(null==hpk || "".equals(hpk)){
			hpk=" ";
		}
		String sql="";
		String qrysql="";
		if(closeflag.equals("Y")){
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){				
				// �ѹر�: (�ѹ�������-�ѿ�Ʊ����)* ִ�е���
				qrysql =" sum(round((nvl(b.yzxnum, 0) - nvl(b.ykpnum, 0)) * zxprice,2)) mny ";
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				// ����װ����-��Ʊ������* ����*ִ�е���
				qrysql=" sum(round((nvl(b.def6, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0) * zxprice,2)) mny";
			}

		}else if(closeflag.equals("N")){
			// δ�ر�: (����-�ѿ�Ʊ����)* ִ�е���
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){	
				qrysql =" sum(round((nvl(b.shul, 0) - nvl(b.ykpnum, 0)) * zxprice,2)) mny ";	

			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				qrysql=" sum(round((nvl(b.carnum, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0) * zxprice,2)) mny";
			}
		}
		if(HgtsPubConst.FHTZD.equals(pk_billtype)){		
			sql = "select "
					+ qrysql
					+ " from hgts_sendnoticebill_b b "
					+ " inner join hgts_sendnoticebill h "
					+ " on b.pk_sendnoticebill=h.pk_sendnoticebill "
					+ " where nvl(b.dr,0) = 0 and nvl(h.dr,0) = 0 "
					+ " and COALESCE(b.rowcloseflag,'N')='"+closeflag+"' "
					+ " and h.pk_cust='"+pk_cust+"' "
				//	+ " and h.pk_dept='"+pk_deptdoc+"'"
					+ (null==pk_balatype||"".equals(pk_balatype)?"": " and h.pk_balatype='"+pk_balatype+"'")
					+ " and h.pk_transporttype='"+pk_transporttype+"'"
					+ " and substr(h.dbilldate,0,10) >='"+dbilldate+"'"
					+ " and h.pk_sendnoticebill<>'"+hpk+"'"
					;
			return  HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		}

		return UFDouble.ZERO_DBL;
	}

	/**
	 * ����֪ͨ��-����ʱУ������
	 */
	@Override
	public UFDouble[] getResidueInfo(String hpk, String pk_transporttype,String pk_billtype,
			String pk_org, String pk_cust, String pk_fhkc, String pk_deptdoc,
			String pk_pz, String dbilldate) throws BusinessException {
		
		// �տ�-����������
		UFDouble shnum=getSHNum( pk_org,pk_transporttype, pk_cust, pk_fhkc, pk_deptdoc, pk_pz);
		// ����-(δ�رյ�����)
		UFDouble sdnum=getSDNum(hpk, pk_org,pk_transporttype, pk_cust, pk_fhkc, pk_deptdoc, pk_pz,"N");
		// ����-(�Ѿ��رյ�ʣ��)
		UFDouble close_num=getSDNum(hpk, pk_org, pk_transporttype,pk_cust, pk_fhkc, pk_deptdoc, pk_pz,"Y");
		
		//���տ������� = �տ����� - δ�رշ��˷����� + �ѹرշ��˵�ʣ����
		UFDouble toltal_sum = shnum.sub(sdnum).add(close_num);
		
		UFDouble[] info=new UFDouble[5];
		info[0] = toltal_sum;
		return info;
	}
	
	/**
	 * 
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_fhkc
	 * @param pk_deptdoc
	 * @param pk_pz
	 * @return
	 * @throws DAOException 
	 */
	private UFDouble getSDNum(String hpk, String pk_org,String pk_transporttype, String pk_cust, String pk_fhkc,
			String pk_deptdoc, String pk_pz,String closeflag) throws DAOException {

		if(null==hpk || "".equals(hpk)){
			hpk=" ";
		}
		String sql="";
		String qrysql="";
		if(closeflag.equals("Y")){
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){				
				// �ѹر�: (�ѹ�������-�ѿ�Ʊ����)
				qrysql =" sum(round((nvl(b.yzxnum, 0) - nvl(b.ykpnum, 0)) ,2)) mny ";
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				// ����װ����-��Ʊ������* ����
				qrysql=" sum(round((nvl(b.def6, 0) - nvl(b.def31, 0)) * nvl(b.carstrong, 0),2)) mny";
			}

		}else if(closeflag.equals("N")){
			// δ�ر�  ����		
			if(HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)){	
				qrysql =" sum(nvl(b.shul, 0)) mny ";	
			}else if(HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)){
				//  ���� * ����
				qrysql=" sum(round(nvl(b.carnum, 0)  * nvl(b.carstrong, 0),2)) mny";
			}
		}
	
		sql = "select "
					+ qrysql
					+ " from hgts_sendnoticebill_b b "
					+ " inner join hgts_sendnoticebill h "
					+ " on b.pk_sendnoticebill=h.pk_sendnoticebill "
					+ " where nvl(b.dr,0) = 0 and nvl(h.dr,0) = 0 "
				//	+ " and h.checktype = 2 "
					+ " and COALESCE(b.rowcloseflag,'N')='"+closeflag+"' "
					+ " and h.pk_org='"+pk_org+"' "
					+ " and h.pk_cust='"+pk_cust+"' "
					+ " and h.pk_dept='"+pk_deptdoc+"'"
					+ " and h.pk_transporttype='"+pk_transporttype+"'"
					+ " and h.pk_fhkc='"+pk_fhkc+"'"
					+ " and b.pz='"+pk_pz+"'"
					+ " and h.pk_sendnoticebill<>'"+hpk+"'"
					;
			return  HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		
	}

	/**
	 * ��ѯ�տ��� �������� ���ջ�������
	 * @param pk_org
	 * @param pk_cust
	 * @param pk_fhkc ��
	 * @param pk_deptdoc
	 * @param pk_pz ú��
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSHNum(String pk_org,String pk_transporttype,String pk_cust,String pk_fhkc,
			String pk_deptdoc,String pk_pz) throws BusinessException{
		String sql="select sum(hgts_sknoticebill_b.ton) "
				+ " from hgts_sknoticebill "
				+ "  inner join hgts_sknoticebill_b "
				+ "     on hgts_sknoticebill.pk_sknotice = hgts_sknoticebill_b.pk_sknotice "
				+ "   where nvl(hgts_sknoticebill.dr , 0) = 0  "
				+ "         AND nvl(hgts_sknoticebill.dr , 0) = 0  "
				+ "         and  hgts_sknoticebill.vbillstatus=1    "
				//+ "        and  hgts_sknoticebill.checktype = 2  "
				+ "          and hgts_sknoticebill.pk_org= '"+pk_org+ "'"
			    + "          and hgts_sknoticebill.pk_transporttype= '"+pk_transporttype+ "'"
				+ "          and hgts_sknoticebill.customer = '"+pk_cust+ "'"
				+ "          and hgts_sknoticebill.pk_dept = '"+pk_deptdoc+ "'"
				+ "          and hgts_sknoticebill.pk_kc =  '"+pk_fhkc+ "'"
				+ "          and hgts_sknoticebill_b.pk_inv = '"+pk_pz+ "'";
		UFDouble sk = HgtsPubTool.getUFDoubleNullAsZero(getQuery().executeQuery(sql, cp));
		return sk;
	}

}
