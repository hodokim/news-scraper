package newsscraper.mapper;

import newsscraper.dto.KeywordDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface KeywordMapper {
    // useGeneratedKeys와 keyProperty를 통해 insert 후 생성된 id가 DTO에 자동으로 채워짐
    int insertKeyword(KeywordDTO keyword);
    List<KeywordDTO> findKeywordsByUserId(Long userId);
    int deleteKeyword(Long keywordId);
}