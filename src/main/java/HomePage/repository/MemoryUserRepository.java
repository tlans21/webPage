package HomePage.repository;

import HomePage.domain.model.User;

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

    public void clearStore() {
        store.clear();
    }
}