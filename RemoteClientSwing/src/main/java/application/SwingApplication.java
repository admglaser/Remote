package application;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.message.KeyEvent;
import model.message.MouseClick;
import ui.access.AccessPanel;
import ui.connection.ConnectionPanel;

@SuppressWarnings("serial")
public class SwingApplication extends JFrame implements Application {

	private TrayIcon trayIcon;
	private Image image;

	public SwingApplication() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		readImage();
		initComponents();
		addTrayIcon();
	}

	private void readImage() {
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tray.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		setTitle("Remote");
		setIconImage(image);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 234, 116, 0 };
		gbl_panel.rowHeights = new int[] { 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.add("Connection", new ConnectionPanel());
		tabbedPane.add("Access", new AccessPanel());
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void addTrayIcon() {
		trayIcon = null;
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();

			PopupMenu popup = new PopupMenu();
			MenuItem exitMenuItem = new MenuItem("Exit");
			exitMenuItem.addActionListener(e -> {
				exit();
			});
			popup.add(exitMenuItem);
			trayIcon = new TrayIcon(image, "Remote", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					activate();
				}
			});
			try {
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void exit() {
		System.exit(0);
	}

	public void activate() {
		this.setVisible(true);
	}

	public void showMessage(String message) {
		trayIcon.displayMessage("Remote", message, MessageType.INFO);
	}

	@Override
	public void mouseClick(MouseClick mouseClick) {
		try {
			Robot robot = new Robot();
			int x = mouseClick.getX();
			int y = mouseClick.getY();
			int button = mouseClick.getButton();
			robot.mouseMove(x, y);
			int mask = -1;
			switch (button) {
			case 0:
				mask = InputEvent.BUTTON1_DOWN_MASK;
				break;
			case 1:
				mask = InputEvent.BUTTON2_DOWN_MASK;
				break;
			case 2:
				mask = InputEvent.BUTTON3_DOWN_MASK;
				break;
			default:
				throw new Exception("Unhandled button for mouseClick.");
			}
			if (mask != -1) {
				robot.mousePress(mask);
				robot.mouseRelease(mask);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void keyEvent(KeyEvent keyEvent) {
		try {
			Robot robot = new Robot();
			int code = keyEvent.getCode();
			int type = keyEvent.getType();
			switch (type) {
			case KeyEvent.TYPE_KEYDOWN:
				robot.keyPress(code);
				break;
			case KeyEvent.TYPE_KEYUP:
				robot.keyRelease(code);
				break;
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getDeviceWidth() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize.width;
	}

	@Override
	public int getDeviceHeight() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize.height;
	}

	@Override
	public String getDeviceName() {
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			return localMachine.getHostName();
		} catch (UnknownHostException e) {
		}
		return "unknown";
	}

}
