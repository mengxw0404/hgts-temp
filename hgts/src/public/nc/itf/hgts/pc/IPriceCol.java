package nc.itf.hgts.pc;

import java.sql.SQLException;

import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

public interface IPriceCol {

	
	public IBill colPrice(IBill bill) throws BusinessException, SQLException;
	
	public IBill colNewPrice(IBill bill) throws BusinessException, SQLException;
	
}
