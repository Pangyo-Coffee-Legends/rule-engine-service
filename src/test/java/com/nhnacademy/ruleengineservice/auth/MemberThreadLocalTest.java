package com.nhnacademy.ruleengineservice.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class MemberThreadLocalTest {

    @Test
    @DisplayName("private 생성자 테스트")
    void testPrivateConstructor() throws Exception {
        Constructor<MemberThreadLocal> constructor = MemberThreadLocal.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        Throwable cause = exception.getCause();

        assertInstanceOf(IllegalStateException.class, cause);
        assertEquals("Utility class", cause.getMessage());
    }
}