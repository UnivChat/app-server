//let stompClient = null;
//let jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGNhdGhvbGljLmFjLmtyIiwiaWF0IjoxNjgzODgxMTMwLCJleHAiOjE3MTU0MTcxMzB9.A89PyNVZcbfOF4KQh8Mf6n7766QzPiU3NzLjkRiieWY6LMmsqQ1-jIKSPfLH5XFeaOlC7QigbEgWEBNrSgW-xw';
//// jwt 토큰을 인증 헤더의 Bearer에 담음
//let header = { Authorization: `Bearer ${jwtToken}`};

// 기숙사 채팅
const enterLiveChattingRoom = () => {
    let socket = new SockJS(`/chat/`);

    // SockJS를 바탕으로 stompClient 생성
    stompClient = Stomp.over(socket);

    // stomp.js 에서 제공하는 콘솔창 로그 설정을 제어할 수 있는 함수
//    stompClient.debug = (res) => {};

    // 기숙사 채팅방 입장(내부적으로 웹소켓 연결)
    stompClient.connect(header,
        // 연결 성공 시 실행하는 함수
        (res) => {
            // console.log('Connected: ' + res);
            // 기숙사 채팅방을 구독 => 기숙사 채팅방으로 오는 메세지를 수신하겠다는 의미
            stompClient.subscribe(`/sub/live`, (stompResponse) => {

                // 메세지 전송 성공 시 메세지 내용을 전달
                if (stompResponse.command === "MESSAGE") {
                    receiveMessage(JSON.parse(stompResponse.body));
                }
            });

            // a 태그로 페이지 이동 시 stompClient 값이 초기화되므로 하는 행동
            // 페이지 이동이 아닌, 한 페이지에서 동작하게 함.
            fetch('/liveChat.html')
                .then(res => res.text())
                .then(html => {
                    const content = document.getElementById('dorm-chat');
                    content.innerHTML = html;
                })
                .catch(err => console.error(err));

            // 최근 채팅 내역을 불러 오는 부분
            const page = 0;
            fetch(`http://localhost:8080/chatting/live/${page}`)
                .then(res => res.json())
                .then(data => {
                    data.result.reverse().forEach((message) => {
                        $("#message-list").append("<tr><td>"
                            + message.messageSendingTime + " / "
                            + message.memberNickname + " / "
                            + message.messageContent + "</td></tr>");
                    })
                })
            }

        // 연결 실패(ERROR) 시 실행할 함수
        ,(err) => {
            alert("연결에 실패 했습니다!");
        });

    // 웹소켓 연결 종료 시 실행되는 함수
    stompClient.ws.onclose = () => {
            console.log("웹소켓 연결이 종료되었습니다.");
    }
}


// 메세지 송신을 위해 실행해야 하는 함수
function sendMessage_live() {
    const message = {
        memberNickname: $("#sender").val(),
        messageContent: $("#message").val(),
    }

    // 첫 번째 인자: 메시지를 보내기 위한 url
    // 두 번째 인자: 헤더
    // 세 번쨰 인자: 보낼 메세지
    // 메세지를 직렬화해서 보내야 함.
    stompClient.send(`/pub/live`, header, JSON.stringify(message));
}

// 메세지 송신 성공하면, 메세지를 반환함.
function receiveMessage(message) {

    // messageRes 객체
    // console.log(message)

    $("#message-list").append("<tr><td>"
        + message.messageSendingTime + " / "
        + message.memberNickname + " / "
        + message.messageContent + "</td></tr>");
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    // console.log("Disconnected");
}