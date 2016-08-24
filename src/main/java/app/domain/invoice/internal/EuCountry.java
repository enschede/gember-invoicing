package app.domain.invoice.internal;

public enum EuCountry {

    BE("Belgium"),
    FR("France"),
    IT("Italy"),
    LU("Luxembourg"),
    NL("The Netherlands"),
    DE("Germany"),
    DK("Denmark"),
    IE("Ireland"),
    GB("United Kingdom of Great Britain and Northern Ireland"),
    GR("Greece"),
    PT("Portugal"),
    ES("Spain"),
    FI("Finland"),
    AT("Austria"),
    SE("Sweden"),
    CY("Cyprus"),
    EE("Astonia"),
    HU("Hungary"),
    LV("Latvia"),
    LT("Lithuania"),
    MT("Malta"),
    PL("Poland"),
    SI("Slovenia"),
    SK("Slovakia"),
    CZ("Czech Republic"),
    BG("Bulgaria"),
    RO("Romania"),
    HR("Croatia");

    private String englishName;

    EuCountry(String englishName) {
        this.englishName = englishName;
    }

    public String getEnglishName() {
        return englishName;
    }
}
