package nc.impl.hgts.ponder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYSuperDMO;
import nc.itf.hgts.ponder.IPonderItf;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.hgts.bd.cal.CalParaVO;
import nc.vo.hgts.invoicesheet.AggInvoicesheetHVO;
import nc.vo.hgts.invoicesheet.CarHistoryVO;
import nc.vo.hgts.invoicesheet.DoListInvoiceAgg;
import nc.vo.hgts.invoicesheet.DoListSendCarAgg;
import nc.vo.hgts.invoicesheet.DoListSendnoticeAgg;
import nc.vo.hgts.invoicesheet.InvoicesheetBVO;
import nc.vo.hgts.invoicesheet.InvoicesheetHVO;
import nc.vo.hgts.pub.FormulaParseTool;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.data.ValueUtils;

public class PonderImpl implements IPonderItf {

	private static final Object[] Object = null;
	BaseDAO dao =new BaseDAO();

	/**
	 * 查询发运通知单
	 * 查询该矿有效日期范围内 对应“数据标识”的数据
	 *  del（数据标识：flag_data：N=1:正常销售；W=2:异常数据（如：超重）；A=3：所有数据（包含正常、异常））
	 */
	@Override
	public  DoListSendnoticeAgg[] SelectSendnoticeVOs(String Sysdate,String ofmine,String flag_data,String pk_material,String pk_cust) throws BusinessException {

		IUAPQueryBS iUAPQueryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		StringBuffer sql= new StringBuffer();
		sql.append("SELECT h.vbillno,  h.pk_busitype, h.pk_cust, h.receiver, h.isneedfb, h.pk_fhkc,h.startdate,h.enddate,h.pk_drkc,h.jytype,h.settlezt,h.isks,h.contcode, b.*");
		sql.append(" FROM hgts_sendnoticebill_b b  left join hgts_sendnoticebill h on h.pk_sendnoticebill = b.pk_sendnoticebill");
		sql.append(" WHERE  h.vbillstatus = 1 and  h.pk_transporttype = '"+HgtsPubConst.TRANSPORT_QY+"' ");
		if(!ofmine.equals("")){
			sql.append(" and (h.pk_fhkc = '"+ofmine+"'");//矿场  2018-4-13 modify 增加调入矿场条件
			sql.append(" or h.pk_drkc = '"+ofmine+"')");
		}
		if(null != pk_cust && !"".equals(pk_cust)){
			sql.append(" and h.pk_cust="+pk_cust);
		}
		
		sql.append(" and('" +Sysdate+"' between substr(startdate, 0, 10) and substr(enddate, 0, 10)) ");
		//福山数据类型校验 
//		if(null !=flag_data && !"".equals(flag_data) && !flag_data.equals("3")){ // 			
//			sql.append(" and h.flag_data='"+flag_data+"'");			
//		}
		if(null != pk_material && !"".equals(pk_material)){
			sql.append(" and b.pz="+pk_material);
		}
		sql.append(" and (nvl(b.dr,0)=0 and nvl(h.dr,0)=0)  " );
		sql.append(" and nvl(b.rowcloseflag, 'N') = 'N'");
		sql.append(" and (");
		sql.append("		 ((h.jytype is null or h.jytype = 1 or h.jytype = 4) and shul > nvl(yzxnum, 0)) ");
		sql.append(" 		or (h.jytype = 3 and shul > nvl(dynum, 0)) ");
		sql.append("		or (h.jytype = 2 and h.pk_fhkc = '"+ofmine+"' and shul > nvl(yzxnum, 0))");
		sql.append("		or (h.jytype = 2 and h.pk_drkc = '"+ofmine+"' and shul > nvl(dynum, 0))");
		sql.append(")");		
		sql.append(" order by b.pz,h.pk_cust ");
		
		DoListSendnoticeAgg[] sendVOs= null;
		try{
			ArrayList<DoListSendnoticeAgg> result = (ArrayList)iUAPQueryBS.executeQuery(sql.toString(), null, new BeanListProcessor(DoListSendnoticeAgg.class));
			sendVOs = new DoListSendnoticeAgg[result.size()];
			for(int i = 0 ; i < result.size() ; i++ ){
				sendVOs[i] = result.get(i);
				sendVOs[i].setSyl(sendVOs[i].getShul().sub(sendVOs[i].getYzxnum()==null?new UFDouble("0"):sendVOs[i].getYzxnum()));
				sendVOs[i].setJytype(sendVOs[i].getJytype()==null?"1":sendVOs[i].getJytype());
			} 
		}catch (BusinessException e){
			throw new BusinessException("查询发运通知单信息出错！");
		}  
		return sendVOs;
	}

	/**
	 *  查询未过毛重的计量数据， 进：中间面板的数据显示； 出：表尾面板的数据显示
	 *  flag_data 0:进 保留原始数据 ; 1:出: 根据当前操作员 设置司磅员及 所属部门
	 */
	public DoListInvoiceAgg[] getDoListInvoiceAgg(String ofmine,String flag_data,String pk_material,String pk_cust) throws BusinessException{

		DoListInvoiceAgg[] agg = null;
		StringBuffer  sql =  new StringBuffer();
		sql.append("select h.vbillno,h.dbilldate,h.pk_cust,h.pk_recive,h.pk_kc, h.sendnoticebillno, b.* " );
		sql.append(" from hgts_invoicesheet_b b");
		sql.append(" left join hgts_invoicesheet h on h.pk_invoice = b.pk_invoice");
		sql.append(" where h.vbillstatus != 1 and   h.def3 is null  ");//  h.def3表示为作废
		sql.append(" and h.pk_transporttype = '"+HgtsPubConst.TRANSPORT_QY+"'  ");
		if(!ofmine.equals("")){
			sql.append(" and h.pk_kc = '"+ofmine+"'");
		}

		if(null !=pk_cust && !"".equals(pk_cust)){
			sql.append(" and h.pk_cust = "+pk_cust);
		}
		if(null !=pk_material && !"".equals(pk_material)){
			sql.append(" and b.pz = "+pk_material);
		}
		sql.append(" and isnull(b.dr,0)=0 and isnull(h.dr,0)=0 ");
		//出库判断商品类型，皮重不为空
		if(null!=flag_data && flag_data.equals("1")){
			sql.append(" and ( (h.jytype = 1 and b.piz > 0) or h.jytype != 1) ");	
		}
		
		sql.append(" order by b.def6,b.pz "); // 当天进矿场序号

		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try{
			ArrayList<DoListInvoiceAgg> list = (ArrayList)iUAPQueryBS.executeQuery(sql.toString(), null, new BeanListProcessor(DoListInvoiceAgg.class));
			agg = new DoListInvoiceAgg[list.size()];
			for( int i = 0 ; i< list.size() ; i++ ) {
				agg[i]=list.get(i);
			}
		}catch (BusinessException e){
			throw new BusinessException("查询已过皮重计量单单信息出错！");
		}

		return agg;
	}

