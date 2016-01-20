package nl.hsleiden.ipsen3;

import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.hsleiden.ipsen3.config.AppConfiguration;
import nl.hsleiden.ipsen3.config.ClientFilter;
import nl.hsleiden.ipsen3.config.HibernateConfiguration;
import nl.hsleiden.ipsen3.core.Actie;
import nl.hsleiden.ipsen3.dao.ActieDAO;
import nl.hsleiden.ipsen3.dao.BestellijstDAO;
import nl.hsleiden.ipsen3.dao.WijnDAO;
import nl.hsleiden.ipsen3.resource.ActieResource;
import nl.hsleiden.ipsen3.resource.MailResource;
import nl.hsleiden.ipsen3.resources.BestellijstResource;
import nl.hsleiden.ipsen3.resources.WijnResource;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Bootstrap class for the App. Most of the configuration will be set here.
 *
 * @author Daan Rosbergen
 * Created by Daan on 30-Nov-15.
 */
public class App extends Application<AppConfiguration> {
    private final HibernateBundle<AppConfiguration> hibernate = new HibernateConfiguration();

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        /**
         * Creates a new AssetsBundle which will configure the service to serve the static files
         * located in {@code src/main/resources/${resourcePath}} as {@code /${uriPath}}. If no file name is
         * in ${uriPath}, ${indexFile} is appended before serving. For example, given a
         * {@code resourcePath} of {@code "/assets"} and a uriPath of {@code "/js"},
         * {@code src/main/resources/assets/example.js} would be served up from {@code /js/example.js}.
         *
         * @param resourcePath        the resource path (in the classpath) of the static asset files
         * @param uriPath             the uri path for the static asset files
         * @param indexFile           the name of the index file to use
         */
        bootstrap.addBundle((ConfiguredBundle)
            new ConfiguredAssetsBundle("/bower_components/", "/client", "index.html"));
    }

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
            environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        configureClientFilter(environment);

        final WijnDAO dao = new WijnDAO(hibernate.getSessionFactory());
        final WijnResource resource = new WijnResource(
                appConfiguration.getTemplate(),
                appConfiguration.getDefaultName(),
                dao
        );
        final MailResource mailResource = new MailResource(
                appConfiguration.getTemplate(),
                appConfiguration.getDefaultName()
        );
        environment.jersey().register(resource);
        environment.jersey().register(mailResource);

        final BestellijstDAO bestellijstDao = new BestellijstDAO(hibernate.getSessionFactory());
        final BestellijstResource BResource = new BestellijstResource(
                appConfiguration.getTemplate(),
                appConfiguration.getDefaultName(),
                bestellijstDao
        );
        environment.jersey().register(BResource);

        final ActieDAO actieDAO = new ActieDAO(hibernate.getSessionFactory());
        final ActieResource AResource = new ActieResource(
                appConfiguration.getTemplate(),
                appConfiguration.getDefaultName(),
                actieDAO
        );
        environment.jersey().register(AResource);
    }

    private void configureClientFilter(Environment environment) {
        environment.getApplicationContext().addFilter(
            new FilterHolder(new ClientFilter()),
            "/*",
            EnumSet.allOf(DispatcherType.class)
        );
    }
}
