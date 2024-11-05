document.addEventListener('DOMContentLoaded', function() {
    const chatbotToggle = document.getElementById('chatbot-toggle');
    const chatbotContainer = document.getElementById('chatbot-container');
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-button');
    const chatbotMessages = document.getElementById('chatbot-messages');

    chatbotToggle.addEventListener('click', function() {
        chatbotContainer.classList.toggle('chatbot-closed');
    });

    function addMessage(message, isUser = false) {
        const messageElement = document.createElement('div');
        messageElement.textContent = message;
        messageElement.classList.add(isUser ? 'user-message' : 'bot-message');
        chatbotMessages.appendChild(messageElement);
        chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
    }

    function handleUserInput() {
        const message = userInput.value.trim();
        if (message) {
            addMessage(message, true);
            userInput.value = '';
            // 여기에 챗봇 응답 로직을 추가하세요
            setTimeout(() => {
                addMessage("죄송합니다, 아직 응답 기능이 구현되지 않았습니다. 곧 업데이트하겠습니다!");
            }, 1000);
        }
    }

    sendButton.addEventListener('click', handleUserInput);
    userInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            handleUserInput();
        }
    });
});