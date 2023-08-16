package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional
@DisplayName("Тесты класса UserService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private User mockUser1;
    private User mockUser2;
    @Mock
    UserRepository userRepository;
    UserServiceImpl userService;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
        mockUser1 = new User(1L, "Дональд", "donald@yandex.ru");
        mockUser2 = new User(2L, "Джо", "joe@yandex.ru");
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание User")
    public void createTest() {
        UserDto userDto = UserMapper.userToDto(mockUser1);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto));
        UserDto userDto2 = userService.create(userDto);

        Mockito.verify(userRepository, Mockito.times(1)).save(mockUser1);
    }

    @Test
    @DisplayName("Тест на создание User c дубликатом Email")
    public void createEmailDuplicateTest() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.com");
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        EmailDuplicateException exception = Assertions.assertThrows(
                EmailDuplicateException.class,
                () -> userService.create(userDto));
        Assertions.assertEquals("Ошибка дубля Email.", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }


    @Test
    @DisplayName("Тест на обновление User")
    public void updateTest() {
        UserDto userDto1 = UserMapper.userToDto(mockUser1);
        UserDto userDto2 = UserMapper.userToDto(mockUser2);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto1));

        Mockito.when(userRepository.saveAndFlush(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto2));

        Mockito.when((userRepository.findById(Mockito.any())))
                .thenReturn(Optional.ofNullable(UserMapper.dtoToUser(userDto1)));

        userService.create(userDto1);
        userDto2.setId(1L);

        User user2 = UserMapper.dtoToUser(userService.update(1L, userDto2));

        Mockito.verify(userRepository, Mockito.times(1)).saveAndFlush(UserMapper.dtoToUser(userDto2));
        Assertions.assertEquals(mockUser2.getName(), user2.getName());
        Assertions.assertEquals(mockUser2.getEmail(), user2.getEmail());
    }

    @Test
    @DisplayName("Тест на получение всех Users")
    public void getAllTest() {
        UserDto userDto1 = UserMapper.userToDto(mockUser1);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto1));

        User user1 = UserMapper.dtoToUser(userService.create(userDto1));

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1));

        List<UserDto> users = userService.getAll();
        Assert.assertArrayEquals(users.toArray(), userService.getAll().toArray(new UserDto[0]));
    }

    @Test
    @DisplayName("Тест на получение User по ID")
    public void getByIdTest() {
        UserDto userDto1 = UserMapper.userToDto(mockUser1);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto1));

        User user1 = UserMapper.dtoToUser(userService.create(userDto1));

        Mockito.when((userRepository.findById(Mockito.any())))
                .thenReturn(Optional.ofNullable(UserMapper.dtoToUser(userDto1)));

        Assert.assertEquals("Дональд", userService.getById(1L).getName());
        Assert.assertEquals("donald@yandex.ru", userService.getById(1L).getEmail());
        Assert.assertEquals(Long.valueOf(1), userService.getById(1L).getId());
    }

    @Test
    @DisplayName("Тест на удаление User по ID")
    public void deleteByIdTest() {
        UserDto userDto1 = UserMapper.userToDto(mockUser1);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto1));

        User user1 = UserMapper.dtoToUser(userService.create(userDto1));

        Mockito.when((userRepository.existsById(Mockito.any())))
                .thenReturn(true);

        userService.deleteById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Тест на удаление User c несуществующим ID")
    public void deleteByIdNoUserTest() {
        long id = 2L;
        Mockito.when(userRepository.existsById(id))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteById(id));
        Assertions.assertEquals("User не найден", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(userRepository, Mockito.times(0)).deleteById(id);
    }

    @Test
    @DisplayName("Тест на получение User c несуществующим ID")
    public void getByIdUserNotFoundTest() {
        long id = 2L;
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(id));
        Assertions.assertEquals("User не найден", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Тест на обновление Email User на дублирующий")
    public void updateEmailDuplicateTest() {
        Long id = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.com");
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        EmailDuplicateException exception = Assertions.assertThrows(
                EmailDuplicateException.class,
                () -> userService.update(id, userDto));
        Assertions.assertEquals("Ошибка дубля Email", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.times(1)).saveAndFlush(Mockito.any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Тест на UserMapper.")
    public void listUsersToListDtoTest() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Test1");
        user1.setEmail("test1@mail.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Test2");
        user2.setEmail("test2@mail.com");

        List<User> users = Arrays.asList(user1, user2);

        List<UserDto> expectedUserDtos = Arrays.asList(
                UserDto.builder().id(1L).name("Test1").email("test1@mail.com").build(),
                UserDto.builder().id(2L).name("Test2").email("test2@mail.com").build()
        );

        List<UserDto> actualUserDtos = UserMapper.listUsersToListDto(users);

        Assertions.assertEquals(expectedUserDtos, actualUserDtos);
    }


    @Test
    @DisplayName("Тест на UserMapper с исключением.")
    public void userToDtoNullUserTest() {
        User user = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UserMapper.userToDto(user);
        });
    }

    @Test
    @DisplayName("Тест на UserMapper с исключением Dto.")
    public void dtoToUserNullDtoTest() {
        UserDto userDto = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UserMapper.dtoToUser(userDto);
        });
    }

}
