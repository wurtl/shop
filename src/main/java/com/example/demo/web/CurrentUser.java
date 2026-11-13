package com.example.demo.web;

import java.lang.annotation.*;

/** Параметр контроллера, в который подставляется текущий авторизованный пользователь. */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
