package network;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import application.Application;
import capture.Capturer;
import model.ImagePiece;
import model.Rectangle;
import model.json.CreateAccountAccess;
import model.json.CreateAccountAccessWrapper;
import model.json.CreateAnonymousAccess;
import model.json.CreateAnonymousAccessWrapper;
import model.json.Identify;
import model.json.IdentifyWrapper;
import model.json.Image;
import model.json.ImageWrapper;
import model.json.ImagesWrapper;
import model.json.KeyEvent;
import model.json.KeyEventWrapper;
import model.json.MessageWrapper;
import model.json.MouseClick;
import model.json.MouseClickWrapper;
import model.json.RemoveAccountAccess;
import model.json.RemoveAccountAccessWrapper;
import model.json.RemoveAnonymousAccess;
import model.json.RemoveAnonymousAccessWrapper;
import model.json.Start;
import model.json.StartWrapper;
import model.json.Stop;
import model.json.StopWrapper;
import model.json.VerifyCreateAccountAccess;
import model.json.VerifyCreateAccountAccessWrapper;
import model.json.VerifyCreateAnonymousAccess;
import model.json.VerifyCreateAnonymousAccessWrapper;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class MessageHandler {

	Application application;

	Capturer capturer;

	AccessPresenter accessPresenter;

	ConnectionPresenter connectionPresenter;

	private Session session;

	public MessageHandler(Session session) {
		this.session = session;
	}

	public void handleMessage(String message) {
		connectionPresenter.messageReceived();
		try {
			ObjectMapper mapper = new ObjectMapper();

			try {
				VerifyCreateAnonymousAccessWrapper verifyCreateAnonymousAccessWrapper = mapper.readValue(message,
						VerifyCreateAnonymousAccessWrapper.class);
				parseVerifyCreateAnonymousAccess(verifyCreateAnonymousAccessWrapper.getVerifyCreateAnonymousAccess());
				return;
			} catch (Exception e) {
			}

			try {
				VerifyCreateAccountAccessWrapper verifyCreateAccountAccessWrapper = mapper.readValue(message,
						VerifyCreateAccountAccessWrapper.class);
				parseVerifyCreateAccountAccess(verifyCreateAccountAccessWrapper.getVerifyCreateAccountAccess());
				return;
			} catch (Exception e) {
			}

			try {
				StartWrapper startWrapper = mapper.readValue(message, StartWrapper.class);
				parseStart(startWrapper.getStart());
				return;
			} catch (Exception e) {
			}

			try {
				StopWrapper stopWrapper = mapper.readValue(message, StopWrapper.class);
				parseStop(stopWrapper.getStop());
				return;
			} catch (Exception e) {
			}

			try {
				MouseClickWrapper mouseClickWrapper = mapper.readValue(message, MouseClickWrapper.class);
				parseMouseClick(mouseClickWrapper.getMouseClick());
				return;
			} catch (Exception e) {
			}
			
			try {
				KeyEventWrapper keyEventWrapper = mapper.readValue(message, KeyEventWrapper.class);
				parseKeyEvent(keyEventWrapper.getKeyEvent());
				return;
			} catch (Exception e) {
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseKeyEvent(KeyEvent keyEvent) {
		application.keyEvent(keyEvent);
	}

	private void parseMouseClick(MouseClick mouseClick) {
		application.mouseClick(mouseClick);
	}

	private void parseStop(Stop stop) {
		if (stop.isStop()) {
			capturer.stopCapture();
		}
	}

	private void parseStart(Start start) {
		if (start.isStart()) {
			capturer.startCapture();
		}
	}

	private void parseVerifyCreateAccountAccess(VerifyCreateAccountAccess verifyCreateAccountAccess) {
		boolean success = verifyCreateAccountAccess.isSuccess();
		accessPresenter.accountConnected(success);
	}

	private void parseVerifyCreateAnonymousAccess(VerifyCreateAnonymousAccess verifyCreateAnonymousAccess) {
		boolean success = verifyCreateAnonymousAccess.isSuccess();
		accessPresenter.anonymousConnected(success);
	}

	public void sendCreateAnonymousAccess(String numericId, String numericPassword) {
		CreateAnonymousAccess createAnonymousAccess = new CreateAnonymousAccess();
		createAnonymousAccess.setNumericId(numericId);
		createAnonymousAccess.setNumericPassword(numericPassword);
		CreateAnonymousAccessWrapper createAnonymousAccessWrapper = new CreateAnonymousAccessWrapper();
		createAnonymousAccessWrapper.setCreateAnonymousAccess(createAnonymousAccess);
		send(createAnonymousAccessWrapper);
	}

	public void sendRemoveAnonymousAccess() {
		RemoveAnonymousAccess removeAnonymousAccess = new RemoveAnonymousAccess();
		removeAnonymousAccess.setRemove(true);
		RemoveAnonymousAccessWrapper removeAnonymousAccessWrapper = new RemoveAnonymousAccessWrapper();
		removeAnonymousAccessWrapper.setRemoveAnonymousAccess(removeAnonymousAccess);
		send(removeAnonymousAccessWrapper);

	}

	public void sendCreateAccountAccess(String username, String password) {
		CreateAccountAccess createAccountAccess = new CreateAccountAccess();
		createAccountAccess.setUsername(username);
		createAccountAccess.setPassword(password);
		CreateAccountAccessWrapper createAccountAccessWrapper = new CreateAccountAccessWrapper();
		createAccountAccessWrapper.setCreateAccountAccess(createAccountAccess);
		send(createAccountAccessWrapper);
	}

	public void sendRemoveAccountAccess() {
		RemoveAccountAccess removeAccountAccess = new RemoveAccountAccess();
		removeAccountAccess.setRemove(true);
		RemoveAccountAccessWrapper removeAccountAccessWrapper = new RemoveAccountAccessWrapper();
		removeAccountAccessWrapper.setRemoveAccountAccess(removeAccountAccess);
		send(removeAccountAccessWrapper);
	}

	public void sendImagePiece(ImagePiece imagePiece) {
		Image image = new Image();
		image.setData(imagePiece.getEncodedString());
		Rectangle area = imagePiece.getArea();
		image.setX(area.getX());
		image.setY(area.getY());
		image.setW(area.getWidth());
		image.setH(area.getHeight());
		ImageWrapper imageWrapper = new ImageWrapper();
		imageWrapper.setImage(image);
		send(imageWrapper);
	}

	public void sendImagePieces(List<ImagePiece> imagePieces) {
		ImagesWrapper imagesWrapper = new ImagesWrapper();
		List<Image> images = new ArrayList<>();
		for (ImagePiece imagePiece : imagePieces) {
			Image image = new Image();
			image.setData(imagePiece.getEncodedString());
			Rectangle area = imagePiece.getArea();
			image.setX(area.getX());
			image.setY(area.getY());
			image.setW(area.getWidth());
			image.setH(area.getHeight());
			images.add(image);
		}
		imagesWrapper.setImages(images);
		send(imagesWrapper);
	}

	public void sendIndentify() {
		Identify identify = new Identify();
		identify.setType("sender");
		identify.setDeviceName(application.getDeviceName());
		identify.setDeviceWidth(application.getDeviceWidth());
		identify.setDeviceHeight(application.getDeviceHeight());
		IdentifyWrapper identifyWrapper = new IdentifyWrapper();
		identifyWrapper.setIdentify(identify);
		send(identifyWrapper);
	}

	private void send(MessageWrapper messageWrapper) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String string = objectMapper.writeValueAsString(messageWrapper);
			this.session.getAsyncRemote().sendText(string);
			connectionPresenter.messageSent();
		} catch (Exception e) {

		}
	}

}
