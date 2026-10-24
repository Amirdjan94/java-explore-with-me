package ru.practicum.ewm.admin.users.service;

import ru.practicum.ewm.admin.users.dto.NewUserDto;
import ru.practicum.ewm.admin.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserDto newUserDto);

    void deleteUserById(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

}
