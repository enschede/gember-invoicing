package app.domain.invoice;

import org.springframework.util.StringUtils;

public class InvoiceVatRegimeDelegate {
    private final InvoiceImpl invoiceImpl;

    protected boolean euIntraCummunityTransaction;
    protected boolean euIntraCummunityServices;

    public InvoiceVatRegimeDelegate(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    InternationalTaxRuleType getInternationalTaxRuleType() {

        if (invoiceImpl.getConsumerInvoice()) {
            return InternationalTaxRuleType.CONSUMER;
        }

        if (invoiceImpl.getCountryOfDestination() != null && !invoiceImpl.getCountryOfDestination().isEuCountry()) {
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
        IsoCountryCode countryOfOrigin = invoiceImpl.getCountryOfOrigin();
        IsoCountryCode countryOfDestination = invoiceImpl.getCountryOfDestination();

        return !invoiceImpl.getConsumerInvoice()
                && countryOfOrigin != null
                && countryOfDestination != null
                && countryOfDestination.isEuCountry()
                && !countryOfDestination.equals(countryOfOrigin)
                && !StringUtils.isEmpty(invoiceImpl.debtor.getEuTaxId());
    }

    // Shows the EU country where the VAT should be declared
    IsoCountryCode getVatDeclarationCountry() {
        IsoCountryCode countryOfDestination = invoiceImpl.getCountryOfDestination();

        return countryOfDestination.isEuCountry() ?
                countryOfDestination : invoiceImpl.getCountryOfOrigin();
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