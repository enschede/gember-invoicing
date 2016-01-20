package app.domain.invoice.testbuilders;

import app.domain.debtor.EasyDebtorImpl;
import app.domain.invoice.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class InvoiceTestBuilder {

    private Configuration configuration = ConfigurationTestBuilder.newInstance().setDefault().build();
    private UUID id;
    private Boolean includingVatInvoice;
    private Debtor debtor;
    private IsoCountryCode countryOfOrigin;
    private IsoCountryCode countryOfDestination;
    private List<InvoiceLine> invoiceLineList = new LinkedList<>();

    public InvoiceTestBuilder createDefault() {
        this.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .setIncludingVatInvoice(false)
                .setDebtor(new EasyDebtorImpl())
                .setCountryOfOrigin("NL")
                .setCountryOfDestination("NL");
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

    public InvoiceTestBuilder setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = new IsoCountryCode(countryOfOrigin);
        return this;
    }

    public InvoiceTestBuilder setCountryOfDestination(String countryOfDestination) {
        this.countryOfDestination = new IsoCountryCode(countryOfDestination);
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
        invoice.setCountryOfOrigin(countryOfOrigin);
        invoice.setCountryOfDestination(countryOfDestination);

        invoiceLineList.stream().forEach(invoiceLine -> invoiceLine.setInvoice(invoice));

        return invoice;
    }
}
