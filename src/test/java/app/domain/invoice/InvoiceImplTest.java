package app.domain.invoice;

import app.domain.invoice.testbuilders.ConfigurationTestBuilder;
import app.domain.invoice.testbuilders.InvoiceLineTestBuilder;
import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertThat;

public class InvoiceImplTest {

    @Test
    public void shouldCalculateInvoiceAmountVatsExclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(2016, 10, 1);

        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(false)
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

        assertThat(invoiceImpl.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.48")));
        assertThat(invoiceImpl.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.00")));
        assertThat(invoiceImpl.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.48")));
    }

    @Test
    public void shouldCalculateInvoiceAmountsOfInclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(2016, 10, 1);

        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(true)
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


        assertThat(invoiceImpl.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.47")));
        assertThat(invoiceImpl.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("2.95")));
        assertThat(invoiceImpl.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.42")));
    }

    @Test
    public void shouldCalculateIntraCommunityInvoiceAmountVatsExclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(2016, 1, 1);

        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(false)
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

        assertThat(invoiceImpl.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.45")));
        assertThat(invoiceImpl.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.00")));
        assertThat(invoiceImpl.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.45")));
    }

    @Test
    public void shouldCalculateIntraCommunityInvoiceAmountsOfInclVatInvoice() {
        LocalDate vatReferenceDate = LocalDate.of(2016, 1, 1);

        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(true)
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


        assertThat(invoiceImpl.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.45")));
        assertThat(invoiceImpl.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("2.97")));
        assertThat(invoiceImpl.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.42")));
    }

}