package network;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import application.Application;
import capture.Capturer;
import model.ImagePiece;
import model.MessageWrapper;
import model.Rectangle;
import model.message.CreateAccountAccess;
import model.message.CreateAnonymousAccess;
import model.message.Identify;
import model.message.Image;
import model.message.KeyEvent;
import model.message.MouseClick;
import model.message.RemoveAccountAccess;
import model.message.RemoveAnonymousAccess;
import model.message.Start;
import model.message.Stop;
import model.message.VerifyCreateAccountAccess;
import model.message.VerifyCreateAnonymousAccess;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class MessageHandler {

	public static final String TYPE = "sender";

	Application application;

	Capturer capturer;

	AccessPresenter accessPresenter;

	ConnectionPresenter connectionPresenter;

	protected Session session;

	public MessageHandler(Session session) {
		this.session = session;
	}

	public void handleMessage(String message) throws NoCommandException, IOException {
		connectionPresenter.messageReceived();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		MessageWrapper wrapper = mapper.readValue(message, MessageWrapper.class);

		VerifyCreateAccountAccess verifyCreateAccountAccess = wrapper.getVerifyCreateAccountAccess();
		if (verifyCreateAccountAccess != null) {
			parseVerifyCreateAccountAccess(verifyCreateAccountAccess);
			return;
		}

		VerifyCreateAnonymousAccess verifyCreateAnonymousAccess = wrapper.getVerifyCreateAnonymousAccess();
		if (verifyCreateAnonymousAccess != null) {
			parseVerifyCreateAnonymousAccess(verifyCreateAnonymousAccess);
			return;
		}

		Start start = wrapper.getStart();
		if (start != null) {
			parseStart(start);
			return;
		}

		Stop stop = wrapper.getStop();
		if (stop != null) {
			parseStop(stop);
			return;
		}

		MouseClick mouseClick = wrapper.getMouseClick();
		if (mouseClick != null) {
			parseMouseClick(mouseClick);
			return;
		}

		KeyEvent keyEvent = wrapper.getKeyEvent();
		if (keyEvent != null) {
			parseKeyEvent(keyEvent);
			return;
		}

		throw new NoCommandException();
	}

	private void parseVerifyCreateAccountAccess(VerifyCreateAccountAccess verifyCreateAccountAccess) {
		boolean success = verifyCreateAccountAccess.isSuccess();
		if (success) {
			accessPresenter.accountConnected();
		} else {
			accessPresenter.accountDisconnected();
		}
	}

	private void parseVerifyCreateAnonymousAccess(VerifyCreateAnonymousAccess verifyCreateAnonymousAccess) {
		boolean success = verifyCreateAnonymousAccess.isSuccess();
		if (success) {
			accessPresenter.anonymousConnected();
		} else {
			accessPresenter.anonymousDisconnected();
		}
	}

	private void parseStart(Start start) {
		capturer.startCapture();
	}

	private void parseStop(Stop stop) {
		capturer.stopCapture();
	}

	private void parseMouseClick(MouseClick mouseClick) {
		application.mouseClick(mouseClick);
	}

	private void parseKeyEvent(KeyEvent keyEvent) {
		application.keyEvent(keyEvent);
	}

	public void sendIndentify() {
		Identify identify = new Identify();
		identify.setType(TYPE);
		identify.setDeviceName(application.getDeviceName());
		identify.setDeviceWidth(application.getDeviceWidth());
		identify.setDeviceHeight(application.getDeviceHeight());
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setIdentify(identify);
		send(wrapper);
	}

	public void sendCreateAccountAccess(String username, String password) {
		CreateAccountAccess createAccountAccess = new CreateAccountAccess();
		createAccountAccess.setUsername(username);
		createAccountAccess.setPassword(password);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccess(createAccountAccess);
		send(wrapper);
	}

	public void sendCreateAnonymousAccess(String numericId, String numericPassword) {
		CreateAnonymousAccess createAnonymousAccess = new CreateAnonymousAccess();
		createAnonymousAccess.setNumericId(numericId);
		createAnonymousAccess.setNumericPassword(numericPassword);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccess(createAnonymousAccess);
		send(wrapper);
	}

	public void sendRemoveAccountAccess() {
		RemoveAccountAccess removeAccountAccess = new RemoveAccountAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAccountAccess(removeAccountAccess);
		send(wrapper);
	}

	public void sendRemoveAnonymousAccess() {
		RemoveAnonymousAccess removeAnonymousAccess = new RemoveAnonymousAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAnonymousAccess(removeAnonymousAccess);
		send(wrapper);

	}

	public void sendImagePiece(ImagePiece imagePiece) {
		Image image = new Image();
		image.setData(imagePiece.getEncodedString());
		Rectangle area = imagePiece.getArea();
		image.setX(area.getX());
		image.setY(area.getY());
		image.setW(area.getWidth());
		image.setH(area.getHeight());
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setImage(image);
		send(wrapper);
	}

	private void send(MessageWrapper wrapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String string = mapper.writeValueAsString(wrapper);
			session.send(string);
			connectionPresenter.messageSent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
