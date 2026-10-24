package ru.practicum.ewm.admin.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.users.dto.NewUserDto;
import ru.practicum.ewm.admin.users.dto.UserDto;
import ru.practicum.ewm.admin.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class UserController {

    private UserService userService;

    public UserController(@Qualifier("userServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Поступил запрос на добавление нового пользователя - {} ", userService);
        return userService.createUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с ИД - {} ", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam (name = "ids", required = false) List<Long> ids,
                                  @RequestParam (name = "from", defaultValue = "10", required = false) Integer from,
                                  @RequestParam (name = "size", defaultValue = "10", required = false) Integer size) {
        return userService.getUsers(ids, from, size);
    }
}
