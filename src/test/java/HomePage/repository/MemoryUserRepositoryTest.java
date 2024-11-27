package HomePage.repository;

import HomePage.domain.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryUserRepositoryTest {
    MemoryUserRepository repository = new MemoryUserRepository();


    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }

    @Test
    public void save() {
        User user = new User();
        user.setUsername("simun");
        user.setPassword("1234");
        user.setEmail("tlans21@naver.com");
        user.setPhoneNumber("010-1234-5678");

        repository.save(user);

        User result = repository.findById(user.getId()).get();

        assertThat(user).isEqualTo(result);
    }

    @Test
    public void findById() {
        User user = new User();
        user.setUsername("simun");
        user.setPassword("1234");
        user.setEmail("tlans21@naver.com");
        user.setPhoneNumber("010-1234-5678");

        User saveUser = repository.save(user);
        User targetUser = repository.findById(saveUser.getId()).get();
        assertThat(saveUser).isEqualTo(targetUser);
    }

    @Test
    public void findByEmail() {
        User user = new User();
        user.setUsername("simun");
        user.setPassword("1234");
        user.setEmail("tlans21@naver.com");
        user.setPhoneNumber("010-1234-5678");

        User saveUser = repository.save(user);
        User targetUser = repository.findByEmail(saveUser.getEmail()).get();
        assertThat(saveUser).isEqualTo(targetUser);
    }

    @Test
    public void findByPhoneNumber() {
        User user = new User();
        user.setUsername("simun");
        user.setPassword("1234");
        user.setEmail("tlans21@naver.com");
        user.setPhoneNumber("010-1234-5678");

        User saveUser = repository.save(user);
        User targetUser = repository.findByPhoneNumber(saveUser.getPhoneNumber()).get();
        assertThat(saveUser).isEqualTo(targetUser);
    }

    @Test
    public void findById_ExceptionOccurs(){

    }
}