package app.domain.invoice;

import app.domain.invoice.internal.countries.Country;

import java.util.Map;
import java.util.Optional;

public interface Company {
    public VatCalculationPolicy getVatCalculationPolicy();

    public String getPrimaryCountryIso();

    public Map<String, String> getVatRegistrations();

    boolean hasVatRegistrationFor(String isoOfCountryOfDestination);

    Optional<String> getVatRegistrationInOrigin(String originCountryOfDefault);
}
