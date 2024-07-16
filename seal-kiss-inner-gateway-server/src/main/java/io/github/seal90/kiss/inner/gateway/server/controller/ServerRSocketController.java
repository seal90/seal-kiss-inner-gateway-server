package io.github.seal90.kiss.inner.gateway.server.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@Controller
public class ServerRSocketController {

    @MessageMapping("callServer")
    public Mono<Object> callServer(@Headers Map<String, Object> metadata, @Payload Mono<List<DataBuffer>> bodyFlux) {
        // proxy http
        log.info("server response");
        // parse rsocket metadata to http headers
        metadata.get("hello");
        Map<String, String> proxyHeaders = new HashMap<>();

        // parse serviceName to ip
        // send http request

        // which api can parse and send http body and proxyHeaders ?
        // which api can parse http response to rsocket response ?
        return WebClient.create()
                .post().uri("www.baidu.com")
                .headers((headers) -> headers.setAll(proxyHeaders))
//                .bodyValue(bodyFlux)
                .exchangeToMono(resp -> {
                    resp.headers();
//                    resp.body()
                    return Mono.just(resp);
                }).map(ClientResponse::headers);
    }

}
