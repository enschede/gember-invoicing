package app.domain.invoice;

import org.springframework.util.StringUtils;

public class InvoiceVatRegimeDelegate {
    private final InvoiceImpl invoiceImpl;

    protected boolean consumerInvoice;
    protected boolean vatShifted;
    protected boolean euIntraCummunityTransaction;
    protected boolean euIntraCummunityServices;

    public InvoiceVatRegimeDelegate(InvoiceImpl invoiceImpl) {
        this.invoiceImpl = invoiceImpl;
    }

    InternationalTaxRuleType getInternationalTaxRuleType() {

        if (consumerInvoice) {
            return InternationalTaxRuleType.CONSUMER;
        }

        if (invoiceImpl.getCountryOfDestination() != null && !invoiceImpl.getCountryOfDestination().isEuCountry()) {
            return InternationalTaxRuleType.EXPORT;
        }

        if (euIntraCummunityTransaction && !euIntraCummunityServices && compliesToEuropeanIntraCummunityTransactions()) {
            return InternationalTaxRuleType.B2B_INTRA_COMMUNITY_GOODS;
        }

        if (euIntraCummunityServices && !euIntraCummunityTransaction && compliesToEuropeanIntraCummunityTransactions()) {
            return InternationalTaxRuleType.B2B_INTRA_COMMUNITY_SHIFTED_VAT;
        }

        if (vatShifted) {
            return InternationalTaxRuleType.B2B_SHIFTED_VAT;
        }

        return InternationalTaxRuleType.B2B_NATIONAL;
    }

    private boolean compliesToEuropeanIntraCummunityTransactions() {
        IsoCountryCode countryOfOrigin = invoiceImpl.getCountryOfOrigin();
        IsoCountryCode countryOfDestination = invoiceImpl.getCountryOfDestination();

        return !consumerInvoice
                && countryOfOrigin != null
                && countryOfDestination != null
                && countryOfDestination.isEuCountry()
                && !countryOfDestination.equals(countryOfOrigin)
                && !StringUtils.isEmpty(invoiceImpl.debtor.getEuTaxId());
    }

    IsoCountryCode getVatOriginCountry() {
        IsoCountryCode countryOfDestination = invoiceImpl.getCountryOfDestination();

        return countryOfDestination.isEuCountry() ?
                countryOfDestination : invoiceImpl.getCountryOfOrigin();
    }

    public enum InternationalTaxRuleType {
        CONSUMER,
        B2B_NATIONAL,
        B2B_INTRA_COMMUNITY_GOODS,
        B2B_INTRA_COMMUNITY_SHIFTED_VAT,
        B2B_SHIFTED_VAT,
        EXPORT
    }
}