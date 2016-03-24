package glue.regime;

import app.domain.invoice.*;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RegimeGlue {

    private Configuration configuration = new Configuration();
    private Debtor debtor;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
    private Boolean consumerInvoice;
    private IsoCountryCode countryOfOrigin;
    private IsoCountryCode countryOfDestination;
    private InvoiceImpl invoice;

    @Given("^A company that resides \"([^\"]*)\"$")
    public void a_company_in(String registrationCountry) throws Throwable {
        configuration.setRegistrationCountry(registrationCountry);

    }

    @Given("^The company is registrated for VAT in \"([^\"]*)\"$")
    public void the_company_is_registrated_for_VAT_in(String arg1) throws Throwable {
        configuration.setVatRegisteredCountries(Arrays.asList(arg1));
    }

    @Given("^The customer is resident in \"([^\"]*)\"$")
    public void the_customer_is_resident_in(String customerCountryId) throws Throwable {
    }

    @Given("^The customer is private and resident in \"([^\"]*)\"$")
    public void the_customer_is_private_and_resident_in(String residentCountry) throws Throwable {
        consumerInvoice = true;

        debtor = new Debtor() {
            @Override
            public String getExternalId() {
                return null;
            }

            @Override
            public String[] getFullAddress() {
                return new String[0];
            }

            @Override
            public String getEuTaxId() {
                return null;
            }

            @Override
            public String getCountryId() {
                return residentCountry;
            }
        };
    }

    @Given("^An invoiceline worth \"([^\"]*)\" euro excl VAT with \"([^\"]*)\" vat level and referencedate is \"([^\"]*)\"$")
    public void an_invoiceline_worth_euro_excl_VAT_at_vat_level(String value, String vatLevel, String refDate) throws Throwable {

        InvoiceLine invoiceLine = new InvoiceLine() {
            @Override
            public VatTariff getVatTariff() {
                return VatTariff.valueOf(vatLevel.toUpperCase());
            }

            @Override
            public LocalDate getVatReferenceDate() {
                return LocalDate.parse(refDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }

            @Override
            public BigDecimal getLineAmountExclVat() {
                return new BigDecimal(value);
            }

            @Override
            public BigDecimal getLineAmountInclVat() {
                return null;
            }

            @Override
            public String[] getDescription() {
                return new String[0];
            }
        };

        invoiceLines.add(invoiceLine);
    }

    @Given("^An invoiceline worth \"([^\"]*)\" euro incl VAT with \"([^\"]*)\" vat level and referencedate is \"([^\"]*)\"$")
    public void an_invoiceline_worth_euro_incl_VAT_at_vat_level(String value, String vatLevel, String refDate) throws Throwable {

        InvoiceLine invoiceLine = new InvoiceLine() {
            @Override
            public VatTariff getVatTariff() {
                return VatTariff.valueOf(vatLevel.toUpperCase());
            }

            @Override
            public LocalDate getVatReferenceDate() {
                return LocalDate.parse(refDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }

            @Override
            public BigDecimal getLineAmountExclVat() {
                return null;
            }

            @Override
            public BigDecimal getLineAmountInclVat() {
                return new BigDecimal(value);
            }

            @Override
            public String[] getDescription() {
                return new String[0];
            }
        };

        invoiceLines.add(invoiceLine);
    }

    @Given("^The delivery is from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_delivery_is_from_to(String countryOfOrigin, String countryOfDestination) throws Throwable {
        this.countryOfOrigin = new IsoCountryCode(countryOfOrigin);
        this.countryOfDestination = new IsoCountryCode(countryOfDestination);
    }

    @When("^An invoice is created at \"([^\"]*)\"$")
    public void an_invoice_is_created_at(String invoiceDate) throws Throwable {

        assert consumerInvoice != null;
        assert invoiceDate != null;

        LocalDate dateOfInvoice = LocalDate.parse(invoiceDate, DateTimeFormatter.ISO_DATE);

        invoice = new InvoiceImpl(configuration);
        invoice.setDebtor(debtor);
        invoice.setInvoiceLines(invoiceLines);

        invoice.setConsumerInvoice(consumerInvoice);
        invoice.setCountryOfOrigin(countryOfOrigin);
        invoice.setCountryOfDestination(countryOfDestination);

        invoice.getInvoiceTotalVat();

    }

    @Then("^The total amount including VAT is \"([^\"]*)\"$")
    public void the_amount_including_VAT_is(String expectedValue) throws Throwable {

        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalInclVat(), Matchers.is(new BigDecimal(expectedValue)));
    }

    @Then("^The total amount excluding VAT is \"([^\"]*)\"$")
    public void the_amount_excluding_VAT_is(String expectedValue) throws Throwable {

        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalExclVat(), Matchers.is(new BigDecimal(expectedValue)));
    }

    @Then("^The total amount VAT is \"([^\"]*)\"$")
    public void the_total_amount_VAT_is(String expectedValue) throws Throwable {

        assert invoice != null;

        Assert.assertThat(invoice.getInvoiceTotalVat(), Matchers.is(new BigDecimal(expectedValue)));
    }

    @Then("^The VAT amount for percentage \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_VAT_amount_for_percentage_is(String percentage, String expectedAmount) throws Throwable {

        assert invoice != null;

        Optional<BigDecimal> actualAmount =
                invoice.getVatPerVatTariff().entrySet().stream()
                        .filter(entry -> entry.getKey().getPercentage().equals(new BigDecimal(percentage)))
                        .map(entry -> entry.getValue().getAmountVat())
                        .findFirst();

        Assert.assertThat(actualAmount.isPresent(), Matchers.is(true));
        Assert.assertThat(actualAmount.get(), Matchers.is(new BigDecimal(expectedAmount)));
    }


}
