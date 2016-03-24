package app.domain.Component;

import app.domain.invoice.Configuration;
import app.domain.invoice.Debtor;
import app.domain.invoice.Invoice;
import app.domain.invoice.InvoiceImpl;

/**
 * Created by marc on 04/03/16.
 */
public class InvoiceBuilerImpl implements InvoiceBuilder {

    private Debtor debtor;

    @Override
    public void setDebtor(Debtor debtor) {

    }

    @Override
    public Invoice build() {
        Configuration configuration = null;
        InvoiceImpl invoice = new InvoiceImpl(configuration);

        invoice.setDebtor(debtor);

        return invoice;
    }
}
