package app.domain.invoice.internal.vatCalculationDelegate.impl;


import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceType;
import app.domain.invoice.VatCalculationPolicy;
import app.domain.invoice.internal.*;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class VatCalculationIncludingVatDelegate extends VatCalculationDelegate {

    protected VatCalculationIncludingVatDelegate(InvoiceImpl invoice) {
        super(invoice);
    }

    @Override
    public BigDecimal getInvoiceSubTotalInclVat() {
        final VatRepository vatRepository = new VatRepository();
        final String declarationCountryIso =
                invoice.invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                        invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                        invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, declarationCountryIso);

        BigDecimal totalAmountInclVat = invoice.getInvoiceLines().stream()
                .map(invoiceLine -> lineVatCalculator.getLineAmountInclVat(invoiceLine))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        return totalAmountInclVat;
    }

    @Override
    public BigDecimal getInvoiceSubTotalExclVat() {

        return null;
    }

    @Override
    public BigDecimal getTotalInvoiceAmountInclVat() {

        return getInvoiceSubTotalInclVat();
    }

    @Override
    public BigDecimal getTotalInvoiceAmountExclVat() {

        return getInvoiceSubTotalInclVat().subtract(getInvoiceTotalVat());
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
                invoice.invoiceVatRegimeDelegate.getVatCalculationRegime();

        final VatRepository vatRepository = new VatRepository();

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.getInvoiceLines().stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> vatRepository.findByTariffAndDate(
                                        invoice.invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                                                invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                                                invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault()),
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

    }

    @Override
    public VatAmountSummary calculateVatAmountForVatTariff(VatPercentage vatPercentage, List<InvoiceLine> cachedInvoiceLinesForVatTariff) {

        if (calculateVatOnSummaryBase()) {
            // This value is not calculated for a including VAT invoiceImpl, as is never used then
            BigDecimal totalSumExclVat = invoice.getInvoiceType() == InvoiceType.BUSINESS ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountExclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            // This value is not calculated for a excluding VAT invoiceImpl, as is never used then
            BigDecimal totalSumInclVat = invoice.getInvoiceType() == InvoiceType.CONSUMER ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountInclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            return vatPercentage.createVatAmountInfo(
                    invoice.getInvoiceType() == InvoiceType.CONSUMER,
                    totalSumExclVat,
                    totalSumInclVat);
        } else {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(
                            invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                            invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault(),
                            invoice.getInvoiceType() == InvoiceType.CONSUMER))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return invoice.getCompany().getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }

}
