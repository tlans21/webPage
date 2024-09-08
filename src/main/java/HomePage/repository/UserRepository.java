package HomePage.repository;

import HomePage.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findAll();
    void setTableName(String tableName);

    int count();
    int countById(Long id);
    int countByUsername(String username);
    int countByEmail(String email);
    int countByRole(String role);
    List<User> findUserPage(int offset, int limit);

    List<User> findUserPageById(int offset, int limit, Long id);
    List<User> findUserPageByUsername(int offset, int limit, String username);
    List<User> findUserPageByEmail(int offset, int limit, String email);
    List<User> findUserPageByRole(int offset, int limit, String role);


}
