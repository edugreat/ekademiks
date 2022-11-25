package com.edugreat.akademiksresource.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.edugreat.akademiksresource.model.Category;
import com.edugreat.akademiksresource.model.Options;
import com.edugreat.akademiksresource.model.Solution;
import com.edugreat.akademiksresource.model.Subject;

@Configuration
public class ProjectConfig implements RepositoryRestConfigurer {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

		// Invokes method to disable unsupported methods
		disableUnsportedMethods(config);

		// invokes method to expose entity's id
		exposeIds(config);

	}

	private void disableUnsportedMethods(RepositoryRestConfiguration config) {
		// Our project is meant to be read only, though the entity classes provide
		// setter methods
		HttpMethod[] unsupportedMethods = { HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE };
		config.getExposureConfiguration().forDomainType(Category.class)
				.withItemExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods))
				.withCollectionExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods));

		config.getExposureConfiguration().forDomainType(Subject.class)
				.withItemExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods))
				.withCollectionExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods));

		config.getExposureConfiguration().forDomainType(Solution.class)
				.withItemExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods))
				.withCollectionExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods));

		config.getExposureConfiguration().forDomainType(Options.class)
				.withItemExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods))
				.withCollectionExposure((x, httpMethods) -> httpMethods.disable(unsupportedMethods));

	}

	// Helper method to expose the entity's id

	private void exposeIds(RepositoryRestConfiguration config) {

		Set<EntityType<?>> myEntities = entityManager.getMetamodel().getEntities();

		List<Class> entityClasses = new ArrayList<>();

		for (EntityType tempEntityType : myEntities) {
			entityClasses.add(tempEntityType.getJavaType());
		}

		Class[] domains = entityClasses.toArray(new Class[0]);
		config.exposeIdsFor(domains);
	}

}
