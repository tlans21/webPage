package HomePage.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Long id;
    private String title;
    private String link;
    private String category;
    private String description;
    private String telephone;
    private String address;
    private String roadAddress;
    private String homePageLink;
    private int mapx;
    private int mapy;
    private String imageUrl;


}
