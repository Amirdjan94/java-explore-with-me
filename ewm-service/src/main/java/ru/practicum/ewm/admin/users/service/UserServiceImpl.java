package ru.practicum.ewm.admin.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.users.dto.NewUserDto;
import ru.practicum.ewm.admin.users.dto.UserDto;
import ru.practicum.ewm.admin.users.mapper.UserMapper;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.admin.users.mapper.UserMapper.toUserDtoFromUser;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserDto newUserDto) {
        User user = UserMapper.toUserFromNewUserDto(newUserDto);
        User newUser = userRepository.save(user);
        log.info("Создали нового пользователя и сохранили в БД - {} ", newUser);
        return toUserDtoFromUser(newUser);
    }

    @Override
    @Transactional
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
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            log.info("Пустой список Ids, запрос в БД для получения списка всех пользователей");
            Page<User> userPage = userRepository.findAll(pageable);
            return userPage.get().map(UserMapper::toUserDtoFromUser).collect(Collectors.toList());
        }
        log.info("Запрос в БД для получения списка пользователей по казанным Ids - {}", ids);
        Page<User> userPage = userRepository.findByIds(ids, pageable);
        return userPage.get().map(UserMapper::toUserDtoFromUser).collect(Collectors.toList());
    }
}
