function updateAuthButtons() {
    fetch('/auth/status')
        .then(response => response.text())
        .then(isAuthenticated => {
            const authContent = document.querySelector('.nav-right');
            if(isAuthenticated === 'anonymous') {
                authContent.innerHTML = `
                    <div>
                        <button class="auth-button" id="authButton">
                            <a href="/loginForm">로그인/회원가입</a>
                        </button>
                    </div>
                `;
            } else {
                authContent.innerHTML = `
                    <div>
                        <form action="/api/logout" method="post">
                            <button type="submit" class="auth-button" id="logoutButton">로그아웃</button>
                        </form>
                    </div>
                `;
            }
        });
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', updateAuthButtons);
