package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.pub.lang.UFDouble;

public class LedNumberPanel
  extends UIPanel
{
  private JLabel lable;
  private LedNumber led;
  private UFDouble dispnumber;
  private int bound;
  private int pre;
  
  public LedNumberPanel(int bound, int pre)
  {
    init(bound, pre);
  }
  
  private void init(int bound, int pre)
  {
    this.bound = bound;
    this.pre = pre;
    this.led = new LedNumber();
    this.lable = new JLabel();
    this.lable.setIcon(new ImageIcon(this.led.getLedImage(new UFDouble(0).doubleValue(), bound, pre)));
    setBackground(Color.black);
    setLayout(new BorderLayout());
    add(this.lable, "East");
  }
  
  public void setDispnumber(UFDouble dispnumber)
  {
    this.dispnumber = dispnumber;
    this.lable.setIcon(new ImageIcon(this.led.getLedImage(dispnumber.doubleValue(), this.bound, this.pre)));
  }
  
  public void setBound(int bound)
  {
    this.bound = bound;
  }
  
  public void setPre(int pre)
  {
    this.pre = pre;
  }
}
