package app.domain.invoice;

import java.util.UUID;

/**
 * Created by marc on 15/01/16.
 */
public class InvoiceFactory {

    public Invoice newInstance() {
        Invoice invoice = new Invoice();

        invoice.setId(UUID.randomUUID());

        return invoice;
    }
}
