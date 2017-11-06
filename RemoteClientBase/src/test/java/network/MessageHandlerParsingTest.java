package network;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import application.Application;
import capture.Capturer;
import model.MessageWrapper;
import model.message.KeyEvent;
import model.message.MouseClick;
import model.message.Start;
import model.message.Stop;
import model.message.VerifyCreateAccountAccess;
import model.message.VerifyCreateAnonymousAccess;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class MessageHandlerParsingTest {

	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Test(expected=NoCommandException.class)
	public void expectNoCommandException() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		messageHandler.handleMessage(getNoCommandMessage());
	}
	
	@Test(expected=JsonParseException.class)
	public void expectIOException() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		messageHandler.handleMessage(getUnparsableJsonMessage());
	}
	
	@Test
	public void handleVerifyCreateAccountAccess() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		messageHandler.handleMessage(getVerifyCreateAccountAccessSuccessMessage());

		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).accountConnected();
	}

	@Test
	public void handleVerifyCreateAnonymousAccess() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);
	
		messageHandler.handleMessage(getVerifyCreateAnonymousAccessSuccessMessage());
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).anonymousConnected();
	}

	@Test
	public void handleStart() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.capturer = mock(Capturer.class);
	
		messageHandler.handleMessage(getStartMessage());
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.capturer).startCapture();
	}

	@Test
	public void handleStop() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.capturer = mock(Capturer.class);
		
		messageHandler.handleMessage(getStopMessage());
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.capturer).stopCapture();
	}

	@Test
	public void handleMouseClick() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		MouseClick mouseClick = new MouseClick();
		mouseClick.setButton(2);
		mouseClick.setX(100);
		mouseClick.setY(100);
		messageHandler.handleMessage(getMouseClickMessage(mouseClick));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.application).mouseClick(mouseClick);
	}

	@Test
	public void handleKeyEvent() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		KeyEvent keyEvent = new KeyEvent();
		keyEvent.setCharacter("a");
		keyEvent.setCode(1);
		keyEvent.setType(KeyEvent.TYPE_KEYDOWN);
		messageHandler.handleMessage(getKeyEventMessage(keyEvent));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.application).keyEvent(keyEvent);
	}

	private String getNoCommandMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper);
	}

	private String getUnparsableJsonMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper).replace("{", "");
	}

	private String getVerifyCreateAccountAccessSuccessMessage() throws JsonProcessingException {
		VerifyCreateAccountAccess verifyCreateAccountAccess = new VerifyCreateAccountAccess();
		verifyCreateAccountAccess.setSuccess(true);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setVerifyCreateAccountAccess(verifyCreateAccountAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getVerifyCreateAnonymousAccessSuccessMessage() throws JsonProcessingException {
		VerifyCreateAnonymousAccess verifyCreateAnonymousAccess = new VerifyCreateAnonymousAccess();
		verifyCreateAnonymousAccess.setSuccess(true);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setVerifyCreateAnonymousAccess(verifyCreateAnonymousAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getStartMessage() throws JsonProcessingException {
		Start start = new Start();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStart(start);
		return mapper.writeValueAsString(wrapper);
	}

	private String getStopMessage() throws JsonProcessingException {
		Stop stop = new Stop();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStop(stop);
		return mapper.writeValueAsString(wrapper);
	}

	private String getMouseClickMessage(MouseClick mouseClick) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMouseClick(mouseClick);
		return mapper.writeValueAsString(wrapper);
	}

	private String getKeyEventMessage(KeyEvent keyEvent) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setKeyEvent(keyEvent);
		return mapper.writeValueAsString(wrapper);
	}

}
