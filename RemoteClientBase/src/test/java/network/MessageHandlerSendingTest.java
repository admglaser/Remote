package network;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import application.Application;
import model.ImagePiece;
import model.MessageWrapper;
import model.Rectangle;
import model.message.CreateAccountAccess;
import model.message.CreateAnonymousAccess;
import model.message.Identify;
import model.message.Image;
import model.message.RemoveAccountAccess;
import model.message.RemoveAnonymousAccess;
import ui.connection.ConnectionPresenter;

public class MessageHandlerSendingTest {

	private ObjectMapper mapper;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	@Test
	public void sendIdentify() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		messageHandler.sendIndentify();

		verify(session).send(getIdentifyMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}
	
	@Test
	public void sendCreateAccountAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		
		String username = "username";
		String password = "password";
		messageHandler.sendCreateAccountAccess(username, password);

		verify(session).send(getCreateAccountAccessMessage(username, password));
		verify(messageHandler.connectionPresenter).messageSent();
	}
	
	@Test
	public void sendCreateAnonymousAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);

		String numericId = "1234";
		String numericPassword = "123 456 789";
		messageHandler.sendCreateAnonymousAccess(numericId, numericPassword);

		verify(session).send(getCreateAnonymousAccessMessage(numericId, numericPassword));
		verify(messageHandler.connectionPresenter).messageSent();
	}
	
	@Test
	public void sendRemoveAccountAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);

		messageHandler.sendRemoveAccountAccess();

		verify(session).send(getRemoveAccountAccessMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}
	
	@Test
	public void sendRemoveAnonymousAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);

		messageHandler.sendRemoveAnonymousAccess();

		verify(session).send(getRemoveAnonymousAccessMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}
	
	@Test
	public void sendImagePiece() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);

		ImagePiece imagePiece = new ImagePiece(new Rectangle(0, 0, 100, 100), "data");
		messageHandler.sendImagePiece(imagePiece);

		verify(session).send(getImageMessage(imagePiece));
		verify(messageHandler.connectionPresenter).messageSent();
	}

	private String getIdentifyMessage() throws JsonProcessingException {
		Identify identify = new Identify();
		identify.setType("sender");
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setIdentify(identify);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAccountAccessMessage(String username, String password) throws JsonProcessingException {
		CreateAccountAccess createAccountAccess = new CreateAccountAccess();
		createAccountAccess.setUsername(username);
		createAccountAccess.setPassword(password);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccess(createAccountAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAnonymousAccessMessage(String numericId, String numericPassword) throws JsonProcessingException {
		CreateAnonymousAccess createAnonymousAccess = new CreateAnonymousAccess();
		createAnonymousAccess.setNumericId(numericId);
		createAnonymousAccess.setNumericPassword(numericPassword);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccess(createAnonymousAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAccountAccessMessage() throws JsonProcessingException {
		RemoveAccountAccess removeAccountAccess = new RemoveAccountAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAccountAccess(removeAccountAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAnonymousAccessMessage() throws JsonProcessingException {
		RemoveAnonymousAccess removeAnonymousAccess = new RemoveAnonymousAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAnonymousAccess(removeAnonymousAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getImageMessage(ImagePiece imagePiece) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		Image image = new Image();
		image.setData(imagePiece.getEncodedString());
		Rectangle area = imagePiece.getArea();
		image.setX(area.getX());
		image.setY(area.getY());
		image.setW(area.getWidth());
		image.setH(area.getHeight());
		wrapper.setImage(image);
		return mapper.writeValueAsString(wrapper);
	}
	
}
