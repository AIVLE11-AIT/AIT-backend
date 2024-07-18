package aivle.ait.Security.Config;

import aivle.ait.Security.JWT.JWTFilter;
import aivle.ait.Security.JWT.JWTUtil;
import aivle.ait.Security.JWT.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CorsConfigurationSource corsConfigurationSource;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/question/*/answer/create", "/question/*/answer/*/update", "/question/*/answer/*/delete").hasRole("USER")
                        .requestMatchers("/interviewGroup/*/*/interviewerQna/*/update", "/interviewGroup/*/companyQna/*/delete").hasRole("USER")
                        .requestMatchers("/interviewGroup/*/interviewer/*/introduce/create", "/interviewGroup/*/interviewer/*/introduce/update", "/interviewGroup/*/interviewer/*/introduce/delete").hasRole("USER")
                        .requestMatchers("/signup/**", "/login", "/update", "/sendTempPassword/**",
                                "/interviewGroup/*/companyQna/**",
                                "/interviewGroup/*/*/interviewerQna/**",
                                "/interviewGroup/*/interviewer/*/file/**",
                                "/interviewGroup/*/interviewer/*/introduce/**",
                                "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/result/finish").permitAll()
                        .requestMatchers("/question/*/answer/**").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("USER", "ADMIN"));

        //JWTFilter 등록 (JWTFilter는 JWT 토큰이 있으면 로그인된 정보를 추출)
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);


        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
