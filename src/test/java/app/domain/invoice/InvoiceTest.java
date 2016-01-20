package app.domain.invoice;

import app.domain.invoice.testbuilders.ConfigurationTestBuilder;
import app.domain.invoice.testbuilders.InvoiceLineTestBuilder;
import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertThat;

public class InvoiceTest {

    @Test
    public void shouldCalculateInvoiceAmountVatsExclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

        Invoice invoice = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setIncludingVatInvoice(false)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.41")));
        assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.00")));
        assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.41")));
    }

    @Test
    public void shouldCalculateInvoiceAmountsOfInclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

        Invoice invoice = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setIncludingVatInvoice(true)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();


        assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.41")));
        assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.01")));
        assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.42")));
    }

    @Test
    public void shouldCalculateIntraCommunityInvoiceAmountVatsExclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

        Invoice invoice = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setIncludingVatInvoice(false)
                .setCountryOfDestination("DE")
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.00")));
        assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.00")));
        assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.00")));
    }

    @Test
    public void shouldCalculateIntraCommunityInvoiceAmountsOfInclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

        Invoice invoice = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setIncludingVatInvoice(true)
                .setCountryOfDestination("DE")
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.HIGH)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.18"))
                        .build())
                .addInvoiceLine(InvoiceLineTestBuilder.newInstance()
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();


        assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.41")));
        assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.01")));
        assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.42")));
    }

}