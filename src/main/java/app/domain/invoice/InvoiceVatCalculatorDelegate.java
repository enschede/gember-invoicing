package app.domain.invoice;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceVatCalculatorDelegate {
    private final Invoice invoice;

    public InvoiceVatCalculatorDelegate(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getTotalAmountVat(IsoCountryCode countryOfDestination) {
        return getAmountSummariesGroupedByVatPercentage(countryOfDestination).values().stream()
                .map(VatAmountSummary::getAmountVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public Map<VatPercentage, VatAmountSummary> getAmountSummariesGroupedByVatPercentage(IsoCountryCode countryOfDestination) {
        if (invoice.intraCommunityTransactionDelegate.isIntraCommunityTransaction()) {
            return new HashMap<>();
        }

        Map<VatPercentage, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.invoiceLines.stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> invoice.configuration.vatRepository.findByTariffAndDate(
                                        invoice.intraCommunityTransactionDelegate.getVatCountry(),
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
            // This value is not calculated for a including VAT invoice, as is never used then
            BigDecimal totalSumExclVat = !invoice.consumerInvoice ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountExclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            // This value is not calculated for a excluding VAT invoice, as is never used then
            BigDecimal totalSumInclVat = invoice.consumerInvoice ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(InvoiceLine::getLineAmountInclVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            return vatPercentage.createVatAmountInfo(
                    invoice.consumerInvoice,
                    totalSumExclVat,
                    totalSumInclVat);
        } else {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(invoice.countryOfDestination, invoice.consumerInvoice))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return !invoice.configuration.isConfiguredForCalculateVatOnIndividualLines();
    }

}