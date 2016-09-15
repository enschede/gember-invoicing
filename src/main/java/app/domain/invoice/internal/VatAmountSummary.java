package app.domain.invoice.internal;

import app.domain.invoice.internal.vatTariffs.VatPercentage;

import java.math.BigDecimal;

public class VatAmountSummary {
    private VatPercentage vatPercentage;
    private BigDecimal amountVat;
    private BigDecimal amountExclVat;
    private BigDecimal amountInclVat;

    public VatAmountSummary(VatPercentage vatPercentage, BigDecimal amountVat, BigDecimal amountExclVat, BigDecimal amountInclVat) {
        this.vatPercentage = vatPercentage;
        this.amountVat = amountVat;
        this.amountExclVat = amountExclVat;
        this.amountInclVat = amountInclVat;
    }

    public BigDecimal getAmountVat() {
        return amountVat;
    }

    public static VatAmountSummary zero(VatPercentage vatPercentage) {
        return new VatAmountSummary(vatPercentage, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static VatAmountSummary add(VatAmountSummary vatAmountSummary, VatAmountSummary vatAmountSummary1) {
        return new VatAmountSummary(vatAmountSummary.vatPercentage,
                vatAmountSummary.amountVat.add(vatAmountSummary1.amountVat),
                vatAmountSummary.amountExclVat.add(vatAmountSummary1.amountExclVat),
                vatAmountSummary.amountInclVat.add(vatAmountSummary1.amountInclVat));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VatAmountSummary that = (VatAmountSummary) o;

        if (vatPercentage != null ? !vatPercentage.equals(that.vatPercentage) : that.vatPercentage != null)
            return false;
        if (amountVat != null ? !amountVat.equals(that.amountVat) : that.amountVat != null) return false;
        if (amountExclVat != null ? !amountExclVat.equals(that.amountExclVat) : that.amountExclVat != null)
            return false;
        return amountInclVat != null ? amountInclVat.equals(that.amountInclVat) : that.amountInclVat == null;

    }

    @Override
    public int hashCode() {
        int result = vatPercentage != null ? vatPercentage.hashCode() : 0;
        result = 31 * result + (amountVat != null ? amountVat.hashCode() : 0);
        result = 31 * result + (amountExclVat != null ? amountExclVat.hashCode() : 0);
        result = 31 * result + (amountInclVat != null ? amountInclVat.hashCode() : 0);
        return result;
    }
}
