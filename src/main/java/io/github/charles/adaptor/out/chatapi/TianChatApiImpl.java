package io.github.charles.adaptor.out.chatapi;

import io.github.charles.application.out.ChatApiPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TianChatApiImpl implements ChatApiPort {
    //example
    //http://api.tianapi.com/txapi/robot/index?key=16e2471e7a72f1e9dca46b2a80486c7d&question=robot

    private final String BASE_URL = "http://api.tianapi.com/txapi/robot/index";
    private final String KEY = "16e2471e7a72f1e9dca46b2a80486c7d";
    WebClient client = WebClient.create(BASE_URL);

    @Override
    public String getResponse(String input) {

        TianResponseDTO result = client.get()
                .uri(builder -> builder
                        .queryParam("key", KEY)
                        .queryParam("question", input)
                        .build())
                .retrieve()
                .bodyToMono(TianResponseDTO.class)
                .block();

        return result.getNewslist().get(0).getReply();
    }

    public static void main(String[] args) {
        TianChatApiImpl t = new TianChatApiImpl();
        System.out.println(t.getResponse("robot"));
    }
}
