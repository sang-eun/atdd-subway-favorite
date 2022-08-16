package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.SecurityUtil;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class FavoriteSteps {

    public static ExtractableResponse<Response> 즐겨찾기_요청(Long sourceId, Long targetId) {
        Map<String, String> params = new HashMap<>();
        params.put("source", sourceId + "");
        params.put("target", targetId + "");

        return SecurityUtil.given()
                .when().contentType(MediaType.APPLICATION_JSON_VALUE).body(params)
                .post("/favorites")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_요청_권한없음(Long sourceId, Long targetId) {
        Map<String, String> params = new HashMap<>();
        params.put("source", sourceId + "");
        params.put("target", targetId + "");

        return RestAssured.given().log().all()
                .when().contentType(MediaType.APPLICATION_JSON_VALUE).body(params)
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
