package com.education.mypaymentservice.model.common;

import com.education.mypaymentservice.model.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private T data;
    private ProblemDetail error;

    private ResponseStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                data,
                null,
                ResponseStatus.SUCCESS,
                LocalDateTime.now()
        );
    }

    public static <T> CommonResponse<T> error(ProblemDetail problemDetail) {
        return new CommonResponse<>(
                null,
                problemDetail,
                ResponseStatus.ERROR,
                LocalDateTime.now()
        );
    }
}
