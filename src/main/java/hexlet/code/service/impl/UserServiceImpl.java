package hexlet.code.service.impl;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {

        return null;
    }

    @Override
    public UserDTO create(UserCreateDTO userCreateDTO) {
        return null;
    }

    @Override
    public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
