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
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import model.FileInfo;
import model.message.KeyEvent;
import model.message.MouseClick;
import ui.access.AccessPanel;
import ui.connection.ConnectionPanel;

@SuppressWarnings("serial")
public class SwingApplication extends JFrame implements Application {

	private TrayIcon trayIcon;
	private Image image;

	private Logger logger = Logger.getLogger(SwingApplication.class);

	public SwingApplication() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		readImage();
		initComponents();
		addTrayIcon();
	}

	private void readImage() {
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));
		} catch (IOException e) {
			logger.error(e);
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
			} catch (AWTException e) {
				logger.error(e);
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
				throw new RuntimeException("Unhandled button for mouseClick.");
			}
			if (mask != -1) {
				robot.mousePress(mask);
				robot.mouseRelease(mask);
			}
		} catch (AWTException e) {
			logger.error(e);
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
			logger.error(e);
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
			logger.error(e);
		}
		return "unknown";
	}

	@Override
	public List<FileInfo> listFileInfos(String path) {
		List<FileInfo> fileInfos = new ArrayList<>();
		if (path == null) {
			for (File root : File.listRoots()) {
				FileInfo fileInfo = new FileInfo(root.getPath(), root.getPath(), null, true, 0);
				fileInfos.add(fileInfo);
			}
		} else {
			File root = new File(path);
			if (root.exists() && root.isDirectory()) {
				for (File f : root.listFiles()) {
					if (!f.isHidden()) {
						FileInfo fileInfo = new FileInfo(f.getName(), f.getPath(), f.getParent(), f.isDirectory(),
								f.isDirectory() ? 0 : f.length());
						fileInfos.add(fileInfo);
					}
				}
			}
			Collections.sort(fileInfos, new Comparator<FileInfo>() {
				@Override
				public int compare(FileInfo f1, FileInfo f2) {
					if (f1.isDirectory() && !f2.isDirectory()) {
						return -1;
					}
					if (f2.isDirectory() && !f1.isDirectory()) {
						return 1;
					}
					return f1.getName().compareTo(f2.getName());
				}
			});
		}
		return fileInfos;
	}

	@Override
	public String getParentPath(String path) {
		if (path != null) {
			File file = new File(path);
			if (file != null && file.exists()) {
				return file.getParent();
			}
		}
		return null;
	}

	@Override
	public File getFile(String path, String name) {
		File file = new File(path + "/" + name);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	@Override
	public String uploadFile(String url, File file) throws IOException {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			FileBody fileBody = new FileBody(file);
			builder.addPart("file", fileBody);
			HttpEntity entity = builder.build();

			HttpPost post = new HttpPost(url);
			post.setEntity(entity);

			HttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				String content = EntityUtils.toString(response.getEntity());
				return content;
			}
		}
		return null;
	}

}
