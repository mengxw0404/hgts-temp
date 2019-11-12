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
 * 结算时加载磅单信息
 * @author zhf
 *
 */
public interface IPriceSettle {

	/**
	 * 查询符合条件的待结算磅单    按照质检批次+过磅日期+客户升序排列
	 * @param custid 客户id
	 * @param invid 煤种id
	 * @param oUserObject 其他条件信息
	 * @throws BusinessException
	 */
	public IBdInforVOForSettle[] loadBDForSettle(String pk_org,String custid,String kb,String invid,Object oUserObject) throws BusinessException;
	
	/**
	 * 数量计算    选中具体行 进行计算
	 * @param infor 代计算行 关键信息
	 * @param dbusidate 结算业务日期   考虑 所在月 当月累计结算量 暂不支持 跨月
	 * @param isks 是否扣水
	 * @param settlezt 结算主体
	 * @return
	 * @throws BusinessException
	 */
	public INumColResult numCol(IBdInforVOForSettle infor,UFDate dbusidate,Object isks)throws BusinessException;
	

	/**
	 * 
	 * @param item 划价结算体  一行
	 * @return 拆行情况会返回多行
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
	 * 2019年3月11日
	 * 运费结算：获取磅单数据
	 * @param Object[]:长度固定为2，[0]:协议运费单价；[1]:磅单vo数组
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public Object[] getAggvos_yfee(String[] str) throws DAOException, ClassNotFoundException;
	
	
	/**
	 * 划价结算单拉磅单保存时，回写过磅单引用标识
	 * @param hpk
	 * @param bpk
	 * @param jzno : 根据质检单号
	 * @throws Exception
	 */
	public void wrtBackBd(String hpks,String bpk,String[] zjno,String actiontype) throws Exception;
	
	/**
	 * 查询装车计划
	 * @param str
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	public AggCarloadingplanHVO[] getAggCarloadingplanVOs(String[] str) throws DAOException, ClassNotFoundException;
	
	/**
	 * 划价结算路运使用
	 * @param infor
	 * @param dbusidate:当前结算日期
	 * @return
	 * @throws BusinessException
	 */
	public INumColResult numColByLy(IBdInforVOForSettle infor,UFDate dbusidate,Object isks)throws BusinessException;
	
	/**
	 * 运费发票参照结算单
	 * 获取运费结算单vo
	 * @param str
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public AggHjsettleHVO[] getYfSettAggvos(String[] str) throws DAOException, ClassNotFoundException;
	
	/**
	 * 内部结算清单 参照 货物结算单
	 * 获取 货物结算单vo
	 * @param str
	 * @return
	 * @throws BusinessException
	 */
	public AggHjsettleHVO[] getSettAggvos(String[] str) throws BusinessException;
	
	
	/**
	 * 获取 装卸费信息vo
	 * @param pk_org
	 * @param kuangc
	 * @param transport
	 * @param pk_zxxy
	 * @return
	 * @throws BusinessException
	 */
	public PactBVO getZXPactB(Object pk_org,Object kuangc, Object transport,String pk_zxxy) throws BusinessException;
	
	/**
	 * 获取运输费信息vo
	 * @param pk_org
	 * @param kuangc
	 * @param transport
	 * @param pk_ysxy
	 * @return 
	 * @throws BusinessException
	 */
	public PactBVO getYSPactB(Object pk_org,Object kuangc, Object transport,String pk_ysxy) throws BusinessException;
}
