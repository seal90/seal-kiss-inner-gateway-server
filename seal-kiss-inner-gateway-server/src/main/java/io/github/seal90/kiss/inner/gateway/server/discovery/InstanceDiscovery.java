package io.github.seal90.kiss.inner.gateway.server.discovery;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

public interface InstanceDiscovery {

    Flux<RSocketRequester> findRequester();

}
