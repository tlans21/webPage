import { createFoodItem } from '@/restaurantUtils';
import { modalService } from '@/ModalService';

class RestaurantService {
   constructor() {
       this.currentRenderingPromise = null;
       this.abortController = null;
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

    async setFilter(filterOptions) {
        // 1. 이전 요청 중단 (이전 요청에서 abortController가 생성되었으므로)
        if (this.abortController) {
            this.abortController.abort();
        }
        if (this.currentRenderingPromise) {
            await this.currentRenderingPromise;
        }
        // 2. 현재 필터 변경
        this.currentFilter = {
            ...this.currentFilter,
            ...filterOptions
        };
        // 3. 필터 변경으로 인한 상태 초기화
        this.resetPagination();

        // 4. 데이터 요청
        await this.fetchFoods();
    }
 
    resetPagination() {
        this.page = 1;
        this.totalPage = 10;
        this.isLoading = false;
        document.getElementById('food-list').innerHTML = '';
    }

   async fetchFoods() {
       if (this.isLoading || this.page > this.totalPage) {
            return;
       }
       
       this.isLoading = true;
       this.abortController = new AbortController();
       document.getElementById('loading').style.display = 'block';
       
       try {
           const data = await this.fetchRestaurantData(this.abortController.signal);
           console.log(data);
           if (!this.abortController.signal.aborted){
                await this.renderRestaurants(data); 
           }
       } catch(error) {
            if(error.name === 'AbortError'){
                console.error("Request Aborting is success");
            } else {
                console.error("Error fetching restaurants:", error);
            }
       } finally {
        if (!this.abortController.signal.aborted){
                document.getElementById('loading').style.display = 'none';
                this.isLoading = false;
            }  
        }
   }

   async fetchRestaurantData(signal) {
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
           },
           signal 
       });

       if(signal.aborted){
            return null;
       }

       return await response.json();
   }

   async renderRestaurants(data) {
        if (!data || this.abortController.signal.aborted) return;
       
        this.totalPage = data.payload.Page.totalPages;
        const restaurants = data.payload.Page.content;
        // const foodList = document.getElementById('food-list');
        // const temporaryItems = [];  // 임시 저장소
    //    for (const restaurant of restaurants) {
    //        if (this.abortController.signal.aborted) break;
    //        try {
    //            const foodItem = await createFoodItem(
    //                restaurant, 
    //                (event) => modalService.openModal(event)
    //            );
    //            if (!this.abortController.signal.aborted) {
    //                 console.log("1");  
    //                 foodList.appendChild(foodItem);
    //            }
               
    //        } catch(error) {
    //            this.imageLoadErrorCount++;
    //            console.log(`Failed to load image for ${restaurant.title}`);
    //        }
    //    }
       
       
       
    //    if (!this.abortController.signal.aborted) {  // 추가
    //         this.page++;
    //         console.log("renderRestaurants");
    //    }
        // for (const restaurant of restaurants) {
        //     if (this.abortController.signal.aborted) break;
        //     try {
        //         const foodItem = await createFoodItem(
        //             restaurant, 
        //             (event) => modalService.openModal(event)
        //         );
        //         if (!this.abortController.signal.aborted) {
        //             temporaryItems.push(foodItem);  // DOM에 바로 추가하지 않고 임시 저장
        //         }
        //     } catch(error) {
        //         this.imageLoadErrorCount++;
        //         console.log(`Failed to load image for ${restaurant.title}`);
        //     }
        // }
        
        // // 모든 아이템이 준비되고 abort되지 않았을 때만 한 번에 DOM에 추가
        // if (!this.abortController.signal.aborted) {
        //     for (const item of temporaryItems){
        //         if (this.abortController.signal.aborted) return;
        //         foodList.appendChild(item);
        //     }
        //     if (!this.abortController.signal.aborted) {
        //         this.page++;
        //         console.log("renderRestaurants");
        //     }
        // }
        this.currentRenderingPromise = (async () => {
            const temporaryItems = [];
            
            for (const restaurant of restaurants) {
                if (this.abortController.signal.aborted) return;
                try {
                    const foodItem = await createFoodItem(
                                    restaurant, 
                                    (event) => modalService.openModal(event)
                    );
                    if (!this.abortController.signal.aborted) {
                        temporaryItems.push(foodItem);
                    }
                } catch(error) {
                    // ... error handling
                }
            }

            if (!this.abortController.signal.aborted) {
                const foodList = document.getElementById('food-list');
                for (const item of temporaryItems) {
                    if (this.abortController.signal.aborted) return;
                    foodList.appendChild(item);
                }
                this.page++;
            }
        })();

        await this.currentRenderingPromise;
        this.currentRenderingPromise = null;
   }
}

export const restaurantService = new RestaurantService();
