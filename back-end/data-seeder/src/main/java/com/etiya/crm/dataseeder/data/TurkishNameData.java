package com.etiya.crm.dataseeder.data;

import java.util.List;

/** Gercekci Turkce isim/adres verisi icin sabit havuzlar. */
public final class TurkishNameData {

	private TurkishNameData() {
	}

	public static final List<String> MALE_FIRST_NAMES = List.of(
			"Ahmet", "Mehmet", "Mustafa", "Ali", "Hasan", "Huseyin", "Ibrahim", "Ismail", "Osman", "Yusuf",
			"Murat", "Emre", "Burak", "Cem", "Kemal", "Serkan", "Volkan", "Tolga", "Baris", "Onur",
			"Deniz", "Caglar", "Erhan", "Fatih", "Gokhan");

	public static final List<String> FEMALE_FIRST_NAMES = List.of(
			"Ayse", "Fatma", "Emine", "Hatice", "Zeynep", "Elif", "Merve", "Busra", "Esra", "Sevgi",
			"Ozge", "Derya", "Gulsen", "Nur", "Pinar", "Selin", "Aylin", "Bahar", "Ceyda", "Dilek",
			"Ebru", "Feride", "Gonca", "Hulya", "Irem");

	public static final List<String> LAST_NAMES = List.of(
			"Yilmaz", "Kaya", "Demir", "Sahin", "Celik", "Yildiz", "Yildirim", "Ozturk", "Aydin", "Ozdemir",
			"Arslan", "Dogan", "Kilic", "Aslan", "Cetin", "Kara", "Koc", "Kurt", "Ozkan", "Simsek",
			"Polat", "Guler", "Aksoy", "Erdogan", "Tas", "Bulut", "Turan", "Ates", "Ozer", "Gunes");

	public static final List<String> STREET_NAMES = List.of(
			"Ataturk Cad.", "Cumhuriyet Cad.", "Istiklal Sok.", "Barbaros Bul.", "Inonu Cad.",
			"Gazi Mustafa Kemal Bul.", "Vatan Cad.", "Fevzi Cakmak Sok.", "Mimar Sinan Cad.", "Yesil Vadi Sok.",
			"Menekse Sok.", "Papatya Sok.", "Zafer Cad.", "Sehit Er Sok.", "Universite Cad.");

	public static final List<String> DESCRIPTIONS = List.of(
			"Ev", "Is yeri", "Yazlik", "Ailenin evi", "Ofis adresi");

	/** shrtCode degerleri lookup-service V5/V13 CITY seed'iyle birebir eslesir. */
	public static final List<String> CITY_SHORT_CODES = List.of(
			"ANKARA", "ISTANBUL", "IZMIR", "BURSA", "ANTALYA", "ADANA", "KONYA",
			"GAZIANTEP", "MERSIN", "KAYSERI", "ESKISEHIR");
}
