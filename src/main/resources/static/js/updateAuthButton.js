function updateAuthButtons() {
    fetch('/api/auth/status')
        .then(response => response.text())
        .then(isAuthenticated => {
            const authContent = document.querySelector('.nav-right');
            // 기존 로그인/로그아웃 버튼 제거
            const existingAuthButton = authContent.querySelector('.auth-button, form');
            if (existingAuthButton) {
                existingAuthButton.parentElement.remove();
            }

            if(isAuthenticated === 'authenticated') {
                const logoutDiv = document.createElement('div');
                // form을 그대로 submit하도록 변경
                logoutDiv.innerHTML = `
                    <form action="/api/logout" method="post">
                        <input type="hidden" name="_csrf" value="${document.querySelector("meta[name='_csrf']").content}" />
                        <button type="submit" class="auth-button" id="logoutButton">로그아웃</button>
                    </form>
                `;
                authContent.insertBefore(logoutDiv, authContent.firstChild);
            } else if(isAuthenticated === 'anonymous') {
                const authDiv = document.createElement('div');
                authDiv.innerHTML = `
                    <button class="auth-button" id="authButton">
                        <a href="/loginForm">로그인/회원가입</a>
                    </button>
                `;
                authContent.insertBefore(authDiv, authContent.firstChild);
            }
        })
        .catch(error => {
            console.error('Authentication status check failed:', error);
        });
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', updateAuthButtons);