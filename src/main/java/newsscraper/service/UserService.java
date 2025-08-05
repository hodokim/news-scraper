// UserService.java
package newsscraper.service;

import newsscraper.dto.UserDTO;
import newsscraper.mapper.UserMapper;
import lombok.RequiredArgsConstructor; // 다시 RequiredArgsConstructor 사용
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }

    public UserDTO getUser(String username) {
        return userMapper.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        return userMapper.findAll();
    }
}