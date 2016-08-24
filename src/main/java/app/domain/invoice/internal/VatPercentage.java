package app.domain.invoice.internal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

public class VatPercentage {

    String isoCountryCode;
    VatTariff vatTariff;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal percentage;

    public VatPercentage(String isoCountryCode, VatTariff vatTariff, LocalDate startDate, LocalDate endDate, BigDecimal percentage) {
        this.isoCountryCode = isoCountryCode;
        this.vatTariff = vatTariff;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
    }

    /**
     * Calculates VAT amount.
     *
     * If consumerInvoice==true, amountVat and amountExclVat are calculated from amountInclVat.
     * If consumerInvoice==false, amountVat and amountInclVat are calculated from amountExclVat.
     */
    public VatAmountSummary createVatAmountInfo(Boolean includingVatInvoice, BigDecimal amountExclVat, BigDecimal amountInclVat) {

        if(includingVatInvoice) {
            BigDecimal vatAmount = getVatAmountFromAmountInclVat(amountInclVat, percentage);

            return new VatAmountSummary(this, vatAmount, amountInclVat.subtract(vatAmount), amountInclVat);
        } else {
            BigDecimal vatAmount = getVatAmountFromAmountExclVat(amountExclVat, percentage);

            return new VatAmountSummary(this, vatAmount, amountExclVat, amountExclVat.add(vatAmount));

        }
    }

    public static BigDecimal getVatAmountFromAmountExclVat(BigDecimal amountExclVat, BigDecimal percentage) {
        return amountExclVat
                        .multiply(
                                percentage.divide(BigDecimal.valueOf(100)))
                        .setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal getVatAmountFromAmountInclVat(BigDecimal amountInclVat, BigDecimal percentage) {

        if(percentage.compareTo(BigDecimal.ZERO) == 0)
            return new BigDecimal("0.00");

        return amountInclVat
                        .divide(
                                percentage.add(BigDecimal.valueOf(100))
                                        .divide(percentage,
                                                new MathContext(10, RoundingMode.HALF_EVEN)),
                                2,
                                RoundingMode.HALF_EVEN);
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public VatTariff getVatTariff() {
        return vatTariff;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VatPercentage that = (VatPercentage) o;

        if (vatTariff != that.vatTariff) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        return percentage != null ? percentage.equals(that.percentage) : that.percentage == null;

    }

    @Override
    public int hashCode() {
        int result = vatTariff != null ? vatTariff.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (percentage != null ? percentage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VatPercentage{" +
                "isoCountryCode=" + isoCountryCode +
                ", vatTariff=" + vatTariff +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", percentage=" + percentage +
                '}';
    }
}
















