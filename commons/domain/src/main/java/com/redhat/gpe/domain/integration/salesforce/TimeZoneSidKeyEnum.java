/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri Feb 19 14:36:19 BRST 2016
 */
package com.redhat.gpe.domain.integration.salesforce;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist TimeZoneSidKey
 */
public enum TimeZoneSidKeyEnum {

    // Africa/Algiers
    AFRICA_ALGIERS("Africa/Algiers"),
    // Africa/Cairo
    AFRICA_CAIRO("Africa/Cairo"),
    // Africa/Casablanca
    AFRICA_CASABLANCA("Africa/Casablanca"),
    // Africa/Johannesburg
    AFRICA_JOHANNESBURG("Africa/Johannesburg"),
    // Africa/Nairobi
    AFRICA_NAIROBI("Africa/Nairobi"),
    // America/Adak
    AMERICA_ADAK("America/Adak"),
    // America/Anchorage
    AMERICA_ANCHORAGE("America/Anchorage"),
    // America/Argentina/Buenos_Aires
    AMERICA_ARGENTINA_BUENOS_AIRES("America/Argentina/Buenos_Aires"),
    // America/Bogota
    AMERICA_BOGOTA("America/Bogota"),
    // America/Caracas
    AMERICA_CARACAS("America/Caracas"),
    // America/Chicago
    AMERICA_CHICAGO("America/Chicago"),
    // America/Denver
    AMERICA_DENVER("America/Denver"),
    // America/El_Salvador
    AMERICA_EL_SALVADOR("America/El_Salvador"),
    // America/Halifax
    AMERICA_HALIFAX("America/Halifax"),
    // America/Indiana/Indianapolis
    AMERICA_INDIANA_INDIANAPOLIS("America/Indiana/Indianapolis"),
    // America/Lima
    AMERICA_LIMA("America/Lima"),
    // America/Los_Angeles
    AMERICA_LOS_ANGELES("America/Los_Angeles"),
    // America/Mazatlan
    AMERICA_MAZATLAN("America/Mazatlan"),
    // America/Mexico_City
    AMERICA_MEXICO_CITY("America/Mexico_City"),
    // America/New_York
    AMERICA_NEW_YORK("America/New_York"),
    // America/Panama
    AMERICA_PANAMA("America/Panama"),
    // America/Phoenix
    AMERICA_PHOENIX("America/Phoenix"),
    // America/Puerto_Rico
    AMERICA_PUERTO_RICO("America/Puerto_Rico"),
    // America/Santiago
    AMERICA_SANTIAGO("America/Santiago"),
    // America/Sao_Paulo
    AMERICA_SAO_PAULO("America/Sao_Paulo"),
    // America/Scoresbysund
    AMERICA_SCORESBYSUND("America/Scoresbysund"),
    // America/St_Johns
    AMERICA_ST_JOHNS("America/St_Johns"),
    // America/Tijuana
    AMERICA_TIJUANA("America/Tijuana"),
    // Asia/Baghdad
    ASIA_BAGHDAD("Asia/Baghdad"),
    // Asia/Baku
    ASIA_BAKU("Asia/Baku"),
    // Asia/Bangkok
    ASIA_BANGKOK("Asia/Bangkok"),
    // Asia/Beirut
    ASIA_BEIRUT("Asia/Beirut"),
    // Asia/Colombo
    ASIA_COLOMBO("Asia/Colombo"),
    // Asia/Dhaka
    ASIA_DHAKA("Asia/Dhaka"),
    // Asia/Dubai
    ASIA_DUBAI("Asia/Dubai"),
    // Asia/Ho_Chi_Minh
    ASIA_HO_CHI_MINH("Asia/Ho_Chi_Minh"),
    // Asia/Hong_Kong
    ASIA_HONG_KONG("Asia/Hong_Kong"),
    // Asia/Jakarta
    ASIA_JAKARTA("Asia/Jakarta"),
    // Asia/Jerusalem
    ASIA_JERUSALEM("Asia/Jerusalem"),
    // Asia/Kabul
    ASIA_KABUL("Asia/Kabul"),
    // Asia/Kamchatka
    ASIA_KAMCHATKA("Asia/Kamchatka"),
    // Asia/Karachi
    ASIA_KARACHI("Asia/Karachi"),
    // Asia/Kathmandu
    ASIA_KATHMANDU("Asia/Kathmandu"),
    // Asia/Kolkata
    ASIA_KOLKATA("Asia/Kolkata"),
    // Asia/Kuala_Lumpur
    ASIA_KUALA_LUMPUR("Asia/Kuala_Lumpur"),
    // Asia/Kuwait
    ASIA_KUWAIT("Asia/Kuwait"),
    // Asia/Manila
    ASIA_MANILA("Asia/Manila"),
    // Asia/Rangoon
    ASIA_RANGOON("Asia/Rangoon"),
    // Asia/Riyadh
    ASIA_RIYADH("Asia/Riyadh"),
    // Asia/Seoul
    ASIA_SEOUL("Asia/Seoul"),
    // Asia/Shanghai
    ASIA_SHANGHAI("Asia/Shanghai"),
    // Asia/Singapore
    ASIA_SINGAPORE("Asia/Singapore"),
    // Asia/Taipei
    ASIA_TAIPEI("Asia/Taipei"),
    // Asia/Tashkent
    ASIA_TASHKENT("Asia/Tashkent"),
    // Asia/Tbilisi
    ASIA_TBILISI("Asia/Tbilisi"),
    // Asia/Tehran
    ASIA_TEHRAN("Asia/Tehran"),
    // Asia/Tokyo
    ASIA_TOKYO("Asia/Tokyo"),
    // Asia/Yekaterinburg
    ASIA_YEKATERINBURG("Asia/Yekaterinburg"),
    // Asia/Yerevan
    ASIA_YEREVAN("Asia/Yerevan"),
    // Atlantic/Azores
    ATLANTIC_AZORES("Atlantic/Azores"),
    // Atlantic/Bermuda
    ATLANTIC_BERMUDA("Atlantic/Bermuda"),
    // Atlantic/Cape_Verde
    ATLANTIC_CAPE_VERDE("Atlantic/Cape_Verde"),
    // Atlantic/South_Georgia
    ATLANTIC_SOUTH_GEORGIA("Atlantic/South_Georgia"),
    // Australia/Adelaide
    AUSTRALIA_ADELAIDE("Australia/Adelaide"),
    // Australia/Brisbane
    AUSTRALIA_BRISBANE("Australia/Brisbane"),
    // Australia/Darwin
    AUSTRALIA_DARWIN("Australia/Darwin"),
    // Australia/Lord_Howe
    AUSTRALIA_LORD_HOWE("Australia/Lord_Howe"),
    // Australia/Perth
    AUSTRALIA_PERTH("Australia/Perth"),
    // Australia/Sydney
    AUSTRALIA_SYDNEY("Australia/Sydney"),
    // Europe/Amsterdam
    EUROPE_AMSTERDAM("Europe/Amsterdam"),
    // Europe/Athens
    EUROPE_ATHENS("Europe/Athens"),
    // Europe/Berlin
    EUROPE_BERLIN("Europe/Berlin"),
    // Europe/Brussels
    EUROPE_BRUSSELS("Europe/Brussels"),
    // Europe/Bucharest
    EUROPE_BUCHAREST("Europe/Bucharest"),
    // Europe/Dublin
    EUROPE_DUBLIN("Europe/Dublin"),
    // Europe/Helsinki
    EUROPE_HELSINKI("Europe/Helsinki"),
    // Europe/Istanbul
    EUROPE_ISTANBUL("Europe/Istanbul"),
    // Europe/Lisbon
    EUROPE_LISBON("Europe/Lisbon"),
    // Europe/London
    EUROPE_LONDON("Europe/London"),
    // Europe/Minsk
    EUROPE_MINSK("Europe/Minsk"),
    // Europe/Moscow
    EUROPE_MOSCOW("Europe/Moscow"),
    // Europe/Paris
    EUROPE_PARIS("Europe/Paris"),
    // Europe/Prague
    EUROPE_PRAGUE("Europe/Prague"),
    // Europe/Rome
    EUROPE_ROME("Europe/Rome"),
    // GMT
    GMT("GMT"),
    // Pacific/Auckland
    PACIFIC_AUCKLAND("Pacific/Auckland"),
    // Pacific/Chatham
    PACIFIC_CHATHAM("Pacific/Chatham"),
    // Pacific/Enderbury
    PACIFIC_ENDERBURY("Pacific/Enderbury"),
    // Pacific/Fiji
    PACIFIC_FIJI("Pacific/Fiji"),
    // Pacific/Gambier
    PACIFIC_GAMBIER("Pacific/Gambier"),
    // Pacific/Guadalcanal
    PACIFIC_GUADALCANAL("Pacific/Guadalcanal"),
    // Pacific/Honolulu
    PACIFIC_HONOLULU("Pacific/Honolulu"),
    // Pacific/Kiritimati
    PACIFIC_KIRITIMATI("Pacific/Kiritimati"),
    // Pacific/Marquesas
    PACIFIC_MARQUESAS("Pacific/Marquesas"),
    // Pacific/Niue
    PACIFIC_NIUE("Pacific/Niue"),
    // Pacific/Norfolk
    PACIFIC_NORFOLK("Pacific/Norfolk"),
    // Pacific/Pago_Pago
    PACIFIC_PAGO_PAGO("Pacific/Pago_Pago"),
    // Pacific/Pitcairn
    PACIFIC_PITCAIRN("Pacific/Pitcairn"),
    // Pacific/Tongatapu
    PACIFIC_TONGATAPU("Pacific/Tongatapu");

    final String value;

    private TimeZoneSidKeyEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static TimeZoneSidKeyEnum fromValue(String value) {
        for (TimeZoneSidKeyEnum e : TimeZoneSidKeyEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
