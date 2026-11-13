package com.example.demo.web;

import com.example.demo.entity.User;
import com.example.demo.service.CurrentUserService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

/**
 * Подставляет в параметры контроллера, помеченные @CurrentUser, текущего пользователя из базы.
 * Так контроллерам не нужно самим доставать пользователя из Authentication.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final CurrentUserService currentUserService;

    // Сервис, который ищет пользователя по логину, внедряется через конструктор
    public CurrentUserArgumentResolver(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    /**
     * Обрабатываем только параметры с аннотацией @CurrentUser типа User.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * Берём логин из Principal (его заполнил Spring Security) и загружаем пользователя.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Principal principal = webRequest.getUserPrincipal();
        return currentUserService.from(principal);
    }
}
