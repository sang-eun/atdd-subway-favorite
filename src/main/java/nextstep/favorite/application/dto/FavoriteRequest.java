package nextstep.favorite.application.dto;

import lombok.Data;

@Data
public class FavoriteRequest {

    private Long source;
    private Long target;

    public FavoriteRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }
}
