package app.domain.invoice.internal.vatCalculationDelegate;

import app.domain.invoice.NoRegistrationInOriginCountryException;
import app.domain.invoice.OriginIsNotEuCountryException;
import app.domain.invoice.ProductCategoryNotSetException;
import app.domain.invoice.internal.InvoiceImpl;
import app.domain.invoice.internal.ProductCategory;
import app.domain.invoice.internal.VatCalculationRegime;
import app.domain.invoice.internal.countries.EuCountry;

import java.util.Arrays;
import java.util.Optional;

public class VatRegimeDeterminator {

    private final InvoiceImpl invoice;

    public VatRegimeDeterminator(InvoiceImpl invoice) {
        this.invoice = invoice;
    }

    VatCalculationRegime getVatCalculationRegime() {

        validateIfOriginCountryIsEuCountry();
        validateIfCompanyHasRegistrationInOriginCountry();

        if (isConsumerInvoice()) {
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

    private void validateIfOriginCountryIsEuCountry() {

        String originCountryOrDefault = VatCalculationDelegate.getOriginCountryOfDefault(invoice);

        if (!isEuCountry(originCountryOrDefault))
            throw new OriginIsNotEuCountryException(originCountryOrDefault);
    }

    private boolean isEuCountry(String originCountryIso) {
        return Arrays
                .stream(EuCountry.values())
                .anyMatch(euCountry -> euCountry.name().equals(originCountryIso));
    }

    private void validateIfCompanyHasRegistrationInOriginCountry() {
        if(!invoice.getCompany().getVatRegistrations().containsKey(VatCalculationDelegate.getOriginCountryOfDefault(invoice))) {
            throw new NoRegistrationInOriginCountryException(VatCalculationDelegate.getOriginCountryOfDefault(invoice));
        }
    }

    private void validateIfProductCategoryIsSet(Optional<ProductCategory> productCategory) {
        if (!productCategory.isPresent())
            throw new ProductCategoryNotSetException();
    }

    private boolean isConsumerInvoice() {
        return !invoice.getCustomer().getEuTaxId().isPresent();
    }

    private boolean isNationalTransaction(String originCountryIso, String destinationCountryIso) {
        return originCountryIso.equals(destinationCountryIso);
    }

    private boolean isIntraEuTransaction(String originCountryIso, String destinationCountryIso) {
        return isEuCountry(destinationCountryIso) && !originCountryIso.equals(destinationCountryIso);
    }

}
