// 1:1 채팅
const enterClassChattingRoom = () => {

    let socket = new SockJS(`/chat/`);

    // SockJS를 바탕으로 stompClient 생성
    stompClient = Stomp.over(socket);

    // stomp.js 에서 제공하는 콘솔창 로그 설정을 제어할 수 있는 함수
    // 개발 시에는 로그를 보며, 이해하고
    // 배포 시 아래처럼 설정하여 콘솔창에 보이지 않게 설정한다.
    stompClient.debug = (res) => {};

    const room= $("#class_id").val()
    // 기숙사 채팅방 입장(내부적으로 웹소켓 연결)
    stompClient.connect(header,
        // 연결 성공 시 실행하는 함수
        (res) => {
            // console.log('Connected: ' + res);
            // 기숙사 채팅방을 구독 => 기숙사 채팅방으로 오는 메세지를 수신하겠다는 의미
//            const room= $("#class_id").val()
            stompClient.subscribe(`/sub/class/${room}`, (stompResponse) => {

                // 메세지 전송 성공 시 메세지 내용을 전달
                if (stompResponse.command === "MESSAGE") {
                    receiveMessage(JSON.parse(stompResponse.body));
                }
            });

//            // a 태그로 페이지 이동 시 stompClient 값이 초기화되므로 하는 행동
//            // 페이지 이동이 아닌, 한 페이지에서 동작하게 함.
            fetch('/classChatRoom.html')
                .then(res => res.text())
                .then(html => {
                    const content = document.getElementById('dorm-chat');
                    content.innerHTML = html;
                })
                .catch(err => console.error(err));
//
            // 최근 채팅 내역을 불러 오는 부분
            // 임의로 페이지를 설정함(가장 최근 10개 -> 페이지 0)
            // 무한 스크롤 등을 구현하여, page 별로 요청하면 됨.
            const page = 0;

//            const room= $("#class_id").val()
            fetch(`http://localhost:8080/chatting/class/${room}/${page}`)
                .then(res => res.json())
                .then(data => {
                    data.result.classChatList.reverse().forEach((message) => {
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
function sendMessage_class() {
    const message = {
        memberNickname: $("#sender").val(),
        messageContent: $("#message").val(),
    }

    // 첫 번째 인자: 메시지를 보내기 위한 url
    // 두 번째 인자: 헤더
    // 세 번쨰 인자: 보낼 메세지
    // 메세지를 직렬화해서 보내야 함.

    const room= $("#class_id").val()
    console.log(room)
    stompClient.send(`/pub/class/${room}`, header, JSON.stringify(message));
}

// 메세지 송신 성공하면, 메세지를 반환함.
function receiveMessage(message) {

    // messageRes 객체
    console.log(message)

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