package com.zhvavyy.backend.grpc.service.interceptor;

import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class UserInterceptor implements ServerInterceptor {

    public static final Context.Key<Long> USER_ID_CTX_KEY = Context.key("user-id");
    private static final Metadata.Key<String> USER_ID_HEADER =
            Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String userId = headers.get(USER_ID_HEADER);
        Context context = Context.current();
        if (userId != null) {
            context = context.withValue(USER_ID_CTX_KEY, Long.parseLong(userId));
        }
        return Contexts.interceptCall(context, call, headers, next);
    }
}
