package app.domain.invoice;

import app.domain.AggregateRootBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Invoice extends AggregateRootBase {

    final InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegateDelegate = new InvoiceVatCalculatorDelegate(this);
    final Configuration configuration;
    Boolean includingVatInvoice;
    private Debtor debtor;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();

    public Invoice(Configuration configuration) {
        this.configuration = configuration;
    }

    public Boolean getIncludingVatInvoice() {
        return includingVatInvoice;
    }

    public void setIncludingVatInvoice(Boolean includingVatInvoice) {
        this.includingVatInvoice = includingVatInvoice;
    }

    public Debtor getDebtor() {
        return debtor;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }

    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    // --- Virtual data ---

    public BigDecimal getInvoiceTotalInclVat() {
        if (includingVatInvoice.booleanValue()) {
            return sumLineTotalsInclVat();
        } else {
            return sumLineTotalsExclVat().add(getInvoiceTotalVat());
        }
    }

    public BigDecimal getInvoiceTotalExclVat() {
        if (!includingVatInvoice.booleanValue()) {
            return sumLineTotalsExclVat();
        } else {
            return sumLineTotalsInclVat().subtract(getInvoiceTotalVat());
        }
    }

    private BigDecimal sumLineTotalsInclVat() {
        return invoiceLines.stream()
                .map(invoiceLine -> invoiceLine.getLineAmountInclVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumLineTotalsExclVat() {
        return invoiceLines.stream()
                .map(invoiceLine -> invoiceLine.getLineAmountExclVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInvoiceTotalVat() {
        return invoiceVatCalculatorDelegateDelegate.getInvoiceTotalVat();
    }

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {

        return invoiceVatCalculatorDelegateDelegate.getVatPerVatPercentage();
    }

    // --- CQRS commands and event handlers ---

    public void createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        this.debtor = createInvoiceCommand.getDebtor();
    }

    public void addInvoiceLines(AddInvoiceLinesCommand addInvoiceLinesCommand) {
        invoiceLines.addAll(Arrays.asList(addInvoiceLinesCommand.getInvoiceLines()));
    }
}
