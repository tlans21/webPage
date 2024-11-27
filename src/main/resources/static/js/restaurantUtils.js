export async function createFoodItem(restaurant, onImageClick) {
    const div = document.createElement('div');
    div.className = 'food-item';
    div.setAttribute('data-category', restaurant.category);
    div.innerHTML = `
        <img src="${restaurant.imageUrl}" alt="${restaurant.title}", data-restaurant-id="${restaurant.id}">
        <h3><bold>${restaurant.title}</bold></h3>
        <p><bold>리뷰 : ${restaurant.reviewCnt}</bold></p>
        <p><bold>조회수 : ${restaurant.viewCnt}</bold></p>
        <p><bold>평점 : ${(!restaurant.averageRating || restaurant.averageRating === 0) ? '리뷰없음' : restaurant.averageRating.toFixed(2)}</bold></p>
    `;
    const img = div.querySelector('img');
    // 이미지 클릭 이벤트 
    img.addEventListener('click', onImageClick); // onImageClick은 openModal을 등록
    await new Promise ((resolve, reject) => {
        img.onload = resolve;
        img.onerror = reject;
    });

    return div;
}