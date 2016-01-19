package app.domain.invoice;

public class Configuration {

    boolean configuredForCalculateVatOnIndividualLines;
    VatRepository vatRepository = new VatRepository();

    public boolean isConfiguredForCalculateVatOnIndividualLines() {
        return configuredForCalculateVatOnIndividualLines;
    }

    public VatRepository getVatRepository() {
        return vatRepository;
    }

    public void setConfiguredForCalculateVatOnIndividualLines(boolean configuredForCalculateVatOnIndividualLines) {
        this.configuredForCalculateVatOnIndividualLines = configuredForCalculateVatOnIndividualLines;

    }
}
