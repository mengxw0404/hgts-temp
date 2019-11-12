package nc.vo.hgts.pub;

public class HgtsPubConst {

	public static final int price_policy_pay = 1;				//价格因素 付款方式
	public static final int price_policy_senddistance = 2;		//价格因素  运输距离
	public static final int price_policy_qulityindex = 3;		//价格因素 质量指标
	public static final int price_policy_numprice = 4;			//价格因素 量价优惠
	public static final int price_policy_transtype = 5;			//价格因素 运输方式

	public static final String biztype_sx = "100101100000000019M1";//业务类型   赊销
	public static final String biztype_ys = "100101100000000019M2";//业务类型   预收

	// 价格政策 值区间
	public static final String pay_xh = "100101100000000019KM";//付款方式 现汇
	public static final String pay_cd = "100101100000000019KN";//付款方式 承兑
	public static final String pay_cd_my = "1001OZ1000000000NLJK";// 2018-8-7 付款方式 承兑民营（民营银行）

	public static final String transtype_qy = "1001OZ1000000000BJ55";//运输方式 汽运
	public static final String transtype_ly = "1001OZ1000000000BJ57";//运输方式 路运

	// 单据类型主键
	public static final String JGZC="0001AX10000000003MW1"; 	// YX01:价格政策
	public static final String FHTZD="0001AX100000000045MP"; 	// YX04:发运通知单
	public static final String ZJBG="0001AX10000000006X4D"; 	// YX09:质检报告
	public static final String FHJLD="0001AX10000000008AD7"; 	// YX11:发货计量单维护
	public static final String ZCJH="0001AX10000000009C53"; 	// YX13:装车计划
	public static final String XSFP="0001OZ1000000000AZ3S"; 	// YX19:销售发票
	public static final String SKTZD="0001OZ1000000000AZWQ"; 	// YX20:收款通知单
	public static final String XSHJD_QY="0001OZ1000000000CMLW"; // YX21:销售划价单(汽运)
	public static final String XSHJD_LY="0001OZ1000000000D20X"; // YX21:销售划价单(路运)
	public static final String TLYSSQJH="0001OA10000000008I3W"; // YX42:铁路运输申请计划
	//TODO 运杂费结算单
	public static final String XSHJD_YZF="0001021000000001EF5C";

	// TODO 运费发票
	public static final String XSFP_YZF="0001021000000001EG8T";
	
	public static final String PCD="0001ZZ1000000001HQVK";

	//TODO 2019年3月4日 合同-货物合同
	public static final String CONTRACT_SALE="0001OA10000000005XJ5";
	// 合同-运费协议
	public static final String CONTRACT_YFXY="0001021000000001DICW";
	// 合同-装卸协议
	public static final String CONTRACT_ZXXY="0001021000000001DJ33";
	
	public static final String JGZC_JJ="0001ZZ1000000001J0I0"; // 竞价价格
	public static final String JGZC_NB="0001021000000001FNCU"; // 内部价格
	public static final String XSHJD_NB="0001021000000001FON6";// 内部结算清单


	public static final String NODECODE_XSHJD_QY="40H10801";	//	销售划价单(汽运)
	public static final String NODECODE_XSHJD_LY="40H10802";	//	销售划价单(路运)

	public static final String auto_id_billtype = "FFYX";//主键自动生成器的  单据类型

	public static final String hjyh_dialog_templet = "10010110000000006396";
	public static final String pricepolicy_ljyh_jgysid = "4";

	public static final String[] ljyh_sort_fields = new String[]{"tsettletime"};//量价优惠对话框 排序字段
	public static final String HF="10010110000000001A9I";//灰分
	public static final String SF="10010110000000001A9G";//全水份
	public static final String FRL="1001061000000000PSFK";		// 发热量
	public static final String LF="1001061000000000PSFE";		// 全硫分
	public static final String WHJHFF="1001ZZ1000000001I6MP";	// 无灰基挥发分
	public static final String GZJLF="1001ZZ1000000001I6MF"; 	// 干燥基硫分
	public static final String NSF="10010110000000001A9J";		// 内水分
	public static final String HFF="10010110000000001A9K";		// 挥发分

	public static final String SAVE_PARAM="FF01"; // 发运通知单保存参数
	public static final String SAVE_TS="提示"; //
	public static final String SAVE_BTS="不提示"; //
	public static final String NOSAVE="不保存"; //

	public static final String paytype_xh = "100101100000000019SX";//付款方式 现汇
	public static final String paytype_cd = "100101100000000019SW";//付款方式 承兑
	public static final String paytype_cd_my = "1001OZ1000000000NLJN";// 2018-8-7 付款方式 承兑民营（民营银行）

	public static final String TRANSPORT_QY="1001OA10000000000Y06";//	汽运
	public static final String TRANSPORT_LY="1001OA10000000000Y07";//	路运

	public static final String PARAM_DELETEACTION="FF02"; // 是否只允许本人操作（删除）单据
	public static final String PARAM_UNAPPROVEACTION="FF03"; // 是否只允许本人操作（取消审批）单据

	// 运费/装卸协议 物料pk
	public static final String PZ_YF="1001061000000000QUQU"; // 运费
	public static final String PZ_ZCF="1001061000000000QUQL";// 装车费
	public static final String PZ_DMF="1001021000000001EV14";// 堆煤费

	public static final String YDPS="0001ZZ1000000001JQMU";// 日计划发运单

}
