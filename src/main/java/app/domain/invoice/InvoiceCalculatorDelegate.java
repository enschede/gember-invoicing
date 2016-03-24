package app.domain.invoice;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceCalculatorDelegate {
    private final InvoiceImpl invoice;
    private final InvoiceVatRegimeDelegate regime;
    private final Configuration configuration;

    public InvoiceCalculatorDelegate(InvoiceImpl invoice) {
        this.invoice = invoice;
        this.regime = invoice.invoiceVatRegimeDelegate;
        this.configuration = invoice.configuration;
    }

    public BigDecimal getInvoiceTotalInclVat() {
        if (regime.consumerInvoice) {
            return sumLineTotalsInclVat();
        } else {
            return sumLineTotalsExclVat().add(getTotalAmountVat());
        }
    }

    public BigDecimal getInvoiceTotalExclVat() {
        if (!regime.consumerInvoice) {
            return sumLineTotalsExclVat();
        } else {
            return sumLineTotalsInclVat().subtract(getTotalAmountVat());
        }
    }

    private BigDecimal sumLineTotalsInclVat() {
        return invoice.invoiceLines.stream()
                .map(InvoiceLine::getLineAmountInclVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    private BigDecimal sumLineTotalsExclVat() {
        return invoice.invoiceLines.stream()
                .map(InvoiceLine::getLineAmountExclVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal getTotalAmountVat() {
        return getAmountSummariesGroupedByVatPercentage().values().stream()
                .map(VatAmountSummary::getAmountVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public Map<VatPercentage, VatAmountSummary> getAmountSummariesGroupedByVatPercentage() {
        InvoiceVatRegimeDelegate.InternationalTaxRuleType internationalTaxRuleType = regime.getInternationalTaxRuleType();
        if (internationalTaxRuleType == InvoiceVatRegimeDelegate.InternationalTaxRuleType.B2B_INTRA_COMMUNITY_SHIFTED_VAT
                || internationalTaxRuleType == InvoiceVatRegimeDelegate.InternationalTaxRuleType.B2B_SHIFTED_VAT) {
            return new HashMap<>();
        }

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.invoiceLines.stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> configuration.vatRepository.findByTariffAndDate(
                                        regime.getVatOriginCountry(),
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
            BigDecimal totalSumExclVat = !regime.consumerInvoice ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountExclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            // This value is not calculated for a excluding VAT invoiceImpl, as is never used then
            BigDecimal totalSumInclVat = regime.consumerInvoice ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountInclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            return vatPercentage.createVatAmountInfo(
                    regime.consumerInvoice,
                    totalSumExclVat,
                    totalSumInclVat);
        } else {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(invoice.getCountryOfDestination(), regime.consumerInvoice))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return !configuration.isConfiguredForCalculateVatOnIndividualLines();
    }

}