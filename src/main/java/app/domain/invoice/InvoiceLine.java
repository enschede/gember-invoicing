package app.domain.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class InvoiceLine {

    Invoice invoice;

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public abstract VatTariff getVatTariff();

    public abstract LocalDate getVatReferenceDate();

    public abstract BigDecimal getLineAmountExclVat();

    public abstract BigDecimal getLineAmountInclVat();

    public abstract String[] getDescription();

    public VatAmountSummary getVatAmount(IsoCountryCode destinationCountry, Boolean consumerInvoice) {
        return invoice.configuration
                .getVatRepository()
                .findByTariffAndDate(
                        invoice.intraCommunityTransactionDelegate.getVatCountry(),
                        getVatTariff(),
                        getVatReferenceDate())
                .createVatAmountInfo(
                        consumerInvoice,
                        getLineAmountExclVat(),
                        getLineAmountInclVat());
    }
}
