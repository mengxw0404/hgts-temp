package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import nc.ui.pub.beans.UIPanel;

public class VedioPanel
  extends UIPanel
{
  private JPanel vedioPanel1 = null;
  private JPanel vedioPanel2 = null;
  private JPanel vedioPanel3 = null;
  private JPanel vedioPanel4 = null;
  
  public VedioPanel()
  {
    initlize();
  }
  
  private void initlize()
  {
    setLayout(new GridLayout(2, 2, 5, 5));
   
    add(getVedioPanel1());
    add(getVedioPanel2());
    add(getVedioPanel3());
    add(getVedioPanel4());
  }
  
  private JPanel getVedioPanel1()
  {
    if (this.vedioPanel1 == null)
    {
      this.vedioPanel1 = new JPanel();
      this.vedioPanel1.setLayout(new GridBagLayout());
      this.vedioPanel1.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.vedioPanel1;
  }
  
  private JPanel getVedioPanel2()
  {
    if (this.vedioPanel2 == null)
    {
      this.vedioPanel2 = new JPanel();
      this.vedioPanel2.setLayout(new GridBagLayout());
      this.vedioPanel2.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.vedioPanel2;
  }
  
  private JPanel getVedioPanel3()
  {
    if (this.vedioPanel3 == null)
    {
      this.vedioPanel3 = new JPanel();
      this.vedioPanel3.setLayout(new GridBagLayout());
      this.vedioPanel3.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.vedioPanel3;
  }
  
  private JPanel getVedioPanel4()
  {
    if (this.vedioPanel4 == null)
    {
      this.vedioPanel4 = new JPanel();
      this.vedioPanel4.setLayout(new GridBagLayout());
      this.vedioPanel4.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
    }
    return this.vedioPanel4;
  }
}
