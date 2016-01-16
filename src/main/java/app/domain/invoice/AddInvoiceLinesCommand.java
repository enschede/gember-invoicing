package app.domain.invoice;

import java.util.UUID;

/**
 * Created by marc on 15/01/16.
 */
public class AddInvoiceLinesCommand implements Command {

    private InvoiceRepository invoiceRepository;
    private UUID invoiceId;
    private InvoiceLine[] invoiceLines;

    public AddInvoiceLinesCommand(InvoiceRepository invoiceRepository, UUID invoiceId, InvoiceLine... invoiceLines) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceId = invoiceId;
        this.invoiceLines = invoiceLines;
    }

    public InvoiceRepository getInvoiceRepository() {
        return invoiceRepository;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public InvoiceLine[] getInvoiceLines() {
        return invoiceLines;
    }

    @Override
    public void execute() {
        Invoice invoice = invoiceRepository.findById(invoiceId);
        invoice.addInvoiceLines(this);

        invoiceRepository.store(invoice);
    }
}
