package app.domain.invoice.internal;

public interface Debtor {

    String getExternalId();

    String[] getFullAddress();

    String getEuTaxId();

    String getCountryId();
}
