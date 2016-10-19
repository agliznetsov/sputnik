package org.sputnik.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.sputnik.model.ForbiddenException;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Slf4j
public class SecurityUtilsTest {

    @Test
    public void test_password() throws Exception {
        log.info(UUID.randomUUID().toString());
        String p1 = SecurityUtils.getRandomPassword();
        String p2 = SecurityUtils.getRandomPassword();
        log.info(p1);
        log.info(p2);
        assertEquals(9, p1.length());
        assertFalse(p1.equals(p2));
    }

    @Test
    public void test_token() throws Exception {
        String token = SecurityUtils.createToken("Joe", new Date(System.currentTimeMillis() + 100000));
        log.info("token {}", token);
        log.info("length {}", token.length());
        String username = SecurityUtils.parseToken(token);
        Assert.assertEquals("Joe", username);
    }

    @Test(expected = ForbiddenException.class)
    public void test_expiration() throws Exception {
        String token = SecurityUtils.createToken("Joe", new Date(0));
        SecurityUtils.parseToken(token);
    }

    @Test(expected = ForbiddenException.class)
    public void test_invalid_token() throws Exception {
        String token = SecurityUtils.createToken("Joe", new Date(System.currentTimeMillis() + 100000));
        SecurityUtils.parseToken(token + "a");
    }

    @Test
    public void test_request() throws Exception {
        String token = SecurityUtils.createToken("Joe", new Date(System.currentTimeMillis() + 100000));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/path");
        request.addHeader("Authorization", "Bearer " + token);
        String user = SecurityUtils.getUser(request);
        assertEquals("Joe", user);
    }

}
