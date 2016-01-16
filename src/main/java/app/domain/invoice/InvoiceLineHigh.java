package app.domain.invoice;

import java.math.BigDecimal;

/**
 * Created by marc on 16/01/16.
 */
public class InvoiceLineHigh extends InvoiceLine {

    @Override
    public VatTariff getVatTariff() {
        return VatTariff.HIGH;
    }

    @Override
    public BigDecimal getLineAmountExclVat() {
        return new BigDecimal("1.00");
    }

    @Override
    public BigDecimal getLineAmountInclVat() {
        return new BigDecimal("1.20");
    }

    @Override
    public String[] getDescription() {
        return new String[] { "Speaker system" };
    }
}
