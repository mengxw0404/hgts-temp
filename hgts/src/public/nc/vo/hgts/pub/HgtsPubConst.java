package nc.vo.hgts.pub;

public class HgtsPubConst {

	public static final int price_policy_pay = 1;				//�۸����� ���ʽ
	public static final int price_policy_senddistance = 2;		//�۸�����  �������
	public static final int price_policy_qulityindex = 3;		//�۸����� ����ָ��
	public static final int price_policy_numprice = 4;			//�۸����� �����Ż�
	public static final int price_policy_transtype = 5;			//�۸����� ���䷽ʽ

	public static final String biztype_sx = "100101100000000019M1";//ҵ������   ����
	public static final String biztype_ys = "100101100000000019M2";//ҵ������   Ԥ��

	// �۸����� ֵ����
	public static final String pay_xh = "100101100000000019KM";//���ʽ �ֻ�
	public static final String pay_cd = "100101100000000019KN";//���ʽ �ж�
	public static final String pay_cd_my = "1001OZ1000000000NLJK";// 2018-8-7 ���ʽ �ж���Ӫ����Ӫ���У�

	public static final String transtype_qy = "1001OZ1000000000BJ55";//���䷽ʽ ����
	public static final String transtype_ly = "1001OZ1000000000BJ57";//���䷽ʽ ·��

	// ������������
	public static final String JGZC="0001AX10000000003MW1"; 	// YX01:�۸�����
	public static final String FHTZD="0001AX100000000045MP"; 	// YX04:����֪ͨ��
	public static final String ZJBG="0001AX10000000006X4D"; 	// YX09:�ʼ챨��
	public static final String FHJLD="0001AX10000000008AD7"; 	// YX11:����������ά��
	public static final String ZCJH="0001AX10000000009C53"; 	// YX13:װ���ƻ�
	public static final String XSFP="0001OZ1000000000AZ3S"; 	// YX19:���۷�Ʊ
	public static final String SKTZD="0001OZ1000000000AZWQ"; 	// YX20:�տ�֪ͨ��
	public static final String XSHJD_QY="0001OZ1000000000CMLW"; // YX21:���ۻ��۵�(����)
	public static final String XSHJD_LY="0001OZ1000000000D20X"; // YX21:���ۻ��۵�(·��)
	public static final String TLYSSQJH="0001OA10000000008I3W"; // YX42:��·��������ƻ�
	//TODO ���ӷѽ��㵥
	public static final String XSHJD_YZF="0001021000000001EF5C";

	// TODO �˷ѷ�Ʊ
	public static final String XSFP_YZF="0001021000000001EG8T";
	
	public static final String PCD="0001ZZ1000000001HQVK";

	//TODO 2019��3��4�� ��ͬ-�����ͬ
	public static final String CONTRACT_SALE="0001OA10000000005XJ5";
	// ��ͬ-�˷�Э��
	public static final String CONTRACT_YFXY="0001021000000001DICW";
	// ��ͬ-װжЭ��
	public static final String CONTRACT_ZXXY="0001021000000001DJ33";
	
	public static final String JGZC_JJ="0001ZZ1000000001J0I0"; // ���ۼ۸�
	public static final String JGZC_NB="0001021000000001FNCU"; // �ڲ��۸�
	public static final String XSHJD_NB="0001021000000001FON6";// �ڲ������嵥


	public static final String NODECODE_XSHJD_QY="40H10801";	//	���ۻ��۵�(����)
	public static final String NODECODE_XSHJD_LY="40H10802";	//	���ۻ��۵�(·��)

	public static final String auto_id_billtype = "FFYX";//�����Զ���������  ��������

	public static final String hjyh_dialog_templet = "10010110000000006396";
	public static final String pricepolicy_ljyh_jgysid = "4";

	public static final String[] ljyh_sort_fields = new String[]{"tsettletime"};//�����ŻݶԻ��� �����ֶ�
	public static final String HF="10010110000000001A9I";//�ҷ�
	public static final String SF="10010110000000001A9G";//ȫˮ��
	public static final String FRL="1001061000000000PSFK";		// ������
	public static final String LF="1001061000000000PSFE";		// ȫ���
	public static final String WHJHFF="1001ZZ1000000001I6MP";	// �޻һ��ӷ���
	public static final String GZJLF="1001ZZ1000000001I6MF"; 	// ��������
	public static final String NSF="10010110000000001A9J";		// ��ˮ��
	public static final String HFF="10010110000000001A9K";		// �ӷ���

	public static final String SAVE_PARAM="FF01"; // ����֪ͨ���������
	public static final String SAVE_TS="��ʾ"; //
	public static final String SAVE_BTS="����ʾ"; //
	public static final String NOSAVE="������"; //

	public static final String paytype_xh = "100101100000000019SX";//���ʽ �ֻ�
	public static final String paytype_cd = "100101100000000019SW";//���ʽ �ж�
	public static final String paytype_cd_my = "1001OZ1000000000NLJN";// 2018-8-7 ���ʽ �ж���Ӫ����Ӫ���У�

	public static final String TRANSPORT_QY="1001OA10000000000Y06";//	����
	public static final String TRANSPORT_LY="1001OA10000000000Y07";//	·��

	public static final String PARAM_DELETEACTION="FF02"; // �Ƿ�ֻ�����˲�����ɾ��������
	public static final String PARAM_UNAPPROVEACTION="FF03"; // �Ƿ�ֻ�����˲�����ȡ������������

	// �˷�/װжЭ�� ����pk
	public static final String PZ_YF="1001061000000000QUQU"; // �˷�
	public static final String PZ_ZCF="1001061000000000QUQL";// װ����
	public static final String PZ_DMF="1001021000000001EV14";// ��ú��

	public static final String YDPS="0001ZZ1000000001JQMU";// �ռƻ����˵�

}
