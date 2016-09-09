package app.domain.invoice.internal;

import app.domain.invoice.InvoiceLine;

import java.math.BigDecimal;

public interface LineVatCalculator {

    BigDecimal getLineAmountInclVat(InvoiceLine invoiceLine);

    public BigDecimal getLineAmountExclVat(InvoiceLine invoiceLine);

    public BigDecimal getVatAmount(InvoiceLine invoiceLine);

}
