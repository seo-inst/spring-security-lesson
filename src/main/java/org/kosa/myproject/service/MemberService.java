package org.kosa.myproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.myproject.dto.MemberCreateRequestDto;
import org.kosa.myproject.dto.MemberResponseDto;
import org.kosa.myproject.entity.Member;
import org.kosa.myproject.entity.MemberRole;
import org.kosa.myproject.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberService - 회원 관리 핵심 비즈니스 로직
 *
 * Spring Security & JWT  필수 기능:
 * 1. 회원가입 (비밀번호 암호화)
 * 2. 회원 조회 (JWT 토큰 발급용)
 * 3. 회원 정보 조회 (인증된 사용자)
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  // 비밀번호 암호화
    /**
     * 회원가입 - JWT 인증의 시작점
     *
     * 처리 과정:
     * 1) username 중복 체크
     * 2) 비밀번호 암호화 (BCrypt)
     * 3) Member 엔티티 생성 및 저장
     * 4) DTO로 변환하여 반환
     *
     * @param requestDto 회원가입 정보
     * @return MemberResponseDto 생성된 회원 정보 (비밀번호 제외)
     */
    @Transactional  // 쓰기 작업
    public MemberResponseDto register(MemberCreateRequestDto requestDto) {
        log.info("회원가입 시도: username={}", requestDto.getUsername());

        // 1. username 중복 체크
        if (memberRepository.existsByUsername(requestDto.getUsername())) {
            log.warn("회원가입 실패 - 중복된 username: {}", requestDto.getUsername());
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2. Member 엔티티 생성 (비밀번호 암호화)
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))  // 암호화!
                .name(requestDto.getName())
                .role(MemberRole.ROLE_USER)  // 기본 권한
                .build();

        // 3. 저장
        Member savedMember = memberRepository.save(member);
        log.info("회원가입 성공: id={}, username={}", savedMember.getId(), savedMember.getUsername());

        // 4. DTO 변환 후 반환
        return MemberResponseDto.from(savedMember);
    }
    /**
     * 2. username으로 회원 조회 - JWT 토큰 발급 시 사용
     *
     * 로그인 과정에서 사용:
     * 1) 사용자가 username/password 전송
     * 2) 이 메서드로 회원 조회
     * 3) 비밀번호 검증
     * 4) JWT 토큰 발급
     *
     * @param username 로그인 ID
     * @return MemberResponseDto 회원 정보
     */
    public MemberResponseDto findByUsername(String username) {
        log.debug("회원 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: username={}", username);
                    return new IllegalArgumentException("존재하지 않는 회원입니다.");
                });

        return MemberResponseDto.from(member);
    }
    /**
     * 3. ID(PK)로 회원 조회 - JWT 토큰 검증 후 사용
     *
     * JWT 토큰에서 추출한 회원 ID로 조회
     * 토큰 검증 후 실제 회원 정보가 필요할 때 사용
     *
     * @param memberId 회원 ID (PK)
     * @return MemberResponseDto 회원 정보
     */
    public MemberResponseDto findById(Long memberId) {
        log.debug("회원 조회: id={}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: id={}", memberId);
                    return new IllegalArgumentException("존재하지 않는 회원입니다.");
                });

        return MemberResponseDto.from(member);
    }

    /**
     * 4. 현재 로그인한 회원 정보 조회
     *
     * JWT 토큰으로 인증된 사용자의 정보 조회
     * SecurityContext에서 username을 추출하여 사용
     *
     * @param username 현재 인증된 사용자의 username
     * @return MemberResponseDto 회원 정보
     */
    public MemberResponseDto getMyInfo(String username) {
        log.debug("내 정보 조회: username={}", username);

        return findByUsername(username);
    }

    /**
     * 5. 비밀번호 검증 - 로그인 시 사용
     *
     * JWT 토큰 발급 전 비밀번호 확인
     * PasswordEncoder가 암호화된 비밀번호와 비교
     *
     * @param username 로그인 ID
     * @param rawPassword 입력된 비밀번호 (평문)
     * @return true: 일치, false: 불일치
     */
    public boolean validatePassword(String username, String rawPassword) {
        log.debug("비밀번호 검증: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        boolean isValid = passwordEncoder.matches(rawPassword, member.getPassword());

        if (isValid) {
            log.info("비밀번호 검증 성공: username={}", username);
        } else {
            log.warn("비밀번호 검증 실패: username={}", username);
        }

        return isValid;
    }

    /**
     * 6. 회원 정보 수정 - 인증된 사용자만 가능
     *
     * JWT 토큰으로 인증된 사용자가 자신의 정보 수정
     * 비밀번호 변경 시 재암호화
     *
     * @param username 수정할 회원의 username
     * @param newName 새 이름 (null 가능)
     * @param newPassword 새 비밀번호 (null 가능)
     * @return MemberResponseDto 수정된 회원 정보
     */
    @Transactional
    public MemberResponseDto updateMember(String username, String newName, String newPassword) {
        log.info("회원 정보 수정: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 비밀번호가 제공된 경우 암호화
        String encodedPassword = null;
        if (newPassword != null && !newPassword.isBlank()) {
            encodedPassword = passwordEncoder.encode(newPassword);
        }

        // 회원 정보 업데이트
        member.updateInfo(newName, encodedPassword);

        log.info("회원 정보 수정 완료: username={}", username);
        return MemberResponseDto.from(member);
    }

    /**
     * 7. 회원 Entity 조회 - 내부용
     *
     * 다른 Service에서 Member 엔티티가 필요할 때 사용
     * (예: PostService에서 작성자 정보 필요)
     *
     * @param username 로그인 ID
     * @return Member 엔티티
     */
    public Member getMemberEntity(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}













