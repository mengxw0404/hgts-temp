package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextArea;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.lang.UFDouble;

/**
 * 称重异常数据处理dlg
 * @author chengli
 *
 */
@SuppressWarnings("serial")
public class ErrorReasonDlg extends UIDialog implements ActionListener {

	private UIPanel panel = null;
	private UIButton buttonOK = null;
	private UIButton buttonCancel = null;

	private UILabel label = null;
	private UILabel label2 = null;
	private UILabel label3 = null;
	private UILabel label4 = null;
	private UIComboBox cb1 = null;
	private UITextArea textArea = null;

	private Object[] errmsg;
	private String note = null;

	public ErrorReasonDlg(Container parent, String title,Object[] errmsg){
		super(parent);
		this.errmsg=errmsg;
		initialize();
		setTitle(title);
	}


	private void initialize() {

		setSize(550, 375);

		getButtonOK().addActionListener(this);
		getButtonCancel().addActionListener(this);

		getPanel().add(getButtonOK());
		getPanel().add(getButtonCancel());

		getPanel().add(getTextArea());

		getPanel().add(getlabel());
		getPanel().add(getlabel2());
		getPanel().add(getlabel3());
		getPanel().add(getlabel4());
		getPanel().add(getcb1());
	}

	public UIPanel getPanel() {
		if (panel==null) {
			panel = new UIPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
		}
		return panel;
	}

	private UIButton getButtonOK() {
		if (buttonOK==null) {
			buttonOK = new UIButton();
			buttonOK.setText("确定");
			buttonOK.setBounds(82, 310, 75, 20);
		}
		return buttonOK;
	}

	private UIButton getButtonCancel() {
		if (buttonCancel==null) {
			buttonCancel = new UIButton();
			buttonCancel.setText("取消");
			buttonCancel.setBounds(182, 310, 75, 20);
		}
		return buttonCancel;
	}
	
	private UILabel getlabel(){
		if (label==null) {
			label = new UILabel();
			label.setBounds(26, 22, 480, 20);

			String info=this.errmsg==null|| errmsg.length<=0?"":this.errmsg[3].toString();
			String[] val=info.split("；");
			label.setText(val[0]);
		}
		return label;
	}

	private UILabel getlabel2(){
		if (label2==null) {
			label2 = new UILabel();
			label2.setBounds(26, 52, 200, 20);

			String info=this.errmsg==null|| errmsg.length<=0?"":this.errmsg[3].toString();
			String[] val=info.split("；");
			label2.setText(val[1]);
		}
		return label2;
	}

	private UILabel getlabel3(){
		if (label3==null) {
			label3 = new UILabel();
			label3.setBounds(26, 82, 200, 20);

			String info=this.errmsg==null|| errmsg.length<=0?"":this.errmsg[3].toString();
			String[] val=info.split("；");
			label3.setText(val[2]);
		}
		return label3;
	}

	//界面第一行
	public UILabel getlabel4() {
		if (label4==null) {
			label4 = new UILabel();
			label4.setBounds(26, 112, 100, 25);
			label4.setText("本次异常原因：");
		}
		return label4;
	}

	public UIComboBox getcb1() {
		if (cb1==null) {
			cb1 = new UIComboBox();
			cb1.setBounds(106, 112, 100, 25);
			String[] items=new String[]{"查超限","煤干","煤质好","控制总吨数","限高","煤种不同"};
			cb1.addItems(items);
			cb1.setSelectedIndex(0);
			cb1.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 自动生成的方法存根
					getTextArea().setText(HgtsPubTool.getStringNullAsTrim(getcb1().getSelectdItemValue()));
				}
				
			});
		}
		return cb1;
	}
	
	private UITextArea getTextArea() {
		if (textArea==null) {
			textArea = new UITextArea();
			textArea.setBounds(26, 162, 339, 100);
			textArea.setLineWrap(true);
			textArea.setText(HgtsPubTool.getStringNullAsTrim(getcb1().getSelectdItemValue()));
		}
		return textArea;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// 保存
		if (e.getSource().equals(getButtonOK())) {
			note =getTextArea().getText();
			if (null==note) {
				MessageDialog.showWarningDlg(null, "提示", "请录入异常原因");
				return;
			}

			super.closeOK();
		}

		// 取消
		if (e.getSource().equals(getButtonCancel())) {
			super.closeCancel();
		}

	}


	public String getNote() {
		return note;
	}


}
