package app.domain.invoice;

import java.util.UUID;

/**
 * Created by marc on 15/01/16.
 */
public class InvoiceFactory {

    public Invoice newInstance() {
        Configuration configuration = new Configuration();
        configuration.setConfiguredForCalculateVatOnIndividualLines(false);

        Invoice invoice = new Invoice(configuration);

        invoice.setId(UUID.randomUUID());

        return invoice;
    }
}
