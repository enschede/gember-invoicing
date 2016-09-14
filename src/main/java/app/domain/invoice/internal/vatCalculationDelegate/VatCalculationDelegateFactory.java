package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.NoRegistrationInOriginCountryException;
import app.domain.invoice.OriginIsNotEuCountryException;
import app.domain.invoice.ProductCategoryNotSetException;
import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.ProductCategory;
import app.domain.invoice.internal.VatCalculationRegime;
import app.domain.invoice.internal.countries.EuCountry;
import app.domain.invoice.internal.vatCalculationDelegate.impl.*;

import java.util.Arrays;
import java.util.Optional;

public class VatCalculationDelegateFactory {

    public static VatCalculationDelegate newVatCalculationDelegate(InvoiceImpl invoice) {

        switch (new VatRegimeDeterminator(invoice).getVatCalculationRegime()) {
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
