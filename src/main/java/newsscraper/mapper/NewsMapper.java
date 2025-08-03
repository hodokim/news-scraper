package newsscraper.mapper;

import newsscraper.dto.NewsDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NewsMapper {
    int insertNews(NewsDTO news);
    List<NewsDTO> findNewsByUserId(Long userId);
    int countByLink(String link);
}
