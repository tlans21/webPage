function parseTimestamp(timestamp) {
    // 타임스탬프를 " "로 분리하여 날짜와 시간을 얻습니다.
    const [datePart, timePart] = timestamp.split(' ');

    // 타임스탬프를 "T" 구분자로 합쳐서 ISO 8601 형식으로 만듭니다.
    const isoString = `${datePart}T${timePart}`;

    return new Date(isoString);
}

const elapsedTime = function(date) {
    const start = new Date(date);
    const end = new Date();

    const seconds = Math.floor((end.getTime() - start.getTime()) / 1000);
    if (seconds < 60) return '방금 전';

    const minutes = seconds / 60;
    if (minutes < 60) return `${Math.floor(minutes)}분 전`;

    const hours = minutes / 60;
    if (hours < 24) return `${Math.floor(hours)}시간 전`;

    const days = hours / 24;
    if (days < 7) return `${Math.floor(days)}일 전`;

    return `${start.toLocaleDateString()}`;
};

// 댓글 작성 시간을 계산하고 표시하는 함수
function displayCommentTimes() {
    const commentTimes = document.querySelectorAll('.board-time');
    commentTimes.forEach(function(timeElement) {
        const timestamp = timeElement.getAttribute('data-timestamp');
        const date = parseTimestamp(timestamp); // 타임스탬프를 Date 객체로 변환
        timeElement.textContent = elapsedTime(date);
    });
}

// 페이지가 로드될 때 displayCommentTimes 함수 호출
document.addEventListener('DOMContentLoaded', displayCommentTimes);