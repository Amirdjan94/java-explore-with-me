package ru.practicum.ewm.admin.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.users.dto.NewUserDto;
import ru.practicum.ewm.admin.users.dto.UserDto;
import ru.practicum.ewm.admin.users.mapper.UserMapper;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        User user = UserMapper.toUserFromNewUserDto(newUserDto);
        User newUser = userRepository.save(user);
        log.info("Создали нового пользователя и сохранили в БД - {} ", newUser);
        return UserMapper.toUserDtoFromUser(newUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователя с указанным ID - {} не существует ", userId);
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
        log.info("Удален пользователь с ID - {} ", userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {

        return List.of();
    }
}
