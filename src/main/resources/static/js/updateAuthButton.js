function updateAuthButtons() {
    fetch('/auth/status')
        .then(response => response.text())
        .then(isAuthenticated => {
            const authContent = document.querySelector('.nav-right');
            // 기존 로그인/로그아웃 버튼 제거
            const existingAuthButton = authContent.querySelector('.auth-button, form');
            if (existingAuthButton) {
                existingAuthButton.parentElement.remove();
            }

            if(isAuthenticated === 'anonymous') {
                const authDiv = document.createElement('div');
                authDiv.innerHTML = `
                    <button class="auth-button" id="authButton">
                        <a href="/loginForm">로그인/회원가입</a>
                    </button>
                `;
                // 맨 앞에 추가
                authContent.insertBefore(authDiv, authContent.firstChild);
            } else {
                const logoutDiv = document.createElement('div');
                logoutDiv.innerHTML = `
                    <form action="/api/logout" method="post">
                        <button type="submit" class="auth-button" id="logoutButton">로그아웃</button>
                    </form>
                `;
                // 맨 앞에 추가
                authContent.insertBefore(logoutDiv, authContent.firstChild);
            }
        });
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', updateAuthButtons);