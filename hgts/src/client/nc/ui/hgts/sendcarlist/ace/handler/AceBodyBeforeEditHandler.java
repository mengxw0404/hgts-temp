package nc.ui.hgts.sendcarlist.ace.handler;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.ToolVOToPcdRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

public class AceBodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent>{

	public AceBodyBeforeEditHandler() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent arg0) {
		// TODO 自动生成的方法存根
		if(arg0.getKey().equals("carno")){
			UIRefPane pane=(UIRefPane) arg0.getBillCardPanel().getBodyItem(arg0.getKey()).getComponent();
			AbstractRefModel model=new ToolVOToPcdRefModel();
			//pane.setMultiSelectedEnabled(true);
			arg0.setReturnValue(true);
		}else{
			arg0.setReturnValue(true);
		}
	}

}
