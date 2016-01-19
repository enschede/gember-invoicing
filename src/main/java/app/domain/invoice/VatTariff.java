package app.domain.invoice;

public enum VatTariff {

    HIGH(true),
    LOW(true),
    ZERO(true),
    NO_VAT(false);

    private boolean calculateVat;

    VatTariff(boolean calculateVat) {
        this.calculateVat = calculateVat;
    }

    public boolean isCalculateVat() {
        return calculateVat;
    }
}
