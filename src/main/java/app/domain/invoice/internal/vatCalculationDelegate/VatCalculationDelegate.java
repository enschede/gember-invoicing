package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.InvoiceType;
import app.domain.invoice.VatCalculationPolicy;
import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.VatAmountSummary;
import app.domain.invoice.internal.VatPercentage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class VatCalculationDelegate {

    public final InvoiceImpl invoice;

    protected VatCalculationDelegate(InvoiceImpl invoice) {
        this.invoice = invoice;
    }

    public abstract BigDecimal getInvoiceSubTotalInclVat();

    public abstract BigDecimal getInvoiceSubTotalExclVat();

    public abstract BigDecimal getTotalInvoiceAmountInclVat();

    public abstract BigDecimal getTotalInvoiceAmountExclVat();

    public abstract BigDecimal getInvoiceTotalVat();

    public abstract Map<VatPercentage, VatAmountSummary> getVatPerVatTariff();

    public abstract Boolean isVatShiftedInvoice();

    public String getVatDeclarationCountryIso(
            final String originCountryIso, final String destinationCountryIso) {

        return originCountryIso;
    }

    public static String getOriginCountryOfDefault(InvoiceImpl invoice) {
        return invoice.countryOfOrigin.orElse(invoice.company.getPrimaryCountryIso());
    }

    public static String getDestinationCountryOfDefault(InvoiceImpl invoice) {
        return invoice.countryOfDestination.orElse(invoice.company.getPrimaryCountryIso());
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
                            getOriginCountryOfDefault(invoice),
                            getDestinationCountryOfDefault(invoice),
                            invoice.getInvoiceType() == InvoiceType.CONSUMER))
                    .reduce(VatAmountSummary.zero(vatPercentage), VatAmountSummary::add);

        }
    }

    private boolean calculateVatOnSummaryBase() {
        return invoice.getCompany().getVatCalculationPolicy() == VatCalculationPolicy.VAT_CALCULATION_ON_TOTAL;
    }


}