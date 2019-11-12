package nc.ui.hgts.ff_sknoticebill.ace.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.hgts.pub.NumberToCN;
import nc.vo.pub.lang.UFDouble;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent>{

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// ÊÕ¿î½ð¶î
		if(e.getKey().equals("skmny")){
			UFDouble skmny=HgtsPubTool.getUFDoubleNullAsZero(e.getValue());
			NumberToCN cn=new NumberToCN();
			String dx=cn.numberCNMontrayUnit(skmny.toBigDecimal());
			e.getBillCardPanel().setHeadItem("def0", dx);
		}
	}

}
