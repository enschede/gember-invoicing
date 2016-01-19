package app.domain.invoice;

import app.domain.debtor.EasyDebtorImpl;
import app.domain.invoice.testbuilders.InvoiceLineTestBuilder;
import app.domain.invoice.testbuilders.InvoiceTestBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
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
                        new InvoiceLine[]{
                                InvoiceLineTestBuilder.newInstance()
                                        .setVatTariff(VatTariff.HIGH)
                                        .setVatReferenceDate(LocalDate.of(1992, 10, 1))
                                        .setLineAmountExclVat(new BigDecimal("1.00"))
                                        .setLineAmountInclVat(new BigDecimal("1.06"))
                                        .build()
                        });

        ArgumentCaptor<Invoice> invoiceArgumentCaptor = ArgumentCaptor.forClass(Invoice.class);
        doNothing().when(invoiceRepository).store(invoiceArgumentCaptor.capture());

        addInvoiceLinesCommand.execute();

        assertThat(invoiceArgumentCaptor.getValue().getInvoiceLines().size(), Matchers.is(1));
    }

}