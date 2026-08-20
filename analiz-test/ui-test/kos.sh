#!/usr/bin/env bash
#
# TEMIZLE + KOS
#
# Kullanim (Git Bash):
#   ./kos.sh              -> single suite (demo testi)
#   ./kos.sh regression   -> baska bir suite
#   ./kos.sh fr009
#
# NEDEN VAR:
#   Coken veya yarida kesilen kosumlar arkalarinda calisan bir chromedriver
#   sureci ve %TEMP%'te profil klasorleri birakiyor (tearDown'a hic ulasilmadigi
#   icin). Birikince yeni tarayici oturumu acilamiyor ve test daha BASLAMADAN
#   setUp'ta patliyor. Kotusu: her cokme yeni artik birakir, o da bir sonrakini
#   bozar - art arda denemek durumu KOTULESTIRIR.
#
#   Bu betik her kosumdan ONCE artiklari temizler, boylece her kosum temiz bir
#   baslangictan gider.
#
# GUVENLIK: yalnizca chromedriver.exe kapatilir. Senin kendi Chrome pencerelerin
# ETKILENMEZ - chromedriver sadece test otomasyonu tarafindan baslatilir.

set -u

cd "$(dirname "$0")" || exit 1

SUITE="${1:-single}"

# --- Temizlik ---------------------------------------------------------------
# Ciktiyi kisa tutuyoruz; amac gurultu degil, temiz baslangic.
PIDS=$(MSYS_NO_PATHCONV=1 wmic process where "Name='chromedriver.exe'" \
        get ProcessId /format:value 2>/dev/null | tr -d '\r' | grep -o '[0-9]\+')
KILLED=0
if [ -n "${PIDS:-}" ]; then
  for p in $PIDS; do
    MSYS_NO_PATHCONV=1 taskkill /PID "$p" /F >/dev/null 2>&1 && KILLED=$((KILLED+1))
  done
fi

TEMP_DIR="${TEMP:-/c/Users/$USERNAME/AppData/Local/Temp}"
TEMP_DIR=$(echo "$TEMP_DIR" | sed 's|\\|/|g; s|^\([A-Za-z]\):|/\L\1|')
DIRS=$(ls -d "$TEMP_DIR"/scoped_dir* 2>/dev/null | wc -l)
rm -rf "$TEMP_DIR"/scoped_dir* 2>/dev/null

echo "Temizlik: $KILLED surec kapatildi, $DIRS klasor silindi. Suite: $SUITE"
echo

# --- Kosum ------------------------------------------------------------------
exec ./mvnw test -Dsuite="$SUITE"
