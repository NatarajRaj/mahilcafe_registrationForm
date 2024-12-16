package com.userInformation.registrationForm;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.net.URI;

@Component
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String appUrl = "http://localhost:8080";

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(appUrl));
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("cmd /c start " + appUrl);
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open " + appUrl);
                } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                    Runtime.getRuntime().exec("xdg-open " + appUrl);
                } else {
                    System.err.println("Failed to launch browser: Unsupported OS.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to launch the browser.");
        }
    }
}

