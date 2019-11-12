package nc.ui.hgts.ponder.ace.base;

import java.awt.event.MouseListener;
import nc.ui.pub.beans.UIDialog;

public class WeightMonitorDlg
  extends UIDialog
{
  private static final long serialVersionUID = 1L;
  private BillWorkPanel billWorkPanel;
  private WeightMonitor monitor;
  
  public WeightMonitorDlg(BillWorkPanel billWorkPanel)
  {
    super(billWorkPanel);
    this.billWorkPanel = billWorkPanel;
    init();
  }
  
  private void init()
  {
    this.monitor = new WeightMonitor(this.billWorkPanel);
    MouseListener[] mouseListeners = this.monitor.getMouseListeners();
    for (MouseListener mouseListener : mouseListeners) {
      this.monitor.removeMouseListener(mouseListener);
    }
    add(this.monitor);
    
    setTitle("过磅重量变化波形图");
    setResizable(true);
  }
  
  protected void close()
  {
    this.monitor.surf.stop();
    super.close();
  }
  
  public WeightMonitor getMonitor()
  {
    return this.monitor;
  }
}
