package pet.jinx.mynx.http.request.session;

import lombok.Builder;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import pet.jinx.mynx.http.MynxHttpClient;
import pet.jinx.mynx.http.MynxHttpRequest;
import pet.jinx.mynx.http.MynxHttpResponse;

@Builder
public class SessionJoinRequest extends MynxHttpRequest<SessionJoinRequest.SessionJoinResponse> {
    @NonNull
    private String continuation;
    @NonNull
    private String hash;

    @Override
    public MynxHttpResponse<SessionJoinResponse> doRequest(MynxHttpClient httpClient) {
        return httpClient.post("/session/join", SessionJoinResponse.class, this, true);
    }

    public record SessionJoinResponse() {}
}
