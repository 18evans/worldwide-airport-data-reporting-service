package evans18.lunatechairportsbackend.data.repository;

/**
 * Index names that ES uses to index documents (classes) and fields (class fields).
 */
public class ElasticSearchConstants {
    public static final String ES_DOC_COUNTRY_FIELD_COUNTRY_NAME = "name";
    public static final String ES_DOC_COUNTRY_FIELD_COUNTRY_CODE = "code";
    public static final String ES_DOC_AIRPORT_FIELD_ISO_COUNTRY_CODE = "isoCountry";

    public static final String DOCUMENT_INDEX_AIRPORTS = "airport";
    public static final String DOCUMENT_INDEX_COUNTRIES = "country";
    public static final String DOCUMENT_INDEX_RUNWAYS = "runway";
}
