let stompClient = null;
let jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3aGVoZGduMTAwMUBrb29rbWluLmFjLmtyIiwiaWF0IjoxNjgzNjEwNTQ0LCJleHAiOjE3MTUxNDY1NDR9.U6I3WKqv4tUuAiyBAODetQmxOtdpMJQO-VknA0_uZFhb6aFGFzMuCWCdM8hcOFALTjbqn-tNaahcjwbO2OuBRA';
// jwt 토큰을 인증 헤더의 Bearer에 담음
let header = { Authorization: `Bearer ${jwtToken}`};

// 기숙사 채팅
const enterDormChattingRoom = () => {

    // jwt 토큰 확인 후 아래 로직을 실행해야 한다.
    // 웹소켓에서 컨트롤하기 까다로움
    // 해당 기숙사인지 확인하는 api에서
    // jwt 토큰을 확인하면 해결해야 한다고 생각

    let socket = new SockJS(`/chat/`);

    // SockJS를 바탕으로 stompClient 생성
    stompClient = Stomp.over(socket);

    // stomp.js 에서 제공하는 콘솔창 로그 설정을 제어할 수 있는 함수
    // 개발 시에는 로그를 보며, 이해하고
    // 배포 시 아래처럼 설정하여 콘솔창에 보이지 않게 설정한다.
    stompClient.debug = (res) => {};

    // 기숙사 채팅방 입장(내부적으로 웹소켓 연결)
    stompClient.connect(header,
        // 연결 성공 시 실행하는 함수
        (res) => {
            // console.log('Connected: ' + res);
            // 기숙사 채팅방을 구독 => 기숙사 채팅방으로 오는 메세지를 수신하겠다는 의미
            stompClient.subscribe(`/sub/dorm`, (stompResponse) => {

                // 메세지 전송 성공 시 메세지 내용을 전달
                if (stompResponse.command === "MESSAGE") {
                    receiveMessage(JSON.parse(stompResponse.body));
                }
            });

            // a 태그로 페이지 이동 시 stompClient 값이 초기화되므로 하는 행동
            // 페이지 이동이 아닌, 한 페이지에서 동작하게 함.
            fetch('/dormChat.html')
                .then(res => res.text())
                .then(html => {
                    const content = document.getElementById('dorm-chat');
                    content.innerHTML = html;
                })
                .catch(err => console.error(err));

            // 초기 채팅 메세지 로드
            loadDormChatMessages(-1);
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

// 채팅 메세지를 로드하는 함수
function loadDormChatMessages(page) {
    fetch(`http://localhost:8080/chatting/dorm/${page}`)
        .then((res) => res.json())
        .then((data) => {
            data.result.dormChatRes.forEach((message) => {  //.reverse()제거
                $("#message-list").prepend(
                    "<tr><td>" +
                    message.messageSendingTime +
                    " / " +
                    message.memberNickname +
                    " / " +
                    message.messageContent +
                    "</td></tr>"
                );
            });
        });
}


// 메세지 송신을 위해 실행해야 하는 함수
function sendMessage_dorm() {
    const message = {
        memberNickname: $("#sender").val(),
        messageContent: $("#message").val(),
        //
    }

    // 첫 번째 인자: 메시지를 보내기 위한 url
    // 두 번째 인자: 헤더
    // 세 번쨰 인자: 보낼 메세지
    // 메세지를 직렬화해서 보내야 함.
    stompClient.send(`/pub/dorm`, header, JSON.stringify(message));
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