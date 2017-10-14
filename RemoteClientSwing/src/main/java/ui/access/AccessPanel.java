package ui.access;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import main.Main;
import model.ConnectionStatus;
import model.LoginStatus;
import network.ServerConnection;

@SuppressWarnings("serial")
public class AccessPanel extends JPanel implements AccessScreen {

	@Inject
	AccessPresenter presenter;
	
	@Inject
	ServerConnection serverConnection;

	private JTextField textFieldUsername;
	private JPasswordField textFieldPassword;
	private JTextField textFieldAnonymousId;
	private JTextField textFieldAnonymousPassword;

	private MaskFormatter idFormat;

	private JButton btnAnonymousConnect;

	private JButton btnAccountConnect;
	private JButton btnGenerateId;
	private JButton btnGeneratePassword;
	private JLabel lblAccountConnectionStatus;
	private JLabel lblAnonymousConnectionStatus;

	public AccessPanel() {
		Main.injector.inject(this);
		presenter.attachScreen(this);
		presenter.attachServerConnection(serverConnection);

		createAnonymousIdFormatter();

		initComponents();
		
		presenter.generateAnonymousId();
		presenter.generateAnonymousPassword();
	}

	private void initComponents() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
	
		JPanel panelLoginAccount = new JPanel();
		panelLoginAccount.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Login with account",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelLoginAccount = new GridBagConstraints();
		gbc_panelLoginAccount.fill = GridBagConstraints.BOTH;
		gbc_panelLoginAccount.insets = new Insets(0, 0, 5, 0);
		gbc_panelLoginAccount.gridx = 0;
		gbc_panelLoginAccount.gridy = 0;
		add(panelLoginAccount, gbc_panelLoginAccount);
		GridBagLayout gbl_panelLoginAccount = new GridBagLayout();
		gbl_panelLoginAccount.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelLoginAccount.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelLoginAccount.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelLoginAccount.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelLoginAccount.setLayout(gbl_panelLoginAccount);
	
		JLabel lblUsername = new JLabel("Username");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 0;
		panelLoginAccount.add(lblUsername, gbc_lblUsername);
	
