package ui.access;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import model.ConnectionStatus;
import model.LoginStatus;
import network.ServerConnection;
import util.RandomDigitsGenerator;

public class AccessPresenterTest {

	@Test
	public void generateAnonymousId() {
		AccessPresenter presenter = new AccessPresenter();
		presenter.setScreen(mock(AccessScreen.class));
		presenter.randomDigitsGenerator = mock(RandomDigitsGenerator.class);
		
		when(presenter.randomDigitsGenerator.generateDigits(9)).thenReturn("123456789");
		
		presenter.generateAnonymousId();
		
		assertEquals("123456789", presenter.getModel().getAnonymousId());
		verify(presenter.getScreen()).updateAnonymousId();
	}

	@Test
	public void generateAnonymousPassword() {
		AccessPresenter presenter = new AccessPresenter();
		presenter.setScreen(mock(AccessScreen.class));
		presenter.randomDigitsGenerator = mock(RandomDigitsGenerator.class);
		
		when(presenter.randomDigitsGenerator.generateDigits(4)).thenReturn("1234");
		
		presenter.generateAnonymousPassword();
		
		assertEquals("1234", presenter.getModel().getAnonymousPassword());
		verify(presenter.getScreen()).updateAnonymousPassword();
	}

	@Test
	public void connectAccountWithNoServerConnection() {
		AccessPresenter presenter = new AccessPresenter();
		presenter.setScreen(mock(AccessScreen.class));
		presenter.setServerConnection(mock(ServerConnection.class));
		
		when(presenter.getServerConnection().isConnected()).thenReturn(false);
		
		presenter.connectAccount();
		
		verify(presenter.getScreen()).showMessage(AccessPresenter.CONNECTION_LOST_TO_SERVER);
	}
	
	@Test
	public void connectAccount() {
		AccessPresenter presenter = new AccessPresenter();
		presenter.setScreen(mock(AccessScreen.class));
		presenter.setServerConnection(mock(ServerConnection.class));
		
		when(presenter.getServerConnection().isConnected()).thenReturn(true);
		presenter.getModel().setAccountUsername("user");
		presenter.getModel().setAccountPassword("password");
		
		presenter.connectAccount();
		
		assertEquals(LoginStatus.WAITING, presenter.getModel().getAccountLoginStatus());
		verify(presenter.getScreen()).updateAccountConnectionStatus();
	}

	@Test
	public void connectAnonymousWithNoServerConnection() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(false);
		
		accessPresenter.connectAnonymous();
		
		verify(accessPresenter.getScreen()).showMessage(AccessPresenter.CONNECTION_LOST_TO_SERVER);
	}

	@Test
	public void connectAnonymous() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(true);
		accessPresenter.getModel().setAnonymousId("123456789");
		accessPresenter.getModel().setAccountPassword("1234");
		
		accessPresenter.connectAnonymous();
		
		assertEquals(ConnectionStatus.WAITING, accessPresenter.getModel().getAnonymousConnectionStatus());
		verify(accessPresenter.getScreen()).updateAnonymousConnectionStatus();
	}
	
	@Test
	public void disconnectAccountWithNoServerConnection() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(false);
		
		accessPresenter.disconnectAccount();
		
		verify(accessPresenter.getScreen()).showMessage(AccessPresenter.CONNECTION_LOST_TO_SERVER);
	}

	@Test
	public void disconnectAccount() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(true);
		
		accessPresenter.disconnectAccount();
		
		assertEquals(LoginStatus.DISCONNECTED, accessPresenter.getModel().getAccountLoginStatus());
		verify(accessPresenter.getScreen()).updateAccountConnectionStatus();
	}
	
	@Test
	public void disconnectAnonymousWithNoServerConnection() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(false);
		
		accessPresenter.disconnectAnonymous();
		
		verify(accessPresenter.getScreen()).showMessage(AccessPresenter.CONNECTION_LOST_TO_SERVER);
	}

	@Test
	public void disconnectAnonymous() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		accessPresenter.setServerConnection(mock(ServerConnection.class));
		
		when(accessPresenter.getServerConnection().isConnected()).thenReturn(true);
		
		accessPresenter.disconnectAnonymous();
		
		assertEquals(ConnectionStatus.DISCONNECTED, accessPresenter.getModel().getAnonymousConnectionStatus());
		verify(accessPresenter.getScreen()).updateAnonymousConnectionStatus();
	}

	@Test
	public void accountConnected() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		
		accessPresenter.accountConnected();
		
		assertEquals(LoginStatus.CONNECTED, accessPresenter.getModel().getAccountLoginStatus());
		verify(accessPresenter.getScreen()).updateAccountConnectionStatus();
	}

	@Test
	public void anonymousConnected() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		
		accessPresenter.anonymousConnected();
		
		assertEquals(ConnectionStatus.CONNECTED, accessPresenter.getModel().getAnonymousConnectionStatus());
		verify(accessPresenter.getScreen()).updateAnonymousConnectionStatus();
	}

	@Test
	public void accountDisconnected() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		
		accessPresenter.accountDisconnected();
		
		assertEquals(LoginStatus.DISCONNECTED, accessPresenter.getModel().getAccountLoginStatus());
		verify(accessPresenter.getScreen()).updateAccountConnectionStatus();
	}

	@Test
	public void anonymousDisconnected() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		
		accessPresenter.anonymousDisconnected();
		
		assertEquals(ConnectionStatus.DISCONNECTED, accessPresenter.getModel().getAnonymousConnectionStatus());
		verify(accessPresenter.getScreen()).updateAnonymousConnectionStatus();
	}

	@Test
	public void showMessage() {
		AccessPresenter accessPresenter = new AccessPresenter();
		accessPresenter.setScreen(mock(AccessScreen.class));
		
		accessPresenter.showMessage("message");
		
		verify(accessPresenter.getScreen()).showMessage("message");
	}

}
