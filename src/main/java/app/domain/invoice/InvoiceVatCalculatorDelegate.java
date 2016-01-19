package app.domain.invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvoiceVatCalculatorDelegate {
    private final Invoice invoice;

    public InvoiceVatCalculatorDelegate(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getInvoiceTotalVat() {
        return getVatPerVatPercentage().values().stream()
                .map(vatAmount -> vatAmount.getAmountVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<VatPercentage, VatAmountSummary> getVatPerVatPercentage() {

        Map<Optional<VatPercentage>, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.getInvoiceLines().stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> invoice.configuration.vatRepository.findByTariffAndDate(
                                        invoiceLine.getVatTariff(),
                                        invoiceLine.getVatReferenceDate())));

        Map<VatPercentage, VatAmountSummary> mapOfVatAmountSummariesPerVatPercentage = mapOfInvoiceLinesPerVatPercentage.entrySet().stream()
                .collect(Collectors.toMap(
                        optionalListEntry -> optionalListEntry.getKey().get(),
                        optionalListEntry1 -> calculateVatAmountForVatTariff(
                                optionalListEntry1.getKey().get(),
                                optionalListEntry1.getValue())));

        return mapOfVatAmountSummariesPerVatPercentage;
    }

    private VatAmountSummary calculateVatAmountForVatTariff(VatPercentage vatPercentage, List<InvoiceLine> cachedInvoiceLinesForVatTariff) {

        if (calculateVatOnSummaryBase()) {
            // This value is not calculated for a including VAT invoice, as is never used then
            BigDecimal totalSumExclVat = !invoice.getIncludingVatInvoice() ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(invoiceLine -> invoiceLine.getLineAmountExclVat())
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            // This value is not calculated for a excluding VAT invoice, as is never used then
            BigDecimal totalSumInclVat = invoice.getIncludingVatInvoice() ?
                    cachedInvoiceLinesForVatTariff.stream()
                            .map(invoiceLine -> invoiceLine.getLineAmountInclVat())
                            .reduce(BigDecimal.ZERO, BigDecimal::add) :
                    BigDecimal.ZERO;

            return vatPercentage.createVatAmountInfo(
                    invoice.getIncludingVatInvoice(),
                    totalSumExclVat,
                    totalSumInclVat);
        } else {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(invoice.getIncludingVatInvoice()))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return !invoice.configuration.isConfiguredForCalculateVatOnIndividualLines();
    }

}