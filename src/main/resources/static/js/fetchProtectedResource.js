// fetchProtectedResource.js

// 보호된 리소스에 접근하는 함수 정의
export function fetchProtectedResource() {
    fetch('/api/protected-resource', {
        method: 'GET',
        credentials: 'same-origin' // 서버와 동일한 출처의 쿠키를 요청에 포함
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Protected Resource:', data);
        // 여기서 데이터를 처리
        // 예: UI에 데이터 표시 등
    })
    .catch(error => {
        console.error('Error fetching protected resource:', error);
    });
}

// 쿠키에서 특정 이름의 쿠키값 가져오는 함수 정의
export function getCookie(name) {
    const cookieString = document.cookie;
    const cookies = cookieString.split(';');
    for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        // 쿠키 이름이 일치하는 경우 쿠키 값을 반환
        if (cookie.startsWith(name + '=')) {
            return cookie.substring(name.length + 1);
        }
    }
    return null;
}