package pet.jinx.mynx.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pet.jinx.mynx.Mynx;

public class MynxHttpResponse<T> {
    @Getter
    @NotNull
    private final ResponseStatus status;

    @Nullable
    private final SuccessResponse<T> success;
    @Nullable
    @Getter
    private final FailResponse fail;
    @Nullable
    @Getter
    private final ErrorResponse error;

    private MynxHttpResponse(@NotNull ResponseStatus status, @Nullable SuccessResponse<T> success, @Nullable FailResponse fail, @Nullable ErrorResponse error) {
        this.status = status;
        this.success = success;
        this.fail = fail;
        this.error = error;
    }

    public T getSuccess() {
        if (this.success == null) return null;
        return this.success.data;
    }

    public boolean isSuccess() {
        return this.success != null;
    }

    public boolean isFail() {
        return this.fail != null;
    }

    public boolean isError() {
        return this.error != null;
    }

    @Getter
    public static class BaseResponse {
        @SerializedName("status")
        @NotNull
        private ResponseStatus status;
    }

    @Getter
    public static class SuccessResponse<T> extends BaseResponse {
        @SerializedName("data")
        @NotNull
        private T data;
    }

    @Getter
    public static class FailResponse extends BaseResponse {
        @SerializedName("code")
        @Nullable
        private String code;

        @SerializedName("message")
        @NotNull
        private String message;

    }

    @Getter
    public static class ErrorResponse extends BaseResponse {
        @SerializedName("code")
        @Nullable
        private String code;

        @SerializedName("message")
        @NotNull
        private String message;
    }

    public enum ResponseStatus {
        @SerializedName("success")
        SUCCESS,
        @SerializedName("fail")
        FAIL,
        @SerializedName("error")
        ERROR
    }

    public static <T> MynxHttpResponse<T> fromBase(String json, Class<T> successClazz) {
        BaseResponse baseResponse = MynxHttpClient.GSON.fromJson(json, BaseResponse.class);

        return switch (baseResponse.getStatus()) {
            case SUCCESS -> {
                SuccessResponse<T> successResponse = MynxHttpClient.GSON.fromJson(json, TypeToken.getParameterized(SuccessResponse.class, successClazz).getType());
                yield new MynxHttpResponse<>(ResponseStatus.SUCCESS, successResponse, null, null);
            }
            case FAIL -> {
                FailResponse failResponse = MynxHttpClient.GSON.fromJson(json, FailResponse.class);
                yield new MynxHttpResponse<>(ResponseStatus.FAIL, null, failResponse, null);
            }
            case ERROR -> {
                ErrorResponse errorResponse = MynxHttpClient.GSON.fromJson(json, ErrorResponse.class);
                yield new MynxHttpResponse<>(ResponseStatus.ERROR, null, null, errorResponse);
            }
        };
    }
}
