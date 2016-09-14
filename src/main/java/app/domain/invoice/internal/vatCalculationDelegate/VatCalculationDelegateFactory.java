package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.vatCalculationDelegate.impl.*;

public class VatCalculationDelegateFactory {

    public static VatCalculationDelegate newVatCalculationDelegate(InvoiceImpl invoice) {

        switch (invoice.invoiceVatRegimeDelegate.getVatCalculationRegime()) {
            case B2C:
                return new VatCalculationForB2CCustomersDelegate(invoice);

            case B2B_NATIONAL:
                return new VatCalculationForB2BNationalCustomersDelegate(invoice);

            case EXPORT:
                return new VatCalculationForExportDelegate(invoice);

            case B2B_EU_GOODS:
                return new VatCalculationB2BEuGoodsDelegate(invoice);

            case B2B_NATIONAL_SHIFTED_VAT:
                return new VatCalculationNationalVatShiftedDelegate(invoice);

            case B2B_EU_SERVICES:
                return new VatCalculationB2BEuServicesDelegate(invoice);

            case B2B_EU_E_SERVICES:
                return new VatCalculationB2BEuEServicesDelegate(invoice);

            default:
                return null;
        }
    }

}
