package app.domain.invoice.internal;

import app.domain.invoice.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceImpl implements Invoice {

    // These attributes are protected as delegates inspect them on attribute base, not on get-method base
    public final InvoiceVatRegimeDelegate invoiceVatRegimeDelegate = new InvoiceVatRegimeDelegate(this);
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

        final VatRepository vatRepository = new VatRepository();
        final IsoCountryCode destinationCountry = invoiceVatRegimeDelegate.getVatDeclarationCountry();

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

    public BigDecimal getInvoiceTotalExclVat() {

        final VatRepository vatRepository = new VatRepository();
        final IsoCountryCode destinationCountry = invoiceVatRegimeDelegate.getVatDeclarationCountry();

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
        final InvoiceVatRegimeDelegate.InternationalTaxRuleType internationalTaxRuleType =
                invoiceVatRegimeDelegate.getInternationalTaxRuleType();
        final VatRepository vatRepository = new VatRepository();

        if (internationalTaxRuleType == InvoiceVatRegimeDelegate.InternationalTaxRuleType.B2B_EU_INTRA_COMMUNITY_SHIFTED_VAT
                || internationalTaxRuleType == InvoiceVatRegimeDelegate.InternationalTaxRuleType.B2B_NATIONAL_SHIFTED_VAT) {
            return new HashMap<>();
        }

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoiceLines.stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> vatRepository.findByTariffAndDate(
                                        invoiceVatRegimeDelegate.getVatDeclarationCountry(),
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
                    .map(invoiceLine -> invoiceLine.getVatAmount(getProductDestinationCountry().get(), getInvoiceType() == InvoiceType.CONSUMER))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return company.getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }

}
