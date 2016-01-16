package app.domain.invoice;

/**
 * Created by marc on 15/01/16.
 */
public class CreateInvoiceCommand implements Command {

    private InvoiceRepository invoiceRepository;
    private Debtor debtor;

    public CreateInvoiceCommand(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Debtor getDebtor() {
        return debtor;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }

    @Override
    public void execute() {
        Invoice invoice = new InvoiceFactory().newInstance();
        invoice.getInvoiceLines().add(new InvoiceLineHigh());
        invoice.createInvoice(this);

        invoiceRepository.store(invoice);
    }
}
