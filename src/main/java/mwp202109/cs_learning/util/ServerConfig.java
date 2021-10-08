package mwp202109.cs_learning.util;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;

    private String port = "8080";

    public String getUrl(){
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress() +":"+port;
        } catch (UnknownHostException e) {
            return "";
        }
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

}
