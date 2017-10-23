package model;

import model.message.Connect;
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
import model.message.VerifyConnect;
import model.message.VerifyCreateAccountAccess;
import model.message.VerifyCreateAnonymousAccess;

public class MessageWrapper {

	private Connect connect;

	private CreateAccountAccess createAccountAccess;
	
	private CreateAnonymousAccess createAnonymousAccess;
	
	private Identify identify;

	private Image image;
	
	private KeyEvent keyEvent;
	
	private MouseClick mouseClick;
	
	private RemoveAccountAccess removeAccountAccess;
	
	private RemoveAnonymousAccess removeAnonymousAccess;
	
	private Start start;
	
	private Stop stop;
	
	private VerifyConnect verifyConnect;
	
	private VerifyCreateAccountAccess verifyCreateAccountAccess;
	
	private VerifyCreateAnonymousAccess verifyCreateAnonymousAccess;

	public Connect getConnect() {
		return connect;
	}

	public void setConnect(Connect connect) {
		this.connect = connect;
	}

	public CreateAccountAccess getCreateAccountAccess() {
		return createAccountAccess;
	}

	public void setCreateAccountAccess(CreateAccountAccess createAccountAccess) {
		this.createAccountAccess = createAccountAccess;
	}

	public CreateAnonymousAccess getCreateAnonymousAccess() {
		return createAnonymousAccess;
	}

	public void setCreateAnonymousAccess(CreateAnonymousAccess createAnonymousAccess) {
		this.createAnonymousAccess = createAnonymousAccess;
	}

	public Identify getIdentify() {
		return identify;
	}

	public void setIdentify(Identify identify) {
		this.identify = identify;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

	public void setKeyEvent(KeyEvent keyEvent) {
		this.keyEvent = keyEvent;
	}

	public MouseClick getMouseClick() {
		return mouseClick;
	}

	public void setMouseClick(MouseClick mouseClick) {
		this.mouseClick = mouseClick;
	}

	public RemoveAccountAccess getRemoveAccountAccess() {
		return removeAccountAccess;
	}

	public void setRemoveAccountAccess(RemoveAccountAccess removeAccountAccess) {
		this.removeAccountAccess = removeAccountAccess;
	}

	public RemoveAnonymousAccess getRemoveAnonymousAccess() {
		return removeAnonymousAccess;
	}

	public void setRemoveAnonymousAccess(RemoveAnonymousAccess removeAnonymousAccess) {
		this.removeAnonymousAccess = removeAnonymousAccess;
	}

	public Start getStart() {
		return start;
	}

	public void setStart(Start start) {
		this.start = start;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

	public VerifyConnect getVerifyConnect() {
		return verifyConnect;
	}

	public void setVerifyConnect(VerifyConnect verifyConnect) {
		this.verifyConnect = verifyConnect;
	}

	public VerifyCreateAccountAccess getVerifyCreateAccountAccess() {
		return verifyCreateAccountAccess;
	}

	public void setVerifyCreateAccountAccess(VerifyCreateAccountAccess verifyCreateAccountAccess) {
		this.verifyCreateAccountAccess = verifyCreateAccountAccess;
	}

	public VerifyCreateAnonymousAccess getVerifyCreateAnonymousAccess() {
		return verifyCreateAnonymousAccess;
	}

	public void setVerifyCreateAnonymousAccess(VerifyCreateAnonymousAccess verifyCreateAnonymousAccess) {
		this.verifyCreateAnonymousAccess = verifyCreateAnonymousAccess;
	}

}
