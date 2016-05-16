package app.domain.invoice;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "~@not_implemented",
        format = {"pretty", "html:target/cucumber"},
        glue = {"nl.marcenschede.financials.invoicing.glue"},
        features = "classpath:features/",
        strict = false
)
public class AllCucumberTests {
}
