package pet.jinx.mynx.http;

import com.github.mizosoft.methanol.MediaType;
import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MoreBodyPublishers;
import com.github.mizosoft.methanol.MutableRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Setter;
import lombok.SneakyThrows;
import pet.jinx.mynx.Mynx;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class MynxHttpClient {
    @Setter
    private String auth;
    private final Methanol methanol;
    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    public MynxHttpClient(String auth) {
        this.auth = auth;
        this.methanol = Methanol.newBuilder()
                .userAgent("Mynxcraft/0.1.0 ()")
                .baseUri("https://api.mynx.lol")
                .executor(Executors.newFixedThreadPool(16))
                .build();
    }

    @SneakyThrows
    public String send(HttpRequest request) {
        var response = methanol.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public <T> MynxHttpResponse<T> get(String uri, Class<T> responseClazz, Boolean needsAuth, Map<String, String> queries) {
        var request = applyAuth(MutableRequest.GET(uri), needsAuth);
        if (!queries.isEmpty()) {
            request = applyQuery(request, queries);
        }
        var response = send(request);
        return MynxHttpResponse.fromBase(response, responseClazz);
    }

    public <T> MynxHttpResponse<T> get(String uri, Class<T> responseClazz, Boolean needsAuth) {
        return get(uri, responseClazz, needsAuth, new HashMap<>());
    }

    public <T, D extends MynxHttpRequest<T>> MynxHttpResponse<T> post(String uri, Class<T> responseClazz, D body, Boolean needsAuth) {
        Mynx.getLOGGER().info(GSON.toJson(body));
        var request = applyAuth(MutableRequest.POST(uri, MoreBodyPublishers.ofMediaType(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)), MediaType.APPLICATION_JSON)), needsAuth);
        var response = send(request);
        return MynxHttpResponse.fromBase(response, responseClazz);
    }

    private MutableRequest applyAuth(MutableRequest request, Boolean needsAuth) {
        if (needsAuth) {
            return request.header("Authorization", "Bearer " + this.auth);
        }
        return request;
    }

    @SneakyThrows
    private MutableRequest applyQuery(MutableRequest request, Map<String, String> queries) {
        var uri = request.uri();
        StringBuilder sb = new StringBuilder(uri.getQuery() == null ? "" : uri.getQuery());
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            if (!sb.isEmpty())
                sb.append('&');
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return request.uri(new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), sb.toString(), uri.getFragment()));
    }
}
