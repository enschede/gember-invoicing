package app.domain.Component;

import app.domain.invoice.Debtor;
import app.domain.invoice.Invoice;

/**
 * Created by marc on 04/03/16.
 */
public interface InvoiceBuilder {

    public void setDebtor(Debtor debtor);

    public Invoice build();
}
