// ModalService.js
import { createMap, loadModalStyle } from '@/MapUtils';
import { reviewService } from '@/ReviewService';


export class ModalService {
    async openModal(event) {
        const restaurantId = event.target.dataset.restaurantId;
        const mapModalContent = document.getElementById('mapModal-content');
        
        try {
            const htmlContent = await this.fetchModalContent(restaurantId);
            this.updateModalContent(htmlContent);
            this.setupModalEvents();
            
            // 리뷰 폼 초기화
            reviewService.initializeReviewForm(restaurantId);
            
            // 지도 초기화
            this.initializeMap();
        } catch (error) {
            console.error('Error fetching restaurant data:', error);
        }
    }

    async fetchModalContent(restaurantId) {
        const response = await fetch('/api/map/modal-map', {
            method: 'POST',
            headers: { 'Content-type': 'application/json' },
            body: JSON.stringify({id: restaurantId})
        });
        
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        
        return response.text();
    }

    updateModalContent(htmlContent) {
        const mapModalContent = document.getElementById('mapModal-content');
        mapModalContent.innerHTML = htmlContent;
        mapModal.style.display = "block";

        // 댓글 시간 초기화 함수 호출
        
    }

    setupModalEvents() {
        const closeBtn = document.querySelector('.closeMapModal');
        if (closeBtn) {
            closeBtn.onclick = () => this.closeModal();
        }
    }

    initializeMap() {
        setTimeout(() => {
            const mapx = document.getElementById('mapx')?.textContent;
            const mapy = document.getElementById('mapy')?.textContent;
            if (mapx && mapy) {
                createMap(parseFloat(mapx), parseFloat(mapy));
            }
        }, 200);
    }

    closeModal() {
        const mapModal = document.getElementById('mapModal');
        mapModal.style.display = "none";
    }
}

export const modalService = new ModalService();

