package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import kobetool.dshowwrapper.CaptureDevice;
import kobetool.dshowwrapper.GUID;
import kobetool.dshowwrapper.PerTabbedPaneUI;
import kobetool.dshowwrapper.VideoFormat;
import nc.itf.hgts.common.YGUtils;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIPanel;
import nc.vo.pub.BusinessException;

public class XYGVideoPanel
  extends UIPanel
{
  private HardWarePanel parents = null;
  private JPanel VideoPanel1 = null;
  private JPanel VideoPanel2 = null;
  private JPanel VideoPanel3 = null;
  private JPanel VideoPanel4 = null;
  public JTabbedPane tabPane0 = null;
  public JTabbedPane tabPane1 = null;
  public JTabbedPane tabPane2 = null;
  public JTabbedPane tabPane3 = null;
  
  public XYGVideoPanel()
  {
    initlize();
  }
  
  private void initlize()
  {
    setLayout(null);
    setBounds(new Rectangle(0, 251, 338, 400));
    setName("VideoPanel");
  }
  
  public JPanel getVideoPanel1()
  {
    if (this.VideoPanel1 == null)
    {
      this.VideoPanel1 = new JPanel();
      this.VideoPanel1.setLayout(null);
      this.VideoPanel1.setBackground(null);
      

      this.VideoPanel1.setBounds(new Rectangle(5, 6, 150, 114));
      
      this.VideoPanel1.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
      
      this.VideoPanel1.setVisible(true);
    }
    return this.VideoPanel1;
  }
  
  private JPanel getVideoPanel2()
  {
    if (this.VideoPanel2 == null)
    {
      this.VideoPanel2 = new JPanel();
      this.VideoPanel2.setLayout(null);
      

      this.VideoPanel2.setBounds(new Rectangle(180, 6, 150, 114));
      
      this.VideoPanel2.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.VideoPanel2;
  }
  
  private JPanel getVideoPanel3()
  {
    if (this.VideoPanel3 == null)
    {
      this.VideoPanel3 = new JPanel();
      this.VideoPanel3.setLayout(null);
      

      this.VideoPanel3.setBounds(new Rectangle(5, 165, 150, 114));
      
      this.VideoPanel3.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.VideoPanel3;
  }
  
  private JPanel getVideoPanel4()
  {
    if (this.VideoPanel4 == null)
    {
      this.VideoPanel4 = new JPanel();
      this.VideoPanel4.setLayout(null);
      

      this.VideoPanel4.setBounds(new Rectangle(180, 165, 150, 114));
      
      this.VideoPanel4.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.VideoPanel4;
  }
  
  private JTabbedPane getJTabbedPane0()
  {
    if (this.tabPane0 == null)
    {
      this.tabPane0 = new JTabbedPane();
      

      this.tabPane0.setBounds(0, 0, 150, 114);
      
      this.tabPane0.setUI(new PerTabbedPaneUI());
    }
    return this.tabPane0;
  }
  
  private JTabbedPane getJTabbedPane1()
  {
    if (this.tabPane1 == null)
    {
      this.tabPane1 = new JTabbedPane();
      

      this.tabPane1.setBounds(0, 0, 150, 114);
      
      this.tabPane1.setUI(new PerTabbedPaneUI());
    }
    return this.tabPane1;
  }
  
  private JTabbedPane getJTabbedPane2()
  {
    if (this.tabPane2 == null)
    {
      this.tabPane2 = new JTabbedPane();
      

      this.tabPane2.setBounds(0, 0, 150, 114);
      
      this.tabPane2.setUI(new PerTabbedPaneUI());
    }
    return this.tabPane2;
  }
  
  private JTabbedPane getJTabbedPane3()
  {
    if (this.tabPane3 == null)
    {
      this.tabPane3 = new JTabbedPane();
      

      this.tabPane3.setBounds(0, 0, 150, 114);
      
      this.tabPane3.setUI(new PerTabbedPaneUI());
    }
    return this.tabPane3;
  }
  
  public void loadVideo()
  {
    int i = 0;
    for (CaptureDevice cd : CaptureDevice.enumDevices())
    {
      if (i == 0)
      {
        add(getVideoPanel1());
        getVideoPanel1().add(getJTabbedPane0(), null);
        BufferedImage zimu = new BufferedImage(100, 50, 5);
        
        Graphics2D g2 = zimu.createGraphics();
        g2.drawImage(zimu, 0, 0, 100, 50, null);
        

        g2.setColor(Color.black);
        g2.setFont(new Font("宋体", 1, 10));
        g2.drawString("32432esdf", 15, 30);
        g2.dispose();
        
        cd.paint(g2);
        cd.repaint();
        this.tabPane0.add("", cd);
        
        this.tabPane0.setSelectedIndex(0);
        
        this.tabPane0.getComponent(0).addMouseListener(new PicViewListener(cd));
      }
      if (i == 1)
      {
        add(getVideoPanel2());
        getVideoPanel2().add(getJTabbedPane1(), null);
        this.tabPane1.add("", cd);
        
        this.tabPane1.setSelectedIndex(0);
        this.tabPane1.getComponent(0).addMouseListener(new PicViewListener(cd));
      }
      if (i == 2)
      {
        add(getVideoPanel3());
        getVideoPanel3().add(getJTabbedPane2(), null);
        this.tabPane2.add("", cd);
        this.tabPane2.setSelectedIndex(0);
        this.tabPane2.getComponent(0).addMouseListener(new PicViewListener(cd));
      }
      if (i == 3)
      {
        add(getVideoPanel4());
        getVideoPanel4().add(getJTabbedPane3(), null);
        this.tabPane3.add("", cd);
        this.tabPane3.setSelectedIndex(0);
        this.tabPane3.getComponent(0).addMouseListener(new PicViewListener(cd));
      }
      i++;
    }
  }
  
  public class PicViewListener
    extends MouseAdapter
  {
    CaptureDevice cd;
    String localFileUrl = null;
    
    PicViewListener(CaptureDevice adaptee)
    {
      this.cd = adaptee;
    }
    
    public void mouseClicked(final MouseEvent e)
    {
      super.mouseClicked(e);
      OrgPicDlg orgPicDlg = null;
      String devicepath = ((CaptureDevice)e.getComponent()).getDevicePath();
      ((CaptureDevice)e.getComponent()).stop();
      try
      {
        orgPicDlg = new OrgPicDlg(devicepath);
      }
      catch (IOException e1)
      {
        JOptionPane.showMessageDialog(null, e1.getMessage());
      }
      catch (BusinessException ex)
      {
        JOptionPane.showMessageDialog(null, ex.getMessage());
      }
      orgPicDlg.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent ex)
        {
          XYGVideoPanel.this.playVideoNew((CaptureDevice)e.getComponent());
        }
      });
      orgPicDlg.showModal();
    }
  }
  
  public void playVideoNew(CaptureDevice cdnew)
  {
    CaptureDevice cd = cdnew;
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
  
  public void playVideo()
  {
    for (int i = 0; i < getComponentCount(); i++)
    {
      JPanel vpanel = (JPanel)getComponent(i);
      JTabbedPane tpanel = (JTabbedPane)vpanel.getComponent(0);
      CaptureDevice cd = (CaptureDevice)tpanel.getComponent(0);
      if (cd == null) {
        return;
      }
      GUID guid = GUID.YUY2;
      

      int width = 640;int height = 400;
      
      VideoFormat vf = null;
      if (!cd.start(vf, false)) {
        JOptionPane.showMessageDialog(this, "加载视频出现错误，如果没有连接摄像头，请重新设置计量参数档案中是否启用摄像头参数为否");
      }
    }
  }
  
  protected String[] grapVideo(int operation, int imageType)
  {
    int type = translateImageType(imageType);
    
    String[] filenames = new String[4];
    for (int i = 0; i < getComponentCount(); i++)
    {
      JPanel vpanel = (JPanel)getComponent(i);
      JTabbedPane tpanel = (JTabbedPane)vpanel.getComponent(0);
      CaptureDevice device = (CaptureDevice)tpanel.getComponent(0);
      if (device == null) {
        return null;
      }
      VideoFormat fmt = device.getFormat();
      if (fmt == null) {
        return null;
      }
      ByteBuffer buf = device.grab(null);
      
      BufferedImage img = byteBuffertoImg(fmt, buf, type);
      

      writeIntoFile(operation, filenames, i, img);
    }
    return filenames;
  }
  
  private int translateImageType(int imageType)
  {
   /* if (imageType == MeasureitfVO.IMAGETYPE_RGB24.intValue()) {
      return 1;
    }
    if (imageType == MeasureitfVO.IMAGETYPE_RGB32.intValue()) {
      return 1;
    }
    if (imageType == MeasureitfVO.IMAGETYPE_RGB555.intValue()) {
      return 9;
    }*/
    return 0;
  }
  
  private void writeIntoFile(int operation, String[] filenames, int i, BufferedImage img)
  {
    Date date = new Date();
    
    String yyyymmdd = date.getYear() + 1900 + "" + (date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : Integer.valueOf(date.getMonth() + 1)) + "" + (date.getDate() < 10 ? "0" + date.getDate() : Integer.valueOf(date.getDate()));
    
    String hhmmss = (date.getHours() < 10 ? "0" + date.getHours() : Integer.valueOf(date.getHours())) + "" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : Integer.valueOf(date.getMinutes())) + "" + (date.getSeconds() < 10 ? "0" + date.getSeconds() : Integer.valueOf(date.getSeconds()));
    
    String fileName = YGUtils.getTSXYG06() + "\\" + ClientEnvironment.getInstance().getUser().getPk_org() + "_" + yyyymmdd + hhmmss + "_" + i + "_" + operation;
    File file = new File(fileName);
    String fn = file.getName().toUpperCase();
    if ((!fn.endsWith(".JPG")) && (!fn.endsWith(".JPEG"))) {
      file = new File(file.getAbsolutePath() + ".jpg");
    }
    try
    {
      ImageIO.write(img, "jpg", file);
    }
    catch (IOException e1)
    {
      fileName = null;
      JOptionPane.showMessageDialog(this, e1.getMessage());
    }
    filenames[i] = file.getName();
  }
  
  private BufferedImage byteBuffertoImg(VideoFormat fmt, ByteBuffer buf, int type)
  {
    BufferedImage img = new BufferedImage(fmt.getWidth(), fmt.getHeight(), type);
    if ((type == 1) || (type == 2) || (type == 3)) {
      for (int x = 0; x < fmt.getWidth(); x++) {
        for (int y = 0; y < fmt.getHeight(); y++)
        {
          int p = (x + fmt.getWidth() * y) * 4;
          int r = buf.get(p) & 0xFF;
          int g = buf.get(p + 1) & 0xFF;
          int b = buf.get(p + 2) & 0xFF;
          img.setRGB(x, fmt.getHeight() - (y + 1), 0xFF000000 | r << 16 | g << 8 | b);
        }
      }
    } else if (type == 5) {
      for (int x = 0; x < fmt.getWidth(); x++) {
        for (int y = 0; y < fmt.getHeight(); y++)
        {
          int p = (x + fmt.getWidth() * y) * 3;
          int b = buf.get(p) & 0xFF;
          int g = buf.get(p + 1) & 0xFF;
          int r = buf.get(p + 2) & 0xFF;
          img.setRGB(x, fmt.getHeight() - (y + 1), 0xFF000000 | r << 16 | g << 8 | b);
        }
      }
    } else if (type == 9) {
      for (int x = 0; x < fmt.getWidth(); x++) {
        for (int y = 0; y < fmt.getHeight(); y++)
        {
          int p = (x + fmt.getWidth() * y) * 2;
          int rgb555 = buf.get(p) << 8 | buf.get(p + 1) & 0xFF;
          int r = rgb555 >> 7 & 0xF8;
          int g = rgb555 >> 2 & 0xF8;
          int b = rgb555 << 3 & 0xF8;
          img.setRGB(x, fmt.getHeight() - (y + 1), 0xFF000000 | r << 16 | g << 8 | b);
        }
      }
    }
    return img;
  }
}
