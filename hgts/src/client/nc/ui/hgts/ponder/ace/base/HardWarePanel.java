package nc.ui.hgts.ponder.ace.base;

import java.awt.GridLayout;

import javax.swing.SwingUtilities;

import kobetool.dshowwrapper.VideoTool;
import nc.bs.logging.Logger;
import nc.ui.hgts.pondUI.ace.Hardware.CommPortRead;
import nc.ui.hgts.pondUI.ace.Hardware.InfraredPortUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hgts.pub.HgtsPubTool;
import nc.vo.pub.BusinessException;

public class HardWarePanel extends UIPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3585898641283530568L;
	private MeasPanel measPanel = null;
	private VedioPanel vedioPanel = null;
	private XYGVideoPanel XYGVideoPanel = null;
	private BaseContainer parents = null;
	public HardWarePanel()
	{
		initlize();
	}

	public HardWarePanel(BaseContainer parents)
	{
		this.parents = parents;
		initlize();
	}

	private void initlize()
	{
		setLayout(new GridLayout(2, 1, 5, 5));
		add(getMeasPanel());
		String vedioparam = null;
		try
		{
			String ofmine=HgtsPubTool.getStringNullAsTrim(this.getMeasPanel().getBillWorkPanel().getMeasDoc().getAttributeValue("ofmine"));
			vedioparam = VideoTool.getVedioParam(ofmine);
		}
		catch (BusinessException e)
		{
			Logger.error(e);
			MessageDialog.showErrorDlg(this, "", e.getMessage());
		}
//		if ("Y".equals(vedioparam)) {
//			SwingUtilities.invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					HardWarePanel.this.XYGVideoPanel = new XYGVideoPanel();
//					HardWarePanel.this.add(HardWarePanel.this.getXYGVideoPanel());
//					HardWarePanel.this.getXYGVideoPanel().loadVideo();
//					HardWarePanel.this.getXYGVideoPanel().playVideo();
//				}
//			});
//		} else {
//			add(getVedioPanel());
//		}
	
	}

	protected MeasPanel getMeasPanel()
	{
		if (this.measPanel == null) {
			this.measPanel = new MeasPanel(this);
		}
		return this.measPanel;
	}

	public CommPortRead getReader()
	{
		return getMeasPanel().getReader();
	}

	public CodeStack getCodeStacker()
	{
		return getMeasPanel().getCodeStacker();
	}

	protected XYGVideoPanel getXYGVideoPanel()
	{
		return this.XYGVideoPanel;
	}

	public VedioPanel getVedioPanel()
	{
		if (this.vedioPanel == null) {
			this.vedioPanel = new VedioPanel();
		}
		return this.vedioPanel;
	}

	public BaseContainer getParents()
	{
		return this.parents;
	}
}
