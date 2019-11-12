package nc.vo.hgts.sendnoticebill;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class SendRefPactVO extends SuperVO {

	/**
	 * 发运通知单参照合同使用
	 */
	private static final long serialVersionUID = 3825632038904755229L;
	
	private String pk_org;
	private String contcode;
	private String vbillno;
	private String pk_cust;
	private String pk_kb;
	private String pk_min; // 物料
	private UFDate dbilldate;
	private UFDouble ches;
	private UFDouble shul;
	private UFDouble syches;
	private UFDouble syshul;
	private String fz;
	private String dz;
	private UFDate sdate;
	private UFDate edate;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	private UFDouble def6;
	private UFDouble def7;
	private UFDouble def8;
	private UFDouble def9;
	private UFDouble def10;
	private UFDouble def11;
	private UFDouble def12;
	private UFDouble def13;
	private UFDouble def14;
	private UFDouble def15;
	
	private String def16;
	private String def17;
	private String def18;
	private String def19;
	private String def20;
	
	private String pk_busitype;
	private String pk_balatype;
	private UFDouble price;
	private UFDouble mny;
	private String pk_pact;
	private String transport;
	private String settlezt;
	private UFDouble yhprice; // 优惠价格
	private UFDouble qyyh;
	private UFDouble ysfsyh;
	private UFDouble gpprice;
	private UFBoolean isks;
	private String pk_pact_b;
	private UFBoolean iskztzd; // 是否控制通知单价格
	
	private UFBoolean isbidding; // 是否竞价合同
	private String pk_dept; // 销售部门
	
	
	
	public UFBoolean getIskztzd() {
		return iskztzd;
	}
	public void setIskztzd(UFBoolean iskztzd) {
		this.iskztzd = iskztzd;
	}
	public UFDouble getQyyh() {
		return qyyh;
	}
	public void setQyyh(UFDouble qyyh) {
		this.qyyh = qyyh;
	}
	public UFDouble getYsfsyh() {
		return ysfsyh;
	}
	public void setYsfsyh(UFDouble ysfsyh) {
		this.ysfsyh = ysfsyh;
	}
	public UFDouble getGpprice() {
		return gpprice;
	}
	public void setGpprice(UFDouble gpprice) {
		this.gpprice = gpprice;
	}
	public String getPk_pact_b() {
		return pk_pact_b;
	}
	public void setPk_pact_b(String pk_pact_b) {
		this.pk_pact_b = pk_pact_b;
	}
	public UFDouble getDef11() {
		return def11;
	}
	public void setDef11(UFDouble def11) {
		this.def11 = def11;
	}
	public UFDouble getDef12() {
		return def12;
	}
	public void setDef12(UFDouble def12) {
		this.def12 = def12;
	}
	public UFDouble getDef13() {
		return def13;
	}
	public void setDef13(UFDouble def13) {
		this.def13 = def13;
	}
	public UFDouble getDef14() {
		return def14;
	}
	public void setDef14(UFDouble def14) {
		this.def14 = def14;
	}
	public UFDouble getDef15() {
		return def15;
	}
	public void setDef15(UFDouble def15) {
		this.def15 = def15;
	}
	public String getDef16() {
		return def16;
	}
	public void setDef16(String def16) {
		this.def16 = def16;
	}
	public String getDef17() {
		return def17;
	}
	public void setDef17(String def17) {
		this.def17 = def17;
	}
	public String getDef18() {
		return def18;
	}
	public void setDef18(String def18) {
		this.def18 = def18;
	}
	public String getDef19() {
		return def19;
	}
	public void setDef19(String def19) {
		this.def19 = def19;
	}
	public String getDef20() {
		return def20;
	}
	public void setDef20(String def20) {
		this.def20 = def20;
	}
	public UFBoolean getIsks() {
		return isks;
	}
	public void setIsks(UFBoolean isks) {
		this.isks = isks;
	}
	public UFDouble getYhprice() {
		return yhprice;
	}
	public void setYhprice(UFDouble yhprice) {
		this.yhprice = yhprice;
	}
	public String getSettlezt() {
		return settlezt;
	}
	public void setSettlezt(String settlezt) {
		this.settlezt = settlezt;
	}
	public String getTransport() {
		return transport;
	}
	public void setTransport(String transport) {
		this.transport = transport;
	}
	public String getPk_pact() {
		return pk_pact;
	}
	public void setPk_pact(String pk_pact) {
		this.pk_pact = pk_pact;
	}
	public String getContcode() {
		return contcode;
	}
	public void setContcode(String contcode) {
		this.contcode = contcode;
	}
	public String getPk_busitype() {
		return pk_busitype;
	}
	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
	}
	public String getPk_balatype() {
		return pk_balatype;
	}
	public void setPk_balatype(String pk_balatype) {
		this.pk_balatype = pk_balatype;
	}
	public UFDouble getPrice() {
		return price;
	}
	public void setPrice(UFDouble price) {
		this.price = price;
	}
	public UFDouble getMny() {
		return mny;
	}
	public void setMny(UFDouble mny) {
		this.mny = mny;
	}
	public String getPk_min() {
		return pk_min;
	}
	public void setPk_min(String pk_min) {
		this.pk_min = pk_min;
	}
	public UFDouble getChes() {
		return ches;
	}
	public void setChes(UFDouble ches) {
		this.ches = ches;
	}
	public UFDouble getShul() {
		return shul;
	}
	public void setShul(UFDouble shul) {
		this.shul = shul;
	}
	public UFDouble getSyches() {
		return syches;
	}
	public void setSyches(UFDouble syches) {
		this.syches = syches;
	}
	public UFDouble getSyshul() {
		return syshul;
	}
	public void setSyshul(UFDouble syshul) {
		this.syshul = syshul;
	}
	public String getFz() {
		return fz;
	}
	public void setFz(String fz) {
		this.fz = fz;
	}
	public String getDz() {
		return dz;
	}
	public void setDz(String dz) {
		this.dz = dz;
	}
	public UFDate getSdate() {
		return sdate;
	}
	public void setSdate(UFDate sdate) {
		this.sdate = sdate;
	}
	public UFDate getEdate() {
		return edate;
	}
	public void setEdate(UFDate edate) {
		this.edate = edate;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getVbillno() {
		return vbillno;
	}
	public void setVbillno(String vbillno) {
		this.vbillno = vbillno;
	}
	public String getPk_cust() {
		return pk_cust;
	}
	public void setPk_cust(String pk_cust) {
		this.pk_cust = pk_cust;
	}
	public String getPk_kb() {
		return pk_kb;
	}
	public void setPk_kb(String pk_kb) {
		this.pk_kb = pk_kb;
	}
	public UFDate getDbilldate() {
		return dbilldate;
	}
	public void setDbilldate(UFDate dbilldate) {
		this.dbilldate = dbilldate;
	}
	public String getDef1() {
		return def1;
	}
	public void setDef1(String def1) {
		this.def1 = def1;
	}
	public String getDef2() {
		return def2;
	}
	public void setDef2(String def2) {
		this.def2 = def2;
	}
	public String getDef3() {
		return def3;
	}
	public void setDef3(String def3) {
		this.def3 = def3;
	}
	public String getDef4() {
		return def4;
	}
	public void setDef4(String def4) {
		this.def4 = def4;
	}
	public String getDef5() {
		return def5;
	}
	public void setDef5(String def5) {
		this.def5 = def5;
	}
	public UFDouble getDef6() {
		return def6;
	}
	public void setDef6(UFDouble def6) {
		this.def6 = def6;
	}
	public UFDouble getDef7() {
		return def7;
	}
	public void setDef7(UFDouble def7) {
		this.def7 = def7;
	}
	public UFDouble getDef8() {
		return def8;
	}
	public void setDef8(UFDouble def8) {
		this.def8 = def8;
	}
	public UFDouble getDef9() {
		return def9;
	}
	public void setDef9(UFDouble def9) {
		this.def9 = def9;
	}
	public UFDouble getDef10() {
		return def10;
	}
	public void setDef10(UFDouble def10) {
		this.def10 = def10;
	}
	
	/**
	 * @return isbidding
	 */
	public UFBoolean getIsbidding() {
		return isbidding;
	}
	/**
	 * @param isbidding 要设置的 isbidding
	 */
	public void setIsbidding(UFBoolean isbidding) {
		this.isbidding = isbidding;
	}
	/**
	 * @return pk_dept
	 */
	public String getPk_dept() {
		return pk_dept;
	}
	/**
	 * @param pk_dept 要设置的 pk_dept
	 */
	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}
}
