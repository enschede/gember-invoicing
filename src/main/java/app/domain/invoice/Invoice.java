package app.domain.invoice;

import app.domain.AggregateRootBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Invoice extends AggregateRootBase {

    // These attributes are protected as delegates inspect them on attribute base, not on get-method base
    protected final InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegateDelegate =
            new InvoiceVatCalculatorDelegate(this);
    protected final IntraCommunityTransactionDelegate intraCommunityTransactionDelegate =
            new IntraCommunityTransactionDelegate(this);
    protected final Configuration configuration;
    protected Boolean consumerInvoice;
    protected Debtor debtor;
    protected IsoCountryCode countryOfOrigin;
    protected IsoCountryCode countryOfDestination;
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();

    public Invoice(Configuration configuration) {
        this.configuration = configuration;
    }

    public Boolean getConsumerInvoice() {
        return consumerInvoice;
    }

    public void setConsumerInvoice(Boolean consumerInvoice) {
        this.consumerInvoice = consumerInvoice;
    }

    public Debtor getDebtor() {
        return debtor;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }

    public IsoCountryCode getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(IsoCountryCode countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public IsoCountryCode getCountryOfDestination() {
        return countryOfDestination;
    }

    public void setCountryOfDestination(IsoCountryCode countryOfDestination) {
        this.countryOfDestination = countryOfDestination;
    }

    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    // --- Virtual data ---

    public BigDecimal getInvoiceTotalInclVat() {
        if (isEffectiveConsumerInvoice()) {
            return sumLineTotalsInclVat();
        } else {
            return sumLineTotalsExclVat().add(getInvoiceTotalVat());
        }
    }

    public BigDecimal getInvoiceTotalExclVat() {
        if (!isEffectiveConsumerInvoice()) {
            return sumLineTotalsExclVat();
        } else {
            return sumLineTotalsInclVat().subtract(getInvoiceTotalVat());
        }
    }

    private boolean isEffectiveConsumerInvoice() {
        return consumerInvoice && !intraCommunityTransactionDelegate.isIntraCommunityTransaction();
    }

    private BigDecimal sumLineTotalsInclVat() {
        return invoiceLines.stream()
                .map(InvoiceLine::getLineAmountInclVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    private BigDecimal sumLineTotalsExclVat() {
        return invoiceLines.stream()
                .map(InvoiceLine::getLineAmountExclVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal getInvoiceTotalVat() {
        return invoiceVatCalculatorDelegateDelegate.getInvoiceTotalVat();
    }

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {
        return invoiceVatCalculatorDelegateDelegate.getAmountSummariesGroupedByVatPercentage();
    }

    // --- CQRS commands and event handlers ---

    public void createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        this.debtor = createInvoiceCommand.getDebtor();
    }

    public void addInvoiceLines(AddInvoiceLinesCommand addInvoiceLinesCommand) {
        invoiceLines.addAll(Arrays.asList(addInvoiceLinesCommand.getInvoiceLines()));
    }
}
