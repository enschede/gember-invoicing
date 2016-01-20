package app.domain.invoice;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceVatCalculatorDelegate {
    private final Invoice invoice;

    public InvoiceVatCalculatorDelegate(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getInvoiceTotalVat() {
        return getAmountSummariesGroupedByVatPercentage().values().stream()
                .map(VatAmountSummary::getAmountVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public Map<VatPercentage, VatAmountSummary> getAmountSummariesGroupedByVatPercentage() {

        if (invoice.intraCommunityTransactionDelegate.isIntraCommunityTransaction()) {
            return new HashMap<>();
        }

        Map<Optional<VatPercentage>, List<InvoiceLine>> mapOfInvoiceLinesPerVatPercentage =
                invoice.invoiceLines.stream()
                        .collect(Collectors.groupingBy(
                                invoiceLine -> invoice.configuration.vatRepository.findByTariffAndDate(
                                        invoiceLine.getVatTariff(),
                                        invoiceLine.getVatReferenceDate())));

        Set<IsoCountryCode> listOfUniqueIsoCountryCodes =
                mapOfInvoiceLinesPerVatPercentage.keySet().stream()
                        .map(vatPercentage -> vatPercentage.get().isoCountryCode)
                        .collect(Collectors.toSet());

        // Moet deze controle wel? Destination country is een attribuut van de invoice
        if (listOfUniqueIsoCountryCodes.size() > 1)
            throw new CannotHaveMoreThanOneDestinationCountryOnOneInvoiceException();

        return mapOfInvoiceLinesPerVatPercentage.entrySet().stream()
                        .collect(Collectors.toMap(
                                optionalListEntry -> optionalListEntry.getKey().get(),
                                optionalListEntry -> calculateVatAmountForVatTariff(
                                        optionalListEntry.getKey().get(),
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
                    .map(invoiceLine -> invoiceLine.getVatAmount(invoice.consumerInvoice))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return !invoice.configuration.isConfiguredForCalculateVatOnIndividualLines();
    }

}