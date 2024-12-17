import { createFoodItem } from '@/restaurantUtils';
import { modalService } from '@/ModalService';

class RestaurantService {
   constructor() {
       this.isLoading = false;
       this.page = 1;
       this.totalPage = 10;
       this.imageLoadErrorCount = 0;
       this.currentFilter = {
           category: null,
           sortOption: '평점순',
           themeOption: null,
           serviceOption: null
       };
       this.initInfiniteScroll();
   }

    initInfiniteScroll() {
    window.addEventListener('scroll', () => {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
                this.fetchFoods();
            }
        });
    }

   async fetchFoods() {
       if (this.isLoading || this.page > this.totalPage) {
           return;
       }
       
       this.isLoading = true;
       document.getElementById('loading').style.display = 'block';
       
       try {
           const data = await this.fetchRestaurantData();
           console.log(data);
           await this.renderRestaurants(data);
       } catch(error) {
           console.error("Error fetching restaurants:", error);
       } finally {
           document.getElementById('loading').style.display = 'none';
           this.isLoading = false;
       }
   }

   async fetchRestaurantData() {
       console.log("현재 필터 버튼의 상태 : ", this.currentFilter.category);
       const params = new URLSearchParams({ page: this.page });
       
       if (this.currentFilter.category) {
           params.append('category', this.currentFilter.category);
       }
       if (this.currentFilter.sortOption) {
           params.append('sortOption', this.currentFilter.sortOption);
       }
       if (this.currentFilter.themeOption) {
           params.append('themeOption', this.currentFilter.themeOption);
       }
       if (this.currentFilter.serviceOption) {
           params.append('serviceOption', this.currentFilter.serviceOption);
       }

       const response = await fetch(`/api/foods?${params.toString()}`, {
           method: 'GET',
           headers: {
               'Content-type': 'application/json',
           }
       });
       return await response.json();
   }

   setFilter(filterOptions) {
       this.currentFilter = {
           ...this.currentFilter,
           ...filterOptions
       };
       this.resetPagination();
   }

   resetPagination() {
       this.page = 1;
       this.totalPage = 10;
       document.getElementById('food-list').innerHTML = '';
   }

   async renderRestaurants(data) {
       if (!data) return;
       
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
       
       
       this.page++;
       console.log("renderRestaurants");
   }
}

export const restaurantService = new RestaurantService();
