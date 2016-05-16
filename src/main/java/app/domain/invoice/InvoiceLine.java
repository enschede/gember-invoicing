package app.domain.invoice;

import app.domain.invoice.internal.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class InvoiceLine {

    VatRepository vatRepository = new VatRepository();

    InvoiceImpl invoiceImpl;

    public abstract BigDecimal getLineAmount();

    public abstract InvoiceLineVatType getInvoiceLineVatType();

    public abstract LocalDate getVatReferenceDate();

    public abstract VatTariff getVatTariff();

    public InvoiceImpl getInvoiceImpl() {
        return invoiceImpl;
    }

    public void setInvoiceImpl(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    public BigDecimal getLineAmountExclVat() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getLineAmountInclVat() {
        return BigDecimal.ZERO;
    }

    public VatAmountSummary getVatAmount(IsoCountryCode destinationCountry, Boolean consumerInvoice) {
        return vatRepository
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
