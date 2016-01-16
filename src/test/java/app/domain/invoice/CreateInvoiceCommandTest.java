package app.domain.invoice;

import app.domain.debtor.EasyDebtorImpl;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateInvoiceCommandTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Test
    public void shouldCreateNewInvoiceWithDebtor() {
        Debtor debtor = new EasyDebtorImpl();

        CreateInvoiceCommand createInvoiceCommand = new CreateInvoiceCommand(invoiceRepository);
        createInvoiceCommand.setDebtor(debtor);

        createInvoiceCommand.execute();

        verify(invoiceRepository).store(any(Invoice.class));
    }

    @Test
    public void shouldCreateNewInvoiceWithDebtorAndInvoiceLine() {
        Invoice expectedInvoice = InvoiceTestBuilder.newInstance().createDefault().build();

        when(invoiceRepository.findById(expectedInvoice.getId())).thenReturn(expectedInvoice);

        AddInvoiceLinesCommand addInvoiceLinesCommand =
                new AddInvoiceLinesCommand(
                        invoiceRepository,
                        expectedInvoice.getId(),
                        new InvoiceLine[]{new InvoiceLineHigh()});

        ArgumentCaptor<Invoice> invoiceArgumentCaptor = ArgumentCaptor.forClass(Invoice.class);
        doNothing().when(invoiceRepository).store(invoiceArgumentCaptor.capture());

        addInvoiceLinesCommand.execute();

        assertThat(invoiceArgumentCaptor.getValue().getInvoiceLines().size(), Matchers.is(1));
    }

    @Test
    public void shouldCalculateInvoiceAmountInclVat() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        BigDecimal invoiceTotalInclVat = expectedInvoice.getInvoiceTotalInclVat();

        assertThat(invoiceTotalInclVat, Matchers.is(new BigDecimal("3.46")));
    }

    @Test
    public void shouldCalculateInvoiceAmountExclVat() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        BigDecimal invoiceTotalInclVat = expectedInvoice.getInvoiceTotalExclVat();

        assertThat(invoiceTotalInclVat, Matchers.is(new BigDecimal("3.00")));
    }

    @Test
    public void shouldCalculateInvoiceAmountVatOfExclVatInvoice() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        BigDecimal invoiceTotalVat = expectedInvoice.getInvoiceTotalVat();

        assertThat(invoiceTotalVat, Matchers.is(new BigDecimal("0.46")));
    }

    @Test
    public void shouldCalculateInvoiceAmountVatOfInclVatInvoice() {
        Invoice expectedInvoice =
                InvoiceTestBuilder.newInstance()
                        .createDefault()
                        .setIncludingVatInvoice(true)
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineHigh())
                        .addInvoiceLine(new InvoiceLineLow())
                        .build();

        BigDecimal invoiceTotalVat = expectedInvoice.getInvoiceTotalVat();

        assertThat(invoiceTotalVat, Matchers.is(new BigDecimal("0.46")));
    }

}