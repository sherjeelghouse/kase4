package demo;

import java.security.Principal;
import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@RestController
@EnableRedisHttpSession
@SessionAttributes("vendors")
public class VendorApplication {

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
	public @ResponseBody List<Vendor> vendorInfo(@ModelAttribute("vendors") List<Vendor> vendors) {

		return vendors;
	}

	@RequestMapping(value = "/addVendor", method = RequestMethod.POST)
	public @ResponseBody void addVendor(@ModelAttribute("vendors") List<Vendor> vendors,  @RequestBody Vendor vendor) {

		vendors.add(vendor);
	}

	@ModelAttribute("vendors")
	public List<Vendor> getFormData() {

		return new ArrayList<Vendor>();
	}

}
