import { createFoodItem } from '@/restaurantUtils';
import { modalService } from '@/ModalService';

class FilterRestaurantService {
    constructor() {
        this.initializeElements();
        this.initializeEventListeners();
    }

    initializeElements() {
        // 필터 버튼 요소들
        this.sortOptions = document.querySelectorAll('.filter-sort-option');
        this.themeOptions = document.querySelectorAll('.filter-theme-option');
        this.serviceOptions = document.querySelectorAll('.filter-service-option');
        this.resetFilterBtn = document.getElementById('resetFilter');
        this.applyFilterBtn = document.getElementById('applyFilter');
        this.filterModal = document.getElementById('filterModal'); // 모달 요소 선택자는 적절히 수정 필요


        // 네비게이션 바 필터 버튼 요소

        this.filterButtons = document.querySelectorAll('.filter-btn');
    }

    initializeEventListeners() {
        // 정렬 옵션 이벤트 리스너
        this.sortOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'sort'))
        );

        // 테마 옵션 이벤트 리스너
        this.themeOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'theme'))
        );

        // 서비스 옵션 이벤트 리스너
        this.serviceOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'service'))
        );

        // 리셋 버튼 이벤트 리스너
        this.resetFilterBtn.addEventListener('click', () => this.handleClickResetFilter());

        // 필터 적용 버튼 이벤트 리스너
        this.applyFilterBtn.addEventListener('click', () => this.handleClickApplyFilter());

        // 네비게이션 바 필터 적용 버튼 이벤트 리스너
        this.filterButtons.forEach(button =>
            button.addEventListener('click', () => {
                const filter = button.getAttribute('data-filter');
                this.filterFoodItems(filter);
            
                // 활성 버튼 스타일 변경
                this.filterButtons.forEach(btn => btn.classList.remove('active'));
                const filterBtnDetail = document.getElementById('filter-btn-detail');
                // 필터 버튼이 활성화 되어있다면 비활성화
                if (filterBtnDetail && filterBtnDetail.classList.contains('active')) {
                    filterBtnDetail.classList.remove('active');
                }
                button.classList.add('active');   
            })
        );
    }

    // 음식점 필터링 함수
    filterFoodItems(filter) {
        const foodItems = document.querySelectorAll('.food-item');
        foodItems.forEach(item => {
            if (filter === 'all' || item.getAttribute('data-category') === filter) {
                    item.style.display = 'block';
                } else {
                    item.style.display = 'none';
                }
        });
    }

    updateActiveButtonStyle(clickedButton) {
        const clickedOptionClass = clickedButton.classList[0];
        const clickedOptions = document.querySelectorAll(`.${clickedOptionClass}`);
        clickedOptions.forEach(option => {
            option.classList.remove('active');
        });
        clickedButton.classList.add('active');
    }

    handleClickFilterOption(event, filterType) {
        const filterOption = event.target.getAttribute('data-filter');
        this.updateActiveButtonStyle(event.target);
    }

    handleClickResetFilter() {
        // 모든 필터 옵션에서 active 클래스 제거
        this.sortOptions.forEach(option => option.classList.remove('active'));
        this.themeOptions.forEach(option => option.classList.remove('active'));
        this.serviceOptions.forEach(option => option.classList.remove('active'));
        
        // 기본 정렬 옵션 활성화
        const defaultSortOption = this.sortOptions[0];
        defaultSortOption.classList.add('active');
    }

    async handleClickApplyFilter() {
        this.filterModal.style.display = "none";

        const filterOptions = this.getActiveFilterOptions();
        
        try {
            console.log("test1");
            const restaurants = await this.fetchFilteredRestaurants(filterOptions);
            console.log("test2");
            await this.updateRestaurantList(restaurants);
        } catch (error) {
            console.error("필터링된 레스토랑을 가져오는 중 오류 발생:", error);
        }
    }

    getActiveFilterOptions() {
        const activeSort = document.querySelector('.filter-sort-option.active');
        const activeTheme = document.querySelector('.filter-theme-option.active');
        const activeService = document.querySelector('.filter-service-option.active');

        return {
            sortOption: activeSort ? activeSort.getAttribute('data-filter') : '평점순',
            themeOption: activeTheme ? activeTheme.getAttribute('data-filter') : null,
            serviceOption: activeService ? activeService.getAttribute('data-filter') : null
        };
    }

    async fetchFilteredRestaurants(filterOptions) {
        const params = new URLSearchParams({ 
            sortOption: filterOptions.sortOption 
        });

        if (filterOptions.themeOption) {
            params.append('theme', filterOptions.themeOption);
        }
        if (filterOptions.serviceOption) {
            params.append('service', filterOptions.serviceOption);
        }

        const response = await fetch(`/api/foods?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
        return data.Page.content;
    }

    async updateRestaurantList(restaurants) {
        const foodList = document.getElementById('food-list');
        foodList.innerHTML = '';

        await Promise.all(
            restaurants.map(async (restaurant) => {
                const foodItem = await createFoodItem(restaurant, (event) => modalService.openModal(event));
                foodList.append(foodItem);
            })
        );
    }
}

// 서비스 인스턴스 생성 및 내보내기
export const filterRestaurantService = new FilterRestaurantService();