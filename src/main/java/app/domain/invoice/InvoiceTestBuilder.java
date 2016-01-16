package app.domain.invoice;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marc on 16/01/16.
 */
public class InvoiceTestBuilder {

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
        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setIncludingVatInvoice(includingVatInvoice);
        invoice.setDebtor(debtor);
        invoice.setInvoiceLines(invoiceLineList);

        return invoice;
    }
}
