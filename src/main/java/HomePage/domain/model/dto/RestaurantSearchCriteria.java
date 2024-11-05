package HomePage.domain.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// RestaurantSearchCriteria DTO
@Getter
@Setter
@Builder
public class RestaurantSearchCriteria {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int pageSize = 10;
    @Builder.Default
    private String sortBy = "averageRating";
    @Builder.Default
    private String sortDirection = "DESC";
    private String theme;
    private String service;

    private int offset;

    public void calculateOffset() {
        this.offset = (page - 1) * pageSize;
    }
}
