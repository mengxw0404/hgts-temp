package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import nc.vo.jcom.lang.StringUtil;

public class WeightSurface
  extends JPanel
  implements Runnable
{
  private static final long serialVersionUID = 1L;
  public Thread thread;
  public long sleepAmount = 100L;
  private int width;
  private int height;
  private BufferedImage bimg;
  private Graphics2D big;
  private Font font = new Font("Times New Roman", 0, 11);
  private int columnInc;
  private int[] pts;
  private int ptNum;
  private float totalWeight = 80.0F;
  private float freeWeight = this.totalWeight;
  private Rectangle graphOutlineRect = new Rectangle();
  private Line2D graphLine = new Line2D.Float();
  private Color graphColor = new Color(46, 139, 87);
  private BillWorkPanel billWorkPanel;
  
  public WeightSurface(BillWorkPanel billWorkPanel)
  {
    this.billWorkPanel = billWorkPanel;
    setBackground(Color.BLACK);
  }
  
  public void paint(Graphics g)
  {
    if (this.big == null) {
      return;
    }
    this.big.setBackground(getBackground());
    this.big.clearRect(0, 0, this.width, this.height);
    if (this.billWorkPanel.getBillModel() == 0) {
      this.totalWeight = 30.0F;
    } else if ((this.billWorkPanel.getBillModel() == 1) || (this.billWorkPanel.getBillModel() == 2)) {
      this.totalWeight = 80.0F;
    }
    String weight = this.billWorkPanel.getParents().getHardWarePanel()
      .getMeasPanel().getWeightField().getText();
    if (StringUtil.isEmpty(weight)) {
      weight = "0";
    }
    this.freeWeight = (this.totalWeight - new java.lang.Float(weight).floatValue());

    this.big.setColor(this.graphColor);
    this.graphOutlineRect.setRect(0.0D, 0.0D, this.width, this.height);
    this.big.draw(this.graphOutlineRect);
    
    int graphRow = this.height / 10;
    for (int j = 0; j <= this.width + this.height; j += graphRow)
    {
      this.graphLine.setLine(0.0D, j, this.width, j);
      this.big.draw(this.graphLine);
    }
    int graphColumn = this.width / 15;
    if (this.columnInc == 0) {
      this.columnInc = graphColumn;
    }
    for (int j = this.columnInc; j < this.width; j += graphColumn)
    {
      this.graphLine.setLine(j, 0.0D, j, this.height);
      this.big.draw(this.graphLine);
    }
    this.columnInc -= 1;
    if (this.pts == null)
    {
      this.pts = new int[this.width];
      this.ptNum = 0;
    }
    else if (this.pts.length != this.width)
    {
      int[] tmp = null;
      if (this.ptNum < this.width)
      {
        tmp = new int[this.ptNum];
        System.arraycopy(this.pts, 0, tmp, 0, tmp.length);
      }
      else
      {
        tmp = new int[this.width];
        System.arraycopy(this.pts, this.pts.length - tmp.length, tmp, 0, 
          tmp.length);
        this.ptNum = (tmp.length - 2);
      }
      this.pts = new int[this.width];
      System.arraycopy(tmp, 0, this.pts, 0, tmp.length);
    }
    else
    {
      this.big.setColor(Color.YELLOW);
      if (this.ptNum > this.pts.length - 1) {
        this.ptNum = (this.pts.length - 1);
      }
      this.pts[this.ptNum] = ((int)(this.height * (this.freeWeight / this.totalWeight)));
      int j = this.width - this.ptNum;
      for (int k = 0; k < this.ptNum; j++)
      {
        if (k != 0) {
          if (this.pts[k] != this.pts[(k - 1)]) {
            this.big.drawLine(j - 1, this.pts[(k - 1)], j, this.pts[k]);
          } else {
            this.big.fillRect(j, this.pts[k], 1, 1);
          }
        }
        k++;
      }
      if (this.ptNum + 2 == this.pts.length)
      {
        for (int j1 = 1; j1 < this.ptNum; j1++) {
          this.pts[(j1 - 1)] = this.pts[j1];
        }
        this.ptNum -= 1;
      }
      else
      {
        this.ptNum += 1;
      }
    }
    g.drawImage(this.bimg, 0, 0, this);
  }
  
  public void start()
  {
    this.thread = new Thread(this);
    this.thread.setPriority(1);
    this.thread.setName("WeightMonitor");
    this.thread.start();
  }
  
  public synchronized void stop()
  {
    this.thread = null;
    notify();
  }
  
  public void run()
  {
    Thread me = Thread.currentThread();
    while (this.thread == me) {
      if (getSize().width == 0)
      {
        try
        {
          Thread.sleep(500L);
        }
        catch (InterruptedException e)
        {
          return;
        }
      }
      else
      {
        Dimension d = getSize();
        if ((d.width != this.width) || (d.height != this.height))
        {
          this.width = d.width;
          this.height = d.height;
          this.bimg = ((BufferedImage)createImage(this.width, this.height));
          this.big = this.bimg.createGraphics();
          this.big.setFont(this.font);
        }
        repaint();
        try
        {
          Thread.sleep(this.sleepAmount);
        }
        catch (InterruptedException e)
        {
          break;
        }
      }
    }
    this.thread = null;
  }
}
