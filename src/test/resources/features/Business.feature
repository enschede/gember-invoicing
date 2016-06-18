Feature: Delivering to a business customer in the EU.
  - A company with registration in the main country delivers goods/services within the same country
  - A company with registration in the main country delivers goods/services within the same country under tax shifted rules
  - A company with registration in the main country delivers goods to a business partner in EU with a tax id
  - A company with registration in the main country delivers goods to a business partner in EU without a tax id
  - A company with registration in the main country delivers goods from a EU country to a business partner in another EU with a tax id
  - A company with registration in the main country delivers goods from a EU country to a business partner in another EU without a tax id
  - A company with registration in the main country delivers services to a business partner in EU without a tax id

  Background:
    Given An invoiceline worth "100.00" euro "excl" VAT with "High" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "excl" VAT with "Low1" vat level and referencedate is "2016-01-01"
    And An invoiceline worth "100.00" euro "excl" VAT with "Zero" vat level and referencedate is "2016-01-01"

  Scenario Outline: A company with registration in the main country delivers goods/services within the same country
    Delivering goods without passing an internal border always requires to calculate VAT

    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer has VAT id "NL56789" in "NL"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  Scenario Outline: A company with registration in the main country delivers goods/services within the same country under tax shifted rules
    Delivering goods without passing an internal border always requires to calculate VAT

    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer has VAT id "NL56789" in "NL"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "true" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat |
      | NL     | NL          | 300.00             | 300.00           | 0.00           |

  Scenario Outline: A company with registration in the main country delivers goods to a business partner in EU with a tax id
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer has VAT id "DE56789" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat |
      | NL     | DE          | 300.00             | 300.00           | 0.00           |

  Scenario Outline: A company with registration in the main country delivers goods to a business partner in EU without a tax id
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And A customer without a validated VAT id
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 21.00         | 21.00     |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | NL     | NL          | 327.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  Scenario Outline: A company with registration in the main country delivers goods from a EU country to a business partner in another EU with a tax id
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And the company has VAT id "BE12345" in "BE"
    And A customer has VAT id "DE56789" in "DE"
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat |
      | BE     | DE          | 300.00             | 300.00           | 0.00           |

  Scenario Outline: A company with registration in the main country delivers goods from a EU country to a business partner in another EU without a tax id
    Given A company in "NL" with vat calculation policy is "VAT_CALCULATION_ON_TOTAL"
    And the company has VAT id "NL0123456789B01" in "NL"
    And the company has VAT id "BE12345" in "BE"
    And A customer without a validated VAT id
    And Country of origin is "<origin>"
    And Country of destination is "<destination>"
    When A "business" invoice with vat shifted equals "false" is created at "2016-01-01"
    Then The total amount including VAT is "<totalAmountInclVat>"
    And The total amount excluding VAT is "<totalAmountExVat>"
    And The total amount VAT is "<totalAmountVat>"
    And The VAT amount for percentage "<vatPercentage>" is "<amountVat>"

    Examples:
      | origin | destination | totalAmountInclVat | totalAmountExVat | totalAmountVat | vatPercentage | amountVat |
      | BE     | NL          | 325.00             | 300.00           | 27.00          | 19.00         | 19.00     |
      | BE     | NL          | 325.00             | 300.00           | 27.00          | 6.00          | 6.00      |
      | BE     | NL          | 325.00             | 300.00           | 27.00          | 0.00          | 0.00      |

  # A company with registration in the main country delivers services to a business partner in EU without a tax id
