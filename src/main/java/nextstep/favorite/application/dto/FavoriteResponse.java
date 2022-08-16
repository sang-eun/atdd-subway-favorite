package nextstep.favorite.application.dto;

import lombok.Data;
import nextstep.subway.applicaion.dto.StationResponse;

@Data
public class FavoriteResponse {
    private Long id;
    private StationResponse source;
    private StationResponse target;

    public FavoriteResponse(Long id, StationResponse source, StationResponse target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }
}
