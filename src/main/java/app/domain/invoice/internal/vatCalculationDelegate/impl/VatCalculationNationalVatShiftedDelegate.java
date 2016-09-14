package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.InvoiceImpl;

public class VatCalculationNationalVatShiftedDelegate extends VatCalculationVatShiftedDelegateImpl {

    public VatCalculationNationalVatShiftedDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
