Feature: Consumenten factuur

  Background:
    Given A company with VAT id "NL0123456789B01" in "NL" and vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And An invoiceline worth "121.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "106.00" euro "incl" VAT with "Low1" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "Zero" vat level and referencedate is "2016-01-01"

  Scenario Outline: levering zonder extra registratie in DE
    Given A customer without a validated VAT id
    Given Country of origin is "<origin>"
    Given Country of destination is "<destination>"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | DE          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |
      | BE     | BE          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | BE     | BE          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | BE     | BE          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |
      | BE     | DE          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | BE     | DE          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | BE     | DE          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |
      | BE     | TR          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | BE     | TR          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | BE     | TR          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |



