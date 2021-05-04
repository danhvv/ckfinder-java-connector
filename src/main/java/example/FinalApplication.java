package example;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cksource.ckfinder.servlet.CKFinderServlet;

@SpringBootApplication
public class FinalApplication implements ServletContextInitializer, WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(FinalApplication.class, args);
	}

	@Bean
	public CorsConfigurationSource corsFilter() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	public void onStartup(ServletContext servletContext) {
		// Register the CKFinder's servlet.
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("ckfinder", new CKFinderServlet());
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/ckfinder/*");
		dispatcher.setInitParameter("scan-path", "example.ckfinder");

		FilterRegistration.Dynamic filter = servletContext.addFilter("x-content-options", new Filter() {
			@Override
			public void init(FilterConfig filterConfig) {
			}

			@Override
			public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
					throws IOException, ServletException {
//                ((HttpServletResponse) response).setHeader("X-Content-Type-Options", "nosniff");
//                public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

				HttpServletResponse response = (HttpServletResponse) res;
				HttpServletRequest request = (HttpServletRequest) req;
				System.out.println("WebConfig; " + request.getRequestURI());
				response.setHeader("Access-Control-Allow-Origin", "http://mkmc.vn");
				response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
				response.setHeader("Access-Control-Allow-Headers",
						"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe");
				response.setHeader("Access-Control-Max-Age", "3600");
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setHeader("Access-Control-Expose-Headers", "Authorization");
				response.addHeader("Access-Control-Expose-Headers", "responseType");
				response.addHeader("Access-Control-Expose-Headers", "observe");
				System.out.println("Request Method: " + request.getMethod());

				if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
					try {
						chain.doFilter(req, res);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Pre-flight");
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT");
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Headers", "Access-Control-Expose-Headers"
							+ "Authorization, content-type," + "USERID" + "ROLE"
							+ "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with,responseType,observe");
					response.setStatus(HttpServletResponse.SC_OK);
				}

				chain.doFilter(req, res);

//                chain.doFilter(request, response);
			}

			@Override
			public void destroy() {
			}
		});

		filter.addMappingForUrlPatterns(null, false, "/userfiles/*");

		String tempDirectory;

		try {
			tempDirectory = Files.createTempDirectory("ckfinder").toString();
		} catch (IOException e) {
			tempDirectory = null;
		}

		dispatcher.setMultipartConfig(new MultipartConfigElement(tempDirectory));
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Configure the resource handler to serve files uploaded with CKFinder.
		String publicFilesDir = String.format("file:%s/userfiles/", System.getProperty("user.dir"));

		registry.addResourceHandler("/userfiles/**").addResourceLocations(publicFilesDir);
	}

}
