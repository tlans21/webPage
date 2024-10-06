package HomePage.repository;

import HomePage.domain.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    void updateLastLoginDate(String username);
    void softDeleteUser(Long userId);
    Optional<User> findById(Long userId);
    Optional<User> findByUsername(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findAll();
    void setTableName(String tableName);

    int count();
    int countById(Long userId);
    int countByUsername(String username);
    int countByEmail(String email);
    int countByRole(String role);
    List<User> findUserPage(int offset, int limit);

    List<User> findUserPageById(int offset, int limit, Long id);
    List<User> findUserPageByUsername(int offset, int limit, String username);
    List<User> findUserPageByEmail(int offset, int limit, String email);
    List<User> findUserPageByRole(int offset, int limit, String role);

    int countByCreatedAtAfter(LocalDateTime date);
    int countByCreatedAtBefore(LocalDateTime date);
    int countByLastLoginAfter(LocalDateTime date);
    int countByDeletedAtAfter(LocalDateTime date);
    int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    int countByDeletedAtBetween(LocalDateTime start, LocalDateTime end);

    boolean updateUserNickName(User user, String nickname);

}
