package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.InvoiceLine;
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

    public abstract VatAmountSummary calculateVatAmountForVatTariff(VatPercentage vatPercentage, List<InvoiceLine> cachedInvoiceLinesForVatTariff);

    public abstract Boolean isVatShiftedInvoice();
}
