package app.domain.invoice.internal;

import app.domain.invoice.InvoiceCalculationException;
import app.domain.invoice.InvoiceType;

import java.util.Optional;

public class InvoiceVatRegimeDelegate {
    private final InvoiceImpl invoiceImpl;

    protected boolean euIntraCummunityTransaction;
    protected boolean euIntraCummunityServices;

    public InvoiceVatRegimeDelegate(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    InternationalTaxRuleType getInternationalTaxRuleType() {

        if (invoiceImpl.getInvoiceType() == InvoiceType.CONSUMER) {
            return InternationalTaxRuleType.CONSUMER;
        }

        if (invoiceImpl.getProductDestinationCountry().isPresent()
                && !invoiceImpl.getProductDestinationCountry().get().isEuCountry()) {
            return InternationalTaxRuleType.EXPORT;
        }

        if (euIntraCummunityTransaction && !euIntraCummunityServices && isCompliantForEuropeanIntraCummunityTransactions()) {
            return InternationalTaxRuleType.B2B_EU_INTRA_COMMUNITY_GOODS;
        }

        if (euIntraCummunityServices && !euIntraCummunityTransaction && isCompliantForEuropeanIntraCummunityTransactions()) {
            return InternationalTaxRuleType.B2B_EU_INTRA_COMMUNITY_SHIFTED_VAT;
        }

        if (invoiceImpl.isVatShifted()) {
            return InternationalTaxRuleType.B2B_NATIONAL_SHIFTED_VAT;
        }

        return InternationalTaxRuleType.B2B_NATIONAL;
    }

    private boolean isCompliantForEuropeanIntraCummunityTransactions() {
        Optional<IsoCountryCode> countryOfOrigin = invoiceImpl.getProductOriginCountry();
        Optional<IsoCountryCode> countryOfDestination = invoiceImpl.getProductDestinationCountry();

        return !(invoiceImpl.getInvoiceType() == InvoiceType.CONSUMER)
                && countryOfOrigin.isPresent()
                && countryOfDestination.isPresent()
                && countryOfDestination.get().isEuCountry()
                && !countryOfDestination.get().equals(countryOfOrigin.get())
//                && !StringUtils.isEmpty(invoiceImpl.debtor.getEuTaxId())
                ;
    }

    // Shows the EU country where the VAT should be declared
    public IsoCountryCode getVatDeclarationCountry() {
        IsoCountryCode countryOfDestination = invoiceImpl.getProductDestinationCountry().get();

        if(countryOfDestination.isEuCountry() && invoiceImpl.getCompany().hasVatRegistrationFor(countryOfDestination))
            return countryOfDestination;

        if(invoiceImpl.getProductOriginCountry().isPresent() && invoiceImpl.getCompany().hasVatRegistrationFor(invoiceImpl.getProductOriginCountry().get()))
            return invoiceImpl.getProductOriginCountry().get();

        throw new InvoiceCalculationException();
    }

    public enum InternationalTaxRuleType {
        CONSUMER,
        B2B_NATIONAL,
        B2B_NATIONAL_SHIFTED_VAT,
        B2B_EU_INTRA_COMMUNITY_GOODS,
        B2B_EU_INTRA_COMMUNITY_SHIFTED_VAT,
        EXPORT
    }
}