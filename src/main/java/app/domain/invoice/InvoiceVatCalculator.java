package app.domain.invoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoiceVatCalculator {
    private final Invoice invoice;

    public InvoiceVatCalculator(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getInvoiceTotalVat() {
        return getVatPerVatTariff().values().stream()
                .map(vatAmount -> vatAmount.getAmountVat()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<VatTariff, VatAmountSummary> getVatPerVatTariff() {
        return Arrays.asList(VatTariff.values()).stream()
                .filter(vatTariff -> isNumberOfInvoiceLinesForVatTariffGreaterThanZero(vatTariff))
                .collect(Collectors.toMap(
                        vatTariff -> vatTariff,
                        vatTariff1 -> calculateVatAmountForVatTariff(vatTariff1)
                ));
    }

    private boolean isNumberOfInvoiceLinesForVatTariffGreaterThanZero(VatTariff vatTariff) {
        return invoice.getInvoiceLines().stream()
                .filter(invoiceLine -> invoiceLine.getVatTariff() == vatTariff)
                .count() > 0;
    }

    private VatAmountSummary calculateVatAmountForVatTariff(VatTariff vatTariff) {
        List<InvoiceLine> cachedInvoiceLinesForVatTariff = createInvoiceLinesForVatTariff(vatTariff);

        if (calculateVatOnIndividualLines()) {
            return cachedInvoiceLinesForVatTariff.stream()
                    .map(invoiceLine -> invoiceLine.getVatAmount(invoice.getIncludingVatInvoice()))
                    .reduce(VatAmountSummary.zero(vatTariff), VatAmountSummary::add);

        } else {
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

            return vatTariff.createVatAmountInfo(
                    invoice.getIncludingVatInvoice(),
                    totalSumExclVat,
                    totalSumInclVat);
        }

    }

    private boolean calculateVatOnIndividualLines() {
        return invoice.configuration.isConfiguredForCalculateVatOnIndividualLines();
    }

    private List<InvoiceLine> createInvoiceLinesForVatTariff(VatTariff vatTariff) {
        return invoice.getInvoiceLines().stream()
                .filter(invoiceLine -> invoiceLine.getVatTariff() == vatTariff)
                .collect(Collectors.toList());


    }

}