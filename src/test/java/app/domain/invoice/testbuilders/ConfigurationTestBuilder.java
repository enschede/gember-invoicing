package app.domain.invoice.testbuilders;

import app.domain.invoice.Configuration;

/**
 * Created by marc on 17/01/16.
 */
public class ConfigurationTestBuilder {

    boolean configuredForCalculateVatOnIndividualLines;
    private String homeCountry;
    private boolean largeCompany;

    public ConfigurationTestBuilder setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
        return this;
    }

    public ConfigurationTestBuilder setLargeCompany(boolean largeCompany) {
        this.largeCompany = largeCompany;
        return this;
    }

    public ConfigurationTestBuilder setDefault() {
        this.configuredForCalculateVatOnIndividualLines = false;
        return this;
    }

    public ConfigurationTestBuilder setConfiguredForCalculateVatOnIndividualLines(boolean configuredForCalculateVatOnIndividualLines) {
        this.configuredForCalculateVatOnIndividualLines = configuredForCalculateVatOnIndividualLines;
        return this;
    }

    public static ConfigurationTestBuilder newInstance() {
        return new ConfigurationTestBuilder();
    }

    public Configuration build() {
        Configuration configuration = new Configuration();
        configuration.setConfiguredForCalculateVatOnIndividualLines(configuredForCalculateVatOnIndividualLines);
        return configuration;
    }
}
