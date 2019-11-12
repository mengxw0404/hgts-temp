package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.vo.pub.BusinessException;

/**
 * 过磅异常，暂无功能调用
 */
public class CausingSplitDialog
  extends UIDialog
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  protected UIPanel contentPanel;
  protected UIPanel centerPanel;
  protected UIPanel bottomPanel;
  protected UIButton btnOK;
  protected UITextField causingField;
  protected UIComboBox causingComBox;
  private String reason;
  private List<String> existHistTarelst = new ArrayList();
  
  public String getReason()
  {
    return this.reason;
  }
  
  public void setReason(String reason)
  {
    this.reason = reason;
  }
  
  private int HGTS_CAUSING_TYPE1 = 0;
  private int HGTS_CAUSING_TYPE2 = 1;
  
  public CausingSplitDialog(int num)
  {
    init(num);
  }
  
  private void init(int num)
  {
    setDefaultCloseOperation(2);
    setSize(360, 150);
    setResizable(true);
    setLocation(666, 250);
    if (num == this.HGTS_CAUSING_TYPE1)
    {//皮重异常
      setName("histTare");
      setTitle("原因输入");
      setContentPane(getUIDialogContentPane1());
    }
    else if (num == this.HGTS_CAUSING_TYPE2)
    {//作废原因
      setName("delete");
      setTitle("原因输入");
      setContentPane(getUIDialogContentPanel2());
    }
    setDefaultCloseOperation(0);
  }
  
  protected void close()
  {
    if (getResult() == 1) {
      super.close();
    }
  }
  
  public void actionPerformed(ActionEvent e) {}
  
  private UIPanel getUIDialogContentPane1()
  {
    if (this.contentPanel == null)
    {
      this.contentPanel = new UIPanel();
      this.contentPanel.setName("contentPanel");
      this.contentPanel.setLayout(new BorderLayout());
      this.contentPanel.add(getCenterPanel1(), "Center");
      this.contentPanel.add(getBottomPanel(this.HGTS_CAUSING_TYPE1), "South");
    }
    return this.contentPanel;
  }
  
  private UIPanel getUIDialogContentPanel2()
  {
    if (this.contentPanel == null)
    {
      this.contentPanel = new UIPanel();
      this.contentPanel.setName("contentPanel");
      this.contentPanel.setLayout(new BorderLayout());
      this.contentPanel.add(getCenterPanel2(), "Center");
      this.contentPanel.add(getBottomPanel(this.HGTS_CAUSING_TYPE2), "South");
    }
    return this.contentPanel;
  }
  
  public UIPanel getCenterPanel1()
  {
    if (this.centerPanel == null)
    {
      this.centerPanel = new UIPanel();
      this.centerPanel.setLayout(null);
      
      JLabel reasonLabel = new JLabel("皮重异常原因:");
      
      reasonLabel.setBounds(new Rectangle(13, 40, 124, 27));
      getCausingComBox().setBounds(new Rectangle(100, 40, 200, 27));
      this.centerPanel.add(reasonLabel, null);
      this.centerPanel.add(getCausingComBox(), null);
    }
    return this.centerPanel;
  }
  
  public UIPanel getCenterPanel2()
  {
    if (this.centerPanel == null)
    {
      this.centerPanel = new UIPanel();
      this.centerPanel.setLayout(null);
      
      JLabel reasonLabel = new JLabel("作废原因:");
      
      reasonLabel.setBounds(new Rectangle(13, 40, 124, 27));
      getCausingField().setBounds(new Rectangle(80, 40, 200, 27));
      this.centerPanel.add(reasonLabel, null);
      this.centerPanel.add(getCausingField(), null);
    }
    return this.centerPanel;
  }
  
  public UIPanel getBottomPanel(int type)
  {
    if (this.bottomPanel == null)
    {
      this.bottomPanel = new UIPanel();
      this.bottomPanel.add(getBtnOK(type));
      getBtnOK(type).addActionListener(this);
    }
    return this.bottomPanel;
  }
  
  public UIButton getBtnOK(final int type)
  {
    if (this.btnOK == null)
    {
      this.btnOK = new UIButton();
      this.btnOK.setName("submit");
      this.btnOK.setText("确定");
      this.btnOK.setEnabled(true);
      this.btnOK.addKeyListener(new KeyAdapter()
      {
        public void keyReleased(KeyEvent e)
        {
          CausingSplitDialog.this.onBtnOK(type);
        }
      });
      this.btnOK.addMouseListener(new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)
        {
          CausingSplitDialog.this.onBtnOK(type);
        }
      });
      this.btnOK.addActionListener(this);
    }
    return this.btnOK;
  }
  
  private void onBtnOK(int type)
  {
    if (type == this.HGTS_CAUSING_TYPE1)
    {
      if ((getCausingComBox().getSelectdItemValue() == null) || ("".equals(getCausingComBox().getSelectdItemValue())))
      {
        JOptionPane.showMessageDialog(this, "皮重异常原因不能为空！", "提示", 1);
        return;
      }
      setReason(getCausingComBox().getSelectdItemValue().toString());
    }
    else if (type == this.HGTS_CAUSING_TYPE2)
    {
      if ((getCausingField().getText() == null) || ("".equals(getCausingField().getText())))
      {
        JOptionPane.showMessageDialog(this, "作废原因不能为空！", "提示", 1);
        return;
      }
      setReason(getCausingField().getText());
    }
    setResult(1);
    close();
  }
  
  public UITextField getCausingField()
  {
    if (this.causingField == null)
    {
      this.causingField = new UITextField();
      this.causingField.setSize(2000, 2000);
    }
    return this.causingField;
  }
  
  public UIComboBox getCausingComBox()
  {
    if (this.causingComBox == null)
    {
      this.causingComBox = new UIComboBox();
      this.causingComBox.setEditable(true);
      getComBoxItem(this.causingComBox);
    }
    return this.causingComBox;
  }
  
  private void getComBoxItem(UIComboBox causingComBox)
  {
    IUAPQueryBS iquerybs = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class);
    try
    {
      ArrayList<Object[]> list = (ArrayList)iquerybs.executeQuery("select hgts_tarereason.vreason from hgts_tarereason", new ArrayListProcessor());
      for (int i = 0; i < list.size(); i++) {
        if ((list.get(i) != null) && 
          (((Object[])list.get(i))[0] != null))
        {
          this.existHistTarelst.add(((Object[])list.get(i))[0].toString());
          causingComBox.addItem(((Object[])list.get(i))[0].toString());
        }
      }
    }
    catch (BusinessException e)
    {
      Logger.error(e.getMessage(), e);
    }
  }
}
