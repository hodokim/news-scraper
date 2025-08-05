package newsscraper.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserPreferenceMapper {
    int insertUserPreferredSite(@Param("userId") Long userId, @Param("siteName") String siteName);
    int deleteUserPreferredSite(@Param("userId") Long userId, @Param("siteName") String siteName);
}