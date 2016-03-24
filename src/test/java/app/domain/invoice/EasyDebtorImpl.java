package app.domain.invoice;

public class EasyDebtorImpl implements Debtor {

    @Override
    public String getExternalId() {
        return "0";
    }

    @Override
    public String[] getFullAddress() {
        return new String[] {"Marc Enschede", "Enschede"};
    }

    @Override
    public String getEuTaxId() {
        return "DE1234567890";
    }

    @Override
    public String getCountryId() {
        return null;
    }
}
