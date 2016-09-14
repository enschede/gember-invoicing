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

        switch (getVatCalculationRegime(invoice)) {
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

    private static VatCalculationRegime getVatCalculationRegime(InvoiceImpl invoice) {

        validateIfOriginCountryIsEuCountry(invoice);
        validateIfCompanyHasRegistrationInOriginCountry(invoice);

        if (isConsumerInvoice(invoice)) {
            if (invoice.getCompany().getVatRegistrations().containsKey(VatCalculationDelegate.getOriginCountryOfDefault(invoice)))
                return VatCalculationRegime.B2C;

        }

        if (isNationalTransaction(VatCalculationDelegate.getOriginCountryOfDefault(invoice), VatCalculationDelegate.getDestinationCountryOfDefault(invoice))) {
            return invoice.getVatShifted() ?
                    VatCalculationRegime.B2B_NATIONAL_SHIFTED_VAT : VatCalculationRegime.B2B_NATIONAL;
        }

        if (isIntraEuTransaction(VatCalculationDelegate.getOriginCountryOfDefault(invoice), VatCalculationDelegate.getDestinationCountryOfDefault(invoice))) {
            validateIfProductCategoryIsSet(invoice.getProductCategory());

            switch (invoice.getProductCategory().get()) {
                case Goods:
                    return VatCalculationRegime.B2B_EU_GOODS;
                case Services:
                    return VatCalculationRegime.B2B_EU_SERVICES;
                case EServices:
                    return VatCalculationRegime.B2B_EU_E_SERVICES;
            }
        }

        return VatCalculationRegime.EXPORT;
    }

    private static void validateIfOriginCountryIsEuCountry(InvoiceImpl invoiceImpl) {

        String originCountryOrDefault = VatCalculationDelegate.getOriginCountryOfDefault(invoiceImpl);

        if (!isEuCountry(originCountryOrDefault))
            throw new OriginIsNotEuCountryException(originCountryOrDefault);
    }

    private static boolean isEuCountry(String originCountryIso) {
        return Arrays
                .stream(EuCountry.values())
                .anyMatch(euCountry -> euCountry.name().equals(originCountryIso));
    }

    private static void validateIfCompanyHasRegistrationInOriginCountry(InvoiceImpl invoiceImpl) {
        if(!invoiceImpl.getCompany().getVatRegistrations().containsKey(VatCalculationDelegate.getOriginCountryOfDefault(invoiceImpl))) {
            throw new NoRegistrationInOriginCountryException(VatCalculationDelegate.getOriginCountryOfDefault(invoiceImpl));
        }
    }

    private static void validateIfProductCategoryIsSet(Optional<ProductCategory> productCategory) {
        if (!productCategory.isPresent())
            throw new ProductCategoryNotSetException();
    }

    private static boolean isConsumerInvoice(InvoiceImpl invoice) {
        return !invoice.getCustomer().getEuTaxId().isPresent();
    }

    private static boolean isNationalTransaction(String originCountryIso, String destinationCountryIso) {
        return originCountryIso.equals(destinationCountryIso);
    }

    private static boolean isIntraEuTransaction(String originCountryIso, String destinationCountryIso) {
        return isEuCountry(destinationCountryIso) && !originCountryIso.equals(destinationCountryIso);
    }


}
