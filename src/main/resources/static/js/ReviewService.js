// ReviewService.js
class ReviewService {
    async submitReview(event, restaurantId) {
        event.preventDefault();
        const form = document.getElementById('reviewForm');
        const content = form.querySelector('textarea[name="content"]').value;
        const ratingInput = form.querySelector('input[name="rating"]:checked');

        if (!ratingInput) {
            alert('평점을 선택해주세요');
            return;
        }
        const rating = ratingInput.value;

        try {
            const response = await fetch(`/api/v1/user/create/review/${restaurantId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(content)}&rating=${encodeURIComponent(rating)}`
            });

            const html = await response.text();
            document.querySelector('.comments-section').innerHTML = html;
            form.reset();
            
            await this.updateReviewCount(restaurantId);
            this.initializeReviewForm(restaurantId);
        } catch (error) {
            console.error('Error:', error);
            alert('리뷰 작성 중 오류가 발생했습니다.');
        }
    }

    async updateReviewCount(restaurantId) {
        try {
            const response = await fetch(`/api/v1/user/review/${restaurantId}`);
            const data = await response.json();
            console.log("Reviews updated:", data.reviews);
            return data;
        } catch (error) {
            console.error('Error updating review count:', error);
        }
    }

    initializeReviewForm(restaurantId) {
        setTimeout(() => {
            const form = document.getElementById('reviewForm');
            if (form) {
                const submitReviewHandler = (event) => this.submitReview(event, restaurantId);
                form.removeEventListener('submit', submitReviewHandler);
                form.addEventListener('submit', submitReviewHandler);
            }
        }, 100);
    }
}

export const reviewService = new ReviewService();