package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.InvoiceImpl;

public class VatCalculationB2BEuServicesDelegate extends VatCalculationVatShiftedDelegateImpl {

    public VatCalculationB2BEuServicesDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
