package nc.ui.hgts.sendnoticebill.actions;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.hgts.sendnoticebill.SendJzVO;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;

/**
 * 
 * <p>
 * <b>本类主要完成以下功能：结转时，发运通知单数据界面</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * @version 1.0
 * @since 1.0
 * @author 程莉
 * @time 2015-4-17 下午11:00:28
 */
public class ShowDataDialog extends UIDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private LoginContext context;// 登陆环境，IOC注入

	private BillListPanel blp;

	private UIPanel buttonPanel;// 按钮面板
	private UIButton cancelButton;// 取消按钮
	private UIButton confirmButton;// 确认按钮
	private String vbillno;
	String pk_org;
	
	String sql;

	BillManageModel model;

	public ShowDataDialog(Container parent, String sql, LoginContext context) {
		super(parent, "发运通知单");
		this.context=context;
		this.sql=sql;
		initialize();

	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(1000, 500));
		this.add(getblp(), BorderLayout.CENTER);
		this.add(getButtonPanel(), BorderLayout.SOUTH);

		//modify 2015-06-10  加载数据时加入线程等待框
		initDataForLazy();

	
		//addEventListener();

	}

	private void initDataForLazy() {
		StringBuffer error = new StringBuffer();
		try{
			nc.itf.uap.IUAPQueryBS bs=NCLocator.getInstance().lookup(nc.itf.uap.IUAPQueryBS.class);
			List<SendJzVO> list=(List<SendJzVO>) bs.executeQuery(sql, new BeanListProcessor(SendJzVO.class));
			if(null != list && list.size()>0){
				SendJzVO[] vos=list.toArray(new SendJzVO[0]);
				getblp().getBodyBillModel().setBodyDataVO(vos);
				getblp().getBodyBillModel().execLoadFormula();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			error.append(e.getMessage());
		}finally{
			if (error.length() > 0) {
				MessageDialog.showErrorDlg(rootPane, "错误", "数据加载失败，发生如下异常 \n" + error);
			}
		}
	}




	
	
	
	

	public BillListPanel getblp() {
		if (blp == null) {
			blp = new BillListPanel();
			String pk_user=AppContext.getInstance().getPkUser();
			blp.loadTemplet("40H1040303", null, pk_user, context.getPk_org());
			blp.getHeadTable().setRowSelectionAllowed(true);
			
			//blp.setParentMultiSelect(true); // 表体不会出现多选框
		}

		return blp;
	}

	public UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new UIPanel(new FlowLayout());
			buttonPanel.setPreferredSize(new Dimension(200, 30));
			buttonPanel.add(getConfirmButton());
			buttonPanel.add(getCancelButton());
			
		}
		return buttonPanel;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton();
			cancelButton.setText("取消");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public UIButton getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = new UIButton();
			confirmButton.setText("确认");
			confirmButton.addActionListener(this);
		}
		return confirmButton;
	}

	


	


	public String getVbillno() {
		return vbillno;
	}

	@Override
	public void actionPerformed(ActionEvent e)  {
		if (e.getSource().equals(getConfirmButton())) {
			try {
				boolean issel=onConfirm();
				if(issel){
					this.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageDialog.showWarningDlg(null, "提示", e1.getMessage());
			}
		} else if (e.getSource().equals(getCancelButton())) {
			closeCancel();
		} 
	}

	private boolean onConfirm() throws Exception {
		boolean issel=false;		
		int row=getblp().getBodyTable().getSelectedRow();
		if(row<0){
			MessageDialog.showErrorDlg(this, "提示", "未选中任何数据！");
			issel=false;
			return issel;
		}
		SendJzVO svo =(SendJzVO) getblp().getBodyBillModel().getBodyValueRowVO(row, SendJzVO.class.getName());//.getBodySelectedVOs(SendJzVO.class.getName());
		if (svo == null) {
			MessageDialog.showErrorDlg(this, "提示", "未选中任何数据！");
			issel=false;
		}else{		
			String svbillno=svo.getVbillno();			
			vbillno=svbillno;
			issel=true;
		}
		return issel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO 自动生成的方法存根
		
	}
	


}
