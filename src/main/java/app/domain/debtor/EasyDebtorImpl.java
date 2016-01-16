package app.domain.debtor;

import app.domain.invoice.Debtor;

/**
 * Created by marc on 15/01/16.
 */
public class EasyDebtorImpl implements Debtor {

    @Override
    public String getExternalId() {
        return "0";
    }

    @Override
    public String[] getFullAddress() {
        return new String[] {"Marc Enschede", "Enschede"};
    }
}
