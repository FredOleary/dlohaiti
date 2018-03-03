package main.java.com.dlohaiti.dlokiosk.client;

import java.util.List;

public class DeliveryJson {
    private DeliveryConfigurationJson configuration;
    private List<DeliveryAgentJson> agents;

    public DeliveryConfigurationJson getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DeliveryConfigurationJson configuration) {
        this.configuration = configuration;
    }

    public List<DeliveryAgentJson> getAgents() {
        return agents;
    }

    public void setAgents(List<DeliveryAgentJson> agents) {
        this.agents = agents;
    }
}
