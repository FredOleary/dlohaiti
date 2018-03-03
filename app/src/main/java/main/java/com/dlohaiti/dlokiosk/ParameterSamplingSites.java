package main.java.com.dlohaiti.dlokiosk;

import main.java.com.dlohaiti.dlokiosk.domain.Parameter;
import main.java.com.dlohaiti.dlokiosk.domain.SamplingSite;

import java.util.List;

public class ParameterSamplingSites {
    private final Parameter parameter;
    private final List<SamplingSite> samplingSite;

    public ParameterSamplingSites(Parameter parameter, List<SamplingSite> samplingSite) {
        this.parameter = parameter;
        this.samplingSite = samplingSite;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public List<SamplingSite> getSamplingSites() {
        return samplingSite;
    }
}
