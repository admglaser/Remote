package jubula;

import java.io.IOException;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TabComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.swing.SwingComponents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GUITest {

	private AUT aut;
	private Configuration configuration;
	
	private TabComponent tabbedPane;
	private TextInputComponent textInputAddress;
	private ButtonComponent buttonConnectServer;
	private TextInputComponent textInputAccountUsername;
	private TextInputComponent textInputAccountPassword;
	private ButtonComponent buttonLoginAccount;
	private ButtonComponent buttonGenerateId;
	private ButtonComponent buttonGeneratePassword;
	private ButtonComponent buttonConnectAnonymous;

	@Before
	public void setup() throws IOException {
		configuration = new Configuration();
		
		AUTAgent autAgent = MakeR.createAUTAgent("localhost", 60000);
		autAgent.connect();
		aut = AUTUtils.getRunningAut(autAgent, configuration.getId());
		aut.connect();
		
		initComponents();
	}
	
	private void initComponents() {
		tabbedPane = SwingComponents.createJTabbedPane(MakeR.createCI(configuration.getMapping("tabbedPane")));
		textInputAddress = SwingComponents.createJTextComponent(MakeR.createCI(configuration.getMapping("textInputAddress")));
		buttonConnectServer = SwingComponents.createAbstractButton(MakeR.createCI(configuration.getMapping("buttonConnectServer")));
		textInputAccountUsername = SwingComponents.createJTextComponent(MakeR.createCI(configuration.getMapping("textInputAccountUsername")));
		textInputAccountPassword = SwingComponents.createJTextComponent(MakeR.createCI(configuration.getMapping("textInputAccountPassword")));
		buttonLoginAccount = SwingComponents.createAbstractButton(MakeR.createCI(configuration.getMapping("buttonLoginAccount")));
		buttonGenerateId = SwingComponents.createAbstractButton(MakeR.createCI(configuration.getMapping("buttonGenerateId")));
		buttonGeneratePassword = SwingComponents.createAbstractButton(MakeR.createCI(configuration.getMapping("buttonGeneratePassword")));
		buttonConnectAnonymous = SwingComponents.createAbstractButton(MakeR.createCI(configuration.getMapping("buttonConnectAnonymous")));
	}

	@Test
	public void test() {
		openConnectionTab();
		connectToServer();
		
		openAccessTab();
		
		loginAccount();
		logoutAccount();
		
		generateAnonymousAccess();
		connectAnonymous();
		disconnectAnonymous();
		
		openConnectionTab();
		disconnectFromServer();
	}

	private void openConnectionTab() {
		aut.execute(tabbedPane.selectTabByValue("Connection", Operator.equals), null);
	}

	private void openAccessTab() {
		aut.execute(tabbedPane.selectTabByValue("Access", Operator.equals), null);
	}

	private void connectToServer() {
		aut.execute(textInputAddress.replaceText("localhost"), null);
		disconnectFromServer();
		aut.execute(buttonConnectServer.checkText("Disconnect", Operator.equals, 3000), null);
	}

	private void disconnectFromServer() {
		aut.execute(buttonConnectServer.click(1, InteractionMode.primary), null);
	}

	private void loginAccount() {
		aut.execute(textInputAccountUsername.replaceText("user"), null);
		aut.execute(textInputAccountPassword.replaceText("user"), null);
		aut.execute(buttonLoginAccount.click(1, InteractionMode.primary), null);
		aut.execute(buttonLoginAccount.checkText("Logout", Operator.equals, 3000), null);
	}

	private void logoutAccount() {
		aut.execute(buttonLoginAccount.click(1, InteractionMode.primary), null);
		aut.execute(buttonLoginAccount.checkText("Login", Operator.equals, 3000), null);
	}

	private void connectAnonymous() {
		aut.execute(buttonConnectAnonymous.click(1, InteractionMode.primary), null);
		aut.execute(buttonConnectAnonymous.checkText("Disconnect", Operator.equals, 3000), null);
	}

	private void disconnectAnonymous() {
		aut.execute(buttonConnectAnonymous.click(1, InteractionMode.primary), null);
		aut.execute(buttonConnectAnonymous.checkText("Connect", Operator.equals, 3000), null);
	}

	private void generateAnonymousAccess() {
		aut.execute(buttonGenerateId.click(1, InteractionMode.primary), null);
		aut.execute(buttonGeneratePassword.click(1, InteractionMode.primary), null);
	}

	@After
	public void tearDown() {
		aut.disconnect();
	}

}
