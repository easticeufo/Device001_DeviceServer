package com.madongfang;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.madongfang.mqtt.MqttService;
import com.madongfang.util.MqttUtil;

/**
 * Application Lifecycle Listener implementation class StartStopListener
 *
 */
@Component
public class StartStopListener implements ServletContextListener {
	
    /**
     * Default constructor. 
     */
    public StartStopListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	mqttUtil.destroy();
    	
        System.out.println("web stopped");
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	mqttUtil.init(serverUri, username, password, subscribeTopic, mqttService);
    	
    	logger.info("web started");
    }
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${mqtt.uri}")
   	private String serverUri;
       
   	@Value("${mqtt.username}")
   	private String username;
   	
   	@Value("${mqtt.password}")
   	private String password;
   	
   	@Value("${mqtt.subscribeTopic}")
   	private String subscribeTopic;
   	
   	@Autowired
   	private MqttUtil mqttUtil;
   	
   	@Autowired
   	private MqttService mqttService;
}
