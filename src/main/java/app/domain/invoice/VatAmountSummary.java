package app.domain.invoice;

import java.math.BigDecimal;

/**
 * Created by marc on 17/01/16.
 */
public class VatAmountSummary {
    private VatTariff vatTariff;
    private BigDecimal amountVat;
    private BigDecimal amountExclVat;
    private BigDecimal amountInclVat;

    public VatAmountSummary(VatTariff vatTariff, BigDecimal amountVat, BigDecimal amountExclVat, BigDecimal amountInclVat) {
        this.vatTariff = vatTariff;
        this.amountVat = amountVat;
        this.amountExclVat = amountExclVat;
        this.amountInclVat = amountInclVat;
    }

    public VatTariff getVatTariff() {
        return vatTariff;
    }

    public BigDecimal getAmountVat() {
        return amountVat;
    }

    public BigDecimal getAmountExclVat() {
        return amountExclVat;
    }

    public BigDecimal getAmountInclVat() {
        return amountInclVat;
    }

    public static VatAmountSummary zero(VatTariff vatTariff) {
        return new VatAmountSummary(vatTariff, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static VatAmountSummary add(VatAmountSummary vatAmountSummary, VatAmountSummary vatAmountSummary1) {
        return new VatAmountSummary(vatAmountSummary.vatTariff,
                vatAmountSummary.amountVat.add(vatAmountSummary1.amountVat),
                vatAmountSummary.amountExclVat.add(vatAmountSummary1.amountExclVat),
                vatAmountSummary.amountInclVat.add(vatAmountSummary1.amountInclVat));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VatAmountSummary that = (VatAmountSummary) o;

        if (vatTariff != that.vatTariff) return false;
        if (amountVat != null ? !amountVat.equals(that.amountVat) : that.amountVat != null) return false;
        if (amountExclVat != null ? !amountExclVat.equals(that.amountExclVat) : that.amountExclVat != null)
            return false;
        return amountInclVat != null ? amountInclVat.equals(that.amountInclVat) : that.amountInclVat == null;

    }

    @Override
    public int hashCode() {
        int result = vatTariff != null ? vatTariff.hashCode() : 0;
        result = 31 * result + (amountVat != null ? amountVat.hashCode() : 0);
        result = 31 * result + (amountExclVat != null ? amountExclVat.hashCode() : 0);
        result = 31 * result + (amountInclVat != null ? amountInclVat.hashCode() : 0);
        return result;
    }
}
