package app.domain.invoice;

import java.math.BigDecimal;

/**
 * Created by marc on 16/01/16.
 */
public class InvoiceLineLow extends InvoiceLine {

    @Override
    public VatTariff getVatTariff() {
        return VatTariff.LOW;
    }

    @Override
    public BigDecimal getLineAmountExclVat() {
        return new BigDecimal("1.00");
    }

    @Override
    public BigDecimal getLineAmountInclVat() {
        return new BigDecimal("1.06");
    }

    @Override
    public String[] getDescription() {
        return new String[] { "Broodje Döner" };
    }
}
