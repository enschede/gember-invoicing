package app.domain.invoice.internal;

public enum VatCalculationRegime {
    CONSUMER(false),
    B2B_NATIONAL(false),
    B2B_NATIONAL_SHIFTED_VAT(true),
    B2B_EU_GOODS(true),
    B2B_EU_SERVICES(true),
    B2B_EU_E_SERVICES(true),
    EXPORT(false);

    private Boolean isVatFreeInvoice;

    VatCalculationRegime(Boolean isVatFreeInvoice) {
        this.isVatFreeInvoice = isVatFreeInvoice;
    }

    public Boolean getVatFreeInvoice() {
        return isVatFreeInvoice;
    }
}
