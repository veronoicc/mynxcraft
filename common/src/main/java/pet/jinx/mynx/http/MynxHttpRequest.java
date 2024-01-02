package pet.jinx.mynx.http;

public abstract class MynxHttpRequest<T> {
    public abstract MynxHttpResponse<T> doRequest(MynxHttpClient httpClient);
}
