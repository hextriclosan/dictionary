package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.mapper.GroupWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.GroupsService;
import in.solomk.dictionary.service.language.SupportedLanguage;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@AllArgsConstructor
public class GetGroupHandler implements HandlerFunction<ServerResponse> {

    private final GroupsService groupsService;
    private final GroupWebApiMapper mapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> groupsService.getGroup(userId, extractLanguageCode(request), request.pathVariable("groupId")))
                      .map(mapper::toGroupResponse)
                      .flatMap(groupResponse -> ServerResponse.ok()
                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                      .bodyValue(groupResponse))
                      .switchIfEmpty(ServerResponse.notFound()
                                                   .build());
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
