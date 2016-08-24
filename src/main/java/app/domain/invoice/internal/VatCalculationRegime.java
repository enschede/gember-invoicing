package app.domain.invoice.internal;

public enum VatCalculationRegime {
    CONSUMER,
    B2B_NATIONAL,
    B2B_NATIONAL_SHIFTED_VAT,
    B2B_EU_GOODS,
    B2B_EU_SERVICES,
    B2B_EU_E_SERVICES,
    EXPORT
}
