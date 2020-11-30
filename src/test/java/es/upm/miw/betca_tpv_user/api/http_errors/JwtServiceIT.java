package es.upm.miw.betca_tpv_user.api.http_errors;

import es.upm.miw.betca_tpv_user.TestConfig;
import es.upm.miw.betca_tpv_user.domain.services.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class JwtServiceIT {

    @Test
    void testJwtExceptionNotBearer() {
        assertTrue(JwtUtil.user("Not Bearer").isEmpty());
    }

    @Test
    void testJwtUtilExtract() {
        assertEquals("t.t.t", JwtUtil.extractToken("Bearer t.t.t"));
    }

    @Test
    void testCreateTokenAndVerify() {
        String token = JwtUtil.createToken("user-id", "name", "ROLE");
        assertEquals(3, token.split("\\.").length);
        assertTrue(token.length() > 30);
        assertEquals("user-id", JwtUtil.user(token));
        assertEquals("name", JwtUtil.name(token));
        assertEquals("ROLE", JwtUtil.role(token));
    }

}
