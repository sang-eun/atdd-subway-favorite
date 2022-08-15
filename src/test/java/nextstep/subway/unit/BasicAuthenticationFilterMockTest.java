package nextstep.subway.unit;

import nextstep.auth.authentication.AuthenticationToken;
import nextstep.auth.authentication.BasicAuthenticationFilter;
import nextstep.auth.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.auth.context.Authentication;
import nextstep.auth.context.SecurityContextHolder;
import nextstep.member.application.LoginMemberService;
import nextstep.member.domain.LoginMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BasicAuthenticationFilterMockTest {

    static String EMAIL = "admin@email.com";
    static String PASSWORD = "password";
    static String BASIC_TOKEN = "Basic YWRtaW5AZW1haWwuY29tOnBhc3N3b3Jk";

    @Mock
    LoginMemberService loginMemberService;

    @InjectMocks
    BasicAuthenticationFilter filter;

    @Test
    void preHandle() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", BASIC_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(loginMemberService.loadUserByUsername(any())).willReturn(new LoginMember(EMAIL, PASSWORD, List.of()));

        assertThat(filter.preHandle(request, response, null)).isTrue();
    }

    @Test
    void convert() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", BASIC_TOKEN);

        given(loginMemberService.loadUserByUsername(any())).willReturn(new LoginMember(EMAIL, PASSWORD, List.of()));

        assertThat(filter.convert(request)).isEqualTo(new Authentication(EMAIL, List.of()));
    }
}
