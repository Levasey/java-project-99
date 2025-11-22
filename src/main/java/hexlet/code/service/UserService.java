package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    UserDTO create(UserCreateDTO userCreateDTO);

    UserDTO update(Long id, UserUpdateDTO userUpdateDTO);

    void deleteById(Long id);
}
