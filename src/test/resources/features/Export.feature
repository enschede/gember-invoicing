Feature: An invoice for export
  The following situations may occur:
  - A company with registration in it's main country export goods
  - A company with registration in it's main country export goods from another EU country
  - A company exports from a EU country where it has no registration, leading to an exception

  Background:
    Given An invoiceline worth "121.00" euro "incl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "106.00" euro "incl" VAT with "Low1" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "incl" VAT with "Zero" vat level and referencedate is "2016-01-01"

  Scenario Outline: A company with registration in it's main country export goods from another EU country
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer without a validated VAT id
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | TR          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  Scenario Outline: A company with registration in it's main country export goods
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And the company has VAT id "BE12345" in "BE"
    And A customer without a validated VAT id
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 19.00         | 19.32     |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 6.00          | 6.00      |
      | BE     | TR          | 327.00             | 301.68           | 25.32          | 0.00          | 0.00      |

  Scenario: A company exports from a EU country where it has no registration, leading to an exception
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer without a validated VAT id
    And Country of origin is "BE"
    And Country of destination is "TR"
    When A "consumer" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT request throws an invoice calculation exception
    Then The total amount excluding VAT request throws an invoice calculation exception
    Then The total amount VAT request throws an invoice calculation exception