	/**
	 * 计量单-进、出 保存
	 */
	@Override
	public InvoicesheetBVO[] insertInvoicesheetVO(AggInvoicesheetHVO aggPonder,
			int fReceOrSend) throws DAOException {
		// TODO 自动生成的方法存根

		InvoicesheetHVO HeadVO = aggPonder.getParentVO();
		HeadVO.setAttributeValue("dr", 0);
		InvoicesheetBVO[] badyVOs = (InvoicesheetBVO[]) aggPonder.getChildrenVO();

		// 2018-3-21 add 进厂序号 、出厂序号  begin
		String dbilldate=HgtsPubTool.getStringNullAsTrim(HeadVO.getAttributeValue("dbilldate"));
		String pk_kc=HgtsPubTool.getStringNullAsTrim(HeadVO.getAttributeValue("pk_kc"));
		// 2018-3-21 add end

		String ofmine=HgtsPubTool.getStringNullAsTrim(HeadVO.getAttributeValue("pk_kc"));
		int jytype=HeadVO.getAttributeValue("jytype")==null?1:Integer.parseInt(HeadVO.getAttributeValue("jytype").toString());
		try {
			boolean isDIn=this.isDrxIn(ofmine, aggPonder);
			//0-进；1-出 
			if(fReceOrSend == 0){
				HeadVO.setAttributeValue("sby",  AppContext.getInstance().getPkUser());//司磅员-进
				if(null == HeadVO.getPrimaryKey()){
					try {
						//获取单据号
						IBillcodeManage codemanage = (IBillcodeManage) NCLocator.getInstance().lookup(IBillcodeManage.class.getName());
						Object[] a= new Object[1];
						a[0]=aggPonder;
						String[] vbillcodes = codemanage.getBatchBillCodesByVOArray("YX11",  HeadVO.getAttributeValue("pk_group").toString(), HeadVO.getAttributeValue("pk_org").toString(), a);	
						
						HeadVO.setAttributeValue("vbillno", vbillcodes[0]);   
						HeadVO.setAttributeValue("vbillstatus", -1);	
						HeadVO.setAttributeValue("dr", 0);
						HeadVO.setAttributeValue("iszjbgyy","N");
						
						String carid=HgtsPubTool.getStringNullAsTrim(badyVOs[0].getAttributeValue("carno"));
						HeadVO.setAttributeValue("def5", carid); // 记录表体车号 2018-5-9
						
						String pk_id = dao.insertVO(HeadVO);		
						for(InvoicesheetBVO bodyVO:badyVOs){
							bodyVO.setPk_invoice(pk_id);
							bodyVO.setAttributeValue("pk_invoice", pk_id);
							
							if(!isDIn || jytype==1 || jytype==4){								
								bodyVO.setAttributeValue("piztime", new UFDateTime(new Date()).toString());
							}else{
								bodyVO.setAttributeValue("maoztime", new UFDateTime(new Date()).toString());
							}
							
							bodyVO.setAttributeValue("dr", 0);
							
							// 2018-3-21 add 进厂序号  begin
							String code=this.createCode(dbilldate, pk_kc, 0,null);
							bodyVO.setAttributeValue("def6", code);
							// 2018-3-21
						}
						dao.insertVOArray(badyVOs);
						return badyVOs;
					}  catch (BusinessException e) {
						e.printStackTrace();
					} 
				}else{
					HeadVO.setAttributeValue("vbillstatus", -1);	
					HeadVO.setAttributeValue("dr", 0);
					HeadVO.setAttributeValue("iszjbgyy","N");
					dao.updateVO(HeadVO);
					for(InvoicesheetBVO bodyVO:badyVOs){
						bodyVO.setAttributeValue("dr", 0);
						if(!isDIn || jytype==1 || jytype==4){								
							bodyVO.setAttributeValue("piztime", new UFDateTime(new Date()).toString());
						}else{
							bodyVO.setAttributeValue("maoztime", new UFDateTime(new Date()).toString());
						}
						// 2018-3-21 add 进厂序号  begin
						String code=this.createCode(dbilldate, pk_kc, 0,null);
						bodyVO.setAttributeValue("def6", code);
						// 2018-3-21
					}
					dao.updateVOArray(badyVOs);	
					return badyVOs;
				}
			}else if(fReceOrSend == 1){
				
				//单据状态-1自由，3提交，1审批，
				HeadVO.setAttributeValue("mztime", new UFDateTime(new Date()).toString());
				HeadVO.setAttributeValue("vbillstatus", 1);
				
				HeadVO.setAttributeValue("iszjbgyy","N");
				HeadVO.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
				for(InvoicesheetBVO bodyVO:badyVOs){
					bodyVO.setAttributeValue("bsby",AppContext.getInstance().getPkUser());//司磅员-出
					if(!isDIn || jytype==1 || jytype==4){						
						bodyVO.setAttributeValue("maoztime", new UFDateTime(new Date()).toString());
					}else{
						bodyVO.setAttributeValue("piztime", new UFDateTime(new Date()).toString());
					}
					bodyVO.setAttributeValue("dr", 0);
					
					// 2018-3-21 add 出厂序号 begin
					try {
						String code = this.createCode(AppContext.getInstance().getBusiDate().toString()/*dbilldate*/, pk_kc, 1,bodyVO.getPrimaryKey());
						bodyVO.setAttributeValue("def7", code);
					} catch (BusinessException e) {
						e.printStackTrace();
					}
					// 2018-3-21
				}
				dao.updateVO(HeadVO);	
				dao.updateVOArray(badyVOs);	
				//已过磅数量会写
				SendInvoice(dao,aggPonder,badyVOs);
				return badyVOs;
			}else{
				for(InvoicesheetBVO bodyVO:badyVOs){
					HeadVO.setAttributeValue("dr", 0);
					if(HgtsPubTool.getUFDoubleNullAsZero(bodyVO.getAttributeValue("piz")).doubleValue()>0
							&& HgtsPubTool.getUFDoubleNullAsZero(bodyVO.getAttributeValue("maoz")).doubleValue()>0 ){
						// 
						HeadVO.setAttributeValue("mztime", new UFDateTime(new Date()).toString());
						HeadVO.setAttributeValue("vbillstatus", 1);
						HeadVO.setAttributeValue("iszjbgyy","N");
						HeadVO.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
						bodyVO.setAttributeValue("maoztime", new UFDateTime(new Date()).toString());
						try {
							String code = this.createCode(AppContext.getInstance().getBusiDate().toString(), pk_kc, 1,bodyVO.getPrimaryKey());
							bodyVO.setAttributeValue("def7", code);
						} catch (BusinessException e) {
							e.printStackTrace();
						}
					}else{
						if(HgtsPubTool.getUFDoubleNullAsZero(bodyVO.getAttributeValue("piz")).doubleValue()>0){
							bodyVO.setAttributeValue("piztime", new UFDateTime(new Date()).toString());
							try {
								String code = this.createCode(AppContext.getInstance().getBusiDate().toString(), pk_kc, 0,bodyVO.getPrimaryKey());
								bodyVO.setAttributeValue("def6", code);
							} catch (BusinessException e) {
								e.printStackTrace();
							}
						}else if(HgtsPubTool.getUFDoubleNullAsZero(bodyVO.getAttributeValue("maoz")).doubleValue()>0){
							bodyVO.setAttributeValue("maoztime", new UFDateTime(new Date()).toString());
							try {
								String code = this.createCode(AppContext.getInstance().getBusiDate().toString(), pk_kc, 1,bodyVO.getPrimaryKey());
								bodyVO.setAttributeValue("def7", code);
							} catch (BusinessException e) {
								e.printStackTrace();
							}
						}
					}
					bodyVO.setAttributeValue("dr", 0);
				}
				dao.updateVO(HeadVO);	
				dao.updateVOArray(badyVOs);	
				//更新发运通知单的 “已过磅量”
				SendInvoice(dao,aggPonder,badyVOs);
				return badyVOs;
			}
		} catch (BusinessException e1) {
			
			e1.printStackTrace();
		}
		return null;
	} 

