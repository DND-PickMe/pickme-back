package com.pickmebackend.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ErrorMessage {
    USER_NOT_FOUND("유저를 찾을 수 없습니다."),
    DUPLICATED_USER("중복된 유저입니다."),
    UNAUTHORIZED_USER("권한이 없는 유저의 요청입니다."),
    SELF_INTERVIEW_NOT_FOUND("셀프 인터뷰를 찾을 수 없습니다."),
    EXPERIENCE_NOT_FOUND("경력을 찾을 수 없습니다."),
    LICENSE_NOT_FOUND("자격증을 찾을 수 없습니다."),
    PRIZE_NOT_FOUND("수상 내역을 찾을 수 없습니다."),
    PROJECT_NOT_FOUND("프로젝트를 찾을 수 없습니다."),
    INVALID_IMAGE("적합한 이미지가 아닙니다."),
    CANNOT_READABLE_IMAGE("이지지를 읽을 수 없습니다."),
    UNVERIFIED_USER("이메일 인증이 되지 않은 사용자입니다.");

    private String value;
}
