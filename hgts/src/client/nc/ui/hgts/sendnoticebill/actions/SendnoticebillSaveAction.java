package nc.ui.hgts.sendnoticebill.actions;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.ValidationException;
import nc.itf.hgts.qry.cust.mny.ICustBalanceInfo;
import nc.itf.uap.busibean.ISysInitQry;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.UIState;
import nc.vo.hgts.pub.HgtsPubConst;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.sendnoticebill.AggSendnoticebillHVO;
import nc.vo.hgts.sendnoticebill.SendYzyjBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillBVO;
import nc.vo.hgts.sendnoticebill.SendnoticebillHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

/**
 * 发货通知单保存
 * 
 * @author Administrator
 * 
 */
public class SendnoticebillSaveAction extends SaveScriptAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -924761588514651561L;

	@Override
	public void doAction(ActionEvent e) throws Exception {

		if (!((BillForm) this.editor).validateValue()) {
			throw new ValidationException();
		}

		IBill bill = (IBill) this.editor.getValue();
		AggSendnoticebillHVO[] originBills = new AggSendnoticebillHVO[] { (AggSendnoticebillHVO) bill };
		SendnoticebillHVO hvo = originBills[0].getParentVO();
		String pk_transport = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_transporttype"));
		if (!"".equals(pk_transport)
				&& HgtsPubConst.TRANSPORT_LY.equals(pk_transport)) {
			String receiver = HgtsPubTool.getStringNullAsTrim(hvo
					.getAttributeValue("receiver"));
			if (null == receiver || "".equals(receiver)) {
				throw new BusinessException("【收货人】不允许为空！");
			}
		}
		String jytype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("jytype")); // 交易类型
		if (null != jytype && !"".equals(jytype)) {
			if ("2".equals(jytype)) {
				String pk_drkc = HgtsPubTool.getStringNullAsTrim(hvo
						.getAttributeValue("pk_drkc")); // 调入矿场
				if (null == pk_drkc || "".equals(pk_drkc)) {
					throw new BusinessException("交易类型为【调入洗】时，【调入矿厂】不允许为空！");
				}
			}
		}
		SendnoticebillBVO[] bvo = (SendnoticebillBVO[]) originBills[0]
				.getTableVO("hgts_sendnoticebill_b");
		if (null == bvo || bvo.length == 0) {
			throw new BusinessException("表体必须输入一行！");
		}
		UFDouble shul = UFDouble.ZERO_DBL;
		for (SendnoticebillBVO i : bvo) {
			shul =HgtsPubTool.getUFDoubleNullAsZero(i.getAttributeValue("shul"));

			if(this.model.getUiState() == UIState.ADD){////是否最新版本
				i.setAttributeValue("blatest", "Y");
			}
			if (!"".equals(pk_transport) && HgtsPubConst.TRANSPORT_LY.equals(pk_transport) ) {
				String startstadion = HgtsPubTool.getStringNullAsTrim(i
						.getAttributeValue("startstadion"));
				String arrviestadion = HgtsPubTool.getStringNullAsTrim(i
						.getAttributeValue("arrviestadion"));
				if (null == startstadion || "".equals(startstadion)) {
					throw new BusinessException("【发站】不允许为空！");
				}
				if (null == arrviestadion || "".equals(arrviestadion)) {
					throw new BusinessException("【到站】不允许为空！");
				}

			}
		}
		SendYzyjBVO[] yjbvo = (SendYzyjBVO[]) originBills[0]
				.getTableVO("pk_yzyj_b");
		if (null != yjbvo && yjbvo.length > 0) {
			for (SendYzyjBVO i : yjbvo) {
				if ("2".equals(HgtsPubTool.getStringNullAsTrim(i
						.getAttributeValue("bkdrule")))) {
					if ("".equals(HgtsPubTool.getStringNullAsTrim(i
							.getAttributeValue("batchcode")))) {
						throw new BusinessException(
								"扣罚方式为【扣吨】，【计算批次】不允许为空，请检查！");
					}
				}
			}
		}

		// 2018-10-11
		if (null != jytype && !"".equals(jytype)) {
			if ("1".equals(jytype)) { // 商品煤
				CommActionCheck check = new CommActionCheck();
				String rst = check.isOver(originBills[0]);
				if (null != rst && !"".equals(rst)) {
					throw new BusinessException(rst);
				}
			}
		}

		// 增加 数据校验类型的 判断
		if(hvo.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx)
			 || (shul.compareTo(new UFDouble(60)) < 0 && hvo.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_ys))){
			//数量小于60 &&  业务为预收
			super.doAction(e);
			return;
		}else{
			Object[] value = checkBalanceInfo(originBills[0]);
			if (null != value && value.length > 0) {
				String param = HgtsPubTool.getStringNullAsTrim(value[0]);
				UFDouble balance = HgtsPubTool.getUFDoubleNullAsZero(value[1]);
				UFDouble fymny = HgtsPubTool.getUFDoubleNullAsZero(value[2]);
				UFDouble chae = fymny.sub(balance);
				DecimalFormat df = new DecimalFormat("#,###.00");
				String msg = "";
				if (balance.doubleValue() == 0) {
					balance = UFDouble.ZERO_DBL.setScale(2,
							UFDouble.ROUND_HALF_UP);
					msg = "客户可用余额：" + balance + "\n";
				} else {
					String kymny = df.format(balance).split("[.]")[0] == null
							|| "".equals(df.format(balance).split("[.]")[0]) ? "0"
							+ df.format(balance)
							: df.format(balance);
							msg = "客户可用余额：" + kymny + "\n";
				}
				if (fymny.doubleValue() == 0) {
					fymny = UFDouble.ZERO_DBL.setScale(2,
							UFDouble.ROUND_HALF_UP);
					msg = msg + "发运金额：" + fymny + "\n";
				} else {
					msg = msg + "发运金额：" + df.format(fymny) + "\n";
				}
				if (chae.doubleValue() == 0) {
					chae = UFDouble.ZERO_DBL
							.setScale(2, UFDouble.ROUND_HALF_UP);
					msg = msg + "差异金额：" + chae;
				} else {
					String c = df.format(chae).split("[.]")[0] == null
							|| "".equals(df.format(chae).split("[.]")[0]) ? "0"
							+ df.format(chae) : df.format(chae);
							msg = msg + "差异金额：" + c;
				}

				if (HgtsPubConst.SAVE_TS.equals(param)) {
					MessageDialog.showWarningDlg(null, "提示", "客户余额不足\n" + msg);
				} else if (HgtsPubConst.SAVE_BTS.equals(param)) {
				} else if (HgtsPubConst.NOSAVE.equals(param)) {
					MessageDialog.showWarningDlg(null, "错误", "客户余额不足\n" + msg);
					return;
				}
			}
		}
		super.doAction(e);
	}

	// 参数
	public String getSysInitPara(String pk_corp) throws Exception {
		ISysInitQry init = (ISysInitQry) NCLocator.getInstance().lookup(
				ISysInitQry.class.getName());
		SysInitVO sysInitVO = init.queryByParaCode(pk_corp,
				HgtsPubConst.SAVE_PARAM);
		String value = sysInitVO.getValue();
		if (null != value && !"".equals(value)) {
			if (HgtsPubConst.SAVE_TS.equals(value)) {
				// msg="余额不足";
			} else if (HgtsPubConst.SAVE_BTS.equals(value)) {
				// msg="";
			} else if (HgtsPubConst.NOSAVE.equals(value)) {
				// msg="余额不足";
			}
		}

		return value;
	}

	/**
	 * 检验余额是否足够
	 * 
	 * @param aggvo
	 * @throws Exception
	 */
	public Object[] checkBalanceInfo(AggSendnoticebillHVO aggvo)
			throws Exception {
		Object[] str = new String[3];
		boolean isbal = false;
		SendnoticebillHVO hvo = aggvo.getParentVO();
		String hpk = hvo.getPrimaryKey();
		String pk_org = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_org"));
		String pk_cust = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_cust"));
		String pk_billtype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_billtypeid"));
		String pk_deptdoc = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_dept"));
		String pk_balatype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_balatype"));// 结算方式
		String pk_transporttype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_transporttype"));
		SendnoticebillBVO[] bvos = (SendnoticebillBVO[]) aggvo
				.getTableVO("hgts_sendnoticebill_b");
		UFDouble total_mny = UFDouble.ZERO_DBL; // 本张单据占用
		UFDouble total_zymny_wgb = UFDouble.ZERO_DBL; // 本张单据历时未关闭占用
		UFDouble total_zymny_gb = UFDouble.ZERO_DBL; // 本张单据历时关闭占用
		if (null != bvos && bvos.length > 0) {
			for (SendnoticebillBVO bvo : bvos) {
				if (null == hpk || "".equals(hpk)) {
					total_mny = total_mny.add(HgtsPubTool
							.getUFDoubleNullAsZero(bvo
									.getAttributeValue("jstotal")));
				} else {
					UFDouble zxprice = HgtsPubTool.getUFDoubleNullAsZero(bvo
							.getAttributeValue("zxprice"));

					if (HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)) {
						UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("shul"));
						UFDouble yzxnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("yzxnum"));
						UFDouble ykpnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("ykpnum"));

						if (!ValueUtils.getUFBoolean(
								bvo.getAttributeValue("rowcloseflag"))
								.booleanValue()) {
							// 未关闭: (数量-已开票数量)* 执行单价
							total_zymny_wgb = (total_zymny_wgb.add((shul
									.sub(ykpnum)).multiply(zxprice))).setScale(
											2, UFDouble.ROUND_HALF_UP);

							// total_mny =
							// HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jstotal")).sub(total_zymny_wgb);
						} else {
							// 已关闭: (已过磅数量-已开票数量)* 执行单价
							total_zymny_gb = (total_zymny_gb.add((yzxnum
									.sub(ykpnum)).multiply(zxprice))).setScale(
											2, UFDouble.ROUND_HALF_UP);
						}
					} else if (HgtsPubConst.TRANSPORT_LY
							.equals(pk_transporttype)) {
						UFDouble carnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("carnum"));
						UFDouble def6 = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("def6"));
						UFDouble def31 = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("def31"));
						UFDouble carstrong = HgtsPubTool
								.getUFDoubleNullAsZero(bvo
										.getAttributeValue("carstrong"));
						if (!ValueUtils.getUFBoolean(
								bvo.getAttributeValue("rowcloseflag"))
								.booleanValue()) {
							// 未关闭: (请车数量-已开票车数)* 标重 * 执行单价
							total_zymny_wgb = (total_zymny_wgb.add((carnum
									.sub(def31)).multiply(zxprice).multiply(
											carstrong))).setScale(2,
													UFDouble.ROUND_HALF_UP);
							// total_mny =
							// HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jstotal")).sub(total_zymny_wgb);
						} else {
							// （已装车数-开票车数）* 标重*执行单价
							total_zymny_gb = (total_zymny_gb.add((def6
									.sub(def31)).multiply(zxprice).multiply(
											carstrong))).setScale(2,
													UFDouble.ROUND_HALF_UP);
						}

					}
				}
			}
		}

		// 检查剩余金额是否满足本次发运金额
		ICustBalanceInfo ic = (ICustBalanceInfo) NCLocator.getInstance()
				.lookup(ICustBalanceInfo.class.getName());
		UFDouble[] info = ic.getBalanceInfo(hpk, pk_billtype, pk_org, pk_cust,
				pk_balatype, pk_deptdoc, "2018-09-01");
		if (null != info && info.length > 0) {
			// 可用额度=信用额度 +客户余额 —业务占用
			UFDouble balance = info[3];

			// 新增--保存
			if (null == hpk || "".equals(hpk)) {
				if (balance.doubleValue() >= total_mny.setScale(2,
						UFDouble.ROUND_HALF_UP).doubleValue()) {
					isbal = true;
				}
			} else {
				// 修改 --保存 态
				balance = balance.sub(total_zymny_wgb).sub(total_zymny_gb);
				if (balance.doubleValue() >= 0) {
					isbal = true;
				}
			}

			String pk_group = HgtsPubTool.getStringNullAsTrim(hvo
					.getAttributeValue("pk_group"));
			String value = "";
			if (!isbal) {
				value = getSysInitPara(pk_group);
			}

			str[0] = value;
			str[1] = balance + "";
			str[2] = total_mny.setScale(2, UFDouble.ROUND_HALF_UP) + "";
			// str[3]=sendmny.add(total_mny)+""; // 占用金额 TODO加上该代码，得改数组长度定义
		}
		return str;
	}

	/**
	 * 检验数量是否足够
	 * 
	 * @param aggvo
	 * @throws Exception
	 */
	public Object[] checkResidueInfo(AggSendnoticebillHVO aggvo)
			throws Exception {
		Object[] str = new String[3];
		boolean isbal = false;
		SendnoticebillHVO hvo = aggvo.getParentVO();
		String hpk = hvo.getPrimaryKey();
		String pk_org = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_org"));
		String pk_cust = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_cust"));// 客户
		String pk_billtype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_billtypeid"));// 单据类型
		String pk_deptdoc = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_dept"));
		String pk_fhkc = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_fhkc")); // 矿场
		String pk_transporttype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_transporttype"));// 运输方式
		SendnoticebillBVO[] bvos = (SendnoticebillBVO[]) aggvo
				.getTableVO("hgts_sendnoticebill_b");
		UFDouble total_num = UFDouble.ZERO_DBL; // 本张单据占用
		UFDouble total_zynum_wgb = UFDouble.ZERO_DBL; // 本张单据历时未关闭占用
		UFDouble total_zynum_gb = UFDouble.ZERO_DBL; // 本张单据历时关闭占用
		String pk_pz = HgtsPubTool.getStringNullAsTrim(bvos[0]
				.getAttributeValue("pz"));
		if (null != bvos && bvos.length > 0) {
			for (SendnoticebillBVO bvo : bvos) {
				if (null == hpk || "".equals(hpk)) {
					total_num = total_num.add(HgtsPubTool
							.getUFDoubleNullAsZero(bvo
									.getAttributeValue("shul")));
				} else {
					// 汽运
					if (HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)) {
						UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("shul"));
						UFDouble yzxnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("yzxnum"));// 已过磅数量
						UFDouble ykpnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("ykpnum"));// 已开票数量

						if (!ValueUtils.getUFBoolean(
								bvo.getAttributeValue("rowcloseflag"))
								.booleanValue()) {
							// 未关闭: (数量-已开票数量)
							total_zynum_wgb = (total_zynum_wgb.add(shul
									.sub(ykpnum))).setScale(2,
											UFDouble.ROUND_HALF_UP);
						} else {
							// 已关闭: (已过磅数量-已开票数量)
							total_zynum_gb = (total_zynum_gb.add(yzxnum
									.sub(ykpnum))).setScale(2,
											UFDouble.ROUND_HALF_UP);
						}
					}
					// 铁路运
					else if (HgtsPubConst.TRANSPORT_LY.equals(pk_transporttype)) {
						UFDouble carnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("carnum"));
						UFDouble def6 = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("def6"));
						UFDouble def31 = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("def31"));
						UFDouble carstrong = HgtsPubTool
								.getUFDoubleNullAsZero(bvo
										.getAttributeValue("carstrong"));
						if (!ValueUtils.getUFBoolean(
								bvo.getAttributeValue("rowcloseflag"))
								.booleanValue()) {
							// 未关闭: (请车数量-已开票车数)* 标重
							total_zynum_wgb = (total_zynum_wgb.add((carnum
									.sub(def31)).multiply(carstrong)))
									.setScale(2, UFDouble.ROUND_HALF_UP);
						} else {
							// （已装车数-开票车数）* 标重
							total_zynum_gb = (total_zynum_gb.add((def6
									.sub(def31)).multiply(carstrong)))
									.setScale(2, UFDouble.ROUND_HALF_UP);
						}

					}
				}
			}
		}

		// //检查剩余金额是否满足本次发运金额
		ICustBalanceInfo ic = (ICustBalanceInfo) NCLocator.getInstance()
				.lookup(ICustBalanceInfo.class.getName());
		UFDouble[] info = ic.getResidueInfo(hpk, pk_transporttype, pk_billtype,
				pk_org, pk_cust, pk_fhkc, pk_deptdoc, pk_pz, "20198-09-01");
		if (null != info && info.length > 0) {
			// 最终可用数量
			UFDouble toltal_sum = info[0];

			// 新增--保存
			if (null == hpk || "".equals(hpk)) {
				if (total_num.doubleValue() <= toltal_sum.setScale(2,
						UFDouble.ROUND_HALF_UP).doubleValue()) {
					isbal = true;
				}else{
					total_num = toltal_sum.sub(total_num);
				}
			} else {
				// 修改 --保存 态
				total_num = toltal_sum.sub(total_zynum_wgb).sub(total_zynum_gb);
				if (total_num.doubleValue() >= 0) {
					isbal = true;
				}
			}

			String pk_group = HgtsPubTool.getStringNullAsTrim(hvo
					.getAttributeValue("pk_group"));
			String value = "";
			if (!isbal) {
				value = getSysInitPara(pk_group);
			}

			str[0] = value;
			str[1] = toltal_sum + "";
			str[2] = total_num.setScale(2, UFDouble.ROUND_HALF_UP) + "";
		}
		return str;
	}
}
