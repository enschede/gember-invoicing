package app.domain.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvoiceImpl implements Invoice {

    // These attributes are protected as delegates inspect them on attribute base, not on get-method base
    protected final Configuration configuration;
    protected final InvoiceVatRegimeDelegate invoiceVatRegimeDelegate;
    protected final InvoiceCalculatorDelegate invoiceCalculatorDelegate;
    protected Debtor debtor;
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();
    protected IsoCountryCode countryOfOrigin;
    protected IsoCountryCode countryOfDestination;
    protected boolean consumerInvoice;
    protected boolean vatShifted;

    public InvoiceImpl(Configuration configuration) {
        this.configuration = configuration;

        invoiceVatRegimeDelegate = new InvoiceVatRegimeDelegate(this);
        invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(this);
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

    public boolean isVatShifted() {
        return vatShifted;
    }

    public void setVatShifted(boolean vatShifted) {
        this.vatShifted = vatShifted;
    }

    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    // --- Virtual data ---

    public BigDecimal getInvoiceTotalInclVat() {
        return invoiceCalculatorDelegate.getInvoiceTotalInclVat();
    }

    public BigDecimal getInvoiceTotalExclVat() {
        return invoiceCalculatorDelegate.getInvoiceTotalExclVat();
    }

    private InvoiceVatRegimeDelegate.InternationalTaxRuleType isEffectiveConsumerInvoice() {
        return invoiceVatRegimeDelegate.getInternationalTaxRuleType();
    }

    public BigDecimal getInvoiceTotalVat() {
        return invoiceCalculatorDelegate.getTotalAmountVat();
    }

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {
        return invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage();
    }

}
