class BoardCommentLikeService{
    
    isButtonActive(button) {
        return button.classList.contains('active');
    }

    updateCountsAndActiveBtn(commentId, data, clickedButton){
        const likeBtn = document.querySelector(`button.like-btn[data-comment-id="${commentId}"]`);
        const dislikeBtn = document.querySelector(`button.dislike-btn[data-comment-id="${commentId}"]`);
        
        const wasLikeActive = this.isButtonActive(likeBtn);
        const wasDislikeActive = this.isButtonActive(dislikeBtn);

        const likeCountSpan = likeBtn.nextElementSibling;
        const dislikeCountSpan = dislikeBtn.nextElementSibling;
        
        likeCountSpan.textContent = data.payload.likeCount;
        dislikeCountSpan.textContent = data.payload.dislikeCount;

        // 클릭된 버튼 타입에 따른 처리
        const isLikeButton = clickedButton.classList.contains('like-btn');
        
        if (isLikeButton) {
            // 좋아요 버튼이 클릭된 경우
            if (wasLikeActive) {
                // 이미 활성화되어 있었다면 비활성화
                likeBtn.classList.remove('active');
            } else {
                // 활성화되어 있지 않았다면 활성화하고 싫어요는 비활성화
                likeBtn.classList.add('active');
                dislikeBtn.classList.remove('active');
            }
        } else {
            // 싫어요 버튼이 클릭된 경우
            if (wasDislikeActive) {
                // 이미 활성화되어 있었다면 비활성화
                dislikeBtn.classList.remove('active');
            } else {
                // 활성화되어 있지 않았다면 활성화하고 좋아요는 비활성화
                dislikeBtn.classList.add('active');
                likeBtn.classList.remove('active');
            }
        }
    }   
    // 좋아요 핸들
    async handleLike(event, commentId) {
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const response = await fetch(`/api/v1/user/comment/${commentId}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken, 
                }
            });
            const data = await response.json();
            console.log(data);
            if(data.statusCode == 200) {
                this.updateCountsAndActiveBtn(commentId, data, event.target);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('좋아요 처리 중 오류가 발생했습니다.');
        }
    }
    //싫어요 핸들
    async handleDislike(event, commentId) {
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const response = await fetch(`/api/v1/user/comment/${commentId}/dislike`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken, 
                }
            });
            const data = await response.json();
            console.log(data);
            if(data.statusCode == 200) {
                this.updateCountsAndActiveBtn(commentId, data, event.target);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('싫어요 처리 중 오류가 발생했습니다.');
        }
    }
    // 좋아요 싫어요 버튼 초기화 작업
    initializeLikeDislikeButtons() {
        document.querySelectorAll('.like-btn, .dislike-btn').forEach(button => {
            const commentId = button.dataset.commentId;
            const isLikeBtn = button.classList.contains('like-btn');
            
            // 기존 이벤트 리스너 제거
            button.removeEventListener('click', (event) => 
                isLikeBtn ? this.handleLike(event, commentId) : this.handleDislike(event, commentId)
            );
            
            // 새 이벤트 리스너 추가
            button.addEventListener('click', (event) => 
                isLikeBtn ? this.handleLike(event, commentId) : this.handleDislike(event, commentId)
            );
        });
    }

}

export default new BoardCommentLikeService();