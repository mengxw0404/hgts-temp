package nc.ui.hgts.ponder.ace.base;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;

public class StatuDesginPanel extends UIPanel
{
	public JTextField TwoInfraredShow = null;
	public JTextField OneInfraredShow = null;
 
  public StatuDesginPanel()
  {
    initlize();
  }
  
  public void initlize()
  {
    GridLayout gridLayout = new GridLayout();
    gridLayout.setRows(1);
    gridLayout.setHgap(3);
    setLayout(gridLayout);
    add(getOneIfraredFiled());
    add(getTwoIfraredFiled());
    setBackground(new Color(102, 102, 102));
  }
  
  public UIPanel getOneIfraredFiled()
  { 
	  UIPanel maxPanel = new UIPanel();
	  maxPanel.setLayout(new FlowLayout());
	  maxPanel.add(createLabel("第一对射状态"));
	  maxPanel.add(getOneInfraredTextShow());
	  maxPanel.setBackground(new Color(51, 51, 51));
	  return maxPanel;
  }
  private JLabel createLabel(String text)
  {
    JLabel label = new JLabel();
    label.setFont(new Font("宋体", 1, 12));
    label.setLayout(new FlowLayout());
    label.setText(text);
    label.setBackground(new Color(51, 51, 51));
    label.setForeground(Color.green);
    
    return label;
  }
  private JTextField getOneInfraredTextShow()
  {
    if (this.OneInfraredShow == null) {
      this.OneInfraredShow = getOneInfraredText();
    }
    return this.OneInfraredShow;
  } 
  private UITextField getOneInfraredText()
  {
    UITextField field = new UITextField();
    field.setFont(new Font("宋体", 1, 12));
    field.setForeground(Color.red);
    field.setText("未知");
    field.setColumns(5);
    field.setEditable(false);
    field.setHorizontalAlignment(4);
    field.setBackground(new Color(51, 51, 51));
    
    return field;
  }
  
  public UIPanel getTwoIfraredFiled()
  { 
	  UIPanel maxPanel = new UIPanel();
	  maxPanel.setLayout(new FlowLayout());
	  maxPanel.add(createTwoLabel("第二对射状态"));
	  maxPanel.add(getTwoInfraredTextShow());
	  maxPanel.setBackground(new Color(51, 51, 51));
	  return maxPanel;
  }
  private JLabel createTwoLabel(String text)
  {
    JLabel label = new JLabel();
    label.setFont(new Font("宋体", 1, 12));
    label.setLayout(new FlowLayout());
    label.setText(text);
    label.setBackground(new Color(51, 51, 51));
    label.setForeground(Color.green);
    
    return label;
  }
  private JTextField getTwoInfraredTextShow()
  {
    if (this.TwoInfraredShow == null) {
      this.TwoInfraredShow = getTwoInfraredText();
    }
    return this.TwoInfraredShow;
  } 
  private UITextField getTwoInfraredText()
  {
    UITextField field = new UITextField();
    field.setFont(new Font("宋体", 1, 12));
    field.setForeground(Color.red);
    field.setText("未知");
    field.setColumns(5);
    field.setEditable(false);
    field.setHorizontalAlignment(4);
    field.setBackground(new Color(51, 51, 51));
    
    return field;
  }
}
