package app.domain.invoice.internal;

import app.domain.invoice.InvoiceLine;

import java.math.BigDecimal;

/**
 * Created by marc on 11/06/16.
 */
public interface LineVatCalculator {

    BigDecimal getLineAmountInclVat(InvoiceLine invoiceLine);

    public BigDecimal getLineAmountExclVat(InvoiceLine invoiceLine);

    public BigDecimal getVatAmount(InvoiceLine invoiceLine);

}
