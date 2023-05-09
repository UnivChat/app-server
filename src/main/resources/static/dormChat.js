let stompClient = null;
let roomId = Math.floor(Math.random() * 1 + 1);
let jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3aGVoZGduMTAwMUBrb29rbWluLmFjLmtyIiwiaWF0IjoxNjgzNjEwNTQ0LCJleHAiOjE3MTUxNDY1NDR9.U6I3WKqv4tUuAiyBAODetQmxOtdpMJQO-VknA0_uZFhb6aFGFzMuCWCdM8hcOFALTjbqn-tNaahcjwbO2OuBRA';
let header = {
        // jwt 토큰을 인증 헤더의 Bearer에 담음
        Authorization: `Bearer ${jwtToken}`
    };

// 기숙사 채팅
const enterDormChattingRoom = () => {
    let socket = new SockJS(`/chat/`);
    stompClient = Stomp.over(socket);
    // 기숙사 채팅방 입장
    stompClient.connect(header,
        // 연결 성공 시 실행하는 함수
        (res) => {
        // console.log('Connected: ' + res);
        // 기숙사 채팅방을 구독 => 기숙사 채팅방으로 오는 메세지를 수신하겠다는 의미
        stompClient.subscribe(`/sub/dorm/${roomId}`, function (stompResponse) {

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

        // 최근 채팅 내역을 불러 오는 부분
        // 임의로 페이지를 설정함(가장 최근 10개 -> 페이지 0)
        // 무한 스크롤 등을 구현하여, page 별로 요청하면 됨.
        const page = 0;
        fetch(`http://localhost:8080/dorm/chat/${roomId}/${page}`)
            .then(res => res.json())
            .then(data => {
                data.result.reverse().forEach((message) => {
                    $("#message-list").append("<tr><td>"
                        + message.messageSendingTime + " / "
                        + message.memberNickname + " / "
                        + message.messageContent + "</td></tr>");
                })
            })

        // 연결 실패 시 실행할 함수
        ,(err) => {
            alert("연결에 실패 했습니다!");
        }
    });

    // 아래 문장 실행 시 응답을 콘솔창에 보여주는
    // 기본 설정을 제어할 수 있음.
    // 아무 내용도 적지 않았으므로 콘솔창에 나오지 않음.
    stompClient.debug = (res) => {};
}

// 메세지 송신을 위해 실행해야 하는 함수
function sendMessage() {
    console.log(roomId)

    const message = {
        roomId: roomId,
        memberNickname: $("#sender").val(),
        messageContent: $("#message").val(),
    }

    // 첫 번째 인자: 메시지를 보내기 위한 url
    // 두 번째 인자: 헤더
    // 세 번쨰 인자: 보낼 메세지
    // 메세지를 직렬화해서 보내야 함.
    stompClient.send(`/pub/dorm/${roomId}`, header, JSON.stringify(message));
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
    console.log("Disconnected");
}