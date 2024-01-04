package com.deep.notes.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitingService {
    @Value("${deep.app.requestLimit}")
    private int REQUEST_LIMIT;  // Change as needed

    @Value("${deep.app.requestLimitMS}")
    private  long TIME_INTERVAL_MILLIS;  // Change as needed (1 minute in this example)

    private final Map<String, Long> clientRequestTimestamps = new HashMap<>();

    private final Map<String, Integer> clientRequestCounts = new HashMap<>();

    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();

        clientRequestCounts.putIfAbsent(clientId, 1);

        // Check if client is in the map
        if (clientRequestTimestamps.containsKey(clientId)) {
            // Get the last request timestamp for the client
            long lastRequestTime = clientRequestTimestamps.get(clientId);

            // Check if the time elapsed since the last request is within the limit
            if (currentTime - lastRequestTime < TIME_INTERVAL_MILLIS) {
                int requestCount = clientRequestCounts.get(clientId);
                if (requestCount >= REQUEST_LIMIT) {
                    // Limit exceeded, deny request
                    return false;
                }
                clientRequestCounts.put(clientId, clientRequestCounts.get(clientId) + 1);
            }
            else {
                clientRequestCounts.remove(clientId);
                clientRequestCounts.putIfAbsent(clientId, 1);
            }
        }

        clientRequestTimestamps.put(clientId, currentTime);



        return true;
    }
}
