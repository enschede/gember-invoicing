package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.*;

public class VatCalculationForB2CCustomersDelegate extends VatCalculationIncludingVatDelegate {

    public VatCalculationForB2CCustomersDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
