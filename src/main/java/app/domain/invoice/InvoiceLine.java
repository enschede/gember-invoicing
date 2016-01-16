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

    public BigDecimal getLineAmountVat(Boolean includingVatInvoice) {
        return getVatTariff().calculateVatAmount(includingVatInvoice, getLineAmountExclVat(), getLineAmountInclVat());
    }
}
