/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service.exception;

import com.sololevelling.gym.sololevelling.util.exception.*;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_STATUS_MAP = new HashMap<>();

    static {
        EXCEPTION_STATUS_MAP.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
        EXCEPTION_STATUS_MAP.put(AccountLockException.class, HttpStatus.LOCKED);
        EXCEPTION_STATUS_MAP.put(DungeonNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(InvalidPasswordException.class, HttpStatus.UNAUTHORIZED);
        EXCEPTION_STATUS_MAP.put(InvalidRefreshTokenException.class, HttpStatus.FORBIDDEN);
        EXCEPTION_STATUS_MAP.put(ItemNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(ItemNotOwnedException.class, HttpStatus.FORBIDDEN);
        EXCEPTION_STATUS_MAP.put(NotEnoughStatPointsException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(QuestNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(RoleNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(StatsLowException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(ExpireException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(TaskCompletedException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(UserAlreadyExistsException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(UserNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(WorkoutNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException ex, WebRequest request) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.BAD_REQUEST);
        return buildErrorResponse(ex, status);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("code", getErrorCode(ex.getClass()));

        return new ResponseEntity<>(body, status);
    }

    private String getErrorCode(Class<?> exceptionClass) {
        return exceptionClass.getSimpleName()
                .replace("Exception", "")
                .toUpperCase();
    }
}