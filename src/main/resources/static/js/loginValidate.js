async function validateLogin() {
    var username = document.getElementById("LoginUsername").value;
    var password = document.getElementById("LoginPassword").value;
    
    if (!username || !password){
        alert('아이디나 패스워드를 입력해주세요');
        return false;
    }

    const response = await fetch('/api/check-user', {
        method: 'POST',
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify({username: username, password: password})
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