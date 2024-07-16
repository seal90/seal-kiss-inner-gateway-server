package io.github.seal90.kiss.inner.gateway.client.config;

import io.github.seal90.kiss.inner.gateway.client.controller.ClientRSocketController;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Configuration
public class ClientRSocketConfig {

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder rsocketRequesterBuilder,
                                                   RSocketStrategies rsocketStrategies){
        log.info("register to server");
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientRSocketController());
        RSocketRequester rsocketRequester = rsocketRequesterBuilder
                .setupRoute("register")
                .setupData("CLIENT_ID")
//                .setupMetadata(user, SIMPLE_AUTH)
//                .rsocketStrategies(builder ->
//                        builder.encoder(new SimpleAuthenticationEncoder()))
                .rsocketConnector(
                        rSocketConnector ->
                                rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)).maxAttempts(1))
                )
                .rsocketConnector(connector -> connector.acceptor(responder))
                .tcp("127.0.0.1", 9898);


        // The above does not connect immediately. When requests are made, a shared connection is established transparently and used.
        // Ensure that it can be linked to the server.
        rsocketRequester.rsocketClient().source().block();

        rsocketRequester.rsocketClient().onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();

        return rsocketRequester;
    }
}
