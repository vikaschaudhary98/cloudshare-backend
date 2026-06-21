package in.vikaschaudhary.cloudshareapi.security;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks-url}")
    private String jkwsUrl;

    private final Map<String, PublicKey> keyCache = new ConcurrentHashMap<>();
    private volatile long lastFetchTime = 0;
    private static final long CACHE_TTL = 3600000; // 1 hour

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    public synchronized PublicKey getPublicKey(String kid) throws Exception {
        if (keyCache.containsKey(kid) && System.currentTimeMillis() - lastFetchTime < CACHE_TTL) {
            return keyCache.get(kid);
        }

        refreshKeys();

        PublicKey key = keyCache.get(kid);
        if (key == null) {
            throw new Exception("No matching JWKS key found for kid: " + kid);
        }
        return key;
    }

    private void refreshKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(jkwsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestMethod("GET");

        try (InputStream is = connection.getInputStream()) {
            JsonNode jwks = mapper.readTree(is);
            JsonNode keys = jwks.get("keys");

            for (JsonNode keyNode : keys) {
                String kid = keyNode.get("kid").asText();
                String kty = keyNode.get("kty").asText();
                String alg = keyNode.get("alg").asText();

                if ("RSA".equals(kty) && "RS256".equals(alg)) {
                    String n = keyNode.get("n").asText();
                    String e = keyNode.get("e").asText();
                    PublicKey publicKey = createPublicKey(n, e);
                    keyCache.put(kid, publicKey);
                }
            }

            lastFetchTime = System.currentTimeMillis();
        } finally {
            connection.disconnect();
        }
    }

    private PublicKey createPublicKey(String modulus, String exponent) throws Exception {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);
        BigInteger modulusBigInt = new BigInteger(1, modulusBytes);
        BigInteger exponentBigInt = new BigInteger(1, exponentBytes);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBigInt, exponentBigInt);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }
}