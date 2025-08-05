package newsscraper.mapper;

import newsscraper.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    int insertUser(UserDTO user);
    UserDTO findByUsername(String username);
    UserDTO findById(Long id);
    List<UserDTO> findAll();
}