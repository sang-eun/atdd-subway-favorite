package nextstep.favorite.application;

import nextstep.auth.authentication.AuthenticationException;
import nextstep.favorite.application.dto.FavoriteRequest;
import nextstep.favorite.application.dto.FavoriteResponse;
import nextstep.favorite.domain.Favorite;
import nextstep.favorite.domain.FavoriteRepository;
import nextstep.member.application.MemberService;
import nextstep.member.domain.LoginMember;
import nextstep.member.domain.Member;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StationService stationService;
    private final MemberService memberService;

    public FavoriteService(FavoriteRepository favoriteRepository, StationService stationService, MemberService memberService) {
        this.favoriteRepository = favoriteRepository;
        this.stationService = stationService;
        this.memberService = memberService;
    }

    public FavoriteResponse createMyFavorite(LoginMember loginMember, FavoriteRequest request) {
        Station source = stationService.findById(request.getSource());
        Station target = stationService.findById(request.getTarget());
        Member member = memberService.findMemberByEmail(loginMember.getEmail());
        Favorite favorite = new Favorite(source, target, member);
        favoriteRepository.save(favorite);

        return toFavoriteResponse(favorite);
    }

    private FavoriteResponse toFavoriteResponse(Favorite favorite) {
        return new FavoriteResponse(favorite.getId(), StationResponse.of(favorite.getSource()), StationResponse.of(favorite.getTarget()));
    }

    public List<FavoriteResponse> getMyFavorite(LoginMember loginMember) {
        if (loginMember.getEmail() == null) {
            throw new AuthenticationException();
        }

        List<Favorite> favorites = favoriteRepository.findByMemberEmail(loginMember.getEmail());

        return favorites.stream().map(this::toFavoriteResponse).collect(Collectors.toList());
    }

    public void deleteMyFavorite(LoginMember loginMember, Long id) {
        if (loginMember.getEmail() == null) {
            throw new AuthenticationException();
        }

        Favorite favorite = favoriteRepository.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 즐겨찾기입니다."));

        if (!Objects.equals(favorite.getMember().getEmail(), loginMember.getEmail())) {
            throw new NoSuchElementException("존재하지 않는 즐겨찾기입니다.");
        }

        favoriteRepository.deleteById(id);
    }
}
