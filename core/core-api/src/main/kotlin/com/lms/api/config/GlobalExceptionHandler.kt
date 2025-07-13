package com.lms.config

import com.lms.core_domain.exception.BusinessException
import com.lms.core_domain.response.ApiResponse
import com.lms.core_domain.response.ValidationError
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException


@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * @RequestBody validation 실패 (@Valid 사용 시)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "유효하지 않은 값입니다",
                rejectedValue = fieldError.rejectedValue?.toString()
            )
        }

        val response = ApiResponse.error<Unit>(
            status = HttpStatus.BAD_REQUEST,
            message = "요청 데이터 검증에 실패했습니다",
            validationErrors = errors
        )

        logger.warn(
            "Validation failed for request: {} {}, errors: {}",
            request.method, request.requestURI, errors
        )

        return ResponseEntity.badRequest().body(response)
    }

    /**
     * @ModelAttribute validation 실패 (Query Parameter, Form Data)
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(
        ex: BindException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val errors = ex.fieldErrors.map { fieldError ->
            ValidationError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "유효하지 않은 값입니다",
                rejectedValue = fieldError.rejectedValue?.toString()
            )
        }

        val response = ApiResponse.error<Unit>(
            status = HttpStatus.BAD_REQUEST,
            message = "요청 파라미터 검증에 실패했습니다",
            validationErrors = errors
        )

        logger.warn(
            "Bind exception for request: {} {}, errors: {}",
            request.method, request.requestURI, errors
        )

        return ResponseEntity.badRequest().body(response)
    }

    /**
     * 필수 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.BAD_REQUEST,
            message = "필수 파라미터 '${ex.parameterName}'이(가) 누락되었습니다"
        )

        logger.warn(
            "Missing parameter: {} for request: {} {}",
            ex.parameterName, request.method, request.requestURI
        )

        return ResponseEntity.badRequest().body(response)
    }

    /**
     * 파라미터 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.BAD_REQUEST,
            message = "파라미터 '${ex.name}'의 타입이 올바르지 않습니다. 기대값: ${ex.requiredType?.simpleName}"
        )

        logger.warn(
            "Type mismatch for parameter: {} with value: {} for request: {} {}",
            ex.name, ex.value, request.method, request.requestURI
        )

        return ResponseEntity.badRequest().body(response)
    }

    /**
     * 지원하지 않는 HTTP 메서드
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.METHOD_NOT_ALLOWED,
            message = "지원하지 않는 HTTP 메서드입니다: ${ex.method}"
        )

        logger.warn("Method not supported: {} for request: {}", ex.method, request.requestURI)

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response)
    }

    /**
     * 지원하지 않는 Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            message = "지원하지 않는 미디어 타입입니다: ${ex.contentType}"
        )

        logger.warn(
            "Unsupported media type: {} for request: {} {}",
            ex.contentType, request.method, request.requestURI
        )

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response)
    }

    /**
     * 존재하지 않는 엔드포인트
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(
        ex: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.NOT_FOUND,
            message = "요청한 리소스를 찾을 수 없습니다"
        )

        logger.warn("No handler found for request: {} {}", ex.httpMethod, ex.requestURL)

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    /**
     * 비즈니스 로직 예외 (커스텀 예외)
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = ex.status,
            message = ex.message
        )

        logger.warn(
            "Business exception: {} for request: {} {}",
            ex.message, request.method, request.requestURI
        )

        return ResponseEntity.status(ex.status).body(response)
    }

    /**
     * 일반적인 예외 (최종 catch)
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error<Unit>(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "서버 내부 오류가 발생했습니다"
        )

        logger.error(
            "Unexpected error for request: {} {}",
            request.method, request.requestURI, ex
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}
