package nc.ui.hgts.ponder.ace.base;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import nc.bs.pub.im.exception.BusinessException;
import nc.ui.pub.beans.UIPanel;
import nc.vo.sr.calculate.entity.CalCondVO;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API;

public class VedioPanel extends UIPanel {

	private Panel vedioPanel1 = null;
	private Panel vedioPanel2 = null;
	private Panel vedioPanel3 = null;
	private Panel vedioPanel4 = null;
	private JPopupMenu popupMenu;
	private JMenuItem menuItem1 = null;
	private JMenuItem menuItem2 = null;
	private JMenuItem menuItem3 = null;
	private JMenuItem menuItem4 = null;
	private boolean isMaxScreen = false;
	private int maxScreenNum = 0;
	private boolean isFullScreen = false;
	private static int SCREENNUM_ZERO = 0;
	private static int SCREENNUM1 = 1;
	private static int SCREENNUM2 = 2;
	private static int SCREENNUM3 = 3;
	private static int SCREENNUM4 = 4;
	private int screenCount = 0;

	private static VideoPlay videoPlay = VideoPlay.INSTANCE;
	private Map<Integer, NativeLong> realPlayReturnIDs = new HashMap();
	private Map<Integer, Integer> N_LOGINID = new HashMap();
	private Map<Integer, W32API.HWND> hwndMap = new HashMap();
	private Map<Integer, Integer> ScreenChannelMap = new HashMap();

	public VedioPanel() {
		setFullScreen(false);
		initlize();
	}

	private void initlize() {
		setLayout(new GridLayout(2, 2, 5, 5));

		add(getVedioPanel1());
		add(getVedioPanel2());
		add(getVedioPanel3());
		add(getVedioPanel4());
		addMouseListener(new SmallPanelMouseListener());
	}

	public void startRealPlay(boolean ismaxscreen, int maxnum)
			throws BusinessException {
		initlizeVideoParams();
		setMaxScreen(ismaxscreen);
		setMaxScreenNum(maxnum);
		InitNetSDK();
		login();
		realPlay(ismaxscreen, maxnum);
	}

	private void InitNetSDK() throws BusinessException {
		boolean blogin = videoPlay.CLIENT_Init(new fdissconnect(), null);
		if (!blogin) {
			BusinessException e = new BusinessException();
			e.setErrorCodeString("SDK");
			throw e;
		}
	}

	private void realPlay(boolean ismaxscreen, int maxnum) {
		realPlaySwitchorNot(ismaxscreen, maxnum, false);
	}

	private void realPlayAfterSwitch(boolean ismaxscreen, int maxnum) {
		realPlaySwitchorNot(ismaxscreen, maxnum, true);
	}

	private void realPlaySwitchorNot(boolean ismaxscreen, int maxnum,
			boolean isSwitch) {
		setMaxScreen(ismaxscreen);
		setMaxScreenNum(maxnum);
		if (ismaxscreen) {
			setVideoPanelStatus(false);
			if (!isSwitch) {
				videoPlay
						.CLIENT_StopRealPlay((NativeLong) this.realPlayReturnIDs
								.get(Integer.valueOf(maxnum)));

				this.realPlayReturnIDs.remove(Integer.valueOf(maxnum));
			}
			NativeLong realPlayReturnID = videoPlay
					.CLIENT_RealPlay(
							new NativeLong(((Integer) this.N_LOGINID
									.get(Integer.valueOf(maxnum))).intValue()),
							((Integer) this.ScreenChannelMap.get(Integer
									.valueOf(maxnum))).intValue(),
							getMainPanelHandle());
			if (!realPlayReturnID.equals(new NativeLong(0L))) {
				this.realPlayReturnIDs
						.put(Integer.valueOf(0), realPlayReturnID);
			} else if (isSwitch) {
				getBaseContainer().showWarningMessage("??????????????");
			} else {
				getBaseContainer().showWarningMessage(
						"??????????" + maxnum + "??????");
			}
		} else {
			setVideoPanelStatus(true);
			for (int i = 1; i <= this.screenCount; i++) {
				NativeLong realPlayReturnID = videoPlay
						.CLIENT_RealPlay(
								new NativeLong(((Integer) this.N_LOGINID
										.get(Integer.valueOf(i))).intValue()),
								((Integer) this.ScreenChannelMap.get(Integer
										.valueOf(i))).intValue(),
								(W32API.HWND) getHWNDMap().get(
										Integer.valueOf(i)));
				if (!realPlayReturnID.equals(new NativeLong(0L))) {
					this.realPlayReturnIDs.put(Integer.valueOf(i),
							realPlayReturnID);
				} else {
					getBaseContainer().showWarningMessage(
							"??????" + i + "??????????????????");
				}
			}
		}
	}

