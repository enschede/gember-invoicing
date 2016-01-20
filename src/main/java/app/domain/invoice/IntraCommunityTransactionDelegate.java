package app.domain.invoice;

import org.springframework.util.StringUtils;

public class IntraCommunityTransactionDelegate {
    private final Invoice invoice;

    public IntraCommunityTransactionDelegate(Invoice invoice) {
        this.invoice = invoice;
    }

    boolean isIntraCommunityTransaction() {
        return !invoice.consumerInvoice
                && invoice.countryOfOrigin!=null
                && invoice.countryOfDestination!=null
                && invoice.countryOfDestination.isEuCountry()
                && !invoice.countryOfDestination.equals(invoice.countryOfOrigin)
                && !StringUtils.isEmpty(invoice.debtor.getEuTaxId());
    }

    IsoCountryCode getVatCountry() {
        return invoice.getCountryOfDestination().isEuCountry() ?
                invoice.getCountryOfDestination() : invoice.countryOfOrigin;
    }
}