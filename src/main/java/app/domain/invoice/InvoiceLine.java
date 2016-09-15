package app.domain.invoice;

import app.domain.invoice.internal.*;
import app.domain.invoice.internal.vatTariffs.VatRepository;
import app.domain.invoice.internal.vatTariffs.VatTariff;

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
        return getInvoiceLineVatType() == InvoiceLineVatType.EXCLUDING_VAT ? getLineAmount() : BigDecimal.ZERO;
    }

    public BigDecimal getLineAmountInclVat() {
        invoiceImpl.getInternationalTaxRuleType();

        return getInvoiceLineVatType() == InvoiceLineVatType.INCLUDING_VAT ? getLineAmount() : BigDecimal.ZERO;
    }

    public VatAmountSummary getVatAmount(String originCountry, String destinationCountry, Boolean consumerInvoice) {
        return vatRepository
                .findByTariffAndDate(
                        invoiceImpl.getCalculationDelegate().getVatDeclarationCountryIso(originCountry, destinationCountry),
                        getVatTariff(),
                        getVatReferenceDate())
                .createVatAmountInfo(
                        consumerInvoice,
                        getLineAmountExclVat(),
                        getLineAmountInclVat());
    }
}