	//更新发运通知单的 “已过磅量”
	public void SendInvoice(BaseDAO dao, AggInvoicesheetHVO aggPonder,InvoicesheetBVO[] badyVOs) throws DAOException{

		InvoicesheetHVO HeadVO = aggPonder.getParentVO();
		String ofmine=HgtsPubTool.getStringNullAsTrim(HeadVO.getAttributeValue("pk_kc"));
		int jytype=HeadVO.getAttributeValue("jytype")==null?1:Integer.parseInt(HeadVO.getAttributeValue("jytype").toString());
		try {
			boolean isDIn=this.isDrxIn(ofmine, aggPonder);
			
			for(InvoicesheetBVO badyVO : badyVOs){
				Object jingz =badyVO.getAttributeValue("jingz") ;// 净重量
				String pkSend=((String)badyVO.getAttributeValue("csourcebid"));
				UFDouble fnum = HgtsPubTool.getUFDoubleNullAsZero(badyVO.getAttributeValue("fnum"));
				String bpk=badyVO.getPrimaryKey();
				// 已过磅量= 净重和 +当前净重
				String condition=" select sum(jingz) from hgts_invoicesheet_b where nvl(dr,0)=0 and pk_invoice_b<>'"+bpk+"' and csourcebid='"+pkSend+"'"
						+" and pk_invoice in (select pk_invoice from hgts_invoicesheet where nvl(dr,0)=0 and pk_kc='"+ofmine+"' ) ";
				
				UFDouble sumJz =  HgtsPubTool.getUFDoubleNullAsZero(dao.executeQuery(condition, new ColumnProcessor()));
				UFDouble ygbnum = HgtsPubTool.getUFDoubleNullAsZero(jingz).add(sumJz);
				
				String sql ="";
				if(!isDIn || jytype==1 || jytype==4){			
					sql = "update hgts_sendnoticebill_b set yzxnum ="+ygbnum +" where  pk_sendnoticebill_b = '"+pkSend+"'";
				}else{
					sql = "update hgts_sendnoticebill_b set dynum ="+ygbnum +" where  pk_sendnoticebill_b = '"+pkSend+"'";
				}
				dao.executeUpdate(sql);
				// 计量单中的剩余数与发运通知单保持一致
				fnum = fnum.sub(sumJz).sub( HgtsPubTool.getUFDoubleNullAsZero(jingz));
				String sql2 = "update hgts_invoicesheet_b set syl = "+fnum+" where csourcebid  = '"+pkSend+"'";		
				dao.executeUpdate(sql2);
			}
		} catch (BusinessException e) {			
			e.printStackTrace();
		}

	}

	@Override
	public InvoicesheetBVO[] SelectInvoicesBodyVO(SendnoticebillHVO HeadVO, SendnoticebillBVO bodyVo) throws BusinessException {
		// TODO 自动生成的方法存根
		IUAPQueryBS daoQuery = NCLocator.getInstance().lookup(IUAPQueryBS.class);

		String sql="select * from hgts_invoicesheet_b a  where a.csourceid = '"+HeadVO.getAttributeValue("pk_sendnoticebill")+"' and a.csourcebid = '"+bodyVo.getAttributeValue("pk_sendnoticebill_b")+"' ";

		List<InvoicesheetBVO> result =  new ArrayList<InvoicesheetBVO>();

		result = (List<InvoicesheetBVO>)daoQuery.executeQuery(sql, new BeanListProcessor(InvoicesheetBVO.class));
		InvoicesheetBVO[] HeadVOs= new InvoicesheetBVO[result.size()];

		for(int i = 0 ; i < result.size() ; i++ ){
			HeadVOs[i] = result.get(i);
		} 

		return HeadVOs;
	}

