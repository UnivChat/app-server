var stompClient = null;
var roomId = Math.floor(Math.random() * 2 + 1)

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    // websocket 연결을 위한 endPont
    var socket = new SockJS(`/chat/`);
    console.log(roomId)
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // 메시지 받는 방 url(구독)
        stompClient.subscribe(`/sub/class/${roomId}`, function (greeting) {
            // console.log(greeting)
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    console.log(roomId)
    // 메시지를 보내기 위한 url(@MessageMapping으로 라우팅되는 url)
    stompClient.send(`/pub/room/${roomId}`, {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});