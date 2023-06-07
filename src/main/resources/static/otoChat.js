// 1:1 채팅
const enterOTOChatting = () => {
    // a 태그로 페이지 이동 시 stompClient 값이 초기화되므로 하는 행동
    // 페이지 이동이 아닌, 한 페이지에서 동작하게 함.
    fetch('/otoChat.html')
        .then(res => res.text())
        .then(html => {
            const content = document.getElementById('dorm-chat');
            content.innerHTML = html;
        })
        .catch(err => console.error(err));

}
