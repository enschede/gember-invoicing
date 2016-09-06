package app.domain.invoice.internal;

import app.domain.invoice.InvoiceType;
import app.domain.invoice.NoRegistrationInDestinationCountryException;
import app.domain.invoice.OriginIsNotEuCountryException;
import app.domain.invoice.ProductCategoryNotSetException;
import app.domain.invoice.internal.countries.EuCountry;

import java.util.Arrays;
import java.util.Optional;

public class InvoiceVatRegimeDelegate {
    private final InvoiceImpl invoiceImpl;

    public InvoiceVatRegimeDelegate(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    VatCalculationRegime getVatCalculationRegime() {

        validateIfOriginCountryIsEuCountry();
        validateIfCompanyHasRegistrationInOriginCountry();

        if (isConsumerInvoice()) {
            if (invoiceImpl.getCompany().getVatRegistrations().containsKey(getOriginCountryOfDefault()))
                return VatCalculationRegime.CONSUMER;

            throw new NoRegistrationInDestinationCountryException();
        }

        if (isNationalTransaction(getOriginCountryOfDefault(), getDestinationCountryOfDefault())) {
            return invoiceImpl.vatShifted ?
                    VatCalculationRegime.B2B_NATIONAL_SHIFTED_VAT : VatCalculationRegime.B2B_NATIONAL;
        }

        if (isIntraEuTransaction(getOriginCountryOfDefault(), getDestinationCountryOfDefault())) {
            validateIfProductCategoryIsSet(invoiceImpl.productCategory);

            switch (invoiceImpl.productCategory.get()) {
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

    private void validateIfCompanyHasRegistrationInOriginCountry() {
        if(!invoiceImpl.getCompany().getVatRegistrations().containsKey(getOriginCountryOfDefault())) {
            throw new NoRegistrationInOriginCountryException(getOriginCountryOfDefault());
        }
    }

    // Shows the EU country where the VAT should be declared
    public String getVatDeclarationCountryIso(final String originCountryIso, final String destinationCountryIso) {

        VatCalculationRegime vatCalculationRegime =
                getVatCalculationRegime();

        if (vatCalculationRegime.equals(VatCalculationRegime.CONSUMER) && invoiceImpl.getCompany().getVatRegistrations().containsKey(destinationCountryIso)) {
            return destinationCountryIso;
        }

        return originCountryIso;
    }

    CalculationMethod getCalculationMethod() {

        VatCalculationRegime vatCalculationRegime = getVatCalculationRegime();

        switch (vatCalculationRegime) {
            case CONSUMER:
            case B2B_NATIONAL:
            case EXPORT:
                return invoiceImpl.invoiceType == InvoiceType.CONSUMER ?
                        CalculationMethod.INCLUDING_VAT : CalculationMethod.EXCLUDING_VAT;

            case B2B_NATIONAL_SHIFTED_VAT:
            case B2B_EU_SERVICES:
            case B2B_EU_E_SERVICES:
                return CalculationMethod.SHIFTED_VAT;

            case B2B_EU_GOODS:
                return CalculationMethod.INTRA_CUMM_B2B;

            default:
                return CalculationMethod.INCLUDING_VAT;
        }
    }

    String getOriginCountryOfDefault() {
        return invoiceImpl.countryOfOrigin.orElse(invoiceImpl.company.getPrimaryCountryIso());
    }

    String getDestinationCountryOfDefault() {
        return invoiceImpl.countryOfDestination.orElse(invoiceImpl.company.getPrimaryCountryIso());
    }


    private void validateIfProductCategoryIsSet(Optional<ProductCategory> productCategory) {
        if (!productCategory.isPresent())
            throw new ProductCategoryNotSetException();
    }

    private void validateIfOriginCountryIsEuCountry() {

        if (!isEuCountry(getOriginCountryOfDefault()))
            throw new OriginIsNotEuCountryException(getOriginCountryOfDefault());
    }

    private boolean isEuCountry(String originCountryIso) {
        return Arrays
                .stream(EuCountry.values())
                .anyMatch(euCountry -> euCountry.name().equals(originCountryIso));
    }

    private boolean isNationalTransaction(String originCountryIso, String destinationCountryIso) {
        return originCountryIso.equals(destinationCountryIso);
    }

    private boolean isIntraEuTransaction(String originCountryIso, String destinationCountryIso) {
        return isEuCountry(destinationCountryIso) && !originCountryIso.equals(destinationCountryIso);
    }

    private boolean isConsumerInvoice() {
        return !invoiceImpl.getCustomer().getEuTaxId().isPresent();
    }


}