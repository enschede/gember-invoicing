package app.domain.invoice.internal;

import app.domain.invoice.*;
import app.domain.invoice.internal.countries.Country;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceImpl implements Invoice {

    // These attributes are protected as delegates inspect them on attribute base, not on get-method base
    public final InvoiceVatRegimeDelegate invoiceVatRegimeDelegate = new InvoiceVatRegimeDelegate(this);
    protected Company company;
    protected Customer customer;
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();
    protected Optional<String> countryOfOrigin = Optional.empty();
    protected Optional<String> countryOfDestination = Optional.empty();
    protected InvoiceType invoiceType;
    protected Boolean vatShifted;
    protected Optional<ProductCategory> productCategory = Optional.empty();

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
    public Optional<String> getProductOriginCountry() {
        return countryOfOrigin;
    }

    @Override
    public void setProductOriginCountry(Optional<String> productOrigin) {
        this.countryOfOrigin = productOrigin;
    }

    @Override
    public Optional<String> getProductDestinationCountry() {
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

    @Override
    public void setVatShifted(Boolean vatShifted) {
        this.vatShifted = vatShifted;
    }

    @Override
    public void setProductCategory(Optional<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    // --- Old ---

    @Override
    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    @Override
    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    // --- Virtual data ---

    public BigDecimal getInvoiceTotalInclVat() throws OriginIsNotEuCountryException, ProductCategoryNotSetException {

        final VatRepository vatRepository = new VatRepository();
        final String destinationCountry =
                invoiceVatRegimeDelegate.getVatDeclarationCountryIso(getOriginCountryOfDefault(), getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, destinationCountry);

        if(this.getInvoiceType()==InvoiceType.CONSUMER) {
            BigDecimal totalAmountInclVat = invoiceLines.stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountInclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountInclVat;
        } else {
            BigDecimal invoiceTotalExclVat = getInvoiceTotalExclVat();
            BigDecimal invoiceTotalVat = getInvoiceTotalVat();

            return invoiceTotalExclVat.add(invoiceTotalVat);
        }
    }

    public BigDecimal getInvoiceTotalExclVat() throws OriginIsNotEuCountryException, ProductCategoryNotSetException {

        final VatRepository vatRepository = new VatRepository();
        final String destinationCountry =
                invoiceVatRegimeDelegate.getVatDeclarationCountryIso(getOriginCountryOfDefault(), getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, destinationCountry);

        if(this.getInvoiceType()==InvoiceType.CONSUMER) {
            return getInvoiceTotalInclVat().subtract(getInvoiceTotalVat());
        } else {
            BigDecimal totalAmountExclVat = invoiceLines.stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountExclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountExclVat;
        }
    }

    @Override
    public BigDecimal getInvoiceTotalVat() {
        Map<VatPercentage, VatAmountSummary> vatPerVatTariff = getVatPerVatTariff();

        return vatPerVatTariff.values().stream()
                .map(VatAmountSummary::getAmountVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    @Override
    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {
        final VatCalculationRegime internationalTaxRuleType =
                invoiceVatRegimeDelegate.getInternationalTaxRuleType(getOriginCountryOfDefault(), getDestinationCountryOfDefault());
        final VatRepository vatRepository = new VatRepository();

        if (internationalTaxRuleType == VatCalculationRegime.B2B_EU_SERVICES
                || internationalTaxRuleType == VatCalculationRegime.B2B_NATIONAL_SHIFTED_VAT) {
            return new HashMap<>();
        }

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoiceLines.stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> vatRepository.findByTariffAndDate(
                                        invoiceVatRegimeDelegate.getVatDeclarationCountryIso(getOriginCountryOfDefault(), getDestinationCountryOfDefault()),
                                        invoiceLine.getVatTariff(),
                                        invoiceLine.getVatReferenceDate())));

        return mapOfInvoiceLinesPerVatPercentage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        optionalListEntry -> calculateVatAmountForVatTariff(
                                optionalListEntry.getKey(),
                                optionalListEntry.getValue())));
    }

    private VatAmountSummary calculateVatAmountForVatTariff(VatPercentage vatPercentage, List<InvoiceLine> cachedInvoiceLinesForVatTariff) {

        if (calculateVatOnSummaryBase()) {
            // This value is not calculated for a including VAT invoiceImpl, as is never used then
            BigDecimal totalSumExclVat = getInvoiceType() == InvoiceType.BUSINESS ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountExclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            // This value is not calculated for a excluding VAT invoiceImpl, as is never used then
            BigDecimal totalSumInclVat = getInvoiceType() == InvoiceType.CONSUMER ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountInclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            return vatPercentage.createVatAmountInfo(
                    getInvoiceType() == InvoiceType.CONSUMER,
                    totalSumExclVat,
                    totalSumInclVat);
        } else {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(getOriginCountryOfDefault(), getDestinationCountryOfDefault(), getInvoiceType() == InvoiceType.CONSUMER))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return company.getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }

    private String getOriginCountryOfDefault() {
        return countryOfOrigin.orElse(company.getPrimaryCountryIso());
    }

    private String getDestinationCountryOfDefault() {
        return countryOfDestination.orElse(company.getPrimaryCountryIso());
    }
}
