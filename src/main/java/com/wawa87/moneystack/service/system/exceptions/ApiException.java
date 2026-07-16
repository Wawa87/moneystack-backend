package com.wawa87.moneystack.service.system.exceptions;

import com.wawa87.moneystack.service.system.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiException extends Exception {
   private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

   public ApiException(String message) {
       super(message);
       logger.error("ApiException: " + message);
   }

   public ApiException(Throwable cause) {
       super(cause);
       logger.error("ApiException: " + cause);
   }

   public ApiException(String message, Throwable cause) {
       super(message, cause);
       logger.error("ApiException: " + cause);
   }
}
