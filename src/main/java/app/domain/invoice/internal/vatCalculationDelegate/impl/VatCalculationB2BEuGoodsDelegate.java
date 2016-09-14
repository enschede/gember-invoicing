package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceType;
import app.domain.invoice.VatCalculationPolicy;
import app.domain.invoice.internal.*;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegate;
import app.domain.invoice.internal.vatCalculationDelegate.VatCalculationDelegateFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VatCalculationB2BEuGoodsDelegate extends VatCalculationDelegate {

    public VatCalculationB2BEuGoodsDelegate(InvoiceImpl invoice) {
        super(invoice);
    }

    @Override
    public BigDecimal getInvoiceSubTotalInclVat() {

        return null;
    }

    @Override
    public BigDecimal getInvoiceSubTotalExclVat() {

        final VatRepository vatRepository = new VatRepository();
        final String destinationCountry =
                getVatDeclarationCountryIso(
                        getOriginCountryOfDefault(invoice),
                        getDestinationCountryOfDefault(invoice));

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, destinationCountry);

        BigDecimal totalAmountExclVat = invoice.getInvoiceLines().stream()
                .map(invoiceLine -> lineVatCalculator.getLineAmountExclVat(invoiceLine))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        return totalAmountExclVat;
    }

    @Override
    public BigDecimal getTotalInvoiceAmountInclVat() {

                return getInvoiceSubTotalExclVat();
    }

    @Override
    public BigDecimal getTotalInvoiceAmountExclVat() {

                return getInvoiceSubTotalExclVat();
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

                Map<VatPercentage, VatAmountSummary> vatPercentageVatAmountSummaryMap2 = new HashMap<>();

                VatPercentage vatPercentage = vatRepository.findByTariffAndDate(
                        getVatDeclarationCountryIso(
                                getOriginCountryOfDefault(invoice),
                                getDestinationCountryOfDefault(invoice)),
                        VatTariff.ZERO, LocalDate.now());

                VatAmountSummary vatAmount = new VatAmountSummary(vatPercentage, new BigDecimal("0.00"), getInvoiceSubTotalExclVat(), getTotalInvoiceAmountExclVat());

                vatPercentageVatAmountSummaryMap2.put(vatPercentage, vatAmount);

                return vatPercentageVatAmountSummaryMap2;
    }

    private boolean calculateVatOnSummaryBase() {
        return invoice.getCompany().getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }

    @Override
    public Boolean isVatShiftedInvoice() {
        return false;
    }
}