		textFieldUsername = new JTextField();
		textFieldUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				accountUsernameTexfieldAction();
			}
		});
		GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUsername.gridx = 1;
		gbc_textFieldUsername.gridy = 0;
		panelLoginAccount.add(textFieldUsername, gbc_textFieldUsername);
		textFieldUsername.setColumns(10);
	
		JLabel lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		panelLoginAccount.add(lblPassword, gbc_lblPassword);
	
		textFieldPassword = new JPasswordField();
		textFieldPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				accountPasswordTexfieldAction();
			}
		});
		GridBagConstraints gbc_textFieldPassword = new GridBagConstraints();
		gbc_textFieldPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPassword.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldPassword.gridx = 1;
		gbc_textFieldPassword.gridy = 1;
		panelLoginAccount.add(textFieldPassword, gbc_textFieldPassword);
		textFieldPassword.setColumns(10);
	
		btnAccountConnect = new JButton(presenter.getModel().getAccountLoginStatus().getAction());
		btnAccountConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accountConnectAction();
			}
		});
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.insets = new Insets(0, 0, 5, 0);
		gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogin.gridx = 1;
		gbc_btnLogin.gridy = 2;
		panelLoginAccount.add(btnAccountConnect, gbc_btnLogin);
		
		lblAccountConnectionStatus = new JLabel(presenter.getModel().getAccountLoginStatus().getStatus());
		GridBagConstraints gbc_lblAccountConnectionStatus = new GridBagConstraints();
		gbc_lblAccountConnectionStatus.anchor = GridBagConstraints.EAST;
		gbc_lblAccountConnectionStatus.gridwidth = 2;
		gbc_lblAccountConnectionStatus.insets = new Insets(0, 0, 0, 5);
		gbc_lblAccountConnectionStatus.gridx = 0;
		gbc_lblAccountConnectionStatus.gridy = 3;
		panelLoginAccount.add(lblAccountConnectionStatus, gbc_lblAccountConnectionStatus);
	
		JPanel panelConnectAnonymously = new JPanel();
		panelConnectAnonymously.setBorder(
				new TitledBorder(null, "Connect anonymously", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelConnectAnonymously = new GridBagConstraints();
		gbc_panelConnectAnonymously.fill = GridBagConstraints.BOTH;
		gbc_panelConnectAnonymously.gridx = 0;
		gbc_panelConnectAnonymously.gridy = 1;
		add(panelConnectAnonymously, gbc_panelConnectAnonymously);
		GridBagLayout gbl_panelConnectAnonymously = new GridBagLayout();
		gbl_panelConnectAnonymously.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panelConnectAnonymously.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelConnectAnonymously.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelConnectAnonymously.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelConnectAnonymously.setLayout(gbl_panelConnectAnonymously);
	
		JLabel lblId = new JLabel("ID");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 0;
		panelConnectAnonymously.add(lblId, gbc_lblId);
	
		textFieldAnonymousId = new JFormattedTextField(idFormat);
		textFieldAnonymousId.setText("123456789");
		textFieldAnonymousId.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldAnonymousId.setEditable(false);
		GridBagConstraints gbc_textFieldAnonymousId = new GridBagConstraints();
		gbc_textFieldAnonymousId.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAnonymousId.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldAnonymousId.gridx = 1;
		gbc_textFieldAnonymousId.gridy = 0;
		panelConnectAnonymously.add(textFieldAnonymousId, gbc_textFieldAnonymousId);
		textFieldAnonymousId.setColumns(10);
	
		btnGenerateId = new JButton("Generate");
		btnGenerateId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				presenter.generateAnonymousId();
			}
		});
		GridBagConstraints gbc_btnGenerateId = new GridBagConstraints();
		gbc_btnGenerateId.insets = new Insets(0, 0, 5, 0);
		gbc_btnGenerateId.gridx = 2;
		gbc_btnGenerateId.gridy = 0;
		panelConnectAnonymously.add(btnGenerateId, gbc_btnGenerateId);
	
		JLabel lblPassword_1 = new JLabel("Password");
		GridBagConstraints gbc_lblPassword_1 = new GridBagConstraints();
		gbc_lblPassword_1.anchor = GridBagConstraints.EAST;
		gbc_lblPassword_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword_1.gridx = 0;
		gbc_lblPassword_1.gridy = 1;
		panelConnectAnonymously.add(lblPassword_1, gbc_lblPassword_1);
	
		textFieldAnonymousPassword = new JTextField();
		textFieldAnonymousPassword.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldAnonymousPassword.setText("1234");
		textFieldAnonymousPassword.setEditable(false);
		GridBagConstraints gbc_textFieldAnonymousPassword = new GridBagConstraints();
		gbc_textFieldAnonymousPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAnonymousPassword.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldAnonymousPassword.gridx = 1;
		gbc_textFieldAnonymousPassword.gridy = 1;
		panelConnectAnonymously.add(textFieldAnonymousPassword, gbc_textFieldAnonymousPassword);
		textFieldAnonymousPassword.setColumns(10);
	
		btnGeneratePassword = new JButton("Generate");
		btnGeneratePassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.generateAnonymousPassword();
			}
		});
		GridBagConstraints gbc_btnGeneratePassword = new GridBagConstraints();
		gbc_btnGeneratePassword.insets = new Insets(0, 0, 5, 0);
		gbc_btnGeneratePassword.gridx = 2;
		gbc_btnGeneratePassword.gridy = 1;
		panelConnectAnonymously.add(btnGeneratePassword, gbc_btnGeneratePassword);
	
		btnAnonymousConnect = new JButton(presenter.getModel().getAnonymousConnectionStatus().getAction());
		btnAnonymousConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				anonymousConnectAction();
			}
		});
	
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.insets = new Insets(0, 0, 5, 0);
		gbc_btnConnect.gridwidth = 2;
		gbc_btnConnect.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnConnect.gridx = 1;
		gbc_btnConnect.gridy = 2;
		panelConnectAnonymously.add(btnAnonymousConnect, gbc_btnConnect);
		
		lblAnonymousConnectionStatus = new JLabel(presenter.getModel().getAnonymousConnectionStatus().getStatus());
		GridBagConstraints gbc_lblAnonymousConnectionStatus = new GridBagConstraints();
		gbc_lblAnonymousConnectionStatus.anchor = GridBagConstraints.EAST;
		gbc_lblAnonymousConnectionStatus.gridwidth = 3;
		gbc_lblAnonymousConnectionStatus.insets = new Insets(0, 0, 0, 5);
		gbc_lblAnonymousConnectionStatus.gridx = 0;
		gbc_lblAnonymousConnectionStatus.gridy = 3;
		panelConnectAnonymously.add(lblAnonymousConnectionStatus, gbc_lblAnonymousConnectionStatus);
	}

	private void createAnonymousIdFormatter() {
		try {
			idFormat = new MaskFormatter("### ### ###");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	protected void accountPasswordTexfieldAction() {
		presenter.getModel().setAccountPassword(textFieldPassword.getText());
	}

	protected void accountUsernameTexfieldAction() {
		presenter.getModel().setAccountUsername(textFieldUsername.getText());
	}

	protected void accountConnectAction() {
		LoginStatus loginStatus = presenter.getModel().getAccountLoginStatus();
		if (loginStatus == LoginStatus.DISCONNECTED) {
			presenter.connectAccount();
		} else if (loginStatus == LoginStatus.CONNECTED) {
			presenter.disconnectAccount();
		}
	}

	protected void anonymousConnectAction() {
		ConnectionStatus connectionStatus = presenter.getModel().getAnonymousConnectionStatus();
		if (connectionStatus == ConnectionStatus.DISCONNECTED) {
			presenter.connectAnonymous();
		} else if (connectionStatus == ConnectionStatus.CONNECTED) {
			presenter.disconnectAnonymous();
		}
	}

	@Override
	public void updateAnonymousId() {
		textFieldAnonymousId.setText(presenter.getModel().getAnonymousId());
	}

	@Override
	public void updateAnonymousPassword() {
		textFieldAnonymousPassword.setText(presenter.getModel().getAnonymousPassword());
	}

	@Override
	public void updateAccountConnectionStatus() {
		LoginStatus loginStatus = presenter.getModel().getAccountLoginStatus();
		boolean editEnabled = loginStatus == LoginStatus.DISCONNECTED;
		boolean connectEnabled = loginStatus != LoginStatus.WAITING;
		textFieldUsername.setEnabled(editEnabled);
		textFieldPassword.setEnabled(editEnabled);
		btnAccountConnect.setEnabled(connectEnabled);
		btnAccountConnect.setText(loginStatus.getAction());
		lblAccountConnectionStatus.setText(loginStatus.getStatus());
	}
	
	@Override
	public void updateAnonymousConnectionStatus() {
		ConnectionStatus connectionStatus = presenter.getModel().getAnonymousConnectionStatus();
		boolean generateEnabled = connectionStatus == ConnectionStatus.DISCONNECTED;
		boolean connectEnabled = connectionStatus != ConnectionStatus.WAITING;
		btnGenerateId.setEnabled(generateEnabled);
		btnGeneratePassword.setEnabled(generateEnabled);
		btnAnonymousConnect.setEnabled(connectEnabled);
		btnAnonymousConnect.setText(connectionStatus.getAction());
		lblAnonymousConnectionStatus.setText(connectionStatus.getStatus());
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

}
