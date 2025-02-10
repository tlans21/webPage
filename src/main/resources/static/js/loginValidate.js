async function validateLogin() {
    const username = document.getElementById("LoginUsername").value;
    const password = document.getElementById("LoginPassword").value;

    if (!username || !password) {
        alert('아이디나 패스워드를 입력해주세요');
        return false;
    }

    // CSRF 토큰 가져오기 (서버에서 전달하는 방식 사용)
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content'); // 예시: meta 태그 사용

    if (!csrfToken) {
        console.error("CSRF token not found!");
        alert("CSRF 토큰을 찾을 수 없습니다. 다시 시도해주세요.");
        return false;
    }
    console.log(csrfToken);
    const response = await fetch('/api/check-user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken // 서버에서 사용하는 CSRF 토큰 헤더 이름에 맞게 변경
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });

    const data = await response.json();
    if (data.exists) {
        return true;
    } else {
        alert('회원정보가 일치하지 않습니다.');
        return false;
    }
}


const loginForm = document.querySelector(".sign-in-form");

loginForm.addEventListener("submit", async function(event) {
    event.preventDefault(); // 기본 제출 방지

    const isValid = await validateLogin();
    if (isValid) {
        this.submit(); // 유효하면 폼 제출
    }
});