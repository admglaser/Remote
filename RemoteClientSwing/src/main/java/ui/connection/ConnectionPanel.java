package ui.connection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import main.Main;
import model.ConnectionStatus;
import network.ServerConnection;

@SuppressWarnings("serial")
public class ConnectionPanel extends JPanel implements ConnectionScreen {

	@Inject
	ConnectionPresenter presenter;
	
	@Inject
	ServerConnection serverConnection;
	
	private JButton btnConnect;
	private JLabel lblConnectionStatus;
	private JLabel lblSentMessages;
	private JLabel lblReceivedMessages;
	private JTextField textFieldAddress;

	public ConnectionPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		Main.injector.inject(this);
		presenter.attachScreen(this);
		presenter.attachServerConnection(serverConnection);
		
		initComponents();
		String address = System.getProperty("remote.address");
		textFieldAddress.setText(address);	
		addressTexfieldAction();
	}

	private void initComponents() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{175, 107, 43, 0};
		gridBagLayout.rowHeights = new int[]{43, 14, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridwidth = 3;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(panel_1, gbc_panel_1);
		panel_1.setBorder(new TitledBorder(null, "Address", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{151, 0, 87, 55, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		textFieldAddress = new JTextField();
		textFieldAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addressTexfieldAction();
			}
		});
		GridBagConstraints gbc_textFieldAddress = new GridBagConstraints();
		gbc_textFieldAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAddress.gridwidth = 3;
		gbc_textFieldAddress.insets = new Insets(0, 0, 0, 5);
		gbc_textFieldAddress.gridx = 0;
		gbc_textFieldAddress.gridy = 0;
		panel_1.add(textFieldAddress, gbc_textFieldAddress);
		textFieldAddress.setColumns(10);
		
		btnConnect = new JButton(presenter.getModel().getConnectionStatus().getAction());
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.gridx = 3;
		gbc_btnConnect.gridy = 0;
		panel_1.add(btnConnect, gbc_btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectAction();
			}
		});
		
		lblSentMessages = new JLabel("Sent messages: 0");
		GridBagConstraints gbc_lblSentMessages = new GridBagConstraints();
		gbc_lblSentMessages.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblSentMessages.insets = new Insets(0, 0, 5, 5);
		gbc_lblSentMessages.gridx = 0;
		gbc_lblSentMessages.gridy = 1;
		add(lblSentMessages, gbc_lblSentMessages);
		
		lblReceivedMessages = new JLabel("Received messages: 0");
		GridBagConstraints gbc_lblReceivedMessages = new GridBagConstraints();
		gbc_lblReceivedMessages.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblReceivedMessages.insets = new Insets(0, 0, 5, 5);
		gbc_lblReceivedMessages.gridx = 0;
		gbc_lblReceivedMessages.gridy = 2;
		add(lblReceivedMessages, gbc_lblReceivedMessages);
		
		lblConnectionStatus = new JLabel(presenter.getModel().getConnectionStatus().getStatus());
		GridBagConstraints gbc_lblConnectionStatus = new GridBagConstraints();
		gbc_lblConnectionStatus.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblConnectionStatus.gridx = 2;
		gbc_lblConnectionStatus.gridy = 4;
		add(lblConnectionStatus, gbc_lblConnectionStatus);
	}

	protected void addressTexfieldAction() {
		presenter.getModel().setAddress(textFieldAddress.getText());
	}

	protected void connectAction() {
		ConnectionStatus connectionStatus = presenter.getModel().getConnectionStatus();
		if (connectionStatus == ConnectionStatus.DISCONNECTED) {
			presenter.connect();
		} else if (connectionStatus == ConnectionStatus.CONNECTED) {
			presenter.disconnect();
		}
	}

	@Override
	public void updateConnectionStatus() {
		ConnectionStatus connectionStatus = presenter.getModel().getConnectionStatus();
		boolean connectEnabled = connectionStatus != ConnectionStatus.WAITING;
		btnConnect.setEnabled(connectEnabled);
		btnConnect.setText(connectionStatus.getAction());
		lblConnectionStatus.setText(connectionStatus.getStatus());
	}

	@Override
	public void updateSentMessages() {
		lblSentMessages.setText("Sent messages: " + presenter.getModel().getSentMessages());
	}

	@Override
	public void updateReceivedMessages() {
		lblReceivedMessages.setText("Received messages: " + presenter.getModel().getReceivedMessages());
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

}
