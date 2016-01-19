package app.domain.invoice.testbuilders;

import app.domain.invoice.InvoiceLine;
import app.domain.invoice.VatTariff;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceLineTestBuilder {

    private VatTariff vatTariff;
    private LocalDate vatReferenceDate;
    private BigDecimal lineAmountExclVat;
    private BigDecimal lineAmountInclVat;
    private String description;

    public InvoiceLineTestBuilder setVatTariff(VatTariff vatTariff) {
        this.vatTariff = vatTariff;
        return this;
    }

    public InvoiceLineTestBuilder setVatReferenceDate(LocalDate vatReferenceDate) {
        this.vatReferenceDate = vatReferenceDate;
        return this;
    }

    public InvoiceLineTestBuilder setLineAmountExclVat(BigDecimal lineAmountExclVat) {
        this.lineAmountExclVat = lineAmountExclVat;
        return this;
    }

    public InvoiceLineTestBuilder setLineAmountInclVat(BigDecimal lineAmountInclVat) {
        this.lineAmountInclVat = lineAmountInclVat;
        return this;
    }

    public InvoiceLineTestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public static InvoiceLineTestBuilder newInstance() {
        return new InvoiceLineTestBuilder();
    }

    public InvoiceLine build() {
        return new InvoiceTestLine(vatTariff, vatReferenceDate, lineAmountExclVat, lineAmountInclVat, description);
    }

    class InvoiceTestLine extends InvoiceLine {

        private final VatTariff vatTariff;
        private final LocalDate vatReferenceDate;
        private final BigDecimal lineAmountExclVat;
        private final BigDecimal lineAmountInclVat;
        private final String description;

        InvoiceTestLine(VatTariff vatTariff, LocalDate vatReferenceDate, BigDecimal lineAmountExclVat, BigDecimal lineAmountInclVat, String description) {
            this.vatTariff = vatTariff;
            this.vatReferenceDate = vatReferenceDate;
            this.lineAmountExclVat = lineAmountExclVat;
            this.lineAmountInclVat = lineAmountInclVat;
            this.description = description;
        }

        @Override
        public VatTariff getVatTariff() {
            return vatTariff;
        }

        @Override
        public LocalDate getVatReferenceDate() {
            return vatReferenceDate;
        }

        @Override
        public BigDecimal getLineAmountExclVat() {
            return lineAmountExclVat;
        }

        @Override
        public BigDecimal getLineAmountInclVat() {
            return lineAmountInclVat;
        }

        @Override
        public String[] getDescription() {
            return new String[]{description};
        }
    }

}
