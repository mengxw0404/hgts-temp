package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

public class LedNumber
  extends Component
{
  private static final long serialVersionUID = 1L;
  private Polygon[] segmentPolygon;
  private int[][] numberSegment = { { 0, 1, 2, 3, 4, 5 }, { 1, 2 }, { 0, 1, 3, 4, 6 }, { 0, 1, 2, 3, 6 }, { 1, 2, 5, 6 }, { 0, 2, 3, 5, 6 }, { 0, 2, 3, 4, 5, 6 }, { 0, 1, 2 }, { 0, 1, 2, 3, 4, 5, 6 }, { 0, 1, 2, 3, 5, 6 }, { 7 }, { 6 } };
  private int[] div = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
  private Image[] numberImage;
  private Color fontColor = Color.green;
  private Color bgColor = Color.black;
  private Color maskColor = Color.darkGray;
  private int dWidth = 25;
  private int dHeight = 42;
  
  public LedNumber()
  {
    init();
  }
  
  public LedNumber(Color fc)
  {
    this.fontColor = fc;
    init();
  }
  
  public LedNumber(Color fc, Color bgc)
  {
    this.fontColor = fc;
    this.bgColor = bgc;
    init();
  }
  
  public LedNumber(Color fc, Color bgc, Color mc)
  {
    this.fontColor = fc;
    this.bgColor = bgc;
    this.maskColor = mc;
    init();
  }
  
  public void setBackGround(Color bgc)
  {
    this.bgColor = bgc;
  }
  
  public void setFontColor(Color fc)
  {
    this.fontColor = fc;
  }
  
  public void setMaskColor(Color mc)
  {
    this.maskColor = mc;
  }
  
  public void setDigitalWidth(int dWidth)
  {
    this.dWidth = dWidth;
  }
  
  public void setDigitalHeight(int dHeight)
  {
    this.dHeight = dHeight;
  }
  
  public Image getLedImage(int dg, int bound)
  {
    dg %= this.div[bound];
    Image image = new BufferedImage(this.dWidth * bound, this.dHeight, 1);
    Graphics g = image.getGraphics();
    
    bound--;
    for (int i = bound; i >= 0; i--)
    {
      g.drawImage(this.numberImage[(dg / this.div[i])], (bound - i) * this.dWidth, 0, this);
      dg %= this.div[i];
    }
    return image;
  }
  
  public Image getLedImage(double weight, int bound, int pre)
  {
    Image image = new BufferedImage(this.dWidth * bound, this.dHeight, 1);
    Graphics g = image.getGraphics();
    
    int ledmax = bound - 1;
    
    String number = "" + weight;
    if (weight < 0.0D) {
      number = number.substring(1);
    }
    int zhengshu = 0;
    int xiaoshu = 0;
    int point = number.indexOf(".");
    if (point == -1)
    {
      zhengshu = new Integer(number).intValue();
      xiaoshu = 0;
    }
    else
    {
      zhengshu = new Integer(number.substring(0, point)).intValue();
    }
    int zhengshulength = ("" + zhengshu).length();
    if (weight < 0.0D) {
      g.drawImage(this.numberImage[11], (ledmax - pre - 1 - (zhengshulength - 1) - 1) * this.dWidth, 0, this);
    }
    while (zhengshulength > 0)
    {
      int disnumber = 0;
      disnumber = (int)(zhengshu / Math.pow(10.0D, zhengshulength - 1));
      zhengshu = (int)(zhengshu % Math.pow(10.0D, zhengshulength - 1));
      g.drawImage(this.numberImage[disnumber], (ledmax - pre - 1 - (zhengshulength - 1)) * this.dWidth, 0, this);
      zhengshulength--;
    }
    if (number.substring(point + 1).length() > pre) {
      xiaoshu = new Integer(number.substring(point + 1, point + 1 + pre)).intValue();
    } else if (number.substring(point + 1).length() < pre) {
      xiaoshu = (int)(new Integer(number.substring(point + 1)).intValue() * Math.pow(10.0D, pre - number.substring(point + 1).length()));
    } else {
      xiaoshu = new Integer(number.substring(point + 1)).intValue();
    }
    g.drawImage(this.numberImage[10], (ledmax - pre) * this.dWidth, 0, this);
    while (pre > 0)
    {
      int disnumber = 0;
      disnumber = (int)(xiaoshu / Math.pow(10.0D, pre - 1));
      xiaoshu = (int)(xiaoshu % Math.pow(10.0D, pre - 1));
      
      g.drawImage(this.numberImage[disnumber], (ledmax - (pre - 1)) * this.dWidth, 0, this);
      pre--;
    }
    return image;
  }
  
  public void init()
  {
    this.segmentPolygon = new Polygon[9];
    this.numberImage = new Image[12];
    setNumberPolygon();
    setNumberImage();
  }
  
  public void setNumberImage()
  {
    int i = 0;
    int j = 0;
    while (i < 12)
    {
      this.numberImage[i] = new BufferedImage(30, 40, 1);
      Graphics g = this.numberImage[i].getGraphics();
      g.setColor(this.bgColor);
      g.fillRect(0, 0, 15, 20);
      
      g.setColor(Color.black);
      j = 0;
      while (j < this.numberSegment[8].length)
      {
        int k = this.numberSegment[8][j];
        g.fillPolygon(this.segmentPolygon[k]);
        j++;
      }
      g.setColor(this.fontColor);
      j = 0;
      while (j < this.numberSegment[i].length)
      {
        int k = this.numberSegment[i][j];
        g.fillPolygon(this.segmentPolygon[k]);
        j++;
      }
      i++;
    }
  }
  
  public void setNumberPolygon()
  {
    int mid = this.dHeight / 2 + 1;
    this.segmentPolygon[0] = new Polygon();
    this.segmentPolygon[0].addPoint(2, 1);
    this.segmentPolygon[0].addPoint(this.dWidth - 2, 1);
    this.segmentPolygon[0].addPoint(this.dWidth - 5, 4);
    this.segmentPolygon[0].addPoint(4, 4);
    this.segmentPolygon[1] = new Polygon();
    this.segmentPolygon[1].addPoint(this.dWidth - 1, 1);
    this.segmentPolygon[1].addPoint(this.dWidth - 1, mid - 1);
    this.segmentPolygon[1].addPoint(this.dWidth - 2, mid - 1);
    this.segmentPolygon[1].addPoint(this.dWidth - 4, mid - 3);
    this.segmentPolygon[1].addPoint(this.dWidth - 4, 4);
    this.segmentPolygon[2] = new Polygon();
    this.segmentPolygon[2].addPoint(this.dWidth - 1, mid);
    this.segmentPolygon[2].addPoint(this.dWidth - 1, this.dHeight - 2);
    this.segmentPolygon[2].addPoint(this.dWidth - 4, this.dHeight - 5);
    this.segmentPolygon[2].addPoint(this.dWidth - 4, mid + 1);
    this.segmentPolygon[2].addPoint(this.dWidth - 3, mid);
    this.segmentPolygon[3] = new Polygon();
    this.segmentPolygon[3].addPoint(this.dWidth - 2, this.dHeight - 1);
    this.segmentPolygon[3].addPoint(1, this.dHeight - 1);
    this.segmentPolygon[3].addPoint(4, this.dHeight - 4);
    this.segmentPolygon[3].addPoint(this.dWidth - 4, this.dHeight - 4);
    this.segmentPolygon[4] = new Polygon();
    this.segmentPolygon[4].addPoint(1, this.dHeight - 2);
    this.segmentPolygon[4].addPoint(1, mid);
    this.segmentPolygon[4].addPoint(3, mid);
    this.segmentPolygon[4].addPoint(4, mid + 1);
    this.segmentPolygon[4].addPoint(4, this.dHeight - 5);
    this.segmentPolygon[5] = new Polygon();
    this.segmentPolygon[5].addPoint(1, mid - 1);
    this.segmentPolygon[5].addPoint(1, 1);
    this.segmentPolygon[5].addPoint(4, 4);
    this.segmentPolygon[5].addPoint(4, mid - 3);
    this.segmentPolygon[5].addPoint(2, mid - 1);
    this.segmentPolygon[6] = new Polygon();
    this.segmentPolygon[6].addPoint(3, mid - 1);
    this.segmentPolygon[6].addPoint(4, mid - 2);
    this.segmentPolygon[6].addPoint(this.dWidth - 4, mid - 2);
    this.segmentPolygon[6].addPoint(this.dWidth - 3, mid - 1);
    this.segmentPolygon[6].addPoint(this.dWidth - 5, mid + 1);
    this.segmentPolygon[6].addPoint(4, mid + 1);
    this.segmentPolygon[7] = new Polygon();
    this.segmentPolygon[7].addPoint(this.dWidth / 2 - 4, this.dHeight - 1);
    this.segmentPolygon[7].addPoint(this.dWidth / 2 + 4, this.dHeight - 1);
    this.segmentPolygon[7].addPoint(this.dWidth / 2 + 4, this.dHeight - 10);
    this.segmentPolygon[7].addPoint(this.dWidth / 2 - 4, this.dHeight - 10);
  }
}
