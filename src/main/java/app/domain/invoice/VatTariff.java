package app.domain.invoice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by marc on 16/01/16.
 */
public enum VatTariff {

    HIGH(new BigDecimal("19.50")),
    LOW(new BigDecimal("6.00")),
    ZERO(new BigDecimal("0.00"));

    private BigDecimal percentage;

    VatTariff(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public VatAmountSummary createVatAmountInfo(Boolean includingVatInvoice, BigDecimal amountExclVat, BigDecimal amountInclVat) {

        if(includingVatInvoice) {
            BigDecimal vatAmount = amountInclVat
                    .divide(
                            percentage.add(BigDecimal.valueOf(100))
                                    .divide(percentage,
                                            new MathContext(10, RoundingMode.HALF_EVEN)),
                            2,
                            RoundingMode.HALF_EVEN);

            return new VatAmountSummary(this, vatAmount, amountInclVat.subtract(vatAmount), amountInclVat);
        } else {
            BigDecimal vatAmount = amountExclVat
                    .multiply(
                            percentage.divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_EVEN);

            return new VatAmountSummary(this, vatAmount, amountExclVat, amountExclVat.add(vatAmount));

        }
    }
}
