package org.kosa.myproject.entity;

/**
 *   MemberRole : 회원 역할을 정의하는 열거형 (Enum)
 *   Enum 은 상수의 집합 정의할 때 사용하는 특별한 형태의 클래스
 *   일반적인 클래스와는 다르게 Enum 은 인스턴스를 미리 정의된 상수 집합으로 제한
 *   사례 )   if(member.getRole() == MemberRole.ROLE_ADMIN)  ===> 직관적으로 사용가능
 *
 *   아래 ENUM 은 데이타베이스에 문자열로 ROLE_USER 와  ROLE_ADMIN 으로 저장
 */
public enum MemberRole {
    ROLE_USER,
    ROLE_ADMIN
}
