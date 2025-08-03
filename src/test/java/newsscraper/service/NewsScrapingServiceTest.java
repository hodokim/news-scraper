package newsscraper.service;

import newsscraper.dto.KeywordDTO;
import newsscraper.dto.UserDTO;
import newsscraper.mapper.KeywordMapper;
import newsscraper.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Spring Boot 애플리케이션 컨텍스트를 로드하여 테스트 환경을 구성합니다.
@SpringBootTest
class NewsScrapingServiceTest {

    @Autowired
    private NewsScrapingService newsScrapingService;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private UserMapper userMapper;

    // MyBatis의 SQL 실행을 제어하기 위해 SqlSessionTemplate을 주입받습니다.
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    @DisplayName("네이버 뉴스 스크래핑 기능이 정상 동작하는지 테스트한다.")
    void scrapeFromNaverTest() {
        // given: 테스트를 위한 준비 단계
        // 1. 테스트용 사용자를 먼저 DB에 저장합니다.
        UserDTO testUser = new UserDTO();
        testUser.setUsername("test_001");
        testUser.setPassword("password");
        testUser.setEmail("scrape@test_001.com");
        userMapper.insertUser(testUser);
        assertNotNull(testUser.getId());

        // 2. 방금 생성된 사용자의 ID를 사용하여 테스트용 키워드를 저장합니다.
        KeywordDTO testKeyword = new KeywordDTO();
        testKeyword.setUserId(testUser.getId());
        testKeyword.setKeyword("대한민국");
        keywordMapper.insertKeyword(testKeyword);
        assertNotNull(testKeyword.getId());

        System.out.println("테스트용으로 생성된 사용자 ID: " + testUser.getId());
        System.out.println("테스트용으로 생성된 키워드 ID: " + testKeyword.getId());

        // 3. (중요) 지금까지의 DB 변경(INSERT) 작업을 강제로 데이터베이스에 동기화합니다.
        sqlSessionTemplate.flushStatements();

        // when: 실제 테스트를 수행하는 단계
        // 이제 모든 데이터가 DB에 반영된 상태에서 스크래핑을 테스트합니다.
        System.out.println("네이버 스크래핑 테스트를 시작합니다. 키워드: " + testKeyword.getKeyword());
        newsScrapingService.scrapeFromNaver(testKeyword);
        System.out.println("네이버 스크래핑 테스트가 종료되었습니다.");

        // then: 결과 검증
        // 이 테스트에서는 로그를 통해 동작을 확인합니다.
    }
}
