package app.domain.invoice;

import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertThat;

/**
 * Created by marc on 17/01/16.
 */
public class InvoiceTest {

    @Test
    public void shouldCalculateInvoiceAmountVatsExclVatInvoice() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        assertThat(expectedInvoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.45")));
        assertThat(expectedInvoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.00")));
        assertThat(expectedInvoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.45")));
    }

    @Test
    public void shouldCalculateInvoiceAmountsOfInclVatInvoice() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .setIncludingVatInvoice(true)
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        assertThat(expectedInvoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal("0.45")));
        assertThat(expectedInvoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal("3.01")));
        assertThat(expectedInvoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal("3.46")));
    }

}