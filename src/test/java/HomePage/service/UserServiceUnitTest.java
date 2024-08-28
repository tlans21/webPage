package HomePage.service;

import HomePage.controller.UserForm;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void join() {
        // given
        User user = new User();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setEmail("test@test.com");
        user.setRoles("ROLE_USER");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
               User savedUser = invocation.getArgument(0);
               savedUser.setId(1L);  // Set an ID to simulate database save
               return savedUser;
        });
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        // when
        Long joinedUserId = userService.join(user);

        // then
        assertThat(joinedUserId).isEqualTo(1L);
        verify(userRepository).save(user);
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void joinWithMissingRequiredField() {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");

        // when & then
        assertThatThrownBy(() -> userService.join(user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이름이 필요합니다.");
    }

    @Test
    void validateDuplicateMember() {
        // given
        User user = new User();
        user.setUsername("test1");
        user.setEmail("test@test.com");

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        // when & then
        User duplicateUsername = new User();
        duplicateUsername.setUsername("test1");
        duplicateUsername.setEmail("different@test.com");

        assertThatThrownBy(() -> userService.validateDuplicateMember(duplicateUsername))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이름입니다.");

        User duplicateEmail = new User();
        duplicateEmail.setUsername("different");
        duplicateEmail.setEmail("test@test.com");

        assertThatThrownBy(() -> userService.validateDuplicateMember(duplicateEmail))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    void findMembers() {
        // given
        List<User> expectedUsers = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("test" + i);
            user.setPassword("testPassword" + i);
            user.setEmail("test" + i + "@test.com");
            user.setRoles("ROLE_USER");
            expectedUsers.add(user);
        }

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // when
        List<User> users = userService.findMembers();

        // then
        assertThat(users).hasSize(3);
        assertThat(users).containsExactlyElementsOf(expectedUsers);
        verify(userRepository).findAll();
    }

    @Test
    void findOne() {
        // given
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setUsername("test");
        expectedUser.setPassword("testPassword");
        expectedUser.setEmail("test@test.com");
        expectedUser.setRoles("ROLE_USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // when
        Optional<User> foundUser = userService.findOne(userId);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(expectedUser);
        verify(userRepository).findById(userId);
    }

    @Test
    void findByUsername() {
        // given
        String username = "test";
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setUsername("test");
        expectedUser.setPassword("testPassword");
        expectedUser.setEmail("test@test.com");
        expectedUser.setRoles("ROLE_USER");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // when
        Optional<User> foundUser = userService.findByUsername(username);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(expectedUser);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void userFormDto() {
        // given
        UserForm userForm = new UserForm();
        userForm.setUsername("test");
        userForm.setPassword("testPassword");
        userForm.setEmail("test@test.com");

        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");

        // when
        User user = userService.userFormDTO(userForm);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder).encode("testPassword");
    }
}