package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.mapper.GroupWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.GroupsService;
import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.language.SupportedLanguage;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@AllArgsConstructor
public class DeleteGroupHandler implements HandlerFunction<ServerResponse> {

    private final GroupsService groupsService;
    private final GroupWebApiMapper mapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMapMany(userId -> deleteGroup(request, userId))
                      .collectList()
                      .map(mapper::tGroupListResponse)
                      .flatMap(allGroupsResponse -> ServerResponse.ok()
                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                      .bodyValue(allGroupsResponse));
    }

    private Flux<Group> deleteGroup(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        var groupId = request.pathVariable("groupId");
        return groupsService.deleteGroup(userId, supportedLanguage, groupId);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
