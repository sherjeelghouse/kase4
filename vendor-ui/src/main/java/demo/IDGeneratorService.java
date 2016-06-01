package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sherjeelg on 5/18/2016.
 * <p>
 * Service Class to Demo Circuit-breaker pattern using Netflix's Hystrix
 */
@Service
public class IDGeneratorService  {

    @Primary
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @HystrixCommand(fallbackMethod = "resilient")
    public String generateIdentifier(String serviceUrl) {
        String identifier = restTemplate().getForObject(serviceUrl, String.class);
        return identifier;
    }

    private AtomicInteger atomicInteger = new AtomicInteger(1000);

    public String resilient(String serviceUrl) {
        return String.valueOf(atomicInteger.getAndIncrement());
    }
}
