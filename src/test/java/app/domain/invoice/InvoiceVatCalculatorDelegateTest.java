package app.domain.invoice;

import app.domain.invoice.testbuilders.ConfigurationTestBuilder;
import app.domain.invoice.testbuilders.InvoiceLineTestBuilder;
import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InvoiceVatCalculatorDelegateTest {

    // A date in 1992 is choosen because of the high VAT tariff a that time (17.5%).
    // The .5 introduces a lot of roundings hell :-)
    private final LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

    @Test
    public void shouldCalculateVatForExclVatInvoiceAndOnSubTotals() {
        Invoice invoice = InvoiceTestBuilder.newInstance()
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
                        .setVatTariff(VatTariff.LOW)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.HIGH, vatReferenceDate).get();
        VatPercentage vatPercentageLow =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.LOW, vatReferenceDate).get();

        // When
        InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegate = new InvoiceVatCalculatorDelegate(invoice);

        // Then
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.35"), new BigDecimal("2.00"), new BigDecimal("2.35"))));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculatorDelegate.getInvoiceTotalVat(), equalTo(new BigDecimal("0.41")));
    }

    @Test
    public void shouldCalculateVatForInclVatInvoiceAndOnSubTotals() {
        // Given
        Invoice invoice = InvoiceTestBuilder.newInstance()
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
                        .setVatTariff(VatTariff.LOW)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.HIGH, vatReferenceDate).get();
        VatPercentage vatPercentageLow =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.LOW, vatReferenceDate).get();

        // When
        InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegate = new InvoiceVatCalculatorDelegate(invoice);

        // Then
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.35"), new BigDecimal("2.01"), new BigDecimal("2.36"))));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculatorDelegate.getInvoiceTotalVat(), equalTo(new BigDecimal("0.41")));
    }

    @Test
    public void shouldCalculateVatForExclVatInvoiceAndOnLineVat() {
        // Given
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(false)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(true)
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
                        .setVatTariff(VatTariff.LOW)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.HIGH, vatReferenceDate).get();
        VatPercentage vatPercentageLow =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.LOW, vatReferenceDate).get();

        // When
        InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegate = new InvoiceVatCalculatorDelegate(invoice);

        // Then
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.36"), new BigDecimal("2.00"), new BigDecimal("2.36"))));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculatorDelegate.getInvoiceTotalVat(), equalTo(new BigDecimal("0.42")));
    }

    @Test
    public void shouldCalculateVatForInclVatInvoiceAndOnLineVat() {
        // Given
        Invoice invoice = InvoiceTestBuilder.newInstance()
                .setIncludingVatInvoice(true)
                .setConfiguration(ConfigurationTestBuilder.newInstance()
                        .setDefault()
                        .setConfiguredForCalculateVatOnIndividualLines(true)
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
                        .setVatTariff(VatTariff.LOW)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.HIGH, vatReferenceDate).get();
        VatPercentage vatPercentageLow =
                invoice.configuration.vatRepository.findByTariffAndDate(VatTariff.LOW, vatReferenceDate).get();

        // When
        InvoiceVatCalculatorDelegate invoiceVatCalculatorDelegate = new InvoiceVatCalculatorDelegate(invoice);

        // Then
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.36"), new BigDecimal("2.00"), new BigDecimal("2.36"))));
        assertThat(invoiceVatCalculatorDelegate.getVatPerVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceVatCalculatorDelegate.getInvoiceTotalVat(), equalTo(new BigDecimal("0.42")));
    }

}