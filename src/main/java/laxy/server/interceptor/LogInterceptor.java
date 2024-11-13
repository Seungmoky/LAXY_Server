package laxy.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 세션 정보
        Map<String, Object> sessionInfo = new LinkedHashMap<>();
        if (request.getSession().getAttribute("userId") != null) {
            sessionInfo.put("userId", request.getSession().getAttribute("userId"));
        }
        if (request.getSession().getAttribute("userName") != null) {
            sessionInfo.put("userName", request.getSession().getAttribute("userName"));
        }
        if (request.getSession().getAttribute("userTags") != null) {
            sessionInfo.put("userTags", request.getSession().getAttribute("userTags"));
        }

        // 헤더 정보
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if ("Content-Type".equalsIgnoreCase(headerName) || "JSESSIONID".equalsIgnoreCase(headerName)) {
                String headerValue = request.getHeader(headerName);
                if (headerValue != null && !headerValue.isEmpty()) {
                    headers.put(headerName, headerValue);
                }
            }
        }

        // 파라미터 정보
        Map<String, String> parameters = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> parameters.put(key, String.join(",", value)));

        // 로그 출력
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("URI: [").append(method).append("] ").append(uri);

        if (!sessionInfo.isEmpty()) {
            logMessage.append(" | Session: ").append(sessionInfo);
        }
        if (!headers.isEmpty()) {
            logMessage.append(" | Headers: ").append(headers);
        }
        if (!parameters.isEmpty()) {
            logMessage.append(" | Parameters: ").append(parameters);
        }

        log.info(logMessage.toString());

        return true;
    }
}
