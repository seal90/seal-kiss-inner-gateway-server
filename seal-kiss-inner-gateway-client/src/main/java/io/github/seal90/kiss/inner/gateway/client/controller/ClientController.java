package io.github.seal90.kiss.inner.gateway.client.controller;

import io.github.seal90.kiss.inner.gateway.client.AppConstant;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ClientController {

    @Autowired
    private RSocketRequester rSocketRequester;

    @PostMapping("/callServer")
    public Mono<ServerHttpResponse> callServer(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        Flux<DataBuffer> bodyFlux = request.getBody();


        Map<String, String> rsocketProxyHttpHeaders = new HashMap<>();
        rsocketProxyHttpHeaders.put(AppConstant.X_INNER_GATEWAY_TARGET_SERVICE, headers.getFirst(AppConstant.X_INNER_GATEWAY_TARGET_SERVICE));
        rsocketProxyHttpHeaders.put(AppConstant.X_INNER_GATEWAY_TARGET_METHOD, headers.getFirst(AppConstant.X_INNER_GATEWAY_TARGET_METHOD));
        rsocketProxyHttpHeaders.put(AppConstant.X_INNER_GATEWAY_TARGET_PATH, headers.getFirst(AppConstant.X_INNER_GATEWAY_TARGET_PATH));
        // TODO and other headers by config
        // http needs: content-type
        // biz needs: like auth

        // which api can send http body and rsocketProxyHttpHeaders ?
        // which api can parse rsocket response to http response ?
        return rSocketRequester.route("callServer").metadata(metadataSpec -> {
                    metadataSpec.metadata("hello:world" , new MediaType("text", "hello"));
                }).data(bodyFlux.collectList())
                .retrieveMono(ServerHttpResponse.class);
    }
}
