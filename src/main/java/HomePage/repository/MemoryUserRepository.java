package HomePage.repository;

import HomePage.domain.model.User;

import java.time.LocalDateTime;
import java.util.*;

public class MemoryUserRepository implements UserRepository {
    private static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(++sequence);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public void softDeleteUser(Long userId) {

    }

    @Override
    public void updateLastLoginDate(String username) {

    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<User> findByUsername(String name) {
        return store.values().stream()
                .filter(member -> member.getUsername().equals(name))
                .findAny();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(member -> member.getEmail().equals(email))
                .findAny();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return store.values().stream()
                .filter(member -> member.getPhoneNumber().equals(phoneNumber))
                .findAny();
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public int countByCreatedAtAfter(LocalDateTime date) {
        return 0;
    }

    @Override
    public int countByCreatedAtBefore(LocalDateTime date) {
        return 0;
    }

    @Override
    public int countByLastLoginAfter(LocalDateTime date) {
        return 0;
    }

    @Override
    public int countByDeletedAtAfter(LocalDateTime date) {
        return 0;
    }

    @Override
    public int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return 0;
    }

    @Override
    public int countByDeletedAtBetween(LocalDateTime start, LocalDateTime end) {
        return 0;
    }

    @Override
    public List<User> findUserPage(int offset, int limit) {
        return null;
    }

    @Override
    public void setTableName(String tableName) {

    }

    public void clearStore() {
        store.clear();
    }

    @Override
    public int countById(Long id) {
        return 0;
    }

    @Override
    public int countByUsername(String username) {
        return 0;
    }

    @Override
    public int countByEmail(String email) {
        return 0;
    }

    @Override
    public int countByRole(String role) {
        return 0;
    }

    @Override
    public List<User> findUserPageById(int offset, int limit, Long id) {
        return null;
    }

    @Override
    public List<User> findUserPageByUsername(int offset, int limit, String username) {
        return null;
    }

    @Override
    public List<User> findUserPageByEmail(int offset, int limit, String email) {
        return null;
    }

    @Override
    public List<User> findUserPageByRole(int offset, int limit, String role) {
        return null;
    }
}