package app.domain.invoice.testbuilders;

import app.domain.invoice.Configuration;
import app.domain.invoice.Debtor;
import app.domain.invoice.Invoice;
import app.domain.invoice.InvoiceLine;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marc on 16/01/16.
 */
public class InvoiceTestBuilder {

    private Configuration configuration = ConfigurationTestBuilder.newInstance().setDefault().build();
    private UUID id;
    private Boolean includingVatInvoice;
    private Debtor debtor;
    private List<InvoiceLine> invoiceLineList = new LinkedList<>();

    public InvoiceTestBuilder createDefault() {
        id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        includingVatInvoice = false;
        debtor = null;
        return this;
    }

    public InvoiceTestBuilder setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public InvoiceTestBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public InvoiceTestBuilder setIncludingVatInvoice(Boolean includingVatInvoice) {
        this.includingVatInvoice = includingVatInvoice;
        return this;
    }

    public InvoiceTestBuilder setDebtor(Debtor debtor) {
        this.debtor = debtor;
        return this;
    }

    public InvoiceTestBuilder addInvoiceLine(InvoiceLine invoiceLine) {
        this.invoiceLineList.add(invoiceLine);
        return this;
    }

    public static InvoiceTestBuilder newInstance() {
        return new InvoiceTestBuilder();
    }

    public Invoice build() {
        Invoice invoice = new Invoice(configuration);
        invoice.setId(id);
        invoice.setIncludingVatInvoice(includingVatInvoice);
        invoice.setDebtor(debtor);
        invoice.setInvoiceLines(invoiceLineList);

        return invoice;
    }
}
