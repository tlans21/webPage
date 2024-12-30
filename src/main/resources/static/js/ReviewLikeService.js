class ReviewLikeService {
    isButtonActive(button) {
        return button.classList.contains('active');
    }
    
    updateCountsAndActiveBtn(reviewId, data, clickedButton) {
        const likeBtn = document.querySelector(`button.like-btn[data-review-id="${reviewId}"]`);
        const dislikeBtn = document.querySelector(`button.dislike-btn[data-review-id="${reviewId}"]`);
        
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

    async handleLike(event, reviewId) {
        try {
            const response = await fetch(`/api/v1/user/review/${reviewId}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            const data = await response.json();
            console.log(data);
            if(data.statusCode == 200) {
                this.updateCountsAndActiveBtn(reviewId, data, event.target);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('좋아요 처리 중 오류가 발생했습니다.');
        }
    }

    async handleDislike(event, reviewId) {
        try {
            const response = await fetch(`/api/v1/user/review/${reviewId}/dislike`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            const data = await response.json();
            console.log(data);
            if(data.statusCode == 200) {
                this.updateCountsAndActiveBtn(reviewId, data, event.target);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('싫어요 처리 중 오류가 발생했습니다.');
        }
    }

    initializeLikeDislikeButtons() {
        document.querySelectorAll('.like-btn, .dislike-btn').forEach(button => {
            const reviewId = button.dataset.reviewId;
            const isLikeBtn = button.classList.contains('like-btn');
            
            // 기존 이벤트 리스너 제거
            button.removeEventListener('click', (event) => 
                isLikeBtn ? this.handleLike(event, reviewId) : this.handleDislike(event, reviewId)
            );
            
            // 새 이벤트 리스너 추가
            button.addEventListener('click', (event) => 
                isLikeBtn ? this.handleLike(event, reviewId) : this.handleDislike(event, reviewId)
            );
        });
    }
}

export const reviewLikeService = new ReviewLikeService();