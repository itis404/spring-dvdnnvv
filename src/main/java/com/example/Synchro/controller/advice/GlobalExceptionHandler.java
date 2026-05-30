package com.example.Synchro.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNotFound(NoResourceFoundException e) {
        log.warn("Страница не найдена: {}", e.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("error", "Запрашиваемая страница не найдена");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalError(Exception e, HttpServletRequest request) {
        log.error("Внутренняя ошибка сервера: ", e);

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("error", "Произошла внутренняя ошибка сервера. Пожалуйста, попробуйте позже.");
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleAjaxError(RuntimeException e, HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        if (isAjax) {
            log.error("AJAX ошибка: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        throw e;
    }
}