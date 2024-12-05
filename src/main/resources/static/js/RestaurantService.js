// RestaurantService.js
import { createFoodItem } from '@/restaurantUtils';
import { modalService } from '@/ModalService';


class RestaurantService {
    constructor() {
        this.isLoading = false;
        this.page = 1;
        this.totalPage = 10;
        this.imageLoadErrorCount = 0;
    }
    async fetchFoods(){
        if (this.isLoading || this.page > this.totalPage) {
            return;
        }
        
        this.isLoading = true;
        document.getElementById('loading').style.display = 'block';
        
        try {
            const data = await this.fetchRestaurantData();
            await this.renderRestaurants(data);
        } catch(error) {
            console.error("Error fetching restaurants:", error);
        } finally {
            document.getElementById('loading').style.display = 'none';
            this.isLoading = false;
        }
    }

    async fetchRestaurantData() {
        const response = await fetch(`/api/foods?page=${this.page}`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
            }
        });
        return await response.json();
    }

    async renderRestaurants(data) {
        if (!data) return;
        console.log(data);
        this.totalPage = data.payload.Page.totalPages;
        const restaurants = data.payload.Page.content;
        const foodList = document.getElementById('food-list');
        
        for (const restaurant of restaurants) {
            try {
                const foodItem = await createFoodItem(
                    restaurant, 
                    (event) => modalService.openModal(event)
                );
                foodList.appendChild(foodItem);
            } catch(error) {
                this.imageLoadErrorCount++;
                console.log(`Failed to load image for ${restaurant.title}`);
            }
        }
        
        document.getElementById('total-restaurant-count').textContent = 
            `추천 맛집 (${data.payload.totalRestaurantCount}개)`;
        this.page++;
    }
}
export const restaurantService = new RestaurantService();