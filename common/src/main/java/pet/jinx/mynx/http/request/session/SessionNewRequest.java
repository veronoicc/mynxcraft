package pet.jinx.mynx.http.request.session;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import pet.jinx.mynx.http.MynxHttpClient;
import pet.jinx.mynx.http.MynxHttpRequest;
import pet.jinx.mynx.http.MynxHttpResponse;

import java.util.UUID;

@Builder
public class SessionNewRequest extends MynxHttpRequest<SessionNewRequest.SessionNewResponse> {
    @Override
    public MynxHttpResponse<SessionNewResponse> doRequest(MynxHttpClient httpClient) {
        return httpClient.get("/session/new", SessionNewResponse.class, true);
    }

    public record SessionNewResponse(String username, UUID uuid, String continuation, @SerializedName("expires_at") Integer expiresAt) {}
}
