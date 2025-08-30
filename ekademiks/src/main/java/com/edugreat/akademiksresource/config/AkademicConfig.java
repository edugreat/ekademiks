package com.edugreat.akademiksresource.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;

@Configuration
public class AkademicConfig implements RepositoryRestConfigurer {

	@Autowired
	private EntityManager entityManager;

	@Bean
	ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		return mapper;
	}

	// Exposes entity endpoints
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

		Class[] classes = entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType)
				.toArray(Class[]::new);

		
		
		config.exposeIdsFor(classes);
		
		
    

	}

//    @Bean
//    RestTemplate restTemplate() {
//		
//		return new RestTemplate();
//		
//		
//	}
//	

	@Bean
 AuditorAware<String> auditorAware(){

		return new AuditorAwareImpl();
		
	}

}
