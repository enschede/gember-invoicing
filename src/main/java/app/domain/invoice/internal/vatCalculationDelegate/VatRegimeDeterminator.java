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
import java.util.function.Predicate;

public class VatRegimeDeterminator {

    private final InvoiceImpl invoice;

    public VatRegimeDeterminator(InvoiceImpl invoice) {
        this.invoice = invoice;
    }

    VatCalculationRegime getVatCalculationRegime() {

        new InvoiceValidator().validate();

        return execute();
    }

    private class InvoiceValidator {
        public void validate() {
            validateIfOriginCountryIsEuCountry();
            validateIfCompanyHasRegistrationInOriginCountry();
        }

        private void validateIfOriginCountryIsEuCountry() {

            String actualOriginCountry = VatCalculationDelegate.getOriginCountryOfDefault(invoice);

            if (!isEuCountry(actualOriginCountry))
                throw new OriginIsNotEuCountryException(actualOriginCountry);
        }

        private void validateIfCompanyHasRegistrationInOriginCountry() {

            Optional<String> hasCompanyRegistrationInOriginCountry =
                    invoice.getCompany().getVatRegistrationInOrigin(VatCalculationDelegate.getOriginCountryOfDefault(invoice));

            if (!hasCompanyRegistrationInOriginCountry.isPresent()) {
                throw new NoRegistrationInOriginCountryException(VatCalculationDelegate.getOriginCountryOfDefault(invoice));
            }
        }
    }

    public VatCalculationRegime execute() {
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

    private boolean isConsumerInvoice() {
        return !invoice.getCustomer().getEuTaxId().isPresent();
    }

    private boolean isNationalTransaction(String originCountryIso, String destinationCountryIso) {
        return originCountryIso.equals(destinationCountryIso);
    }

    private boolean isIntraEuTransaction(String originCountryIso, String destinationCountryIso) {
        return isEuCountry(destinationCountryIso) && !originCountryIso.equals(destinationCountryIso);
    }

    private void validateIfProductCategoryIsSet(Optional<ProductCategory> productCategory) {
        if (!productCategory.isPresent())
            throw new ProductCategoryNotSetException();
    }

    private boolean isEuCountry(String originCountryIso) {
        Predicate<EuCountry> filterToMatchOriginCountry =
                euCountry -> euCountry.name().equals(originCountryIso);

        return Arrays
                .stream(EuCountry.values())
                .anyMatch(filterToMatchOriginCountry);
    }


}
