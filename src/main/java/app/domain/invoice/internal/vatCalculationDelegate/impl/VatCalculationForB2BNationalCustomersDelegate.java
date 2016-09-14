package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.InvoiceImpl;

public class VatCalculationForB2BNationalCustomersDelegate extends VatCalculationIncludingVatDelegate {

    public VatCalculationForB2BNationalCustomersDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
