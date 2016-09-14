package app.domain.invoice.internal.vatCalculationDelegate.impl;


import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceType;
import app.domain.invoice.VatCalculationPolicy;
import app.domain.invoice.internal.*;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegate;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegateFactory;

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
                getVatDeclarationCountryIso(
                        getOriginCountryOfDefault(invoice),
                        getDestinationCountryOfDefault(invoice));

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

        final VatRepository vatRepository = new VatRepository();

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.getInvoiceLines().stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> vatRepository.findByTariffAndDate(
                                        getVatDeclarationCountryIso(
                                                getOriginCountryOfDefault(invoice),
                                                getDestinationCountryOfDefault(invoice)),
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
    public Boolean isVatShiftedInvoice() {
        return false;
    }
}
