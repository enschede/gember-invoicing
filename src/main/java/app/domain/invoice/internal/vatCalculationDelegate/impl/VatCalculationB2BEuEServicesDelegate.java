package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.InvoiceImpl;

public class VatCalculationB2BEuEServicesDelegate extends VatCalculationVatShiftedDelegateImpl {

    public VatCalculationB2BEuEServicesDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
