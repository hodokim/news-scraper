package newsscraper.service;

import lombok.RequiredArgsConstructor;
import newsscraper.mapper.UserPreferenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;

    @Transactional
    public void addPreferredSite(Long userId, String siteName) {
        userPreferenceMapper.insertUserPreferredSite(userId, siteName);
    }

    @Transactional
    public void removePreferredSite(Long userId, String siteName) {
        userPreferenceMapper.deleteUserPreferredSite(userId, siteName);
    }
}