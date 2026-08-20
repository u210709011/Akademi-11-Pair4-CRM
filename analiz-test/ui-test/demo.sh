#!/usr/bin/env bash
#
# DEMO KOSUMU - Maven'i atlar, dogrudan TestNG calistirir.
#
# Kullanim (Git Bash):
#   ./demo.sh                 -> testng/single.xml (demo testi)
#   ./demo.sh olcum-fr017     -> baska bir suite
#
# NEDEN VAR:
#   ./mvnw test -Dsuite=single  ~90-100 saniye suruyor. Bunun ~41 saniyesi
#   Maven'in KENDI acilisi (wrapper + JVM + faz taramasi) - testle ilgisi yok.
#   Ayni testi TestNG'ye dogrudan verince toplam 34-40 saniyeye iniyor.
#
#   Onemli: Maven'in raporladigi "Total time" wrapper acilisini SAYMAZ, bu yuzden
#   Maven daha hizli GORUNUR. Gercek duvar saati yukaridaki gibidir.
#
# NE DEGISMEZ: ayni test, ayni listener'lar, ayni Allure sonuclari.
#
# ON KOSUL:
#   - Tum servisler + frontend ayakta
#   - target/test-classes derlenmis  (./mvnw -o test-compile)
#   - target/cp.txt mevcut           (asagida yoksa otomatik uretilir)
#
# NOT: bu test GERCEK MUSTERI ve GERCEK SIPARIS olusturur.

set -u
cd "$(dirname "$0")" || exit 1

SUITE="${1:-single}"
XML="testng/${SUITE}.xml"

[ -f "$XML" ] || { echo "HATA: suite bulunamadi: $XML"; exit 1; }

# --- 1. Artik temizligi (bkz. kos.sh - ayni gerekce) ------------------------
PIDS=$(MSYS_NO_PATHCONV=1 wmic process where "Name='chromedriver.exe'" \
        get ProcessId /format:value 2>/dev/null | tr -d '\r' | grep -o '[0-9]\+')
for p in ${PIDS:-}; do MSYS_NO_PATHCONV=1 taskkill /PID "$p" /F >/dev/null 2>&1; done

TEMP_DIR="${TEMP:-/c/Users/$USERNAME/AppData/Local/Temp}"
TEMP_DIR=$(echo "$TEMP_DIR" | sed 's|\\|/|g; s|^\([A-Za-z]\):|/\L\1|')
rm -rf "$TEMP_DIR"/scoped_dir* 2>/dev/null

# --- 2. Derlenmis siniflar ve classpath -------------------------------------
if [ ! -d target/test-classes ]; then
  echo "test-classes yok, derleniyor..."
  ./mvnw -o -q test-compile || exit 1
fi
if [ ! -f target/cp.txt ]; then
  echo "classpath uretiliyor (tek seferlik)..."
  ./mvnw -o -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt >/dev/null 2>&1 \
    || ./mvnw -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt >/dev/null 2>&1
fi

CP="$(cat target/cp.txt);target/test-classes;src/test/resources"

# Surucuyu sabitle: Selenium Manager'in her kosumda surum kontrolu yapmasini onler.
#
# DIKKAT: onbellekte BIRDEN FAZLA surucu surumu bulunur. Rastgele (or. en yenisini)
# secmek "This version of ChromeDriver only supports Chrome version 152" hatasi verir.
# Bu yuzden KURULU CHROME'un ana surumuyle eslesen surucu secilir.
# Eslesme bulunamazsa hic sabitlenmez; Selenium Manager devreye girer (birkac saniye
# yavaslatir ama DOGRU surucuyu bulur) - yanlis surucu ile patlamaktansa yavas olsun.
CHROME_MAJOR=$(ls -d "/c/Program Files/Google/Chrome/Application"/[0-9]* 2>/dev/null \
                | sed 's|.*/||' | cut -d. -f1 | sort -n | tail -1)
DRV_OPT=""
if [ -n "${CHROME_MAJOR:-}" ]; then
  DRV=$(ls -d "$HOME/.cache/selenium/chromedriver/win64/${CHROME_MAJOR}."*/chromedriver.exe 2>/dev/null | tail -1)
  if [ -n "${DRV:-}" ]; then
    DRV_OPT="-Dwebdriver.chrome.driver=$(cygpath -w "$DRV")"
  else
    echo "Not: Chrome $CHROME_MAJOR icin onbellekte surucu yok, Selenium Manager kullanilacak."
  fi
fi

# --- 3. Kosum ---------------------------------------------------------------
START=$(date +%s)
MSYS_NO_PATHCONV=1 java -cp "$CP" ${DRV_OPT:-} \
  -Dallure.results.directory=target/allure-results \
  org.testng.TestNG "$XML" -d target/testng-out
CODE=$?
END=$(date +%s)

echo
echo "=============================================="
echo " Sure: $((END-START)) saniye"
[ $CODE -eq 0 ] && echo " SONUC: BASARILI" || echo " SONUC: BASARISIZ (cikis kodu $CODE)"
echo "=============================================="
exit $CODE
