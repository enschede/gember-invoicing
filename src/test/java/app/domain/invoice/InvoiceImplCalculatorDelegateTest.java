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

public class InvoiceImplCalculatorDelegateTest {

    // A date in 1992 is choosen because of the high VAT tariff a that time (17.5%).
    // The .5 introduces a lot of roundings hell :-)
    private final LocalDate vatReferenceDate = LocalDate.of(1992, 10, 1);

    IsoCountryCode nl = new IsoCountryCode("NL");
    IsoCountryCode de = new IsoCountryCode("DE");

    @Test
    public void shouldCalculateVatForBusinessInvoiceAndOnSubTotals() {
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

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.35"), new BigDecimal("2.00"), new BigDecimal("2.35"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.41")));
    }

    @Test
    public void shouldCalculateVatForConsumerInvoiceAndOnSubTotals() {
        // Given
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

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.35"), new BigDecimal("2.01"), new BigDecimal("2.36"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.41")));
    }

    @Test
    public void shouldCalculateVatForBusinessInvoiceAndOnLineVat() {
        // Given
        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(false)
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
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.36"), new BigDecimal("2.00"), new BigDecimal("2.36"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.42")));
    }

    @Test
    public void shouldCalculateVatForConsumerInvoiceAndOnLineVat() {
        // Given
        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(true)
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
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.36"), new BigDecimal("2.00"), new BigDecimal("2.36"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.42")));
        assertThat(invoiceCalculatorDelegate.getInvoiceTotalExclVat(), equalTo(new BigDecimal("3.00")));
        assertThat(invoiceCalculatorDelegate.getInvoiceTotalInclVat(), equalTo(new BigDecimal("3.42")));
    }

    @Test
    public void shouldCalculateVatForEuBusinessInvoice() {
        // Given
        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(false)
                .setCountryOfDestination("DE")
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
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
    }

    @Test
    public void shouldCalculateVatForEuConsumerInvoice() {
        // Given
        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(true)
                .setCountryOfDestination("DE")
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
                        .setVatTariff(VatTariff.LOW1)
                        .setVatReferenceDate(vatReferenceDate)
                        .setLineAmountExclVat(new BigDecimal("1.00"))
                        .setLineAmountInclVat(new BigDecimal("1.06"))
                        .build())
                .build();

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(de, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(de, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.28"), new BigDecimal("2.08"), new BigDecimal("2.36"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.07"), new BigDecimal("0.99"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.35")));
    }

    @Test
    public void shouldCalculateVatForConsumerExportInvoiceAndOnSubTotals() {
        // Given
        InvoiceImpl invoiceImpl = InvoiceTestBuilder.newInstance()
                .createDefault()
                .setConsumerInvoice(true)
                .setCountryOfDestination("TR")
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

        VatPercentage vatPercentageHigh =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.HIGH, vatReferenceDate);
        VatPercentage vatPercentageLow =
                invoiceImpl.configuration.vatRepository.findByTariffAndDate(nl, VatTariff.LOW1, vatReferenceDate);

        // When
        InvoiceCalculatorDelegate invoiceCalculatorDelegate = new InvoiceCalculatorDelegate(invoiceImpl);

        // Then
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().size(), Matchers.is(2));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageHigh),
                equalTo(new VatAmountSummary(vatPercentageHigh, new BigDecimal("0.35"), new BigDecimal("2.01"), new BigDecimal("2.36"))));
        assertThat(invoiceCalculatorDelegate.getAmountSummariesGroupedByVatPercentage().get(vatPercentageLow),
                equalTo(new VatAmountSummary(vatPercentageLow, new BigDecimal("0.06"), new BigDecimal("1.00"), new BigDecimal("1.06"))));
        assertThat(invoiceCalculatorDelegate.getTotalAmountVat(), equalTo(new BigDecimal("0.41")));
    }

}