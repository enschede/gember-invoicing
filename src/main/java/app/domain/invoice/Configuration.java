package app.domain.invoice;

/**
 * Created by marc on 17/01/16.
 */
public class Configuration {

    private boolean configuredForCalculateVatOnIndividualLines;

    public boolean isConfiguredForCalculateVatOnIndividualLines() {
        return configuredForCalculateVatOnIndividualLines;
    }

    public void setConfiguredForCalculateVatOnIndividualLines(boolean configuredForCalculateVatOnIndividualLines) {
        this.configuredForCalculateVatOnIndividualLines = configuredForCalculateVatOnIndividualLines;
    }
}
