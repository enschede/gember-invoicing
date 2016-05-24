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
//    protected Debtor debtor;
    protected Company company;
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();
    protected Optional<IsoCountryCode> countryOfOrigin;
    protected Optional<IsoCountryCode> countryOfDestination;
    protected boolean vatShifted;
    protected InvoiceType invoiceType;

    // -- New --

    @Override
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    @Override
    public void setInvoiceType(InvoiceType invoiceType) {

        this.invoiceType = invoiceType;
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
        this.company = company;
    }

    @Override
    public void setCustomer(Customer customer) {

    }

    @Override
    public Company getCompany() {
        return company;
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

//    public Debtor getDebtor() {
//        return debtor;
//    }
//
//    public void setDebtor(Debtor debtor) {
//        this.debtor = debtor;
//    }

    @Override
    public boolean isVatShifted() {
        return vatShifted;
    }

    @Override
    public void setVatShifted(boolean vatShifted) {
        this.vatShifted = vatShifted;
    }

    @Override
    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    @Override
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
