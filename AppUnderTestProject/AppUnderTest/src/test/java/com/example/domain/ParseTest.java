package com.example.domain;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
public class ParseTest {

	private static final String WEBAPP_SRC = "src/main/webapp";
	@Drone
	DefaultSelenium browser;
	@ArquillianResource
	URL deploymentURL;

	   @Deployment(testable = false) // testable = false to run as a client
		public static WebArchive createDeployment() {
		   MavenDependencyResolver resolver = DependencyResolvers.use(
				   MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
			return ShrinkWrap.create(WebArchive.class)
				.addAsResource("META-INF/persistence.xml")
				.addAsWebResource(new File(WEBAPP_SRC, "index.html"))
				.addAsWebResource(new File(WEBAPP_SRC, "index2.html"))
				.addAsWebResource(new File(WEBAPP_SRC, "myInfo.html"))
				.addAsWebResource(new File(WEBAPP_SRC, "friendsInfo.html"))
							.addAsLibraries(resolver.artifacts("org.jboss.forge:forge-parser-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-java")
									.resolveAsFiles())
							.addAsLibraries(resolver.artifacts("org.seleniumhq.selenium:selenium-server")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-impl")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium")
									.resolveAsFiles())									
							.addAsLibraries(resolver.artifacts("org.jboss.arquillian.extension:arquillian-drone-selenium-server")
									.resolveAsFiles())
							.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		}
	static public WebArchive getDeployment() {
		return ShrinkWrap.create(WebArchive.class)

				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	@Test
	public void testName() throws InterruptedException {
		browser.open(deploymentURL + "index.html");
	}
}