	private void login() throws BusinessException {
		VideoPlay.NET_DVR_DEVICEINFO deviceinfo = new VideoPlay.NET_DVR_DEVICEINFO();
		deviceinfo.byAlarmInPortNum = 4;
		deviceinfo.byAlarmOutPortNum = 4;
		deviceinfo.byChanNum = 4;
		deviceinfo.byDiskNum = 4;
		deviceinfo.byDVRType = 4;
		String TAG = "#";

		List<String> loginInfoList = new ArrayList();
		for (int i = 1; i <= this.screenCount; i++) {
			// 去读计量器中视频参数
			String loginInfo = "";// combineLoginInfo(TAG, i);
			if (loginInfoList.contains(loginInfo)) {
				for (int j = 0; j < loginInfoList.size(); j++) {
					if (loginInfo.equals(loginInfoList.get(j))) {
						this.N_LOGINID.put(Integer.valueOf(i),
								(Integer) this.N_LOGINID.get(Integer
										.valueOf(j + 1)));
					}
				}
			} else {
				loginInfoList.add(loginInfo);
				String pchDVRIP = "";// getMeasVO().getAttributeValue("video" +
										// i + "ip").toString();
				int wDVRPort = 1;// Integer.parseInt(getMeasVO().getAttributeValue("video"
									// + i + "port").toString());
				String pchUserName = "";// getMeasVO().getAttributeValue("video"
										// + i + "userid").toString();
				String pchPassword = "";// getMeasVO().getAttributeValue("video"
										// + i + "password").toString();
				int log = videoPlay.CLIENT_Login(pchDVRIP, wDVRPort,
						pchUserName, pchPassword, deviceinfo, 0);
				if (log == 0) {
					showLoginErrorReason(log, i);
				}
				this.N_LOGINID.put(Integer.valueOf(i), Integer.valueOf(log));
			}
		}
	}
	
	public W32API.HWND getMainPanelHandle()
	  {
	    W32API.HWND hwnd = new W32API.HWND(Native.getComponentPointer(this));
	    return hwnd;
	  }
	
	public Map<Integer, W32API.HWND> getHWNDMap()
	  {
	    if (((this.hwndMap == null ? 1 : 0) | (this.hwndMap.size() <= 0 ? 1 : 0)) != 0) {
	      getHWND();
	    }
	    return this.hwndMap;
	  }
	
	public void getHWND()
	  {
	    Component component = null;
	    component = getVedioPanel1();
	    while (component != null)
	    {
	      component.setVisible(true);
	      component = component.getParent();
	    }
	    int i = 1;
	    if (this.screenCount > 0)
	    {
	      this.hwndMap.put(Integer.valueOf(i++), new W32API.HWND(Native.getComponentPointer(getVedioPanel1())));
	      if (this.screenCount > 1)
	      {
	        this.hwndMap.put(Integer.valueOf(i++), new W32API.HWND(Native.getComponentPointer(getVedioPanel2())));
	        if (this.screenCount > 2)
	        {
	          this.hwndMap.put(Integer.valueOf(i++), new W32API.HWND(Native.getComponentPointer(getVedioPanel3())));
	          if (this.screenCount > 3) {
	            this.hwndMap.put(Integer.valueOf(i++), new W32API.HWND(Native.getComponentPointer(getVedioPanel4())));
	          }
	        }
	      }
	    }
	  }
	public Panel getVedioPanel1() {
		if (this.vedioPanel1 == null) {
			this.vedioPanel1 = new Panel();
			this.vedioPanel1.setVisible(true);
			this.vedioPanel1.setLayout(new GridLayout());

			this.vedioPanel1.addMouseListener(new SmallPanelMouseListener());
		}
		return this.vedioPanel1;
	}

