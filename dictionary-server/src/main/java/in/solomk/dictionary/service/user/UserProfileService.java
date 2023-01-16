package in.solomk.dictionary.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public Mono<UserProfile> getByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public Mono<UserProfile> getUserProfileBySocialProviderId(String socialProvider, String subjectId) {
        String socialProviderId = "{" + socialProvider + "}" + subjectId;
        return userProfileRepository.findBySocialProviderIds(socialProviderId)
                                    .switchIfEmpty(userProfileRepository.createUser(socialProviderId));
    }
}
