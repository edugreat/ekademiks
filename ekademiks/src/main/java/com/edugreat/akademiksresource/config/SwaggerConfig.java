package com.edugreat.akademiksresource.config;

import java.util.List;
import java.util.function.Predicate;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@SecurityScheme(
		
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER
		)
public class SwaggerConfig {
	
	@Bean
	OpenAPI  EkademiksOpenAPI() {
		
		return new OpenAPI()
				.info(new Info().title("Ekademiks API Documentation")
						.description("Ekademiks application ")
						.version("v1.0")
						.contact(new Contact().name("Support")
								.email("anyanwuchinedu596@gmail.com")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
				
				
	}
//	
//	@Bean
//	OpenApiCustomiser adminOnlyEndpoints() {
//		
//		
//		return openApi -> {
//			
//			 // check if the path starts /learning path 
//			Predicate<String> isLearningPath = path -> path.startsWith("/learning/");
//			
//			openApi.getPaths().entrySet().removeIf(entry -> {
//				
//				if(isLearningPath.test(entry.getKey())) {
//					
////					add admin security requirement to learning end-point
//					entry.getValue().readOperations().forEach(operation -> {
//						
//						operation.setSecurity(List.of(
//								
//								new SecurityRequirement().addList("bearerAuth", List.of("Admin"))
//								));
//						
//						operation.addTagsItem("Admin Only");
//					});
//					
//					return false;
//				}
//				
//				return false;
//			});
//		};
//	}

}
