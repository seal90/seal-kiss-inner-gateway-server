package io.github.seal90.kiss.inner.gateway.server.controller;

import io.github.seal90.kiss.inner.gateway.server.AppConstant;
import io.github.seal90.kiss.inner.gateway.server.discovery.InstanceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ServerController {

    @Autowired
    private InstanceDiscovery instanceDiscovery;

    @PostMapping("/callClient")
    public Mono<Object> callClient(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String targetService = headers.getFirst(AppConstant.X_INNER_GATEWAY_TARGET_SERVICE);
        String targetPath = headers.getFirst(AppConstant.X_INNER_GATEWAY_TARGET_PATH);

        Flux<DataBuffer> bodyFlux = request.getBody();


        Flux<RSocketRequester> rSocketRequesterFlux = instanceDiscovery.findRequester();
        return rSocketRequesterFlux.next().flatMap(rSocketRequester -> rSocketRequester
                .route("callClient")
                .data(bodyFlux)
                .retrieveMono(Object.class)).defaultIfEmpty(Mono.empty());
    }
}
