package com.zhvavyy.scheduler.service.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserIdGrpcClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> USER_ID_KEY =
            Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER);

    public static final ThreadLocal<Long> USER_ID_CONTEXT = new ThreadLocal<>();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Long userId = USER_ID_CONTEXT.get();
                if (userId != null) {
                    headers.put(USER_ID_KEY, userId.toString());
                    log.debug("Added user-id={} to gRPC metadata", userId);
                } else {
                    log.warn("No userId in context, skipping header");
                }
                super.start(responseListener, headers);
            }
        };
    }
}
