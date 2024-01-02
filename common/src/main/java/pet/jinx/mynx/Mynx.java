package pet.jinx.mynx;
import com.github.mizosoft.methanol.Methanol;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pet.jinx.mynx.http.MynxHttpClient;
import pet.jinx.mynx.http.request.SearchServersRequest;
import pet.jinx.mynx.http.request.session.SessionNewRequest;


public class Mynx {
    @Getter
    private static final MynxHttpClient MYNX_CLIENT = new MynxHttpClient("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjMsImV4cCI6MTcwNDIzMjUzOX0.11Y-sGVRBgPhaPFgabB1b4gJvbFG23sGI5cfoXTqxj4");
    public static final String MOD_ID = "mynx";
    @NotNull
    @Getter
    private static Minecraft MINECRAFT = Minecraft.getInstance();
    @Getter
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        MINECRAFT = Minecraft.getInstance();
    }
}
