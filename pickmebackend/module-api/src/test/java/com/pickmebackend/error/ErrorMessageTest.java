package com.pickmebackend.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageTest {

    @Test
    void valueTest() {
        assertEquals("유저를 찾을 수 없습니다.", ErrorMessage.USER_NOT_FOUND.getValue(), "유저 낫 파운드 실패!");
        assertEquals("중복된 유저입니다.", ErrorMessage.DUPLICATED_USER.getValue(), "중복된 유저 실패!");
    }
}