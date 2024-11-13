package laxy.server.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = event.getApplicationContext().getEnvironment().getProperty("server.port", "8001");
        System.out.println();
        System.out.println("URL: http://localhost:" + port);
        System.out.println("URL: http://43.202.77.176:" + port);
        System.out.println();
    }
}
