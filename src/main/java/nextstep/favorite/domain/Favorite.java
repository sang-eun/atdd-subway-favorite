package nextstep.favorite.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.member.domain.Member;
import nextstep.subway.domain.Station;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "source_id")
    private Station source;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "target_id")
    private Station target;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_email")
    private Member member;

    public Favorite(Station source, Station target, Member member) {
        this.source = source;
        this.target = target;
        this.member = member;
    }
}
