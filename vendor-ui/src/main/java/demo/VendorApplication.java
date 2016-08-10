package demo;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@EnableRedisHttpSession
@EnableEurekaClient
@EnableCircuitBreaker
@SessionAttributes("vendors")
public class VendorApplication {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private IDGeneratorService idGeneratorService;

    @RequestMapping("/user")
    public Map<String, String> user(Principal user) {
        return Collections.singletonMap("name", user.getName());
    }

    public static void main(String[] args) {
        SpringApplication.run(VendorApplication.class, args);
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .httpBasic().and()
                    .authorizeRequests()
                    .antMatchers("/index.html", "/").permitAll()
                    .anyRequest().hasRole("USER");
            // @formatter:on
        }
    }

    @RequestMapping(value = "/vendorInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Vendor> vendorInfo(@ModelAttribute("vendors") List<Vendor> vendors) {
        return vendors;
    }

    @RequestMapping(value = "/addVendor", method = RequestMethod.POST)
    public
    @ResponseBody
    void addVendor(@ModelAttribute("vendors") List<Vendor> vendors, @RequestBody Vendor vendor) {
        String identifier = idGeneratorService.generateIdentifier(serviceUrl() + "/vendor/idGenerator");

        vendor.setIdentifier(identifier);

        vendors.add(vendor);
    }

    public String serviceUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances("vendor-service");
        return toURLString(instances.stream().findFirst().get());
    }

    String toURLString(ServiceInstance server) {
        try {
            return server.getUri().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ModelAttribute("vendors")
    public List<Vendor> getFormData() {
        return new ArrayList<Vendor>();
    }


    @Autowired
    private RestTemplate restTemplate;

    public String generateIdentifier(String serviceUrl) {
        String identifier = restTemplate.getForObject(serviceUrl, String.class);
        return identifier;
    }
}