	public Panel getVedioPanel2() {
		if (this.vedioPanel2 == null) {
			this.vedioPanel2 = new Panel();

			this.vedioPanel2.setVisible(true);
			this.vedioPanel2.setLayout(new GridLayout());
			this.vedioPanel2.addMouseListener(new SmallPanelMouseListener());
		}
		return this.vedioPanel2;
	}

	public Panel getVedioPanel3() {
		if (this.vedioPanel3 == null) {
			this.vedioPanel3 = new Panel();

			this.vedioPanel3.setVisible(true);
			this.vedioPanel3.setLayout(new GridLayout());
			this.vedioPanel3.addMouseListener(new SmallPanelMouseListener());
		}
		return this.vedioPanel3;
	}

	public Panel getVedioPanel4() {
		if (this.vedioPanel4 == null) {
			this.vedioPanel4 = new Panel();

			this.vedioPanel4.setVisible(true);
			this.vedioPanel4.setLayout(new GridLayout());
			this.vedioPanel4.addMouseListener(new SmallPanelMouseListener());
		}
		return this.vedioPanel4;
	}

	// 获取视频参数
	private void initlizeVideoParams() {
		// 获取摄像头参数（IP，端口，登陆名，密码）
		// checkMeasDocInfo(getMeasVO());
		// 设置屏幕数
		setScreenCount(1);
		// setScreenCount(Integer.parseInt(getMeasVO().getVideocount()));
		// 获取画面通道号
		setScreenChannelMap(this.screenCount, new CalCondVO());
	}

	private void setScreenChannelMap(int screenCount, CalCondVO measVO) {
		for (int i = 1; i <= screenCount; i++) {
			// 画面对应通道号
			this.ScreenChannelMap.put(Integer.valueOf(i), Integer.valueOf(i + 1));
			// this.ScreenChannelMap.put(Integer.valueOf(i),
			// Integer.valueOf(Integer.parseInt(measVO.getAttributeValue("video"
			// + i + "channelid").toString()) - 1));
		}
	}

	class SmallPanelMouseListener implements MouseListener {
		SmallPanelMouseListener() {
		}

		public void mouseClicked(MouseEvent e) {/*
			if ((!VedioPanel.this.getBaseContainer().getBillWorkPanel()
					.getMeasDoc().getDef2().booleanValue())
					|| ((VedioPanel.this.isMaxScreen()) && (e.getComponent() != VedioPanel.this))
					|| ((!VedioPanel.this.isMaxScreen()) && (e.getComponent() == VedioPanel.this))) {
				return;
			}
			saveMouseClickedVideoPanel(e);
			if ((VedioPanel.this.getMaxScreenNum() > VedioPanel.this
					.getScreenCount())
					|| ((e.getComponent() != VedioPanel.this) && (!VedioPanel.this
							.getRealPlayReturnIDs().containsKey(
									Integer.valueOf(VedioPanel.this
											.getMaxScreenNum()))))) {
				return;
			}
			if (e.getButton() == 3) {
				e.getComponent().requestFocus();
				VedioPanel.this.setMenuStatusBeforeButton3();
				VedioPanel.this.getMenu1().add(VedioPanel.this.getMenuItem1());
				VedioPanel.this.getMenu1().add(VedioPanel.this.getMenuItem2());
				VedioPanel.this.getMenu1().add(VedioPanel.this.getMenuItem3());
				VedioPanel.this.getMenu1().add(VedioPanel.this.getMenuItem4());
				VedioPanel.this.getMenu1().show(e.getComponent(), e.getX(),
						e.getY());
			}
			if ((e.getButton() == 1) && (e.getClickCount() >= 2)) {
				if (VedioPanel.this.isMaxScreen()) {
					VedioPanel.this.restoreScreen();
				} else {
					VedioPanel.this.maxScreen();
				}
			}
		*/}

