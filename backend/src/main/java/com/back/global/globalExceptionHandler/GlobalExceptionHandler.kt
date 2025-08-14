package com.back.global.globalExceptionHandler

import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<RsData<Unit>> {
        return ResponseEntity(
            RsData("404-1", "해당 데이터가 존재하지 않습니다."),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(ex: ConstraintViolationException): ResponseEntity<RsData<Unit>> {
        val message = ex.constraintViolations
            .map { violation ->
                val field = violation.propertyPath.toString().split(".", limit = 2)[1]
                val messageTemplateBits = violation.messageTemplate.split(".")
                val code = messageTemplateBits[messageTemplateBits.size - 2]
                val violationMessage = violation.message
                "$field-$code-$violationMessage"
            }
            .sorted()
            .joinToString("\n")

        return ResponseEntity(
            RsData("400-1", message),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<RsData<Unit>> {
        val message = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .map { error -> "${error.field}-${error.code}-${error.defaultMessage}" }
            .sorted()
            .joinToString("\n")

        return ResponseEntity(
            RsData("400-1", message),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<RsData<Unit>> {
        return ResponseEntity(
            RsData("400-1", "요청 본문이 올바르지 않습니다."),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(ex: MissingRequestHeaderException): ResponseEntity<RsData<Unit>> {
        return ResponseEntity(
            RsData(
                "400-1",
                "${ex.headerName}-NotBlank-${ex.localizedMessage}"
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(ServiceException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(ex: ServiceException): ResponseEntity<RsData<Unit>> {
        val rsData = ex.rsData
        return ResponseEntity(rsData, HttpStatus.valueOf(rsData.statusCode))
    }
}
