package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.FavoriteSteps.즐겨찾기_요청;
import static nextstep.subway.acceptance.FavoriteSteps.즐겨찾기_요청_권한없음;
import static nextstep.subway.acceptance.LineSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }


    /**
     * When 즐겨찾기를 추가하면
     * Then 즐겨찾기가 추가된다.
     * Then 즐겨찾기 목록을 조회할 수 있다.
     */
    @DisplayName("즐겨찾기를 생성한다.")
    @Test
    void createFavorite() {
        // when
        ExtractableResponse<Response> response = 즐겨찾기_요청(강남역, 남부터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> favorite = SecurityUtil.given().log().all()
                .when().get("/favorites")
                .then().log().all()
                .extract();

        Long sourceId = favorite.jsonPath().getLong("[0].source.id");
        Long targetId = favorite.jsonPath().getLong("[0].target.id");

        assertThat(sourceId).isEqualTo(강남역);
        assertThat(targetId).isEqualTo(남부터미널역);
    }

    /**
     * When 권한 없이 즐겨찾기를 추가하면
     * Then 401을 리턴한다.
     */
    @DisplayName("즐겨찾기를 생성 권한 실패")
    @Test
    void fail_createFavoriteWithNoAuthority() {
        // when
        ExtractableResponse<Response> response = 즐겨찾기_요청_권한없음(강남역, 남부터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Given 즐겨찾기를 생성하고
     * When 즐겨찾기를 조회하면
     * Then 즐겨찾기를 찾아온다.
     */
    @DisplayName("즐겨찾기를 조회한다.")
    @Test
    void getFavorites() {
        // given
        즐겨찾기_요청(강남역, 남부터미널역);

        // when
        ExtractableResponse<Response> response = SecurityUtil.given()
                .when().get("/favorites")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        Long sourceId = response.jsonPath().getLong("[0].source.id");
        Long targetId = response.jsonPath().getLong("[0].target.id");

        assertThat(sourceId).isEqualTo(강남역);
        assertThat(targetId).isEqualTo(남부터미널역);

    }

    /**
     * When 권한 없이 즐겨찾기를 조회하면
     * Then 401에러가 발생한다.
     */
    @DisplayName("즐겨찾기 조회 권한실패")
    @Test
    void fail_getFavoritesWithoutAuthority() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/favorites")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Given 즐겨찾기를 생성하고
     * When 즐겨찾기를 삭제하면
     * Then 즐겨찾기 조회 시 데이터가 존재하지 않는다.
     */
    @DisplayName("즐겨찾기를 제거한다.")
    @Test
    void deleteFavorite() {
        // given
        ExtractableResponse<Response> createResponse = 즐겨찾기_요청(강남역, 남부터미널역);

        // when
        String location = createResponse.header("location");
        ExtractableResponse<Response> response = SecurityUtil.given()
                .when()
                .delete(location)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> favorite = SecurityUtil.given().log().all()
                .when().get("/favorites")
                .then().log().all()
                .extract();
        assertThat(favorite.jsonPath().getList("id")).hasSize(0);
    }

    /**
     * When 권한 없는 즐겨찾기를 삭제 시
     * Then 401 에러 발생
     */
    @DisplayName("즐겨찾기 삭제 권한 실패")
    @Test
    void fail_deleteFavoriteWithoutAuthority() {
        // when
        String location = "/favorites/1";
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(location)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private Long 지하철_노선_생성_요청(String name, String color, Long upStation, Long downStation, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStation + "");
        lineCreateParams.put("downStationId", downStation + "");
        lineCreateParams.put("distance", distance + "");

        return LineSteps.지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }
}