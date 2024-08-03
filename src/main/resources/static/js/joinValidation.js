let isUsernameAvailable = false;

// username 중복 확인
// 비동기 실행 방식
const confirmDuplicationBtn = document.querySelector('.check-id-btn');

confirmDuplicationBtn.addEventListener("click", async function confirmDuplication(){
    var username = document.getElementById("username").value;

    if (!username) {
        alert('아이디를 입력해주세요.');
        isUsernameAvailable = false;
        return;
    }

    if (username !== username.trim()){
        alert("아이디의 공백을 제거해주세요.");
        isUsernameAvailable = false;
        return;
    }

    // await를 사용하며 비동기 함수가 완료 될때까지 기다리게함. 중복 확인이 거치고 나서 다음 단계를 진행해야하기 때문.
    const response = await fetch('/api/check-username', {
        method: 'POST',
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify({username: username})
    });

    const data = await response.json();
    if (!data.exists) {
        isUsernameAvailable = true;
        alert('사용하실 수 있는 회원 이름입니다.');
        return;
    } else {
        isUsernameAvailable = false
        alert('이미 존재하는 회원 이름입니다.');
        return;
    }
});


// 아이디 입력 필드 변경 시 플래그만 업데이트
document.getElementById("username").addEventListener("input", function() {
    // 사용자 이름 입력이 비어있는 경우 중복 확인을 하지 않음
    if (this.value.trim() === '') {
        isUsernameAvailable = false;
    }
});



// 비밀번호 검증
validatePassword = () => {
    var password = document.getElementById("password").value;
    var confirm_password = document.getElementById("confirm_password").value;

    if (password !== password.trim()){
        alert("비밀번호에 있는 공백을 제거해주세요.");
        return false;
    }

    if (password != confirm_password) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        return false;
    }
    return true;
}


// 회원가입 전체 로직
function join(){
    var username = document.getElementById("username").value;
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var confirm_password = document.getElementById("confirm_password").value;

    if (!username || !email || !password || !confirm_password) {
        alert("모든 필드를 입력해주세요.");
        return false;
    }

    if (!isUsernameAvailable) {
        alert("중복 확인을 체크해주세요.");
        return false;
    }

    // 비밀번호 확인 검증
    if (!validatePassword()) {
        return false;
    }

    
    return true;
}

// 폼 제출 이벤트 리스너
document.querySelector('.sign-up-form').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 폼 제출 동작 막기

    const submitButton = this.querySelector('input[type="submit"]');
    submitButton.disabled = true; // 버튼 클릭 방지

    const result = join(); 
    if (result){
        this.submit();
    }

    submitButton.disabled = false; // 버튼 클릭 방지 해제
});