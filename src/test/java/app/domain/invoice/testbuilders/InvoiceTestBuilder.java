package app.domain.invoice.testbuilders;

import app.domain.invoice.*;

import java.util.LinkedList;
import java.util.List;

public class InvoiceTestBuilder {

    private Configuration configuration = ConfigurationTestBuilder.newInstance().setDefault().build();
    private Boolean consumerInvoice;
    private Debtor debtor;
    private IsoCountryCode countryOfOrigin;
    private IsoCountryCode countryOfDestination;
    private List<InvoiceLine> invoiceLineList = new LinkedList<>();

    public InvoiceTestBuilder createDefault() {
        this.setConsumerInvoice(false)
                .setDebtor(new EasyDebtorImpl())
                .setCountryOfOrigin("NL")
                .setCountryOfDestination("NL");
        return this;
    }

    public InvoiceTestBuilder setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public InvoiceTestBuilder setConsumerInvoice(Boolean consumerInvoice) {
        this.consumerInvoice = consumerInvoice;
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

    public InvoiceImpl build() {
        InvoiceImpl invoiceImpl = new InvoiceImpl(configuration);
        invoiceImpl.setConsumerInvoice(consumerInvoice);
        invoiceImpl.setDebtor(debtor);
        invoiceImpl.setInvoiceLines(invoiceLineList);
        invoiceImpl.setCountryOfOrigin(countryOfOrigin);
        invoiceImpl.setCountryOfDestination(countryOfDestination);

        invoiceLineList.stream().forEach(invoiceLine -> invoiceLine.setInvoiceImpl(invoiceImpl));

        return invoiceImpl;
    }
}
