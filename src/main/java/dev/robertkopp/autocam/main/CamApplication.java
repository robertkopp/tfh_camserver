/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.main;

/**
 *
 * @author robert kopp
 */
import com.xeiam.dropwizard.sundial.SundialBundle;
import com.xeiam.dropwizard.sundial.SundialConfiguration;
import dev.robertkopp.autocam.SampleData.Generator;
import dev.robertkopp.autocam.dataaccess.AppSettingsDataRepository;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.dataaccess.PhotoCollectionDataRepository;
import dev.robertkopp.autocam.dataaccess.SettingsForCameraRepository;
import dev.robertkopp.autocam.model.AppSettings;
import dev.robertkopp.autocam.model.Camera;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PhotoCollection;
import dev.robertkopp.autocam.model.SettingsForCamera;
import dev.robertkopp.autocam.resources.AppSettingsResource;
import dev.robertkopp.autocam.resources.ClientRaspiResource;
import dev.robertkopp.autocam.resources.PhotoCollectionResource;
import dev.robertkopp.autocam.resources.PingResource;
import dev.robertkopp.autocam.resources.SettingsResource;
import dev.robertkopp.autocam.resources.SingleImageResource;
import dev.robertkopp.autocam.resources.WebSocketHandler;
import dev.robertkopp.autocam.services.CamSenderServiceImpl;
import dev.robertkopp.autocam.services.CryptoSvcImpl;
import dev.robertkopp.autocam.services.ICamSenderService;
import dev.robertkopp.autocam.services.ICryptoService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.servlet.ServletRegistration;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.BroadcasterFactory;
import org.hibernate.SessionFactory;

public class CamApplication extends Application<DevelopmentConfiguration> {

    public static void main(String[] args) throws Exception {
        new CamApplication().run(args);
    }

    private final HibernateBundle<DevelopmentConfiguration> hibernate = new HibernateBundle<DevelopmentConfiguration>(
            Camera.class, ClientRaspi.class, AppSettings.class, PhotoCollection.class, SettingsForCamera.class
    ) {
        @Override
        public DataSourceFactory getDataSourceFactory(DevelopmentConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<DevelopmentConfiguration> bootstrap) {
        // nothing to do yet
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/", "index.html"));
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new SundialBundle<DevelopmentConfiguration>() {

            @Override
            public SundialConfiguration getSundialConfiguration(DevelopmentConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });

    }

    @Override
    public void run(DevelopmentConfiguration configuration,
            Environment environment) {

        AtmosphereServlet servlet = new AtmosphereServlet();
        servlet.framework().addInitParameter(ApplicationConfig.ANNOTATION_PACKAGE, WebSocketHandler.class.getPackage().getName());
        servlet.framework().addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");

        ServletRegistration.Dynamic registration = environment.servlets().addServlet("atmosphere", servlet);
        registration.addMapping("/websocket/*");

        BroadcasterFactory broadcasterFactory = servlet.framework().getBroadcasterFactory();

        SessionFactory session = hibernate.getSessionFactory();

        if (configuration.isDebugEnabled()) {
            Generator exampleDataGenerator = new Generator(session);
            exampleDataGenerator.run();
        }
        final ClientRaspiRepository clientRaspiRepository = new ClientRaspiRepository(session);
        final ICryptoService cryptoSvc = new CryptoSvcImpl();

        final PhotoCollectionDataRepository photoCollectionDataRepository = new PhotoCollectionDataRepository(session);

        final AppSettingsDataRepository appSettingsDataRepository = new AppSettingsDataRepository(session);
        final SettingsForCameraRepository settingsRepo = new SettingsForCameraRepository(session);
        final ICamSenderService senderSvc = new CamSenderServiceImpl(photoCollectionDataRepository, appSettingsDataRepository);

        Object[] resources = new Object[]{
            new SingleImageResource(clientRaspiRepository, broadcasterFactory),
            new ClientRaspiResource(
            clientRaspiRepository, broadcasterFactory),
            new AppSettingsResource(
            appSettingsDataRepository, broadcasterFactory),
            new PhotoCollectionResource(
            clientRaspiRepository, photoCollectionDataRepository,
            cryptoSvc,
            senderSvc, broadcasterFactory
            ),
            new PingResource(clientRaspiRepository),
            new SettingsResource(settingsRepo, clientRaspiRepository, broadcasterFactory)

        };

        for (Object resource : resources) {
            environment.jersey().register(resource);
        }

        environment.getApplicationContext().setAttribute("raspiRepo", clientRaspiRepository);
        environment.getApplicationContext().setAttribute("sessionFactory", session);
        environment.getApplicationContext().setAttribute("broadcaster", broadcasterFactory);

    }

}
