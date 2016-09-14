package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.internal.InvoiceImpl;

/**
 * Created by marc on 14/09/16.
 */
public class VatCalculationDelegateFactory {

    public static VatCalculationDelegate newVatCalculationDelegate(InvoiceImpl invoice) {
        return new VatCalculationDelegateImpl(invoice);
    }

}
