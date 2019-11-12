package nc.vo.hgts.pub;

import java.math.BigDecimal;
import java.util.List;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class HgtsPubTool {
	/**
	 * ��ֵת��ΪUFDouble����
	 * 
	 * @param value Ҫת����ֵ
	 * @return ����ΪUFDouble��ֵ
	 */
	public static UFDouble getUFDoubleNullAsZero(Object value) {
		UFDouble ret = null;
		if (value == null) {
			return UFDouble.ZERO_DBL;
		}

		if (value instanceof UFDouble) {
			ret = (UFDouble) value;
		}
		else if (value instanceof BigDecimal) {
			BigDecimal temp = (BigDecimal) value;
			ret = new UFDouble(temp);
		}
		else if (value instanceof Number) {
			Number number = (Number) value;
			double temp = number.doubleValue();
			ret = new UFDouble(temp);
		}
		else {
			String str = value.toString();
			try {
				ret = new UFDouble(str);
			}
			catch (Exception ex) {
				throwIllegalArgumentException(value, ex);
			}
		}
		return ret;
	}

	/**
	 * ��ֵת��ΪUFBoolean����
	 * 
	 * @param value Ҫת����ֵ
	 * @return ����ΪUFBoolean��ֵ
	 */
	public static UFBoolean getUFBooleanNullAsFalse(Object value) {
		UFBoolean ret = null;
		if (value == null) {
			return UFBoolean.FALSE;
		}

		if (value instanceof UFBoolean) {
			ret = (UFBoolean) value;
		}
		else {
			String str = value.toString().trim();
			try {
				ret = new UFBoolean(str);
			}
			catch (Exception ex) {
				throwIllegalArgumentException(value, ex);
			}
		}
		return ret;
	}
	
	public static String getStringNullAsTrim(Object value) {
		String retValue = null;
		if (value == null) {
			return "";
		}

		retValue = value.toString().trim();
		return retValue;
	}

	private static void throwIllegalArgumentException(Object value, Exception ex) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("the value is:");
		buffer.append(value);
		buffer.append(" the error message is :");
		buffer.append(ex.getMessage());
		throw new IllegalArgumentException(buffer.toString());
	}

	public static String getConSql(List<String> lid){
		StringBuffer str = new StringBuffer();
		str.append("(");
		for(String id:lid){
			str.append("'");
			str.append(id);
			str.append("',");
		}
		str.append("'aa'");
		str.append(")");
		return str.toString();
	}

	/**
	 * ��ɽ
	 * ��������/���ۣ�=1.111 ton=1.1
	 * ��������/���ۣ�=1.110 ton=1.11
	 * @param ton
	 * @return
	 */
	public static UFDouble getTon(UFDouble ton){
		if(null !=ton && ton.doubleValue()>0){
			String var=ton.toString();
			if(var.contains(".")){
				int i=var.indexOf(".");
				String left=var.substring(0, i);
				String right=var.substring((i+1), var.length());
				if(!right.substring(2, 3).equals("0")){ // �ж�С��������λ�Ƿ�Ϊ0
					ton=new UFDouble(left+"."+right.substring(0, 1));
				}else if(right.substring(2, 3).equals("0")){
					ton=new UFDouble(left+"."+right.substring(0, 2));
				}
			}
		}

		return ton;
	}

	/**
	 * Ҥ��
	 * ��ֵȨ��
	 * @param curMthTon�����·�����
	 * @param curTon�����շ�����
	 * @param curRz��������ֵ
	 * @return
	 */
	public static UFDouble getAvgRz(UFDouble curMthTon,UFDouble curTon,UFDouble curRz){
		UFDouble avgRz=UFDouble.ZERO_DBL;		
		avgRz=curTon.div(curMthTon).multiply(curRz);
		return avgRz;
	}
}
