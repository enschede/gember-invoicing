package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.internal.VatAmountSummary;
import app.domain.invoice.internal.VatPercentage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface VatCalculationDelegate {
    BigDecimal getInvoiceSubTotalInclVat();

    BigDecimal getInvoiceSubTotalExclVat();

    BigDecimal getTotalInvoiceAmountInclVat();

    BigDecimal getTotalInvoiceAmountExclVat();

    BigDecimal getInvoiceTotalVat();

    Map<VatPercentage, VatAmountSummary> getVatPerVatTariff();

    VatAmountSummary calculateVatAmountForVatTariff(VatPercentage vatPercentage, List<InvoiceLine> cachedInvoiceLinesForVatTariff);
}
