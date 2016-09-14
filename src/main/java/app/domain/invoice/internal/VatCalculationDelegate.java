package app.domain.invoice.internal;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceType;
import app.domain.invoice.VatCalculationPolicy;
import app.domain.invoice.internal.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VatCalculationDelegate {

    private final InvoiceImpl invoice;

    public VatCalculationDelegate(InvoiceImpl invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getInvoiceSubTotalInclVat() {
        final VatRepository vatRepository = new VatRepository();
        final String declarationCountryIso =
                invoice.invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                        invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                        invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, declarationCountryIso);

        if (invoice.invoiceVatRegimeDelegate.getCalculationMethod() == CalculationMethod.INCLUDING_VAT) {
            BigDecimal totalAmountInclVat = invoice.getInvoiceLines().stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountInclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountInclVat;
        } else {
            return null;
        }
    }

    public BigDecimal getInvoiceSubTotalExclVat() {

        final VatRepository vatRepository = new VatRepository();
        final String destinationCountry =
                invoice.invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                        invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                        invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault());

        LineVatCalculator lineVatCalculator = new LineVatCalculatorImpl(vatRepository, destinationCountry);

        CalculationMethod calculationMethod = invoice.invoiceVatRegimeDelegate.getCalculationMethod();

        if (calculationMethod == CalculationMethod.EXCLUDING_VAT ||
                calculationMethod == CalculationMethod.SHIFTED_VAT ||
                calculationMethod == CalculationMethod.INTRA_CUMM_B2B) {

            BigDecimal totalAmountExclVat = invoice.getInvoiceLines().stream()
                    .map(invoiceLine -> lineVatCalculator.getLineAmountExclVat(invoiceLine))
                    .reduce(new BigDecimal("0.00"), BigDecimal::add);

            return totalAmountExclVat;
        } else {
            return null;
        }
    }

    public BigDecimal getTotalInvoiceAmountInclVat() {

        switch (invoice.invoiceVatRegimeDelegate.getCalculationMethod()) {

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

    public BigDecimal getTotalInvoiceAmountExclVat() {

        switch (invoice.invoiceVatRegimeDelegate.getCalculationMethod()) {

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

    public BigDecimal getInvoiceTotalVat() {

        Map<VatPercentage, VatAmountSummary> vatPerVatTariff = getVatPerVatTariff();

        return vatPerVatTariff.values().stream()
                .map(VatAmountSummary::getAmountVat)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public Map<VatPercentage, VatAmountSummary> getVatPerVatTariff() {

        final VatCalculationRegime vatCalculationRegime =
                invoice.invoiceVatRegimeDelegate.getVatCalculationRegime();

        final VatRepository vatRepository = new VatRepository();

        switch (vatCalculationRegime) {
            case CONSUMER:
            case B2B_NATIONAL:
            case EXPORT:
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

            case B2B_NATIONAL_SHIFTED_VAT:
            case B2B_EU_SERVICES:
            case B2B_EU_E_SERVICES:
                return new HashMap<>();

            case B2B_EU_GOODS:
                Map<VatPercentage, VatAmountSummary> vatPercentageVatAmountSummaryMap2 = new HashMap<>();

                VatPercentage vatPercentage = vatRepository.findByTariffAndDate(
                        invoice.invoiceVatRegimeDelegate.getVatDeclarationCountryIso(
                                invoice.invoiceVatRegimeDelegate.getOriginCountryOfDefault(),
                                invoice.invoiceVatRegimeDelegate.getDestinationCountryOfDefault()),
                        VatTariff.ZERO, LocalDate.now());

                VatAmountSummary vatAmount = new VatAmountSummary(vatPercentage, new BigDecimal("0.00"), getInvoiceSubTotalExclVat(), getTotalInvoiceAmountExclVat());

                vatPercentageVatAmountSummaryMap2.put(vatPercentage, vatAmount);

                return vatPercentageVatAmountSummaryMap2;

            default:
                return null;
        }

    }

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
