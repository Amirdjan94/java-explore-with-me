package ru.practicum.ewm.admin.users.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.users.dto.NewUserDto;
import ru.practicum.ewm.admin.users.dto.UserDto;
import ru.practicum.ewm.admin.users.dto.UserShortDto;
import ru.practicum.ewm.admin.users.model.User;

@Component
@NoArgsConstructor
public class UserMapper {
    public static User toUserFromNewUserDto(NewUserDto newUserDto) {
        User user = new User();
        user.setEmail(newUserDto.getEmail());
        user.setName(newUserDto.getName());
        return user;
    }

    public static UserDto toUserDtoFromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static UserShortDto toUserShortDtoFromUserEntity(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }
}
