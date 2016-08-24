package app.domain.invoice.internal;

import java.util.Arrays;
import java.util.Optional;

public class InvoiceVatRegimeDelegate {
    private final InvoiceImpl invoiceImpl;

    public InvoiceVatRegimeDelegate(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    VatCalculationRegime getInternationalTaxRuleType(
            final String originCountryIso, final String destinationCountryIso) {

        if (isConsumerInvoice()) {
            return VatCalculationRegime.CONSUMER;
        }

        if (isNationalTransaction(originCountryIso, destinationCountryIso)) {
            return invoiceImpl.vatShifted ?
                    VatCalculationRegime.B2B_NATIONAL_SHIFTED_VAT : VatCalculationRegime.B2B_NATIONAL;
        }

        if (isIntraEuTransaction(originCountryIso, destinationCountryIso)) {
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

    private void validateIfProductCategoryIsSet(Optional<ProductCategory> productCategory) {
        if(!productCategory.isPresent())
            throw new ProductCategoryNotSetException();
    }

    private void validateIfOriginCountryIsEuCountry(String originCountryIso)
            throws OriginIsNotEuCountryException {
        if( !isEuCountry(originCountryIso) )
            throw new OriginIsNotEuCountryException(originCountryIso);
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


    // Shows the EU country where the VAT should be declared
    public String getVatDeclarationCountryIso(final String originCountryIso, final String destinationCountryIso) {

        VatCalculationRegime vatCalculationRegime = getInternationalTaxRuleType(originCountryIso, destinationCountryIso);

        if(vatCalculationRegime.equals(VatCalculationRegime.CONSUMER) && invoiceImpl.getCompany().getVatRegistrations().containsKey(destinationCountryIso)) {
            return destinationCountryIso;
        }

        return originCountryIso;
    }

}