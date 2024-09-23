document.addEventListener('DOMContentLoaded', function() {
    let isUsernameAvailable = false;
    let isAutenticationAvailable = false;
    // username 중복 확인
    const confirmDuplicationBtn = document.querySelector('.check-id-btn');
    if (confirmDuplicationBtn) {
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
            } else {
                isUsernameAvailable = false;
                alert('이미 존재하는 회원 이름입니다.');
            }
        });
    }

    //이메일 인증번호 보내는 로직
    const emailSenderBtn = document.querySelector('.check-email-btn');

    if (emailSenderBtn) {
        emailSenderBtn.addEventListener("click", sendEmail);
    } else {
        alert(".check-email-btn DOM 생성되기 전에 스크립트 발생");
    }

    async function sendEmail() {
        const email = document.getElementById("email").value;

        if (!email) {
            alert('이메일 주소를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch('/send-mail', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            var respAuthenticationNum;
            if (data.success) {
                respAuthenticationNum = data.num;
                isAutenticationAvailable = true;
                alert('이메일 전송이 성공했습니다.');
            } else {
                isAutenticationAvailable = false;
                alert('이메일 전송이 실패했습니다. ' + (data.error || ''));
            }
        } catch (error) {
            console.error('Error:', error);
            alert('이메일 전송 중 오류가 발생했습니다: ' + error.message);
        }
    }

    // 아이디 입력 필드 변경 시 플래그만 업데이트
    const usernameInput = document.getElementById("username");
    if (usernameInput) {
        usernameInput.addEventListener("input", function() {
            if (this.value.trim() === '') {
                isUsernameAvailable = false;
            }
        });
    }

    

    // 회원가입 전체 로직
    async function join(){
        var username = document.getElementById("username").value;
        var email = document.getElementById("email").value;
        var password = document.getElementById("password").value;
        var confirm_password = document.getElementById("confirm_password").value;
        var authentication_number = document.getElementById("authentication_number").value;
        if (!username || !email || !password || !confirm_password || !authentication_number) {
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
        
        // 이메일 인증 버튼 검증
        if (!isAutenticationAvailable){
            alert("이메일 인증 번호 발송을 해야합니다.")
            return false;
        }

        const isMailValid = await mailCheck(email, authentication_number);
        // 이메일 인증 번호 검증
        if (!isMailValid){
            return false;
        }

        return true;
    }

    // 비밀번호 검증
    function validatePassword() {
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

    async function mailCheck(email, authenticationNumber) {
        const response = await fetch(`/mailCheck?email=${encodeURIComponent(email)}&userNumber=${encodeURIComponent(authenticationNumber)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        const isMatch = await response.json();
        
        if(isMatch){
            return true;
        }
        else {
            alert("인증 번호가 일치하지 않습니다.")
            return false;
        }
    }

    // 폼 제출 이벤트 리스너
    const signUpForm = document.querySelector('.sign-up-form');
    if (signUpForm) {
        signUpForm.addEventListener('submit', async function (event) {
            event.preventDefault(); // 기본 폼 제출 동작 막기

            const submitButton = this.querySelector('input[type="submit"]');
            submitButton.disabled = true; // 버튼 클릭 방지
            try {
                const result = await join(); 
                if (result){
                    this.submit();
                    alert("회원가입에 성공하셨습니다.");
                } else {
                    alert("not");
                }
            } catch(error) {
                console.error('Error during form submission:', error);
                alert('폼 제출 중 오류가 발생했습니다.');
            } finally {
                submitButton.disabled = false; // 버튼 클릭 방지 해제
            }
        });
    }
});