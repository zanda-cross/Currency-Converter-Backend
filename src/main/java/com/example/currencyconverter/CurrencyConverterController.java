package com.example.currencyconverter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class CurrencyConverterController {

    // API key
    @Value("${exchange.api.key}")
    private String apiKey;

    @Value("${exchange.api.url}")
    private String apiUrl;

    // Mapping currency codes to country names
    private final Map<String, String> currencyNames = Map.ofEntries(
            Map.entry("AED", "United Arab Emirates Dirham"),
            Map.entry("AFN", "Afghan Afghani"),
            Map.entry("ALL", "Albanian Lek"),
            Map.entry("AMD", "Armenian Dram"),
            Map.entry("ANG", "Netherlands Antillean Guilder"),
            Map.entry("AOA", "Angolan Kwanza"),
            Map.entry("ARS", "Argentine Peso"),
            Map.entry("AUD", "Australian Dollar"),
            Map.entry("AWG", "Aruban Florin"),
            Map.entry("AZN", "Azerbaijani Manat"),
            Map.entry("BAM", "Bosnia and Herzegovina Convertible Mark"),
            Map.entry("BBD", "Barbadian Dollar"),
            Map.entry("BDT", "Bangladeshi Taka"),
            Map.entry("BGN", "Bulgarian Lev"),
            Map.entry("BHD", "Bahraini Dinar"),
            Map.entry("BIF", "Burundian Franc"),
            Map.entry("BMD", "Bermudian Dollar"),
            Map.entry("BND", "Brunei Dollar"),
            Map.entry("BOB", "Bolivian Boliviano"),
            Map.entry("BRL", "Brazilian Real"),
            Map.entry("BSD", "Bahamian Dollar"),
            Map.entry("BTN", "Bhutanese Ngultrum"),
            Map.entry("BWP", "Botswana Pula"),
            Map.entry("BYN", "Belarusian Ruble"),
            Map.entry("BZD", "Belize Dollar"),
            Map.entry("CAD", "Canadian Dollar"),
            Map.entry("CDF", "Congolese Franc"),
            Map.entry("CHF", "Swiss Franc"),
            Map.entry("CLP", "Chilean Peso"),
            Map.entry("CNY", "Chinese Yuan"),
            Map.entry("COP", "Colombian Peso"),
            Map.entry("CRC", "Costa Rican Colón"),
            Map.entry("CUP", "Cuban Peso"),
            Map.entry("CVE", "Cape Verdean Escudo"),
            Map.entry("CZK", "Czech Koruna"),
            Map.entry("DJF", "Djiboutian Franc"),
            Map.entry("DKK", "Danish Krone"),
            Map.entry("DOP", "Dominican Peso"),
            Map.entry("DZD", "Algerian Dinar"),
            Map.entry("EGP", "Egyptian Pound"),
            Map.entry("ERN", "Eritrean Nakfa"),
            Map.entry("ETB", "Ethiopian Birr"),
            Map.entry("EUR", "Euro"),
            Map.entry("FJD", "Fijian Dollar"),
            Map.entry("FKP", "Falkland Islands Pound"),
            Map.entry("FOK", "Faroese Króna"),
            Map.entry("GBP", "British Pound Sterling"),
            Map.entry("GEL", "Georgian Lari"),
            Map.entry("GGP", "Guernsey Pound"),
            Map.entry("GHS", "Ghanaian Cedi"),
            Map.entry("GIP", "Gibraltar Pound"),
            Map.entry("GMD", "Gambian Dalasi"),
            Map.entry("GNF", "Guinean Franc"),
            Map.entry("GTQ", "Guatemalan Quetzal"),
            Map.entry("GYD", "Guyanese Dollar"),
            Map.entry("HKD", "Hong Kong Dollar"),
            Map.entry("HNL", "Honduran Lempira"),
            Map.entry("HRK", "Croatian Kuna"),
            Map.entry("HTG", "Haitian Gourde"),
            Map.entry("HUF", "Hungarian Forint"),
            Map.entry("IDR", "Indonesian Rupiah"),
            Map.entry("ILS", "Israeli New Shekel"),
            Map.entry("INR", "Indian Rupee"),
            Map.entry("IQD", "Iraqi Dinar"),
            Map.entry("IRR", "Iranian Rial"),
            Map.entry("ISK", "Icelandic Króna"),
            Map.entry("JEP", "Jersey Pound"),
            Map.entry("JMD", "Jamaican Dollar"),
            Map.entry("JOD", "Jordanian Dinar"),
            Map.entry("JPY", "Japanese Yen"),
            Map.entry("KES", "Kenyan Shilling"),
            Map.entry("KGS", "Kyrgystani Som"),
            Map.entry("KHR", "Cambodian Riel"),
            Map.entry("KID", "Kiribati dollar"),
            Map.entry("KMF", "Comorian Franc"),
            Map.entry("KRW", "South Korean Won"),
            Map.entry("KWD", "Kuwaiti Dinar"),
            Map.entry("KYD", "Cayman Islands Dollar"),
            Map.entry("KZT", "Kazakhstani Tenge"),
            Map.entry("LAK", "Lao Kip"),
            Map.entry("LBP", "Lebanese Pound"),
            Map.entry("LKR", "Sri Lankan Rupee"),
            Map.entry("LRD", "Liberian Dollar"),
            Map.entry("LSL", "Lesotho Loti"),
            Map.entry("LYD", "Libyan Dinar"),
            Map.entry("MAD", "Moroccan Dirham"),
            Map.entry("MDL", "Moldovan Leu"),
            Map.entry("MGA", "Malagasy Ariary"),
            Map.entry("MKD", "Macedonian Denar"),
            Map.entry("MMK", "Burmese Kyat"),
            Map.entry("MNT", "Mongolian Tögrög"),
            Map.entry("MOP", "Macanese Pataca"),
            Map.entry("MUR", "Mauritian Rupee"),
            Map.entry("MRU", "Mauritanian ouguiya"),
            Map.entry("MVR", "Maldivian Rufiyaa"),
            Map.entry("MWK", "Malawian Kwacha"),
            Map.entry("MXN", "Mexican Peso"),
            Map.entry("MYR", "Malaysian Ringgit"),
            Map.entry("MZN", "Mozambican Metical"),
            Map.entry("NAD", "Namibian Dollar"),
            Map.entry("NGN", "Nigerian Naira"),
            Map.entry("NIO", "Nicaraguan Córdoba"),
            Map.entry("NOK", "Norwegian Krone"),
            Map.entry("NPR", "Nepalese Rupee"),
            Map.entry("NZD", "New Zealand Dollar"),
            Map.entry("OMR", "Omani Rial"),
            Map.entry("PAB", "Panamanian Balboa"),
            Map.entry("PEN", "Peruvian Sol"),
            Map.entry("PGK", "Papua New Guinean Kina"),
            Map.entry("PHP", "Philippine Peso"),
            Map.entry("PKR", "Pakistani Rupee"),
            Map.entry("PLN", "Polish Zloty"),
            Map.entry("PYG", "Paraguayan Guaraní"),
            Map.entry("QAR", "Qatari Riyal"),
            Map.entry("RON", "Romanian Leu"),
            Map.entry("RSD", "Serbian Dinar"),
            Map.entry("RUB", "Russian Ruble"),
            Map.entry("RWF", "Rwandan Franc"),
            Map.entry("SAR", "Saudi Riyal"),
            Map.entry("SBD", "Solomon Islands dollar"),
            Map.entry("SCR", "Seychellois Rupee"),
            Map.entry("SDG", "Sudanese Pound"),
            Map.entry("SEK", "Swedish Krona"),
            Map.entry("SGD", "Singapore Dollar"),
            Map.entry("SHP", "Saint Helena Pound"),
            Map.entry("SLE", "Sierra Leonean Leone"),
            Map.entry("SOS", "Somali Shilling"),
            Map.entry("SRD", "Surinamese Dollar"),
            Map.entry("SSP", "South Sudanese pound"),
            Map.entry("STN", "Sao Tomean Dobra"),
            Map.entry("SYP", "Syrian Pound"),
            Map.entry("SZL", "Swazi Lilangeni"),
            Map.entry("THB", "Thai Baht"),
            Map.entry("TJS", "Tajikistani Somoni"),
            Map.entry("TMT", "Turkmen manat"),
            Map.entry("TND", "Tunisian Dinar"),
            Map.entry("TOP", "Tongan Paʻanga"),
            Map.entry("TRY", "Turkish Lira"),
            Map.entry("TTD", "Trinidad and Tobago Dollar"),
            Map.entry("TVD", "The Tuvaluan Dollar"),
            Map.entry("TWD", "New Taiwan Dollar"),
            Map.entry("TZS", "Tanzanian Shilling"),
            Map.entry("UAH", "Ukrainian Hryvnia"),
            Map.entry("UGX", "Ugandan Shilling"),
            Map.entry("USD", "United States Dollar"),
            Map.entry("UYU", "Uruguayan Peso"),
            Map.entry("UZS", "Uzbekistani Soʻm"),
            Map.entry("VES", "Venezuelan Bolívar"),
            Map.entry("VND", "Vietnamese Đồng"),
            Map.entry("VUV", "Vanuatu Vatu"),
            Map.entry("WST", "Samoan Tala"),
            Map.entry("XAF", "Central African CFA Franc"),
            Map.entry("XCD", "East Caribbean Dollar"),
            Map.entry("XCG", "Caribbean guilder"),
            Map.entry("XDR", "Special Drawing Rights"),
            Map.entry("XOF", "West African CFA Franc"),
            Map.entry("XPF", "CFP Franc"),
            Map.entry("YER", "Yemeni Rial"),
            Map.entry("ZAR", "South African Rand"),
            Map.entry("ZMW", "Zambian Kwacha"),
            Map.entry("ZWL", "Zimbabwean Dollar")
    );

    // Currency Conversion
    @GetMapping("/convert")
    public String convertCurrency(@RequestParam String from,
                                  @RequestParam String to,
                                  @RequestParam double amount) {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + "/" + apiKey + "/latest/" + from;

        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);

        if (!json.has("conversion_rates") || !json.getJSONObject("conversion_rates").has(to)) {
            return "{\"error\":\"Invalid currency code\"}";
        }

        double rate = json.getJSONObject("conversion_rates").getDouble(to);
        double convertedAmount = amount * rate;

        JSONObject result = new JSONObject();
        result.put("from", from);
        result.put("to", to);
        result.put("amount", amount);
        result.put("convertedAmount", convertedAmount);
        result.put("rate", rate);

        return result.toString();
    }

    // all currencies with country names
    @GetMapping("/currencies")
    public List<Map<String, String>> getCurrencies() {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + "/" + apiKey + "/latest/USD";
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        List<Map<String, String>> currencyList = new ArrayList<>();

        for (String code : json.getJSONObject("conversion_rates").keySet()) {
            Map<String, String> entry = new HashMap<>();
            entry.put("code", code);
            entry.put("name", currencyNames.getOrDefault(code, "Unknown Currency"));
            currencyList.add(entry);
        }

        return currencyList;
    }


}
