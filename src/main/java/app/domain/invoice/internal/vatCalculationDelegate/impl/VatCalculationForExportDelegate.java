package app.domain.invoice.internal.vatCalculationDelegate.impl;

import app.domain.invoice.internal.InvoiceImpl;

public class VatCalculationForExportDelegate extends VatCalculationIncludingVatDelegate {

    public VatCalculationForExportDelegate(InvoiceImpl invoice) {
        super(invoice);
    }
}
