package ui.connection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import model.ConnectionStatus;
import network.ServerConnection;

public class ConnectionPresenterTest {

	@Test
	public void connect() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));
		presenter.setServerConnection(mock(ServerConnection.class));

		presenter.connect();

		assertEquals(ConnectionStatus.WAITING, presenter.getModel().getConnectionStatus());
		verify(presenter.getScreen()).updateConnectionStatus();
		verify(presenter.getServerConnection()).connect(presenter.getModel().getAddress());
	}

	@Test
	public void disconnect() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setServerConnection(mock(ServerConnection.class));

		presenter.disconnect();

		verify(presenter.getServerConnection()).disconnect();
	}

	@Test
	public void connected() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));

		presenter.connected();

		assertEquals(ConnectionStatus.CONNECTED, presenter.getModel().getConnectionStatus());
		assertEquals(0, presenter.getModel().getSentMessages());
		assertEquals(0, presenter.getModel().getReceivedMessages());
		verify(presenter.getScreen()).updateConnectionStatus();
		verify(presenter.getScreen()).updateSentMessages();
		verify(presenter.getScreen()).updateReceivedMessages();
	}

	@Test
	public void disconnected() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));

		presenter.disconnected();

		assertEquals(ConnectionStatus.DISCONNECTED, presenter.getModel().getConnectionStatus());
		verify(presenter.getScreen()).updateConnectionStatus();
	}

	@Test
	public void messageSent() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));

		presenter.messageSent();

		assertEquals(1, presenter.getModel().getSentMessages());
		verify(presenter.getScreen()).updateSentMessages();
	}

	@Test
	public void messageReceived() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));

		presenter.messageReceived();

		assertEquals(1, presenter.getModel().getReceivedMessages());
		verify(presenter.getScreen()).updateReceivedMessages();
	}

	@Test
	public void showMessage() {
		ConnectionPresenter presenter = new ConnectionPresenter();
		presenter.setScreen(mock(ConnectionScreen.class));

		presenter.showMessage("message");

		verify(presenter.getScreen()).showMessage("message");
	}

}
