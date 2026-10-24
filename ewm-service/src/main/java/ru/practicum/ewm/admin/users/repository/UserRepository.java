package ru.practicum.ewm.admin.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
