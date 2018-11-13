package io.dymatics.cogny.api.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.dymatics.cogny.api.service.UserService;
import io.dymatics.cogny.domain.model.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRetrieveResolver implements HandlerMethodArgumentResolver {
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            long userNo = Long.parseLong(webRequest.getHeader("userNo"));
            return userService.getUser(userNo);
        } catch (Exception e) {
            return null;
        }
    }
}
