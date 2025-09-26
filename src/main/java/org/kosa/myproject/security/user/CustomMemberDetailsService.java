package org.kosa.myproject.security.user;

import org.kosa.myproject.entity.Member;
import org.kosa.myproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 CustomMemberDetailsService -> Spring Security 의 UserDetailsService 의 구현체
 역할 :  Spring Filter Proxy 에서 사용자 인증 (로그인 체크) 시  DB 와 연동해 인증 여부를 담당

 */
@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * Spring Security가 인증 과정에서 자동으로 호출하는 메서드
     *
     *  동작 과정
     *  1. 사용자가 로그인 폼에  username 과  password 입력
     *  2. Spring Security Filter Proxy 가 username 으로 아래 메서드 호출
     *  3. Repository 를 이용해  사용자 정보 조회 후 UserDetails (사용자 정보) 를 반환
     *  4. Spring Security Filter Proxy 가 입력한 비밀 번호와 UserDetails 비밀번호를 비교
     *  5. 일치하면 인증 성공, Authentication 객체 생성 ( SecurityContext 에 저장되는 정보 )
     *                                                                                여러 컨트롤러들이 접근해 사용자 정보를 이용한다
     *
     * @param username 로그인 시 입력한 사용자명
     * @return UserDetails 사용자 상세 정보
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== Spring Security 인증 시작 ===");
        log.info("로그인 시도: username={}", username);
        // 주의점 : findById 가 아니고 findByUsername 메서드를 호출
      Member findMember = memberRepository.findByUsername(username).orElseThrow(() -> {
           log.error("인증 실패 - 존재하지 않는 사용자 : {}", username);
           return new UsernameNotFoundException("아이디에 해당하는 회원이 존재하지 않습니다"+username);
       });
      log.info("사용자 정보 확인  username={} , name={} , role={}",findMember.getUsername(),findMember.getName(),findMember.getRole());
      //Security 가 이해하는 사용자 타입인 UserDetails 인터페이스의 구현체를 반환
        return new CustomMemberDetails(findMember);
    }

    /**
     * JWT 토큰 검증 시 사용할 수 있는 추가 메서드
     * memberId로 사용자 조회
     *
     * @param memberId 회원 ID (PK)
     * @return UserDetails 사용자 상세 정보
     */
    public UserDetails loadUserByMemberId(Long memberId) {
        log.info("=== JWT 검증을 위한 사용자 조회: memberId={} ===", memberId);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->{
            log.error("사용자를 찾을 수 없음");
            return new UsernameNotFoundException("사용자를 찾을 수 없음: ID ="+memberId);
        });

        log.info("사용자 조회 성공: username={}, role={}",
                member.getUsername(), member.getRole());

        return new CustomMemberDetails(member);
    }
}