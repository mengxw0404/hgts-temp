package nc.ui.hgts.ponder.ace.base;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class WeightMonitor
  extends JPanel
  implements MouseListener
{
  public WeightSurface surf;
  boolean doControls;
  BillWorkPanel billWorkPanel;
  WeightMonitorDlg monitorDlg;
  
  public WeightMonitor(BillWorkPanel billWorkPanel)
  {
    this.billWorkPanel = billWorkPanel;
    setLayout(new BorderLayout());
    add(this.surf = new WeightSurface(billWorkPanel));
    addMouseListener(this);
  }
  
  public void mouseClicked(MouseEvent e)
  {
    getMonitorDlg().getMonitor().surf.start();
    getMonitorDlg().showModal();
  }
  
  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void mousePressed(MouseEvent e) {}
  
  public void mouseReleased(MouseEvent e) {}
  
  public WeightMonitorDlg getMonitorDlg()
  {
    if (this.monitorDlg == null) {
      this.monitorDlg = new WeightMonitorDlg(this.billWorkPanel);
    }
    return this.monitorDlg;
  }
}
