package app.domain.invoice.internal;

import app.domain.invoice.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public Boolean isShiftedVat() {
        return invoiceVatRegimeDelegate.getCalculationMethod() == CalculationMethod.SHIFTED_VAT;
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

    @Override
    public BigDecimal getInvoiceSubTotalInclVat() {

        final VatRepository vatRepository = new VatRepository();
        final String declarationCountryIso =
                invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                        invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                        invoiceVatRegimeDelegate.getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, declarationCountryIso);

        if (this.invoiceVatRegimeDelegate.getCalculationMethod() == CalculationMethod.INCLUDING_VAT) {
            BigDecimal totalAmountInclVat = invoiceLines.stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountInclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountInclVat;
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal getInvoiceSubTotalExclVat() {

        final VatRepository vatRepository = new VatRepository();
        final String destinationCountry =
                invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                        invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                        invoiceVatRegimeDelegate.getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, destinationCountry);

        CalculationMethod calculationMethod = this.invoiceVatRegimeDelegate.getCalculationMethod();

        if (calculationMethod == CalculationMethod.EXCLUDING_VAT ||
                calculationMethod == CalculationMethod.SHIFTED_VAT ||
                calculationMethod == CalculationMethod.INTRA_CUMM_B2B) {

            BigDecimal totalAmountExclVat = invoiceLines.stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountExclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountExclVat;
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal getTotalInvoiceAmountInclVat() {

        switch (this.invoiceVatRegimeDelegate.getCalculationMethod()) {

            case INCLUDING_VAT:
                return getInvoiceSubTotalInclVat();

            case EXCLUDING_VAT:
                return getInvoiceSubTotalExclVat().add(getInvoiceTotalVat());

            case SHIFTED_VAT:
            case INTRA_CUMM_B2B:
                return getInvoiceSubTotalExclVat();

            default:
                return null;
        }

    }

    @Override
    public BigDecimal getTotalInvoiceAmountExclVat() {

        switch (this.invoiceVatRegimeDelegate.getCalculationMethod()) {

            case INCLUDING_VAT:
                return getInvoiceSubTotalInclVat().subtract(getInvoiceTotalVat());

            case EXCLUDING_VAT:
            case SHIFTED_VAT:
            case INTRA_CUMM_B2B:
                return getInvoiceSubTotalExclVat();

            default:
                return null;
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
        final VatCalculationRegime vatCalculationRegime =
                invoiceVatRegimeDelegate.getVatCalculationRegime();

        final VatRepository vatRepository = new VatRepository();

        switch (vatCalculationRegime) {
            case CONSUMER:
            case B2B_NATIONAL:
            case EXPORT:
                Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                        invoiceLines.stream()
                                .collect(Collectors.groupingBy(
                                        invoiceLine -> vatRepository.findByTariffAndDate(
                                                invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                                                        invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                                                        invoiceVatRegimeDelegate.getDestinationCountryOfDefault()),
                                                invoiceLine.getVatTariff(),
                                                invoiceLine.getVatReferenceDate())));

                Map<VatPercentage, VatAmountSummary> vatPercentageVatAmountSummaryMap =
                        mapOfInvoiceLinesPerVatPercentage.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        optionalListEntry -> calculateVatAmountForVatTariff(
                                                optionalListEntry.getKey(),
                                                optionalListEntry.getValue())));

                return vatPercentageVatAmountSummaryMap;

            case B2B_NATIONAL_SHIFTED_VAT:
            case B2B_EU_SERVICES:
            case B2B_EU_E_SERVICES:
                return new HashMap<>();

            case B2B_EU_GOODS:
                Map<VatPercentage, VatAmountSummary> vatPercentageVatAmountSummaryMap2 = new HashMap<>();

                VatPercentage vatPercentage = vatRepository.findByTariffAndDate(
                        invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                                invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                                invoiceVatRegimeDelegate.getDestinationCountryOfDefault()),
                        VatTariff.ZERO, LocalDate.now());

                        VatAmountSummary vatAmount = new VatAmountSummary(vatPercentage, new BigDecimal("0.00"), getInvoiceSubTotalExclVat(), getTotalInvoiceAmountExclVat());

                vatPercentageVatAmountSummaryMap2.put(vatPercentage, vatAmount);

                return vatPercentageVatAmountSummaryMap2;

            default:
                return null;
        }

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
                    .map(invoiceLine -> invoiceLine.getVatAmount(
                            invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                            invoiceVatRegimeDelegate.getDestinationCountryOfDefault(),
                            getInvoiceType() == InvoiceType.CONSUMER))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return company.getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }
}
