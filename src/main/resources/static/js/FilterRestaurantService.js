import { restaurantService } from '@/RestaurantService';

class FilterRestaurantService {
    constructor() {
        this.initializeElements();
        this.initializeEventListeners();
        this.restaurantService = restaurantService;
    }
 
    initializeElements() {
        this.sortOptions = document.querySelectorAll('.filter-sort-option');
        this.themeOptions = document.querySelectorAll('.filter-theme-option');
        this.serviceOptions = document.querySelectorAll('.filter-service-option');
        this.resetFilterBtn = document.getElementById('resetFilter');
        this.applyFilterBtn = document.getElementById('applyFilter');
        this.filterModal = document.getElementById('filterModal');
        this.filterButtons = document.querySelectorAll('.filter-btn');
    }
 
    initializeEventListeners() {
        this.sortOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'sortOption'))
        );
 
        this.themeOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'themeOption'))
        );
 
        this.serviceOptions.forEach(option => 
            option.addEventListener('click', (event) => this.handleClickFilterOption(event, 'serviceOption'))
        );
 
        this.resetFilterBtn.addEventListener('click', () => this.handleClickResetFilter());
 
        this.applyFilterBtn.addEventListener('click', () => this.handleClickApplyFilter());
 
        this.filterButtons.forEach(button =>
            button.addEventListener('click', async () => {
                const filter = button.getAttribute('data-filter');
                
                this.filterButtons.forEach(btn => btn.classList.remove('active'));
                const filterBtnDetail = document.getElementById('filter-btn-detail');
                if (filterBtnDetail && filterBtnDetail.classList.contains('active')) {
                    filterBtnDetail.classList.remove('active');
                }
                button.classList.add('active');
                
                this.filterFoodItems(filter);
            })
        );
    }
 
    async filterFoodItems(filter) {
        this.restaurantService.setFilter({
            category: filter === 'all' ? null : filter
        });
        this.restaurantService.fetchFoods();
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

        this.restaurantService.setFilter({
            [filterType]: filterOption
        });
    }
 
    handleClickResetFilter() {
        this.sortOptions.forEach(option => option.classList.remove('active'));
        this.themeOptions.forEach(option => option.classList.remove('active'));
        this.serviceOptions.forEach(option => option.classList.remove('active'));
        
        const defaultSortOption = this.sortOptions[0];
        defaultSortOption.classList.add('active');
    }
 
    async handleClickApplyFilter() {
        this.filterModal.style.display = "none";
        const filterOptions = this.getActiveFilterOptions();
        
        this.restaurantService.setFilter(filterOptions);
        await this.restaurantService.fetchFoods();
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
 }
 
 export const filterRestaurantService = new FilterRestaurantService();