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
 * ����֪ͨ������
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
				throw new BusinessException("���ջ��ˡ�������Ϊ�գ�");
			}
		}
		String jytype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("jytype")); // ��������
		if (null != jytype && !"".equals(jytype)) {
			if ("2".equals(jytype)) {
				String pk_drkc = HgtsPubTool.getStringNullAsTrim(hvo
						.getAttributeValue("pk_drkc")); // �����
				if (null == pk_drkc || "".equals(pk_drkc)) {
					throw new BusinessException("��������Ϊ������ϴ��ʱ��������󳧡�������Ϊ�գ�");
				}
			}
		}
		SendnoticebillBVO[] bvo = (SendnoticebillBVO[]) originBills[0]
				.getTableVO("hgts_sendnoticebill_b");
		if (null == bvo || bvo.length == 0) {
			throw new BusinessException("�����������һ�У�");
		}
		UFDouble shul = UFDouble.ZERO_DBL;
		for (SendnoticebillBVO i : bvo) {
			shul =HgtsPubTool.getUFDoubleNullAsZero(i.getAttributeValue("shul"));

			if(this.model.getUiState() == UIState.ADD){////�Ƿ����°汾
				i.setAttributeValue("blatest", "Y");
			}
			if (!"".equals(pk_transport) && HgtsPubConst.TRANSPORT_LY.equals(pk_transport) ) {
				String startstadion = HgtsPubTool.getStringNullAsTrim(i
						.getAttributeValue("startstadion"));
				String arrviestadion = HgtsPubTool.getStringNullAsTrim(i
						.getAttributeValue("arrviestadion"));
				if (null == startstadion || "".equals(startstadion)) {
					throw new BusinessException("����վ��������Ϊ�գ�");
				}
				if (null == arrviestadion || "".equals(arrviestadion)) {
					throw new BusinessException("����վ��������Ϊ�գ�");
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
								"�۷���ʽΪ���۶֡������������Ρ�������Ϊ�գ����飡");
					}
				}
			}
		}

		// 2018-10-11
		if (null != jytype && !"".equals(jytype)) {
			if ("1".equals(jytype)) { // ��Ʒú
				CommActionCheck check = new CommActionCheck();
				String rst = check.isOver(originBills[0]);
				if (null != rst && !"".equals(rst)) {
					throw new BusinessException(rst);
				}
			}
		}

		// ���� ����У�����͵� �ж�
		if(hvo.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_sx)
			 || (shul.compareTo(new UFDouble(60)) < 0 && hvo.getAttributeValue("pk_busitype").equals(HgtsPubConst.biztype_ys))){
			//����С��60 &&  ҵ��ΪԤ��
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
					msg = "�ͻ�������" + balance + "\n";
				} else {
					String kymny = df.format(balance).split("[.]")[0] == null
							|| "".equals(df.format(balance).split("[.]")[0]) ? "0"
							+ df.format(balance)
							: df.format(balance);
							msg = "�ͻ�������" + kymny + "\n";
				}
				if (fymny.doubleValue() == 0) {
					fymny = UFDouble.ZERO_DBL.setScale(2,
							UFDouble.ROUND_HALF_UP);
					msg = msg + "���˽�" + fymny + "\n";
				} else {
					msg = msg + "���˽�" + df.format(fymny) + "\n";
				}
				if (chae.doubleValue() == 0) {
					chae = UFDouble.ZERO_DBL
							.setScale(2, UFDouble.ROUND_HALF_UP);
					msg = msg + "�����" + chae;
				} else {
					String c = df.format(chae).split("[.]")[0] == null
							|| "".equals(df.format(chae).split("[.]")[0]) ? "0"
							+ df.format(chae) : df.format(chae);
							msg = msg + "�����" + c;
				}

				if (HgtsPubConst.SAVE_TS.equals(param)) {
					MessageDialog.showWarningDlg(null, "��ʾ", "�ͻ�����\n" + msg);
				} else if (HgtsPubConst.SAVE_BTS.equals(param)) {
				} else if (HgtsPubConst.NOSAVE.equals(param)) {
					MessageDialog.showWarningDlg(null, "����", "�ͻ�����\n" + msg);
					return;
				}
			}
		}
		super.doAction(e);
	}

	// ����
	public String getSysInitPara(String pk_corp) throws Exception {
		ISysInitQry init = (ISysInitQry) NCLocator.getInstance().lookup(
				ISysInitQry.class.getName());
		SysInitVO sysInitVO = init.queryByParaCode(pk_corp,
				HgtsPubConst.SAVE_PARAM);
		String value = sysInitVO.getValue();
		if (null != value && !"".equals(value)) {
			if (HgtsPubConst.SAVE_TS.equals(value)) {
				// msg="����";
			} else if (HgtsPubConst.SAVE_BTS.equals(value)) {
				// msg="";
			} else if (HgtsPubConst.NOSAVE.equals(value)) {
				// msg="����";
			}
		}

		return value;
	}

	/**
	 * ��������Ƿ��㹻
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
				.getAttributeValue("pk_balatype"));// ���㷽ʽ
		String pk_transporttype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_transporttype"));
		SendnoticebillBVO[] bvos = (SendnoticebillBVO[]) aggvo
				.getTableVO("hgts_sendnoticebill_b");
		UFDouble total_mny = UFDouble.ZERO_DBL; // ���ŵ���ռ��
		UFDouble total_zymny_wgb = UFDouble.ZERO_DBL; // ���ŵ�����ʱδ�ر�ռ��
		UFDouble total_zymny_gb = UFDouble.ZERO_DBL; // ���ŵ�����ʱ�ر�ռ��
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
							// δ�ر�: (����-�ѿ�Ʊ����)* ִ�е���
							total_zymny_wgb = (total_zymny_wgb.add((shul
									.sub(ykpnum)).multiply(zxprice))).setScale(
											2, UFDouble.ROUND_HALF_UP);

							// total_mny =
							// HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jstotal")).sub(total_zymny_wgb);
						} else {
							// �ѹر�: (�ѹ�������-�ѿ�Ʊ����)* ִ�е���
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
							// δ�ر�: (�복����-�ѿ�Ʊ����)* ���� * ִ�е���
							total_zymny_wgb = (total_zymny_wgb.add((carnum
									.sub(def31)).multiply(zxprice).multiply(
											carstrong))).setScale(2,
													UFDouble.ROUND_HALF_UP);
							// total_mny =
							// HgtsPubTool.getUFDoubleNullAsZero(bvo.getAttributeValue("jstotal")).sub(total_zymny_wgb);
						} else {
							// ����װ����-��Ʊ������* ����*ִ�е���
							total_zymny_gb = (total_zymny_gb.add((def6
									.sub(def31)).multiply(zxprice).multiply(
											carstrong))).setScale(2,
													UFDouble.ROUND_HALF_UP);
						}

					}
				}
			}
		}

		// ���ʣ�����Ƿ����㱾�η��˽��
		ICustBalanceInfo ic = (ICustBalanceInfo) NCLocator.getInstance()
				.lookup(ICustBalanceInfo.class.getName());
		UFDouble[] info = ic.getBalanceInfo(hpk, pk_billtype, pk_org, pk_cust,
				pk_balatype, pk_deptdoc, "2018-09-01");
		if (null != info && info.length > 0) {
			// ���ö��=���ö�� +�ͻ���� ��ҵ��ռ��
			UFDouble balance = info[3];

			// ����--����
			if (null == hpk || "".equals(hpk)) {
				if (balance.doubleValue() >= total_mny.setScale(2,
						UFDouble.ROUND_HALF_UP).doubleValue()) {
					isbal = true;
				}
			} else {
				// �޸� --���� ̬
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
			// str[3]=sendmny.add(total_mny)+""; // ռ�ý�� TODO���ϸô��룬�ø����鳤�ȶ���
		}
		return str;
	}

	/**
	 * ���������Ƿ��㹻
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
				.getAttributeValue("pk_cust"));// �ͻ�
		String pk_billtype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_billtypeid"));// ��������
		String pk_deptdoc = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_dept"));
		String pk_fhkc = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_fhkc")); // ��
		String pk_transporttype = HgtsPubTool.getStringNullAsTrim(hvo
				.getAttributeValue("pk_transporttype"));// ���䷽ʽ
		SendnoticebillBVO[] bvos = (SendnoticebillBVO[]) aggvo
				.getTableVO("hgts_sendnoticebill_b");
		UFDouble total_num = UFDouble.ZERO_DBL; // ���ŵ���ռ��
		UFDouble total_zynum_wgb = UFDouble.ZERO_DBL; // ���ŵ�����ʱδ�ر�ռ��
		UFDouble total_zynum_gb = UFDouble.ZERO_DBL; // ���ŵ�����ʱ�ر�ռ��
		String pk_pz = HgtsPubTool.getStringNullAsTrim(bvos[0]
				.getAttributeValue("pz"));
		if (null != bvos && bvos.length > 0) {
			for (SendnoticebillBVO bvo : bvos) {
				if (null == hpk || "".equals(hpk)) {
					total_num = total_num.add(HgtsPubTool
							.getUFDoubleNullAsZero(bvo
									.getAttributeValue("shul")));
				} else {
					// ����
					if (HgtsPubConst.TRANSPORT_QY.equals(pk_transporttype)) {
						UFDouble shul = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("shul"));
						UFDouble yzxnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("yzxnum"));// �ѹ�������
						UFDouble ykpnum = HgtsPubTool.getUFDoubleNullAsZero(bvo
								.getAttributeValue("ykpnum"));// �ѿ�Ʊ����

						if (!ValueUtils.getUFBoolean(
								bvo.getAttributeValue("rowcloseflag"))
								.booleanValue()) {
							// δ�ر�: (����-�ѿ�Ʊ����)
							total_zynum_wgb = (total_zynum_wgb.add(shul
									.sub(ykpnum))).setScale(2,
											UFDouble.ROUND_HALF_UP);
						} else {
							// �ѹر�: (�ѹ�������-�ѿ�Ʊ����)
							total_zynum_gb = (total_zynum_gb.add(yzxnum
									.sub(ykpnum))).setScale(2,
											UFDouble.ROUND_HALF_UP);
						}
					}
					// ��·��
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
							// δ�ر�: (�복����-�ѿ�Ʊ����)* ����
							total_zynum_wgb = (total_zynum_wgb.add((carnum
									.sub(def31)).multiply(carstrong)))
									.setScale(2, UFDouble.ROUND_HALF_UP);
						} else {
							// ����װ����-��Ʊ������* ����
							total_zynum_gb = (total_zynum_gb.add((def6
									.sub(def31)).multiply(carstrong)))
									.setScale(2, UFDouble.ROUND_HALF_UP);
						}

					}
				}
			}
		}

		// //���ʣ�����Ƿ����㱾�η��˽��
		ICustBalanceInfo ic = (ICustBalanceInfo) NCLocator.getInstance()
				.lookup(ICustBalanceInfo.class.getName());
		UFDouble[] info = ic.getResidueInfo(hpk, pk_transporttype, pk_billtype,
				pk_org, pk_cust, pk_fhkc, pk_deptdoc, pk_pz, "20198-09-01");
		if (null != info && info.length > 0) {
			// ���տ�������
			UFDouble toltal_sum = info[0];

			// ����--����
			if (null == hpk || "".equals(hpk)) {
				if (total_num.doubleValue() <= toltal_sum.setScale(2,
						UFDouble.ROUND_HALF_UP).doubleValue()) {
					isbal = true;
				}else{
					total_num = toltal_sum.sub(total_num);
				}
			} else {
				// �޸� --���� ̬
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