	@Override
	public HashMap<String, String> getSysInits(String[] codes, String ccorpid) throws BusinessException {

		StringBuffer sql = new StringBuffer("select initcode,value from pub_sysinit where initcode in (");
		for (int i = 0; i < codes.length; i++) {
			if (i == codes.length - 1) {
				sql.append("'").append(codes[i]).append("'");
			} else {
				sql.append("'").append(codes[i]).append("',");
			}
		}
		sql.append(",'TS03') and pk_org='").append(ccorpid).append("' and isnull(dr,0)=0 ");

		String[][] str = new String[0][0];

		try {
			ResultSet rs = (ResultSet) new BaseDAO().executeQuery(sql.toString(), new ArrayListProcessor());
			ResultSetMetaData rsmd = rs.getMetaData();
			int nColumnCount = rsmd.getColumnCount();
			while (rs.next()){

				rsmd.getColumnType(0);
				rsmd.getPrecision(0);
				rsmd.getScale(0);
				rs.getObject(0);
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		if ((str != null) && (str.length != 0))
		{
			HashMap<String, String> map = new HashMap();
			for (String[] s : str) {
				map.put(s[0], s[1]);
			}
			return map;
		}
		return null; 
	}

	/**
	 * 查询磅房IP
	 */
	@Override
	public CalParaVO getDateVO(String pond_ip, String pk_corp) throws BusinessException {

		CalParaVO CalParaVO = null;
		String sql = "select * from hgts_calpara where isnull(dr,0)=0 ";//and pk_org='" + pk_corp + "'
		//
		sql+= null==pond_ip ||pond_ip.equals("") ? "" : " and poundip='" + pond_ip + "' "; 
		
		

		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try
		{
			ArrayList list = (ArrayList)iUAPQueryBS.executeQuery(sql, null, new BeanListProcessor(CalParaVO.class));
			if (list.size() == 1) {
				CalParaVO = (CalParaVO)list.get(0);
			}else{
				throw new BusinessException("查询ip：" + pond_ip + "磅秤有"+list.size()+"个信息！");
			}
		}
		catch (BusinessException e)
		{
			throw new BusinessException("查询ip：" + pond_ip + "磅秤信息出错！");
		}
		return CalParaVO;
	}

	@Override
	public AggregatedValueObject getAggInvoiceVO(DoListInvoiceAgg bodyVo) throws BusinessException {
		// TODO 自动生成的方法存根
		AggregatedValueObject aggVO = new AggInvoicesheetHVO();
		String sql = "select * from hgts_invoicesheet where pk_invoice ='"+bodyVo.getPk_invoice()+"'";
		String sql2 = "select * from hgts_invoicesheet_b where pk_invoice ='"+bodyVo.getPk_invoice()+"' and pk_invoice_b ='"+bodyVo.getPk_invoice_b()+"'";

		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try{
			ArrayList<InvoicesheetHVO> HeadVO = (ArrayList)iUAPQueryBS.executeQuery(sql, null, new BeanListProcessor(InvoicesheetHVO.class));
			ArrayList<InvoicesheetBVO> BodyVOs= (ArrayList)iUAPQueryBS.executeQuery(sql2, null, new BeanListProcessor(InvoicesheetBVO.class));
			aggVO.setParentVO(HeadVO.get(0));
			InvoicesheetBVO[] body= new InvoicesheetBVO[BodyVOs.size()];
			for( int i = 0 ; i< BodyVOs.size() ; i++ ) {
				body[i]=BodyVOs.get(i);
			}
			aggVO.setChildrenVO(body);
		}catch (BusinessException e){
			throw new BusinessException("查询已过皮重计量单单信息出错！");
		}

		return aggVO;
	}

	//点击“发运通知单”将信息显示在待过磅计量页面中
	@Override
	public AggregatedValueObject getNewAggInvoiceVO(DoListSendnoticeAgg bodyVo,String ofmine) throws BusinessException {
		// TODO 自动生成的方法存根
		AggInvoicesheetHVO AggInvoiceVO = new AggInvoicesheetHVO();
		String sql = "select * from hgts_sendnoticebill where pk_sendnoticebill ='"+bodyVo.getPk_sendnoticebill()+"'";
		String sql2 = "select * from hgts_sendnoticebill_b where pk_sendnoticebill ='"+bodyVo.getPk_sendnoticebill()+"' and pk_sendnoticebill_b ='"+bodyVo.getPk_sendnoticebill_b()+"'";

		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try{
			ArrayList<SendnoticebillHVO> HeadVO = (ArrayList)iUAPQueryBS.executeQuery(sql, null, new BeanListProcessor(SendnoticebillHVO.class));
			ArrayList<SendnoticebillBVO> BodyVOs = (ArrayList)iUAPQueryBS.executeQuery(sql2, null, new BeanListProcessor(SendnoticebillBVO.class));
			InvoicesheetHVO  SheetHVO = new InvoicesheetHVO();
			InvoicesheetBVO  SheetBVO = new InvoicesheetBVO();
			//主表转换
			SheetHVO.setAttributeValue("pk_group", HeadVO.get(0).getAttributeValue("pk_group"));
			SheetHVO.setAttributeValue("pk_org", HeadVO.get(0).getAttributeValue("pk_org"));
			SheetHVO.setAttributeValue("pk_org_v", HeadVO.get(0).getAttributeValue("pk_org_v"));
			SheetHVO.setAttributeValue("vbillstatus", BillStatusEnum.FREE.value());
			SheetHVO.setAttributeValue("pk_billtypecode", "YX11");
			SheetHVO.setAttributeValue("pk_billtypeid", HgtsPubConst.FHJLD);
			SheetHVO.setAttributeValue("pk_busitype", HeadVO.get(0).getAttributeValue("pk_busitype"));		
			SheetHVO.setAttributeValue("pk_cust", HeadVO.get(0).getAttributeValue("pk_cust"));
			SheetHVO.setAttributeValue("sendnoticebillno", HeadVO.get(0).getAttributeValue("vbillno"));
			SheetHVO.setAttributeValue("pk_recive", HeadVO.get(0).getAttributeValue("receiver"));
			SheetHVO.setAttributeValue("pk_transporttype", HeadVO.get(0).getAttributeValue("pk_transporttype"));
			SheetHVO.setAttributeValue("pk_kc",ofmine /*HeadVO.get(0).getAttributeValue("pk_fhkc")*/);
			SheetHVO.setAttributeValue("pk_balatype", HeadVO.get(0).getAttributeValue("pk_balatype"));
			SheetHVO.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
			SheetHVO.setAttributeValue("jytype", HeadVO.get(0).getAttributeValue("jytype")==null?1:Integer.parseInt(HeadVO.get(0).getAttributeValue("jytype").toString())); // 交易类型
//			SheetHVO.setAttributeValue("flag_data", HeadVO.get(0).getAttributeValue("flag_data")==null?"1":HeadVO.get(0).getAttributeValue("flag_data"));
			SheetHVO.setAttributeValue("contcode", HeadVO.get(0).getAttributeValue("contcode"));
			
			SheetBVO.setAttributeValue("pz", BodyVOs.get(0).getAttributeValue("pz"));
			SheetBVO.setAttributeValue("pk_group", HeadVO.get(0).getAttributeValue("pk_group"));
			SheetBVO.setAttributeValue("pk_org", HeadVO.get(0).getAttributeValue("pk_org"));
			SheetBVO.setAttributeValue("pk_org_v", HeadVO.get(0).getAttributeValue("pk_org_v"));
			SheetBVO.setAttributeValue("csourcetypecode", "YX04");
			SheetBVO.setAttributeValue("vsourcecode", HeadVO.get(0).getAttributeValue("vbillno"));
			SheetBVO.setAttributeValue("csourceid", HeadVO.get(0).getAttributeValue("pk_sendnoticebill"));
			SheetBVO.setAttributeValue("csourcebid", BodyVOs.get(0).getAttributeValue("pk_sendnoticebill_b"));
			SheetBVO.setAttributeValue("vsourcerowno", BodyVOs.get(0).getAttributeValue("rowno"));
			SheetBVO.setAttributeValue("vsourcetrantype", HeadVO.get(0).getAttributeValue("ctrantypeid"));
			SheetBVO.setAttributeValue("fyigb", BodyVOs.get(0).getAttributeValue("yzxnum"));
			SheetBVO.setAttributeValue("fnum", BodyVOs.get(0).getAttributeValue("shul"));
			UFDouble shul = new UFDouble(BodyVOs.get(0).getAttributeValue("shul").toString());
			UFDouble yzxnum = new UFDouble(BodyVOs.get(0).getAttributeValue("yzxnum")==null?"0":BodyVOs.get(0).getAttributeValue("yzxnum").toString());
			SheetBVO.setAttributeValue("syl",shul.sub(yzxnum) );
			AggInvoiceVO.setParentVO(SheetHVO);
			AggInvoiceVO.setChildrenVO(new InvoicesheetBVO[]{SheetBVO});

		}catch (BusinessException e){
			throw new BusinessException("查询发运通知单信息出错！");
		}

		return AggInvoiceVO;
	}

	/**
	 * 把状态改为 审批通过，表头启用一个字段，标记 为 作废状态
	 * 表体： 皮重=毛重，净重为null
	 */
	@Override
	public void DeleteInvoiceVO(AggInvoicesheetHVO billvo) throws BusinessException {
		// TODO 自动生成的方法存根
		try {	
			InvoicesheetHVO hvo = billvo.getParentVO();
			InvoicesheetBVO[] items =(InvoicesheetBVO[]) billvo.getChildrenVO();

			for(int i = 0, len = items.length; i < len; i++ ){
				String pkSend=((String)items[i].getAttributeValue("csourcebid"));
				if(pkSend==null||pkSend.length()==0)
					return;
				String sqlb =  "select nvl(jingz,0) from hgts_invoicesheet_b where pk_invoice_b='"+items[i].getPrimaryKey()+"' and nvl(dr,0)=0 ";
				UFDouble jingz =HgtsPubTool.getUFDoubleNullAsZero(dao.executeQuery(sqlb, new ColumnProcessor()));
				if(null !=jingz && jingz.doubleValue()>0){
					String sql = "update hgts_sendnoticebill_b set yzxnum = nvl(yzxnum,0)-"+jingz
							+" where pk_sendnoticebill_b ='"+pkSend+"'";
					dao.executeUpdate(sql);
				}
				String sql2="update hgts_invoicesheet_b set maoz=piz,jingz=null where pk_invoice_b = '"+items[i].getPrimaryKey()+"' ";
				dao.executeUpdate(sql2);
			}
			String sqlH="update hgts_invoicesheet set vbillstatus=1,def3='作废',"
					+ " approver='"+InvocationInfoProxy.getInstance().getUserId()+"',"
					+ " tapprovetime='"+new UFDateTime(new Date()).toString()+"'"
					+ " where pk_invoice = '"+hvo.getPrimaryKey()+"' ";
			dao.executeUpdate(sqlH);

		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CarHistoryVO[] OnQueryCarHistory(String where,String carid) throws BusinessException {
		try{
			// TODO 自动生成的方法存根
			StringBuffer sql = new StringBuffer();
			sql.append("select b.pk_invoice_b, b.piztime,  b.piz,  h.sby , b.maoztime,  b.maoz, b.jingz, ");
			sql.append(" case ");
			sql.append("   when defmax.maxpiz is not null then ");
			sql.append(" '最大皮重' ");
			sql.append(" when defmin.minpiz is not null then ");
			sql.append(" '最小皮重' ");
			sql.append(" when car.id  is not null then ");
			sql.append(" '车辆标重' ");
			sql.append(" else ");
			sql.append(" '' ");
			sql.append("end as pizhistory ");
			sql.append(" from hgts_invoicesheet_b b ");
			sql.append(" left join hgts_invoicesheet h ");
			sql.append("   on h.pk_invoice = b.pk_invoice ");
			sql.append(" left join (select distinct max(piz) maxpiz from hgts_invoicesheet_b where hgts_invoicesheet_b.carno = '"+carid+"' and isnull(hgts_invoicesheet_b.dr,0)=0 ) defmax ");
			sql.append("   on (defmax.maxpiz = b.piz) ");
			sql.append(" left join (select distinct min(piz) minpiz from hgts_invoicesheet_b where hgts_invoicesheet_b.carno = '"+carid+"' and isnull(hgts_invoicesheet_b.dr,0)=0 ) defmin ");
			sql.append("   on (defmin.minpiz = b.piz) ");
			sql.append(" left join hgts_tool car ");
			sql.append("  on car.id = b.carno ");
			sql.append(" and car.standardweight = b.piz ");
			sql.append("where isnull(b.dr,0)=0  and ");
			sql.append(where);
			sql.append(" order by pizhistory ,b.piztime");

			ArrayList<CarHistoryVO> list = (ArrayList)dao.executeQuery(sql.toString(), null, new BeanListProcessor(CarHistoryVO.class));
			CarHistoryVO[] HeadVOs= new CarHistoryVO[list.size()];

			for(int i = 0 ; i < list.size() ; i++ ){
				HeadVOs[i] = list.get(i);
			} 
			return HeadVOs;
		}catch (BusinessException e){
			throw new BusinessException("错误 ");
		} 
	}

	@Override
	public int OnQueryCarHnum(Object carid, String pk_org)
			throws BusinessException {
		// TODO 自动生成的方法存根
		StringBuffer sql = new StringBuffer();
		sql.append("select * from  hgts_invoicesheet_b b left join hgts_invoicesheet h "
				+ " on b.pk_invoice = h.pk_invoice "
				+ " where b.carno = '"+carid+"' "
				+"  and h.pk_kc='"+pk_org+"'" // 2018-3-25 由之前的组织条件，换成 矿场
					//	+ " and pk_org= '"+pk_org+"'  " 
								+ " and nvl(h.dr,0)=0 and nvl(b.dr,0)=0");

		ArrayList<InvoicesheetBVO> list = (ArrayList)dao.executeQuery(sql.toString(), null, new BeanListProcessor(InvoicesheetBVO.class));

		return null==list ? 0 : list.size();
	}

	@Override
	public void getCarWeight(AggInvoicesheetHVO aggPonder) throws BusinessException {
		// TODO 自动生成的方法存根
		InvoicesheetBVO[] body = (InvoicesheetBVO[]) aggPonder.getChildrenVO();
		String sqlb =  "select standardweight from hgts_tool where id='"+body[0].getAttributeValue("carno").toString()+"'";
		ArrayList rs = (ArrayList)dao.executeQuery(sqlb.toString(), new BeanListProcessor(String.class)); 
		if(null ==rs.get(0) || rs.get(0).equals("")){
			String sql = "update hgts_tool set standardweight ="+body[0].getAttributeValue("piz").toString() +" where id = '"+body[0].getAttributeValue("carno").toString()+"'  ";
			dao.executeUpdate(sql);
		}

	}


	/**
	 * 过磅计量单-进、出  车辆顺序 编号
	 * 系统自动生成，规则：矿场+当天，例如：邯郸洗选厂，2018-3-21：1,2；马洗，2018-3-21：1,2
	 * @throws BusinessException 
	 */
	public String createCode(String dbilldate,String pk_kb,int flag,String pk_invoice_b) throws BusinessException{
		UFDouble yspzcode=new UFDouble(1);
		String sql="";
		if(flag==0){ // 进
			sql=" select max(b.def6) code ";
		}else{ // 出
			//  避免  过重车后,取消审批,重新过重车,出场序号被替换
			String v_sql="select def7 from hgts_invoicesheet_b "
					+ " where nvl(dr, 0)=0 and pk_invoice_b='"+pk_invoice_b+"' ";
			UFDouble outno=HgtsPubTool.getUFDoubleNullAsZero( dao.executeQuery(v_sql, new ColumnProcessor()));
			if(null !=outno && outno.doubleValue()>0){
				yspzcode=outno;
				
				return yspzcode+"";
				
			}else{		
				
				sql=" select max(b.def7) code ";
			}
		}
		sql=sql+ " from hgts_invoicesheet h inner join hgts_invoicesheet_b b "
				+ " on h.pk_invoice = b.pk_invoice "
				+ " where nvl(h.dr, 0) = 0 and  nvl(b.dr, 0) = 0 "
				+ "  and substr(dbilldate, 1, 10) = '"+dbilldate.substring(0, 10)+"' "
				+ "  and substr(creationtime, 1, 10) = '"+dbilldate.substring(0, 10)+"' " // 计量-进、出不在同一天，剔除前一天的进厂序号
				+ "  and h.pk_kc = '"+pk_kb+"' ";
		UFDouble list= HgtsPubTool.getUFDoubleNullAsZero( dao.executeQuery(sql, new ColumnProcessor()));
		if(null !=list && list.doubleValue()>0){
			
			yspzcode =yspzcode.add(list);

		}
		return yspzcode+"";
	}
	
	/**
	 * 称重异常数据处理
	 * 2018-3-24
	 * 取 矿+车号的最近10次称重(净重)最大值；和本次净重比较（最大值-本次值）。
		如果净重最大值大于等于60吨  并且 误差》=5；必须 录入原因
		如果净重最大值小于60吨  并且 误差》=3；必须 录入原因

	 */
	@Override
	public Object[] contErrorData(String pk_kc,String carno,UFDouble jingz,String pk_material){
		Object[] errmsg=null;
		
		try {

			String sql=" select vbillno,sby,dbilldate,nvl(jingz,0) jingz "
					+" from (select h.vbillno,h.sby,substr(h.dbilldate,0,10) dbilldate,nvl(b.jingz,0) jingz "
					+" from hgts_invoicesheet_b b "
					+" left join hgts_invoicesheet h "
					+" on b.pk_invoice = h.pk_invoice "
					+" where nvl(b.dr, 0) = 0 "
					+"   and nvl(h.dr, 0) = 0 "
					+"   and h.pk_kc='"+pk_kc+"' "
					+"   and h.pk_transporttype = '"+HgtsPubConst.TRANSPORT_QY+"'"
					+"   and b.carno = '"+carno+"' "
					+"   and b.pz='"+pk_material+"' "					
					+" order by h.dbilldate desc "
					+ " )"
					+ " where rownum <= 10 ";
			List listmap=(List) dao.executeQuery(sql, new MapListProcessor());
			UFDouble maxjingz=UFDouble.ZERO_DBL;
			String vbillno="";
			String sby="";
			String dbilldate="";
			if(null !=listmap && listmap.size()>0){
				Map smap=(Map) listmap.get(0);
				maxjingz= HgtsPubTool.getUFDoubleNullAsZero(smap.get("jingz"));
				for(int i=0;i<listmap.size();i++){
					Map map=(Map) listmap.get(i);
					UFDouble u_jingz=HgtsPubTool.getUFDoubleNullAsZero(map.get("jingz"));
					if(null !=maxjingz && maxjingz.doubleValue()>=0 
							&& maxjingz.doubleValue()<u_jingz.doubleValue() ){
						maxjingz=u_jingz;
						vbillno=HgtsPubTool.getStringNullAsTrim(map.get("vbillno"));
						sby=HgtsPubTool.getStringNullAsTrim(map.get("sby"));
						dbilldate=HgtsPubTool.getStringNullAsTrim(map.get("dbilldate"));
					}
				}
			}
			
			String sbyname="";
			if(null !=sby && !"".equals(sby)){
				sbyname=HgtsPubTool.getStringNullAsTrim(dao.executeQuery("select user_name from sm_user where nvl(dr,0)=0 and cuserid='"+sby+"'", new ColumnProcessor()));
			}
			if(null !=maxjingz && maxjingz.doubleValue()>0){
				UFDouble wucha=maxjingz.sub(jingz).abs();
				if(maxjingz.doubleValue()>=60){
					if(null !=wucha && wucha.doubleValue()>=5){
						errmsg= new Object[5];
						errmsg[0]=maxjingz;
						errmsg[1]=jingz;
						errmsg[2]=wucha;
						errmsg[3]="前十次称重最大值："+maxjingz.setScale(3,UFDouble.ROUND_HALF_UP )
								+"，单据号"+vbillno+"，单据日期"+dbilldate
								+"，司磅员"+sbyname
								+"；本次净重："+jingz.setScale(3,UFDouble.ROUND_HALF_UP )
								+"；本次差值："+wucha.setScale(3,UFDouble.ROUND_HALF_UP );
					}
				}else{
					if(null !=wucha && wucha.doubleValue()>=3){
						errmsg= new Object[5];
						errmsg[0]=maxjingz;
						errmsg[1]=jingz;
						errmsg[2]=wucha;
						errmsg[3]="前十次称重最大值："+maxjingz.setScale(3,UFDouble.ROUND_HALF_UP )
								+"，单据号"+vbillno
								+"，单据日期"+dbilldate
								+"，司磅员"+sbyname
								+"；本次净重："+jingz.setScale(3,UFDouble.ROUND_HALF_UP )
								+"；本次差值："+wucha.setScale(3,UFDouble.ROUND_HALF_UP );
					}
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}

		return errmsg;
	}
	

	@Override
	public boolean onQueryFreeCarnoNum(String hpk,String pk_kc, String carno)
			throws BusinessException {
		if(null==hpk || "".equals(hpk)){
			hpk=" ";			
		}
		String sql="select count(0) from hgts_invoicesheet h left join hgts_invoicesheet_b b" 
				+" on h.pk_invoice=b.pk_invoice where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 "
				+" and h.vbillstatus=-1 and h.pk_kc='"+pk_kc+"' "
				+ " and b.carno='"+carno+"' and h.pk_invoice <> '"+hpk+"'";
		int rst=ValueUtils.getInt(dao.executeQuery(sql,  new ColumnProcessor()));
		if(rst>=1){
			return false;
		}
		return true;
	}

	@Override
	public boolean isDrxIn(String ofmine, AggInvoicesheetHVO aggPonder) throws BusinessException {

		  boolean isDIn=true;
		 // String ofmine=HgtsPubTool.getStringNullAsTrim(this.measDoc.getAttributeValue("ofmine"));
		  InvoicesheetHVO headervo=aggPonder.getParentVO();
		  // 交易类型
		  String jytype=HgtsPubTool.getStringNullAsTrim(headervo.getAttributeValue("jytype"));
		  if(null !=jytype && !"".equals(jytype)){
			  // 商品煤、计量
			  if(Integer.parseInt(jytype)==1 || Integer.parseInt(jytype)==4){
				  isDIn= false;
			  }else if(Integer.parseInt(jytype)==2){ // 调入洗
				  // 判断是调入洗的 “调入业务”还是“调出业务”
				  String sendno=HgtsPubTool.getStringNullAsTrim(headervo.getAttributeValue("sendnoticebillno"));
				  try {
					  HYSuperDMO hydmo=new HYSuperDMO();
					  SendnoticebillHVO[] bills=(SendnoticebillHVO[])hydmo.queryByWhereClause(SendnoticebillHVO.class, " nvl(dr,0)=0 and vbillno='"+sendno+"'");
					  SendnoticebillHVO hvo=bills[0];
					  String pk_fhkc=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_fhkc"));
					  String pk_drkc=HgtsPubTool.getStringNullAsTrim(hvo.getAttributeValue("pk_drkc"));
					  if(null !=ofmine && !"".equals(ofmine)){
						  if(ofmine.equals(pk_fhkc)){ // 调出业务
							  isDIn= false;
						  }else if(ofmine.equals(pk_drkc)){ // 调入业务
							  isDIn= true;
						  }
					  }
				  } catch (DAOException e) {
					  e.printStackTrace();
				  }
			  }else if(Integer.parseInt(jytype)==3){
				  isDIn= true;
			  }
		  }else{
			  isDIn= false;
		  }
		  return isDIn;
	}

	@Override
	public AggregatedValueObject getNewAggInvoiceVO(String driveridcard,
			String ofmine, String pcdate) throws BusinessException {
		AggInvoicesheetHVO aggInvoiceVO = new AggInvoicesheetHVO();
		String sql="select pk_group,pk_org,pk_org_v,sendbillno,"
				+" pk_cust,pk_mine,pk_inv,shul,yzxnum,syl,b.carno,"
				+"b.drivername,b.drivertel,b.driveridcord"
				+" from hgts_sendcarlist h inner join hgts_sendcarlist_b b "
				+" on h.pk_sendcarlist = b.pk_sendcarlist "
				+" where nvl(h.dr, 0) = 0  and nvl(b.dr, 0) = 0 "
				+" and b.driveridcord = '"+driveridcard+"' "
				+" and h.pk_mine = '"+ofmine+"' "
				+" and substr(h.pcdate,0,10) = '"+pcdate.substring(0, 10)+"'";
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		ArrayList<DoListSendCarAgg> list = (ArrayList)iUAPQueryBS.executeQuery(sql, new BeanListProcessor(DoListSendCarAgg.class));
		if(null !=list && list.size()>0){
			InvoicesheetHVO  sheetHVO = new InvoicesheetHVO();
			InvoicesheetBVO  sheetBVO = new InvoicesheetBVO();
			
			String pk_sendnoticebill_b=HgtsPubTool.getStringNullAsTrim(list.get(0).getAttributeValue("sendbillno"));
			String sql_b = "select * from hgts_sendnoticebill_b where nvl(dr,0)=0 and pk_sendnoticebill_b ='"+pk_sendnoticebill_b+"'";
			ArrayList<SendnoticebillBVO> bodyVOs = (ArrayList)iUAPQueryBS.executeQuery(sql_b, new BeanListProcessor(SendnoticebillBVO.class));
			String pk_sendnoticebill=HgtsPubTool.getStringNullAsTrim(bodyVOs.get(0).getPk_sendnoticebill());
			String sql_h = "select * from hgts_sendnoticebill where pk_sendnoticebill ='"+pk_sendnoticebill+"'";
			ArrayList<SendnoticebillHVO> headVO = (ArrayList)iUAPQueryBS.executeQuery(sql_h,new BeanListProcessor(SendnoticebillHVO.class));
			
			//主表转换
			sheetHVO.setAttributeValue("pk_group", list.get(0).getAttributeValue("pk_group"));
			sheetHVO.setAttributeValue("pk_org", list.get(0).getAttributeValue("pk_org"));			
			sheetHVO.setAttributeValue("pk_org_v", list.get(0).getAttributeValue("pk_org_v"));
			sheetHVO.setAttributeValue("vbillstatus", BillStatusEnum.FREE.value());
			sheetHVO.setAttributeValue("pk_billtypecode", "YX11");
			sheetHVO.setAttributeValue("pk_billtypeid", HgtsPubConst.FHJLD);
			sheetHVO.setAttributeValue("pk_cust", list.get(0).getAttributeValue("pk_cust"));
			sheetHVO.setAttributeValue("pk_supplier", list.get(0).getAttributeValue("pk_supplier"));
			sheetHVO.setAttributeValue("pk_transporttype", HgtsPubConst.TRANSPORT_QY);
			sheetHVO.setAttributeValue("pk_kc",ofmine);
			sheetHVO.setAttributeValue("dbilldate", AppContext.getInstance().getBusiDate());
			
			sheetHVO.setAttributeValue("pk_busitype", headVO.get(0).getAttributeValue("pk_busitype"));		
			sheetHVO.setAttributeValue("sendnoticebillno", headVO.get(0).getAttributeValue("vbillno"));
			sheetHVO.setAttributeValue("pk_balatype", headVO.get(0).getAttributeValue("pk_balatype"));
			sheetHVO.setAttributeValue("jytype", headVO.get(0).getAttributeValue("jytype")==null?1:Integer.parseInt(headVO.get(0).getAttributeValue("jytype").toString())); // 交易类型
//			sheetHVO.setAttributeValue("flag_data", headVO.get(0).getAttributeValue("flag_data"));
			//司磅员及所属部门
			sheetHVO.setAttributeValue("sby", AppContext.getInstance().getPkUser());
			String pk_dept = new FormulaParseTool().getBsNameByID("bd_psnjob", "pk_dept", "pk_psndoc",  
								new FormulaParseTool().getBsNameByID("sm_user", "pk_psndoc", "cuserid",AppContext.getInstance().getPkUser()));
			sheetHVO.setAttributeValue("pk_dept",pk_dept);
            //创建人及时间
			sheetHVO.setAttributeValue("creator", AppContext.getInstance().getPkUser());
			sheetHVO.setAttributeValue("creationtime", AppContext.getInstance().getServerTime());
			sheetHVO.setAttributeValue("def4",list.get(0).getAttributeValue("pk_inv")); // 品种 
			sheetHVO.setAttributeValue("settlezt", headVO.get(0).getAttributeValue("settlezt"));
			sheetHVO.setAttributeValue("isks",  headVO.get(0).getAttributeValue("isks"));
			
			// 子表转换
			sheetBVO.setAttributeValue("pz", list.get(0).getAttributeValue("pk_inv"));
			sheetBVO.setAttributeValue("pk_group", list.get(0).getAttributeValue("pk_group"));
			sheetBVO.setAttributeValue("pk_org", list.get(0).getAttributeValue("pk_org"));
			sheetBVO.setAttributeValue("pk_org_v", list.get(0).getAttributeValue("pk_org_v"));
			sheetBVO.setAttributeValue("csourcetypecode", "YX04");
			sheetBVO.setAttributeValue("vsourcecode", headVO.get(0).getAttributeValue("vbillno"));
			sheetBVO.setAttributeValue("csourceid", pk_sendnoticebill);
			sheetBVO.setAttributeValue("csourcebid", pk_sendnoticebill_b);
			sheetBVO.setAttributeValue("vsourcerowno", bodyVOs.get(0).getAttributeValue("rowno"));
			
			sheetBVO.setAttributeValue("fyigb", bodyVOs.get(0).getAttributeValue("yzxnum"));
			sheetBVO.setAttributeValue("fnum", bodyVOs.get(0).getAttributeValue("shul"));
			UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(bodyVOs.get(0).getAttributeValue("shul"));
			UFDouble yzxnum = HgtsPubTool.getUFDoubleNullAsZero(bodyVOs.get(0).getAttributeValue("yzxnum"));
			sheetBVO.setAttributeValue("syl",shul.sub(yzxnum));
			aggInvoiceVO.setParentVO(sheetHVO);
			aggInvoiceVO.setChildrenVO(new InvoicesheetBVO[]{sheetBVO});
		}
		return aggInvoiceVO;
	}
}
