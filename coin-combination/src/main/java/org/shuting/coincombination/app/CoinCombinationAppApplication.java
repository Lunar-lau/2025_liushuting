package org.shuting.coincombination.app;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.shuting.coincombination.app.resources.CoinResource;
import org.shuting.coincombination.app.service.CoinCalculator;

import java.util.EnumSet;

import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

public class CoinCombinationAppApplication extends Application<CoinCombinationAppConfiguration> {

    public static void main(final String[] args) throws Exception {
        new CoinCombinationAppApplication().run(args);
    }

    @Override
    public String getName() {
        return "CoinCombinationApp";
    }

    @Override
    public void initialize(final Bootstrap<CoinCombinationAppConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final CoinCombinationAppConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter(ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(new CoinResource(new CoinCalculator()));
    }

}
