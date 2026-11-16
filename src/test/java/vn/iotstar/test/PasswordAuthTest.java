package vn.iotstar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.controller.LogoutController;
import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.PasswordUtil;

class PasswordAuthTest {

    @Test
    void testHashAndVerifyPassword() {
        String hash1 = PasswordUtil.hashPassword("secret123");
        String hash2 = PasswordUtil.hashPassword("secret123");

        assertNotNull(hash1);
        assertNotEquals("secret123", hash1);
        assertNotEquals(hash1, hash2);
        assertTrue(PasswordUtil.isHashed(hash1));
        assertTrue(PasswordUtil.verifyPassword("secret123", hash1));
        assertTrue(PasswordUtil.verifyPassword("secret123", hash2));
        assertFalse(PasswordUtil.verifyPassword("wrong", hash1));
        assertFalse(PasswordUtil.verifyPassword(null, hash1));
        assertFalse(PasswordUtil.verifyPassword("", hash1));
        assertFalse(PasswordUtil.verifyPassword("123", "123"));
        assertFalse(PasswordUtil.isHashed("plain-text"));
        assertFalse(PasswordUtil.isHashed(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
    }

    @Test
    void testRegisterHashesPasswordAndLoginVerifies() {
        StubUserDao userDao = new StubUserDao();
        UserServiceImpl service = new UserServiceImpl(userDao, (to, name, otp) -> {
        });

        boolean registered = service.register("hashuser", "MyPass@2026", "hash@example.com", "Hash User", "0912345678");
        assertTrue(registered);

        User stored = userDao.get("hashuser");
        assertNotNull(stored);
        assertNotEquals("MyPass@2026", stored.getPassWord());
        assertTrue(PasswordUtil.isHashed(stored.getPassWord()));

        User loggedIn = service.login("hashuser", "MyPass@2026");
        assertNotNull(loggedIn);
        assertEquals("hashuser", loggedIn.getUserName());
        assertNull(service.login("hashuser", "wrongpass"));
        assertNull(service.login("nonexistent", "MyPass@2026"));
    }

    @Test
    void testInsertHashesPlainPasswordAndLegacyLoginMigrates() {
        StubUserDao userDao = new StubUserDao();
        UserServiceImpl service = new UserServiceImpl(userDao, (to, name, otp) -> {
        });

        User inserted = new User("ins@example.com", "insuser", "Inserted", "plainPass", null, 5, null,
                new Date(System.currentTimeMillis()));
        service.insert(inserted);
        assertTrue(PasswordUtil.isHashed(inserted.getPassWord()));
        assertNotNull(service.login("insuser", "plainPass"));

        User legacy = new User("admin@gmail.com", "admin", "Admin", "123", null, 1, "0987654321",
                new Date(System.currentTimeMillis()));
        userDao.insert(legacy);
        assertFalse(PasswordUtil.isHashed(legacy.getPassWord()));

        assertNull(service.login("admin", "wrong"));
        assertFalse(PasswordUtil.isHashed(userDao.get("admin").getPassWord()));

        User migrated = service.login("admin", "123");
        assertNotNull(migrated);
        assertTrue(PasswordUtil.isHashed(migrated.getPassWord()));
        assertTrue(PasswordUtil.verifyPassword("123", migrated.getPassWord()));
        assertNotNull(service.login("admin", "123"));
    }

    @Test
    void testLogoutInvalidatesSessionClearsRememberCookieAndRedirects() throws Exception {
        AtomicBoolean sessionInvalidated = new AtomicBoolean(false);
        List<Cookie> addedCookies = new ArrayList<>();
        Map<String, String> headers = new HashMap<>();
        StringBuilder redirectedUrl = new StringBuilder();

        HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> {
                    if ("invalidate".equals(method.getName())) {
                        sessionInvalidated.set(true);
                        return null;
                    }
                    return null;
                });

        Cookie rememberCookie = new Cookie(Constant.COOKIE_REMEMBER, "hashuser");
        Cookie otherCookie = new Cookie("JSESSIONID", "abc");

        HttpServletRequest req = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> switch (method.getName()) {
                    case "getSession" -> session;
                    case "getCookies" -> new Cookie[] { rememberCookie, otherCookie };
                    case "getContextPath" -> "/JPA";
                    default -> null;
                });

        HttpServletResponse resp = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> switch (method.getName()) {
                    case "addCookie" -> {
                        addedCookies.add((Cookie) args[0]);
                        yield null;
                    }
                    case "setHeader" -> {
                        headers.put((String) args[0], (String) args[1]);
                        yield null;
                    }
                    case "sendRedirect" -> {
                        redirectedUrl.append((String) args[0]);
                        yield null;
                    }
                    default -> null;
                });

        TestableLogoutController controller = new TestableLogoutController();
        controller.invokePost(req, resp);

        assertTrue(sessionInvalidated.get());
        assertEquals(1, addedCookies.size());
        Cookie cleared = addedCookies.get(0);
        assertEquals(Constant.COOKIE_REMEMBER, cleared.getName());
        assertEquals("", cleared.getValue());
        assertEquals(0, cleared.getMaxAge());
        assertEquals("/JPA", cleared.getPath());
        assertTrue(cleared.isHttpOnly());
        assertEquals("no-cache, no-store, must-revalidate", headers.get("Cache-Control"));
        assertEquals("/JPA/login", redirectedUrl.toString());
    }

    private static class TestableLogoutController extends LogoutController {
        private static final long serialVersionUID = 1L;

        void invokePost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            doPost(req, resp);
        }
    }

    private static class StubUserDao implements UserDao {
        private final Map<Integer, User> storage = new HashMap<>();
        private final AtomicInteger sequence = new AtomicInteger(1);

        @Override
        public User get(String username) {
            return storage.values().stream()
                    .filter(u -> username.equals(u.getUserName()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public User findByEmail(String email) {
            return storage.values().stream()
                    .filter(u -> email.equals(u.getEmail()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void insert(User user) {
            int id = sequence.getAndIncrement();
            user.setId(id);
            storage.put(id, user);
        }

        @Override
        public void update(User user) {
            storage.put(user.getId(), user);
        }

        @Override
        public void delete(int id) {
            storage.remove(id);
        }

        @Override
        public boolean checkExistEmail(String email) {
            return findByEmail(email) != null;
        }

        @Override
        public boolean checkExistUsername(String username) {
            return get(username) != null;
        }

        @Override
        public boolean checkExistPhone(String phone) {
            return storage.values().stream().anyMatch(u -> phone.equals(u.getPhone()));
        }
    }
}
