package app.domain.invoice.internal;

import app.domain.invoice.*;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegate;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegateFactory;
import app.domain.invoice.internal.vatTariffs.VatPercentage;

import java.math.BigDecimal;
import java.util.*;

public class InvoiceImpl implements Invoice {

    private Company company;
    private Customer customer;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
    private Optional<String> countryOfOrigin = Optional.empty();
    private Optional<String> countryOfDestination = Optional.empty();
    private InvoiceType invoiceType;
    private Boolean vatShifted;
    private Optional<ProductCategory> productCategory = Optional.empty();

    @Override
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    @Override
    public void setInvoiceType(InvoiceType invoiceType) {

        this.invoiceType = invoiceType;
    }

    @Override
    public Optional<String> getCountryOfOrigin() {
        return countryOfOrigin;
    }

    @Override
    public void setCountryOfOrigin(Optional<String> productOrigin) {
        this.countryOfOrigin = productOrigin;
    }

    @Override
    public Optional<String> getCountryOfDestination() {
        return countryOfDestination;
    }

    @Override
    public void setProductDestinationCountry(Optional<String> productDestination) {
        this.countryOfDestination = productDestination;
    }

    @Override
    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Company getCompany() {
        return company;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public VatCalculationRegime getInternationalTaxRuleType() {
        return null;
    }

    public Boolean getVatShifted() {
        return vatShifted;
    }

    @Override
    public void setVatShifted(Boolean vatShifted) {
        this.vatShifted = vatShifted;
    }


    public Optional<ProductCategory> getProductCategory() {
        return productCategory;
    }

    @Override
    public void setProductCategory(Optional<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public Boolean isShiftedVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.isVatShiftedInvoice();
    }

    @Override
    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    @Override
    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    @Override
    public BigDecimal getInvoiceSubTotalInclVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getInvoiceSubTotalInclVat();
    }

    @Override
    public BigDecimal getInvoiceSubTotalExclVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getInvoiceSubTotalExclVat();
    }

    @Override
    public BigDecimal getTotalInvoiceAmountInclVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getTotalInvoiceAmountInclVat();
    }

    @Override
    public BigDecimal getTotalInvoiceAmountExclVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getTotalInvoiceAmountExclVat();
    }

    @Override
    public BigDecimal getInvoiceTotalVat() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getInvoiceTotalVat();
    }

    @Override
    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {

        final VatCalculationDelegate vatCalculationDelegate =
                VatCalculationDelegateFactory.newVatCalculationDelegate(this);
        return vatCalculationDelegate.getVatPerVatTariff();
    }

    public VatCalculationDelegate getCalculationDelegate() {
        return VatCalculationDelegateFactory.newVatCalculationDelegate(this);
    }
}
