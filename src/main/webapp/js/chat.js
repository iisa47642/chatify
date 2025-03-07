// Функция для автоматического обновления чата
function refreshChat() {
    const urlParams = new URLSearchParams(window.location.search);
    const receiverId = urlParams.get('receiverId');

    // Периодическое обновление каждые 5 секунд
    setInterval(() => {
        fetch(`chat?receiverId=${receiverId}`)
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                const newMessages = doc.querySelector('.messages').innerHTML;
                document.querySelector('.messages').innerHTML = newMessages;
                // Прокрутка к последнему сообщению
                const messagesDiv = document.querySelector('.messages');
                messagesDiv.scrollTop = messagesDiv.scrollHeight;
            })
            .catch(error => console.error('Error refreshing chat:', error));
    }, 5000);
}

// Вызываем функцию при загрузке страницы
document.addEventListener('DOMContentLoaded', refreshChat);