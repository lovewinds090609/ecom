package com.ecommerce.project.exception;

import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice 主要用來統一處理 REST API 的異常，減少 try-catch 重複代碼
//可以攔截所有 Controller 的異常，或者 限定特定的 Controller

/**
 * 攔截Exception
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //透過 @ExceptionHandler，可以處理特定異常，例如 NullPointerException 或 IllegalArgumentException

    /**
     * 攔截輸入不合規範欄位拋出的錯誤
     * @param e 拋出的錯誤
     * @return ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        //使用Map原因為在 Spring Boot 中，當 @RestController 方法回傳 Map，Spring 會自動將其轉換為 JSON 格式
        Map<String,String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            response.put(fieldName,message);
            String Date = LocalDate.now().toString();
            response.put("time",Date);
        });

        return new ResponseEntity<Map<String,String>>(response,HttpStatus.BAD_REQUEST);
    }

    /**
     * 攔截當資源無法找到時拋出的錯誤
     * @param e 拋出的錯誤
     * @return ResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }

    /**
     * 攔截一些錯誤,例如變數已存在
     * @param e 拋出的錯誤
     * @return ResponseEntity
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
    /**
     * 攔截空指針錯誤,例如資料庫資料出錯
     * @param e 拋出的錯誤
     * @return ResponseEntity
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<APIResponse> myNullPointerException(NullPointerException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
