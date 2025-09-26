package org.kosa.myproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Spring Security 표준 : "username"으로 로그인 함
    @Column(nullable = false, unique = true, length = 50)
    private String username; // 식별 아이디 역할

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호 저장

    @Column(nullable = false, length = 100)
    private String name; // 실명

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 가입일시

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // 데이터베이스에 문자열 타입으로 저장할 것을 지정 (ROLE_USER 또는 ROLE_ADMIN으로 저장 )
    @Builder.Default // @Builder 사용시 기본값을 설정해주는 역할 , ROLE_USER를 기본값으로 저장
    private MemberRole role = MemberRole.ROLE_USER;

    /**
     *  회원 정보 수정
     */
    public void updateInfo(String name,String password){
        if(name !=null && !name.isBlank()) {
            this.name = name.trim();
        }
        if(password!=null && !password.isBlank()){
            this.password = password.trim(); // 암호화 해서 전달해야 함
        }
    }
    /**
     *   권한 변경 - 관리자가 회원 권한 변경시 사용
     */
    public void changeRole(MemberRole newRole){
        if(newRole !=null )
            this.role = role;
    }
}















