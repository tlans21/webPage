// 마이페이지 모달 스크립트
document.addEventListener('DOMContentLoaded', function() {
    const myPageButton = document.getElementById('myPageButton');
    const myPageModal = new bootstrap.Modal(document.getElementById('myPageModal'));
    const myPageContent = document.getElementById('myPageContent');

    myPageButton.addEventListener('click', function() {
        fetch('/api/v1/user/mypage-content')
            .then(response => response.text())
            .then(html => {
                myPageContent.innerHTML = html;
                myPageModal.show();
                initializeProfileEdit(); // DOM이 업데이트된 후 초기화 함수 호출
            })
            .catch(error => console.error('Error:', error));
    });
});

// 프로필 편집 초기화 함수
function initializeProfileEdit() {
    console.log("Initializing profile edit");
    
    const toggleEditProfileBtn = document.getElementById('toggleEditProfile');
    const editProfileForm = document.getElementById('editProfileForm');
    const userActivities = document.getElementById('userActivities');
    const cancelEditBtn = document.getElementById('cancelEdit');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const previewUsername = document.getElementById('previewUsername');
    const previewEmail = document.getElementById('previewEmail');

    if (toggleEditProfileBtn) {
        toggleEditProfileBtn.addEventListener('click', function() {
            console.log("Toggle button clicked");
            editProfileForm.style.display = 'block';
            userActivities.style.display = 'none';
            toggleEditProfileBtn.style.display = 'none';
        });
    }

    if (cancelEditBtn) {
        cancelEditBtn.addEventListener('click', function() {
            editProfileForm.style.display = 'none';
            userActivities.style.display = 'block';
            toggleEditProfileBtn.style.display = 'inline-block';
        });
    }

    if (usernameInput && previewUsername) {
        usernameInput.addEventListener('input', function(event) {
            previewUsername.textContent = event.target.value;
        });
    }

    if (emailInput && previewEmail) {
        emailInput.addEventListener('input', function(event) {
            previewEmail.textContent = event.target.value;
        });
    }

    if (editProfileForm) {
        editProfileForm.addEventListener('submit', handleProfileSubmit);
    }
}

function handleProfileSubmit(event) {
    event.preventDefault();
    
    const nickname = document.getElementById('nickname').value;

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    console.log(csrfToken);
    fetch('/api/v1/user/mypage-content/edit/profile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken,
        },
        body: JSON.stringify({ nickname: nickname })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // 성공 처리
        const isSuccess = data.payload.success;
        if (isSuccess) {
            // 프로필 정보 업데이트
            document.querySelector('.card-body h4').textContent = nickname;
            // 편집 폼 숨기기 및 활동 표시
            document.getElementById('editProfileForm').style.display = 'none';
            document.getElementById('userActivities').style.display = 'block';
            document.getElementById('toggleEditProfile').style.display = 'inline-block';
            // 성공 메시지 표시
            alert('프로필이 성공적으로 업데이트되었습니다.');
        } else {
            throw new Error('프로필 업데이트에 실패했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('프로필 업데이트 중 오류가 발생했습니다: ' + error.message);
    });
}



// 이벤트 위임을 사용한 전역 클릭 이벤트 리스너
document.addEventListener('click', function(event) {
    if (event.target && event.target.id === 'toggleEditProfile') {
        console.log("Toggle button clicked (global listener)");
        const editProfileForm = document.getElementById('editProfileForm');
        const userActivities = document.getElementById('userActivities');
        if (editProfileForm && userActivities) {
            editProfileForm.style.display = 'block';
            userActivities.style.display = 'none';
            event.target.style.display = 'none';
        }
    }
});