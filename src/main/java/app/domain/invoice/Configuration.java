package app.domain.invoice;

import java.util.List;

public class Configuration {

    boolean configuredForCalculateVatOnIndividualLines = false;
    VatRepository vatRepository = new VatRepository();
    private String registrationCountry;
    private List<String> vatRegisteredCountries;

    /**
     * Discriminates if VAT calculation is done on summed amounts per vat tariff or on individual lines.
     * See http://www.belastingdienst.nl/wps/wcm/connect/bldcontentnl/belastingdienst/zakelijk/btw/administratie_bijhouden/facturen_maken/btw-bedrag_afronden for more information.
     */
    public boolean isConfiguredForCalculateVatOnIndividualLines() {
        return configuredForCalculateVatOnIndividualLines;
    }

    public VatRepository getVatRepository() {
        return vatRepository;
    }

    public void setConfiguredForCalculateVatOnIndividualLines(boolean configuredForCalculateVatOnIndividualLines) {
        this.configuredForCalculateVatOnIndividualLines = configuredForCalculateVatOnIndividualLines;

    }

    public void setRegistrationCountry(String registrationCountry) {
        this.registrationCountry = registrationCountry;
    }

    public String getRegistrationCountry() {
        return registrationCountry;
    }

    public void setVatRegisteredCountries(List<String> vatRegisteredCountries) {
        this.vatRegisteredCountries = vatRegisteredCountries;
    }

    public List<String> getVatRegisteredCountries() {
        return vatRegisteredCountries;
    }
}
