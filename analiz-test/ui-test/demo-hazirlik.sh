#!/usr/bin/env bash
#
# DEMO ONCESI HAZIRLIK
#
# Kullanim (Git Bash):
#   ./demo-hazirlik.sh
#
# Ne yapar:
#   1. Sahipsiz chromedriver sureclerini kapatir
#   2. %TEMP%'teki yarim kalmis tarayici klasorlerini siler
#   3. Isitma kosumu yapar
#
# NEDEN GEREKLI:
#   Coken veya yarida kesilen kosumlar arkalarinda calisan bir chromedriver
#   sureci ve %TEMP%'te scoped_dir* klasorleri birakiyor. Birikince yeni
#   tarayici oturumu acilamiyor ve test daha BASLAMADAN setUp'ta patliyor
#   ("unknown error: JavaScript code failed" / maximize sirasinda).
#
#   Ayrica ortam yeni acildiysa ilk kosum ~47 sn suruyor (JIT + bos lookup
#   onbellekleri), ikinci kosum ~24 sn. Isitma bunu da halleder.
#
# ON KOSUL: tum servisler + frontend ayakta olmalidir.
#
# ONEMLI: frontend "npm start" (ng serve) ile DEGIL, front-end/ icinde
# "npm run start:static" ile baslatilmis olmali. ng serve, lazy-load edilen
# her route'u ilk istekte anlik derliyor; demo sirasinda daha once hic
# ziyaret edilmemis bir route'a gidildiginde bu derleme suresi (birkac-onlarca
# saniye) isitma kosumunun kapsamadigi ekstra gecikme olarak eklenir ve demo
# "eskisi gibi 30 sn" yerine cok daha uzun surer. start:static onceden
# derlenmis (ng build) statik dosyalari servis eder, bu gecikmeyi tamamen
# ortadan kaldirir.
# NOT: isitma kosumu GERCEK MUSTERI ve GERCEK SIPARIS olusturur.

set -u

cd "$(dirname "$0")" || exit 1

echo "=============================================="
echo " DEMO HAZIRLIK"
echo "=============================================="

# --- 1. Sahipsiz chromedriver surecleri -------------------------------------
# Yalnizca chromedriver.exe hedeflenir; kullanicinin normal Chrome pencereleri
# ETKILENMEZ (chromedriver sadece otomasyon tarafindan baslatilir).
echo
echo "[1/3] Sahipsiz chromedriver surecleri kapatiliyor..."
PIDS=$(MSYS_NO_PATHCONV=1 wmic process where "Name='chromedriver.exe'" \
        get ProcessId /format:value 2>/dev/null | tr -d '\r' | grep -o '[0-9]\+')
if [ -z "${PIDS:-}" ]; then
  echo "      calisan chromedriver yok - temiz"
else
  for p in $PIDS; do
    MSYS_NO_PATHCONV=1 taskkill /PID "$p" /F >/dev/null 2>&1 && echo "      kapatildi: PID $p"
  done
fi

# --- 2. Temp birikmesi ------------------------------------------------------
echo
echo "[2/3] Yarim kalmis tarayici klasorleri siliniyor..."
TEMP_DIR="${TEMP:-/c/Users/$USERNAME/AppData/Local/Temp}"
TEMP_DIR=$(echo "$TEMP_DIR" | sed 's|\\|/|g; s|^\([A-Za-z]\):|/\L\1|')
BEFORE=$(ls -d "$TEMP_DIR"/scoped_dir* 2>/dev/null | wc -l)
rm -rf "$TEMP_DIR"/scoped_dir* 2>/dev/null
AFTER=$(ls -d "$TEMP_DIR"/scoped_dir* 2>/dev/null | wc -l)
echo "      scoped_dir: $BEFORE -> $AFTER"

# --- 3. Isitma kosumu -------------------------------------------------------
echo
echo "[3/3] Isitma kosumu basliyor (tarayici acilacak, ~30-50 sn)..."
echo
./mvnw -o test -Dsuite=single 2>&1 | grep -E "GECTI|KALDI|Tests run:|BUILD"

echo
echo "=============================================="
echo " Yukarida BUILD SUCCESS gorduysen hazirsin."
echo " Demo komutu:  ./mvnw test -Dsuite=single"
echo "=============================================="
