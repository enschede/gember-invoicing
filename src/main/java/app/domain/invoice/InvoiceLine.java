package app.domain.invoice;

import java.math.BigDecimal;

/**
 * Created by marc on 15/01/16.
 */
public abstract class InvoiceLine {

    public abstract VatTariff getVatTariff();

    public abstract BigDecimal getLineAmountExclVat();

    public abstract BigDecimal getLineAmountInclVat();

    public abstract String[] getDescription();

    public VatAmountSummary getVatAmount(Boolean includingVatInvoice) {
        return getVatTariff().createVatAmountInfo(
                includingVatInvoice,
                getLineAmountExclVat(),
                getLineAmountInclVat());
    }
}
