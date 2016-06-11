package app.domain.invoice.internal;

import java.util.Arrays;
import java.util.List;

public class IsoCountryCode {

    static final List<String> euCountryCodes = Arrays.asList("BE", "DE", "NL");

    String isoCountryCode;

    public IsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public boolean isEuCountry() {
        return isEuCountry(isoCountryCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoCountryCode that = (IsoCountryCode) o;

        return isoCountryCode.equals(that.isoCountryCode);

    }

    @Override
    public int hashCode() {
        return isoCountryCode.hashCode();
    }

    public static boolean isEuCountry(String countryCode) {
        return euCountryCodes.contains(countryCode);
    }
}
