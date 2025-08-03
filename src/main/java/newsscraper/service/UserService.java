package newsscraper.service;

import newsscraper.dto.UserDTO;
import newsscraper.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
    }

    public UserDTO getUser(String username) {
        return userMapper.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        return userMapper.findAll();
    }
}