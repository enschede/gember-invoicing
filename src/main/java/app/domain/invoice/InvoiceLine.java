package app.domain.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class InvoiceLine {

    InvoiceImpl invoiceImpl;

    public InvoiceImpl getInvoiceImpl() {
        return invoiceImpl;
    }

    public void setInvoiceImpl(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    public abstract VatTariff getVatTariff();

    public abstract LocalDate getVatReferenceDate();

    public abstract BigDecimal getLineAmountExclVat();

    public abstract BigDecimal getLineAmountInclVat();

    public abstract String[] getDescription();

    public VatAmountSummary getVatAmount(IsoCountryCode destinationCountry, Boolean consumerInvoice) {
        return invoiceImpl.configuration
                .getVatRepository()
                .findByTariffAndDate(
                        invoiceImpl.invoiceVatRegimeDelegate.getVatDeclarationCountry(),
                        getVatTariff(),
                        getVatReferenceDate())
                .createVatAmountInfo(
                        consumerInvoice,
                        getLineAmountExclVat(),
                        getLineAmountInclVat());
    }
}
