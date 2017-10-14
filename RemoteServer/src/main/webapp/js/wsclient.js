$(document).ready(function() {
	connect();
	$('body').on('contextmenu', '#canvas', function(e) {
		return false;
	});
	$("#increaseSizeButton").click(function(){
		increaseCanvasSize();
	}); 
	$("#decreaseSizeButton").click(function(){
		decreaseCanvasSize();
	});
	$("#sizeLabelButton").click(function(){
		originalCanvasSize();
	});
	$("#mouseButton").click(function(){
		mouseButton();
	});
	$("#keyboardButton").click(function(){
		keyboardButton();
	});
	
//	function onMouseDown(event) {
//		if (mouseCaptureEnabled) {
//			var pos = getMousePos(canvas, event);
//			var button = event.button;
//			sendMouseClick(pos.x, pos.y, button);
//		}
//	}
	
	$("#canvas").mousedown(function(e){
		if (mouseCaptureEnabled) {
			var pos = getMousePos(canvas, event);
			var button = event.button;
			sendMouseClick(0, pos.x, pos.y, button);
		}
	});
	
	$("#canvas").mouseup(function(e){
		if (mouseCaptureEnabled) {
			var pos = getMousePos(canvas, event);
			var button = event.button;
			sendMouseClick(1, pos.x, pos.y, button);
		}
	});
	
	$(document).keydown(function(e){
		if (keyboardCaptureEnabled) {
			sendKeyEvent(0, e.keyCode, e.originalEvent.key);
		}
	});
	$(document).keydown(function(e){
		if (keyboardCaptureEnabled) {
			sendKeyEvent(1, e.keyCode, e.originalEvent.key);
		}
	});
	$(document).keypress(function(e){
		if (keyboardCaptureEnabled) {
			sendKeyEvent(2, e.keyCode, e.originalEvent.key);
		}
	});
});

var socket;
var receiving = false; 
var mouseCaptureEnabled = false;
var keyboardCaptureEnabled = false;

function increaseCanvasSize() {
	var canvas = document.getElementById("canvas");
	canvas.width = canvas.width * 1.1;
	canvas.height = canvas.height * 1.1;
	setSizeLabel();
}

function decreaseCanvasSize() {
	var canvas = document.getElementById("canvas");
	canvas.width = canvas.width * 0.9;
	canvas.height = canvas.height * 0.9;
	setSizeLabel();
}

function originalCanvasSize() {
	var canvas = document.getElementById("canvas");
	canvas.width = imageWidth;
	canvas.height = imageHeight;
	setSizeLabel();
}

function mouseButton() {
	var on = "Mouse: ON";
	var off = "Mouse: OFF";
	text = $("#mouseButton").text();
	if (text == on) {
		$("#mouseButton").text(off);
		mouseCaptureEnabled = false;
	} else {
		$("#mouseButton").text(on);
		mouseCaptureEnabled = true;
	}
}

function keyboardButton() {
	var on = "Keyboard: ON";
	var off = "Keyboard: OFF";
	
	text = $("#keyboardButton").text();
	if (text == on) {
		$("#keyboardButton").text(off);
		keyboardCaptureEnabled = false;
	} else {
		$("#keyboardButton").text(on);
		keyboardCaptureEnabled = true;
	}
}

function setSizeLabel() {
	var canvas = document.getElementById("canvas");
	var header = document.getElementById("sizeLabelButton");
	header.innerText = "Size: " + canvas.width + " x " + canvas.height;
}

function connect() {
	if (!window.WebSocket) {
		alert("WebSocket not supported by this browser!");
		return;
	}
	var address = 'ws://' + server;
	console.log("Connecting to " + address);
	socket = new WebSocket(address);
	socket.onmessage = onMessage;
	socket.onopen = onOpen;
	socket.onclose = onClose;
}

function onOpen() {
	console.log("Websocket connected.");

	sendIdentify();

	if (!(sessionId.length === 0)) {
		sendConnect();
	} else {
		alert("No session id!");
	}

	originalCanvasSize();
}

function onClose() {
	console.log("Websocket disconnected.");
}

function onMessage(msg) {
	var json = JSON.parse(msg.data);
	if ("identify" in json) {
		console.log("onMessage: " + JSON.stringify(json));
		var idJson = json["identify"];
		var id = idJson["id"];
		sendIdToServer(id);
	} else if ("verifyConnect" in json) {
		console.log("onMessage: " + JSON.stringify(json));
		var verifyJson = json["verifyConnect"];
		var success = verifyJson["success"];
		if (success) {
			receiving = true;
		}
	} else if ("image" in json) {
		var imageJson = json["image"];
		var data = imageJson["data"];
		var x = imageJson["x"];
		var y = imageJson["y"];
		var w = imageJson["w"];
		var h = imageJson["h"];
		drawImage(data, x, y, w, h);
	}
}

function getMousePos(canvas, evt) {
	var rect = canvas.getBoundingClientRect();
	return {
		x : evt.clientX - rect.left,
		y : evt.clientY - rect.top
	};
}

function sendIdentify() {
	var json = {
		"identify" : {
			"type" : "receiver"
		}
	};
	console.log("sending: " + JSON.stringify(json));
	socket.send(JSON.stringify(json));
}

function sendConnect() {
	var json = {
		"connect" : {
			"id" : sessionId
		}
	};
	console.log("sending: " + JSON.stringify(json));
	socket.send(JSON.stringify(json));
}

function sendMouseClick(type, x, y, button) {
	var canvas = document.getElementById("canvas");
	var d = canvas.width / imageWidth;
	x = Math.round(x / d);
	y = Math.round(y / d);
	var json = {
		"mouseClick" : {
			"type" : type,
			"x" : x,
			"y" : y,
			"button" : button
		}
	};
	console.log("sending: " + JSON.stringify(json));
	socket.send(JSON.stringify(json));
}

function sendKeyEvent(type, code, character) {
    var json = {
		"keyEvent" : {
			"type" : type,
			"code" : code,
			"character" : character
		}
	};
	console.log("sending: " + JSON.stringify(json));
	socket.send(JSON.stringify(json));
}

function drawImage(data, x, y, w, h) {
	var img = new Image();
	img.onload = function() {
		var canvas = document.getElementById("canvas");
		var ctx = canvas.getContext("2d");
		var d = canvas.width / imageWidth;
		x = x * d;
		y = y * d;
		w = w * d;
		h = h * d;
		ctx.drawImage(img, x, y, w, h);y
	}
	img.src = 'data:image/png;base64,' + data;
}

function parseImage(data) {
	var img = new Image();
	img.onload = function() {
		var canvas = document.getElementById("canvas");
		canvas.width = img.width;
		canvas.height = img.height;
		var ctx = canvas.getContext("2d");
	}
	img.src = 'data:image/png;base64,' + data;
}
