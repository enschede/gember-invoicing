package app.domain.invoice.internal;

import app.domain.invoice.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InvoiceImpl implements Invoice {

    // These attributes are protected as delegates inspect them on attribute base, not on get-method base
    public final InvoiceVatRegimeDelegate invoiceVatRegimeDelegate = new InvoiceVatRegimeDelegate(this);
    protected final InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(this);
    protected Debtor debtor;
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();
    protected Optional<IsoCountryCode> countryOfOrigin;
    protected Optional<IsoCountryCode> countryOfDestination;
    protected boolean consumerInvoice;
    protected boolean vatShifted;

    // -- New --

    @Override
    public InvoiceType getInvoiceType() {
        return null;
    }

    @Override
    public void setInvoiceType(InvoiceType invoiceType) {

    }

    @Override
    public Optional<IsoCountryCode> getProductOriginCountry() {
        return countryOfOrigin;
    }

    @Override
    public void setProductOriginCountry(Optional<IsoCountryCode> productOrigin) {
        this.countryOfOrigin = productOrigin;
    }

    @Override
    public Optional<IsoCountryCode> getProductDestinationCountry() {
        return countryOfDestination;
    }

    @Override
    public void setProductDestinationCountry(Optional<IsoCountryCode> productDestination) {
        this.countryOfDestination = productDestination;
    }

    @Override
    public void setCompany(Company company) {

    }

    @Override
    public void setCustomer(Customer customer) {

    }

    @Override
    public Company getCompany() {
        return null;
    }

    @Override
    public Customer getCustomer() {
        return null;
    }

    @Override
    public InvoiceVatRegimeDelegate.InternationalTaxRuleType getInternationalTaxRuleType() {
        return null;
    }


    // --- Old ---

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
