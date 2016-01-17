package app.domain.invoice;

import app.domain.invoice.testbuilders.ConfigurationTestBuilder;
import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by marc on 17/01/16.
 */
public class InvoiceVatCalculatorTest {

    @Test
    public void shouldCalculateVatForInclVatInvoiceAndOnSubTotals() {
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(true)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineLow())
                .build();

        InvoiceVatCalculator invoiceVatCalculator = new InvoiceVatCalculator(invoice);

        assertThat(invoiceVatCalculator.getVatPerVatTariff().size(), Matchers.is(2));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.HIGH),
                equalTo(new VatAmountSummary(VatTariff.HIGH, new BigDecimal("0.39"), new BigDecimal("2.01"), new BigDecimal("2.40"))));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.LOW),
                equalTo(new VatAmountSummary(VatTariff.LOW, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculator.getInvoiceTotalVat(), equalTo(new BigDecimal("0.45")));
    }

    @Test
    public void shouldCalculateVatForExclVatInvoiceAndOnSubTotals() {
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(false)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(false)
                        .build())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineLow())
                .build();

        InvoiceVatCalculator invoiceVatCalculator = new InvoiceVatCalculator(invoice);

        assertThat(invoiceVatCalculator.getVatPerVatTariff().size(), Matchers.is(2));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.HIGH),
                equalTo(new VatAmountSummary(VatTariff.HIGH, new BigDecimal("0.39"), new BigDecimal("2.00"), new BigDecimal("2.39"))));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.LOW),
                equalTo(new VatAmountSummary(VatTariff.LOW, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculator.getInvoiceTotalVat(), equalTo(new BigDecimal("0.45")));
    }

    @Test
    public void shouldCalculateVatForInclVatInvoiceAndOnLineVat() {
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(true)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(true)
                        .build())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineLow())
                .build();

        InvoiceVatCalculator invoiceVatCalculator = new InvoiceVatCalculator(invoice);

        assertThat(invoiceVatCalculator.getVatPerVatTariff().size(), Matchers.is(2));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.HIGH),
                equalTo(new VatAmountSummary(VatTariff.HIGH, new BigDecimal("0.40"), new BigDecimal("2.00"), new BigDecimal("2.40"))));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.LOW),
                equalTo(new VatAmountSummary(VatTariff.LOW, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculator.getInvoiceTotalVat(), equalTo(new BigDecimal("0.46")));
    }

    @Test
    public void shouldCalculateVatForExclVatInvoiceAndOnLineVat() {
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(false)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(true)
                        .build())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineHigh())
                .addInvoiceLine(new InvoiceLineLow())
                .build();

        InvoiceVatCalculator invoiceVatCalculator = new InvoiceVatCalculator(invoice);

        assertThat(invoiceVatCalculator.getVatPerVatTariff().size(), Matchers.is(2));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.HIGH),
                equalTo(new VatAmountSummary(VatTariff.HIGH, new BigDecimal("0.40"), new BigDecimal("2.00"), new BigDecimal("2.40"))));
        assertThat(invoiceVatCalculator.getVatPerVatTariff().get(VatTariff.LOW),
                equalTo(new VatAmountSummary(VatTariff.LOW, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculator.getInvoiceTotalVat(), equalTo(new BigDecimal("0.46")));
    }

}