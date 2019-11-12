package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import kobetool.dshowwrapper.CaptureDevice;
import kobetool.dshowwrapper.GUID;
import kobetool.dshowwrapper.PerTabbedPaneUI;
import kobetool.dshowwrapper.VideoFormat;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.vo.pub.BusinessException;

public class OrgPicDlg
  extends UIDialog
{
  private String localFileUrl = null;
  private JTabbedPane tabPane = null;
  private UIPanel panel = null;
  
  public OrgPicDlg(String localFileUrl)
    throws BusinessException, IOException
  {
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        CaptureDevice cd = (CaptureDevice)OrgPicDlg.this.tabPane.getComponentAt(0);
        if (cd != null) {
          cd.destroy();
        }
      }
    });
    setLocalFileUrl(localFileUrl);
    init();
  }
  
  private void init()
    throws BusinessException, IOException
  {
    setTitle("视频放大");
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int)screensize.getWidth();
    int height = (int)screensize.getHeight();
    
    setBounds(new Rectangle(0, 0, width / 2, height / 2));
    initPanel();
  }
  
  private void initPanel()
    throws BusinessException, IOException
  {
    getContentPane().setLayout(null);
    getPanel();
    setLayout(null);
    setBounds(new Rectangle(0, 0, 518, 335));
    setName("vedioPanelNew");
    loadVedio();
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        OrgPicDlg.this.playVedio();
      }
    });
  }
  
  public void loadVedio()
  {
    /*for (CaptureDevice cd : ) {
      if (getLocalFileUrl().equals(cd.getDevicePath()))
      {
        add(getPanel());
        this.panel.add(getJTabbedPane0(), null);
        this.tabPane.add("", cd);
        this.tabPane.setSelectedIndex(0);
      }
    }*/
  }
  
  public void playVedio()
  {
    CaptureDevice cd = (CaptureDevice)this.tabPane.getComponent(0);
    if (cd == null) {
      return;
    }
    GUID guid = GUID.RGB32;
    
    int width = 768;int height = 480;
    

    VideoFormat vf = null;
    if (!cd.start(vf, false)) {
      JOptionPane.showMessageDialog(this, "加载视频出现错误，如果没有连接摄像头，请重新设置计量参数档案中是否启用摄像头参数为否");
    }
  }
  
  private UIPanel getPanel()
  {
    if (this.panel == null)
    {
      this.panel = new UIPanel();
      this.panel.setLayout(null);
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int width = (int)screensize.getWidth();
      int height = (int)screensize.getHeight();
      this.panel.setBounds(new Rectangle(0, 0, width / 2, height / 2));
      this.panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.panel;
  }
  
  private JTabbedPane getJTabbedPane0()
  {
    if (this.tabPane == null)
    {
      this.tabPane = new JTabbedPane();
      this.tabPane.setBounds(0, 0, getWidth(), getHeight());
      this.tabPane.setUI(new PerTabbedPaneUI());
    }
    return this.tabPane;
  }
  
  public String getLocalFileUrl()
  {
    return this.localFileUrl;
  }
  
  public void setLocalFileUrl(String localFileUrl)
  {
    this.localFileUrl = localFileUrl;
  }
}