		private void saveMouseClickedVideoPanel(MouseEvent e) {
			if (e.getComponent() == VedioPanel.this.getVedioPanel1()) {
				VedioPanel.this.setMaxScreenNum(VedioPanel.SCREENNUM1);
			}
			if (e.getComponent() == VedioPanel.this.getVedioPanel2()) {
				VedioPanel.this.setMaxScreenNum(VedioPanel.SCREENNUM2);
			}
			if (e.getComponent() == VedioPanel.this.getVedioPanel3()) {
				VedioPanel.this.setMaxScreenNum(VedioPanel.SCREENNUM3);
			}
			if (e.getComponent() == VedioPanel.this.getVedioPanel4()) {
				VedioPanel.this.setMaxScreenNum(VedioPanel.SCREENNUM4);
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	private BaseContainer getBaseContainer() {
		if (isFullScreen()) {
			return (BaseContainer) getParent().getParent();
		}
		return (BaseContainer) getParent().getParent().getParent().getParent();
	}

	// 错误返回信息，判断
	private void showLoginErrorReason(int nError, int screenNO)
			throws BusinessException {
		if (1 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ������������");
		} else if (2 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ��������������");
		} else if (3 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ��������");
		} else if (4 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ����������");
		} else if (5 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ������������");
		} else if (6 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ����������������");
		} else if (7 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ����������������");
		} else if (9 == nError) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ����������");
		} else if (nError == 0) {
			getBaseContainer().showWarningMessage(
					"��" + screenNO + "������ ��������");
		}
	}

	private void setVideoPanelStatus(boolean status) {
		getVedioPanel1().setVisible(status);
		getVedioPanel2().setVisible(status);
		getVedioPanel3().setVisible(status);
		getVedioPanel4().setVisible(status);
	}

	public boolean isMaxScreen() {
		return this.isMaxScreen;
	}

	public void setMaxScreen(boolean isMaxScreen) {
		this.isMaxScreen = isMaxScreen;
	}

	public int getMaxScreenNum() {
		return this.maxScreenNum;
	}

	public void setMaxScreenNum(int maxScreenNum) {
		this.maxScreenNum = maxScreenNum;
	}

	public boolean isFullScreen() {
		return this.isFullScreen;
	}

	public void setFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public int getScreenCount() {
		return this.screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}

	public Map<Integer, NativeLong> getRealPlayReturnIDs() {
		return this.realPlayReturnIDs;
	}

	public void setRealPlayReturnIDs(Map<Integer, NativeLong> realPlayReturnIDs) {
		this.realPlayReturnIDs = realPlayReturnIDs;
	}

	public Map<Integer, Integer> getN_LOGINID() {
		return this.N_LOGINID;
	}

	public void setN_LOGINID(Map<Integer, Integer> n_LOGINID) {
		this.N_LOGINID = n_LOGINID;
	}

	public Map<Integer, Integer> getScreenChannelMap() {
		return this.ScreenChannelMap;
	}

	public void setScreenChannelMap(Map<Integer, Integer> screenChannelMap) {
		this.ScreenChannelMap = screenChannelMap;
	}

	class fdissconnect implements VideoPlay.fDisConnect {
		fdissconnect() {
		}

		public void invoke(NativeLong lLoginID, String pchDVRIP,
				NativeLong nDVRPort, NativeLong dwUser) {
		}
	}
}
