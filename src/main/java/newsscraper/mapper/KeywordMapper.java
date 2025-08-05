package newsscraper.mapper;

import newsscraper.dto.KeywordDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface KeywordMapper {
    int insertKeyword(KeywordDTO keyword);
    List<KeywordDTO> findKeywordsByUserId(Long userId);
    int deleteKeyword(Long keywordId);
}