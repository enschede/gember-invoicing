package app.domain.invoice;

public interface Debtor {

    String getExternalId();

    String[] getFullAddress();

    String getEuTaxId();

    String getCountryId();
}
