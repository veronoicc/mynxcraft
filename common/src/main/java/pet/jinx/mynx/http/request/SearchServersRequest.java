package pet.jinx.mynx.http.request;

import lombok.Builder;
import pet.jinx.mynx.http.MynxHttpClient;
import pet.jinx.mynx.http.MynxHttpRequest;
import pet.jinx.mynx.http.MynxHttpResponse;

@Builder
public class SearchServersRequest extends MynxHttpRequest<SearchServersRequest.SearchServersResponse> {
    @Override
    public MynxHttpResponse<SearchServersResponse> doRequest(MynxHttpClient httpClient) {
        return httpClient.get("/search/servers", SearchServersResponse.class, true);
    }

    public record SearchServersResponse() {}
}
