package app.domain.invoice;

import app.domain.invoice.internal.IsoCountryCode;

import java.util.Map;

/**
 * Created by marc on 06/05/16.
 */
public interface Company {
    public VatCalculationPolicy getVatCalculationPolicy();

    public IsoCountryCode getDefaultVatCountry();

    public Map<IsoCountryCode, String> getVatRegistrations();
}
