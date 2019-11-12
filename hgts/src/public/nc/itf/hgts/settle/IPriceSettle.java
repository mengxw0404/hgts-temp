package nc.itf.hgts.settle;

import java.sql.SQLException;

import nc.bs.dao.DAOException;
import nc.vo.hgts.hjsettle.AggHjsettleHVO;
import nc.vo.hgts.hjsettle.HjsettleBVO;
import nc.vo.hgts.hjsettle.HjsettleHVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.sopact.PactBVO;
import nc.vo.hgts.carloadingplan.AggCarloadingplanHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ����ʱ���ذ�����Ϣ
 * @author zhf
 *
 */
public interface IPriceSettle {

	/**
	 * ��ѯ���������Ĵ��������    �����ʼ�����+��������+�ͻ���������
	 * @param custid �ͻ�id
	 * @param invid ú��id
	 * @param oUserObject ����������Ϣ
	 * @throws BusinessException
	 */
	public IBdInforVOForSettle[] loadBDForSettle(String pk_org,String custid,String kb,String invid,Object oUserObject) throws BusinessException;
	
	/**
	 * ��������    ѡ�о����� ���м���
	 * @param infor �������� �ؼ���Ϣ
	 * @param dbusidate ����ҵ������   ���� ������ �����ۼƽ����� �ݲ�֧�� ����
	 * @param isks �Ƿ��ˮ
	 * @param settlezt ��������
	 * @return
	 * @throws BusinessException
	 */
	public INumColResult numCol(IBdInforVOForSettle infor,UFDate dbusidate,Object isks)throws BusinessException;
	

	/**
	 * 
	 * @param item ���۽�����  һ��
	 * @return ��������᷵�ض���
	 * @throws BusinessException
	 * @throws SQLException 
	 */
	public HjsettleBVO[] priceCol(HjsettleHVO hvo,HjsettleBVO item,boolean ishaveflag) throws BusinessException, SQLException;
	
	/**
	 * 
	 * @param str
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public AggInvoicesheetHVO[] getAggvos(String[] str) throws DAOException, ClassNotFoundException;
	
	/**
	 * 2019��3��11��
	 * �˷ѽ��㣺��ȡ��������
	 * @param Object[]:���ȹ̶�Ϊ2��[0]:Э���˷ѵ��ۣ�[1]:����vo����
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public Object[] getAggvos_yfee(String[] str) throws DAOException, ClassNotFoundException;
	
	
	/**
	 * ���۽��㵥����������ʱ����д���������ñ�ʶ
	 * @param hpk
	 * @param bpk
	 * @param jzno : �����ʼ쵥��
	 * @throws Exception
	 */
	public void wrtBackBd(String hpks,String bpk,String[] zjno,String actiontype) throws Exception;
	
	/**
	 * ��ѯװ���ƻ�
	 * @param str
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public AggCarloadingplanHVO[] getAggCarloadingplanVOs(String[] str) throws DAOException, ClassNotFoundException;
	
	/**
	 * ���۽���·��ʹ��
	 * @param infor
	 * @param dbusidate:��ǰ��������
	 * @return
	 * @throws BusinessException
	 */
	public INumColResult numColByLy(IBdInforVOForSettle infor,UFDate dbusidate,Object isks)throws BusinessException;
	
	/**
	 * �˷ѷ�Ʊ���ս��㵥
	 * ��ȡ�˷ѽ��㵥vo
	 * @param str
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public AggHjsettleHVO[] getYfSettAggvos(String[] str) throws DAOException, ClassNotFoundException;
	
	/**
	 * �ڲ������嵥 ���� ������㵥
	 * ��ȡ ������㵥vo
	 * @param str
	 * @return
	 * @throws BusinessException
	 */
	public AggHjsettleHVO[] getSettAggvos(String[] str) throws BusinessException;
	
	
	/**
	 * ��ȡ װж����Ϣvo
	 * @param pk_org
	 * @param kuangc
	 * @param transport
	 * @param pk_zxxy
	 * @return
	 * @throws BusinessException
	 */
	public PactBVO getZXPactB(Object pk_org,Object kuangc, Object transport,String pk_zxxy) throws BusinessException;
	
	/**
	 * ��ȡ�������Ϣvo
	 * @param pk_org
	 * @param kuangc
	 * @param transport
	 * @param pk_ysxy
	 * @return 
	 * @throws BusinessException
	 */
	public PactBVO getYSPactB(Object pk_org,Object kuangc, Object transport,String pk_ysxy) throws BusinessException;
}
